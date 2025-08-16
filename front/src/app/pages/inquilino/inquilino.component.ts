import { Component } from '@angular/core';
import { IdCardIcon, LucideAngularModule, MailIcon, PhoneIcon, PlusIcon } from 'lucide-angular';

@Component({
  selector: 'app-inquilino',
  standalone: true,
  imports: [LucideAngularModule],
  templateUrl: './inquilino.component.html',
  styleUrl: './inquilino.component.css'
})
export class InquilinoComponent {
  readonly plusIcon = PlusIcon;
  readonly idCardIcon = IdCardIcon;
  readonly phoneIcon = PhoneIcon;
  readonly mailIcon = MailIcon;
}
