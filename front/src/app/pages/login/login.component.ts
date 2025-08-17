import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LucideAngularModule, BuildingIcon } from 'lucide-angular';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../_service/auth.service';
import { finalize } from 'rxjs';
import { UtilMethods } from '../../util/util';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [LucideAngularModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  readonly buildingIcon = BuildingIcon
  loginForm: FormGroup

  constructor(
    private router: Router,
    private authService: AuthService
  ) { 
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required])
    });
  }

  onSubmit() {
    if (!this.loginForm.valid) {
      return;
    }
    const { email, password } = this.loginForm.value;
    this.authService.login(email, password)
      .pipe(
        finalize(() => {
          // Code to run after the login request completes (success or error)
        })
      )
      .subscribe({
        next: (response) => {
          UtilMethods.getInstance().setJwtToken(response.data.access_token);
          this.router.navigate(['/admin']);
        },
        error: (error) => {
          // Handle login error
          console.error('Login failed:', error);
        }
      });
  }
}
