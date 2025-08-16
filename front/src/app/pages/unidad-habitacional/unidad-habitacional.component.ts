import { Component } from '@angular/core';
import { DropletsIcon, HouseIcon, LucideAngularModule, PlusIcon, ZapIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UnidadEdicionDialogComponent } from '../../modals/unidad-edicion-dialog/unidad-edicion-dialog.component';

@Component({
  selector: 'app-unidad-habitacional',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule],
  templateUrl: './unidad-habitacional.component.html',
  styleUrl: './unidad-habitacional.component.css'
})
export class UnidadHabitacionalComponent {
  readonly plusIcon = PlusIcon;
  readonly dropletsIcon = DropletsIcon;
  readonly zapIcon = ZapIcon;
  readonly houseIcon = HouseIcon;

  constructor(
    private dialog: MatDialog
  ) {}

  openDialog() {
    const dialogRef = this.dialog.open(UnidadEdicionDialogComponent, {
      data: { /* Pass data to the dialog if needed */ },
      panelClass: 'w-full'
    });
  }
}
