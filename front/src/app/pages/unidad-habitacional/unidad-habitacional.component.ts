import { Component } from '@angular/core';
import { DropletsIcon, HouseIcon, LucideAngularModule, ZapIcon } from 'lucide-angular';

@Component({
  selector: 'app-unidad-habitacional',
  standalone: true,
  imports: [LucideAngularModule],
  templateUrl: './unidad-habitacional.component.html',
  styleUrl: './unidad-habitacional.component.css'
})
export class UnidadHabitacionalComponent {
  readonly dropletsIcon = DropletsIcon;
  readonly zapIcon = ZapIcon;
  readonly houseIcon = HouseIcon;
}
