import { Component, Renderer2 } from '@angular/core';
import { Router } from '@angular/router';
import { LucideAngularModule, BuildingIcon } from 'lucide-angular';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../_service/auth.service';
import { finalize } from 'rxjs';
import { UtilMethods } from '../../util/util';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../_service/notification.service';
import { Message } from '../../_model/message';
import { FormMethods } from '../../util/form';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [LucideAngularModule, ReactiveFormsModule, LoaderComponent, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  readonly buildingIcon = BuildingIcon
  isLoading = false;
  loginForm: FormGroup

  constructor(
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService,
    private renderer: Renderer2
  ) {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required])
    });

    FormMethods.addSubscribesForm(this.loginForm, this.renderer);
  }

  onSubmit() {
    if (!this.loginForm.valid) {
      FormMethods.validateForm(this.loginForm, this.renderer);
      this.notificationService.setMessageChange(
        Message.error('Por favor, complete todos los campos requeridos correctamente.')
      );
      return;
    }
    this.isLoading = true;
    const { email, password } = this.loginForm.value;
    this.authService.login(email, password)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: (response) => {
          UtilMethods.getInstance().setJwtToken(response.data.access_token);
          this.router.navigate(['/admin']);
        },
        error: (error) => {
          this.notificationService.setMessageChange(
            Message.error('Error de inicio de sesión. Por favor, inténtelo de nuevo.')
          );
        }
      });
  }
}
