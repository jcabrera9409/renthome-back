import { Component } from '@angular/core';
import { LucideAngularModule, PlusIcon } from 'lucide-angular';

@Component({
  selector: 'app-contrato',
  standalone: true,
  imports: [LucideAngularModule],
  templateUrl: './contrato.component.html',
  styleUrl: './contrato.component.css'
})
export class ContratoComponent {
  readonly plusIcon = PlusIcon;
}
