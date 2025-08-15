import { Component } from '@angular/core';
import { CreditCardIcon, FileTextIcon, HouseIcon, LucideAngularModule, UsersIcon } from 'lucide-angular';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LucideAngularModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  readonly houseIcon = HouseIcon;
  readonly fileTextIcon = FileTextIcon;
  readonly usersIcon = UsersIcon;
  readonly creditCardIcon = CreditCardIcon;
}
