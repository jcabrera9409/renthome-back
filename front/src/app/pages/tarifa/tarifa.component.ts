import { Component } from '@angular/core';
import { LucideAngularModule, PlusIcon } from 'lucide-angular';

@Component({
  selector: 'app-tarifa',
  standalone: true,
  imports: [LucideAngularModule],
  templateUrl: './tarifa.component.html',
  styleUrl: './tarifa.component.css'
})
export class TarifaComponent {
  readonly plusIcon = PlusIcon;
}
