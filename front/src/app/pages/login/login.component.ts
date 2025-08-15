import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LucideAngularModule, BuildingIcon } from 'lucide-angular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [LucideAngularModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  readonly buildingIcon = BuildingIcon

  constructor(
    private router: Router
  ) { }

  onSubmit() {
    this.router.navigate(['/admin']);
  }
}
