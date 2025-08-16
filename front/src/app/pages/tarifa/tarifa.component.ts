import { Component } from '@angular/core';
import { LucideAngularModule, PlusIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TarifaEdicionDialogComponent } from '../../modals/tarifa-edicion-dialog/tarifa-edicion-dialog.component';

@Component({
  selector: 'app-tarifa',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule],
  templateUrl: './tarifa.component.html',
  styleUrl: './tarifa.component.css'
})
export class TarifaComponent {
  readonly plusIcon = PlusIcon;

  constructor(private dialog: MatDialog) {}

  openDialog() {
    const dialogRef = this.dialog.open(TarifaEdicionDialogComponent, {
      data: { /* Pass data to the dialog if needed */ },
      panelClass: 'w-full'
    });
  }
}
