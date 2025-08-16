import { Component } from '@angular/core';
import { LucideAngularModule, PlusIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ContratoEdicionDialogComponent } from '../../modals/contrato-edicion-dialog/contrato-edicion-dialog.component';

@Component({
  selector: 'app-contrato',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule],
  templateUrl: './contrato.component.html',
  styleUrl: './contrato.component.css'
})
export class ContratoComponent {
  readonly plusIcon = PlusIcon;

  constructor(private dialog: MatDialog) {}

  openDialog() {
    const dialogRef = this.dialog.open(ContratoEdicionDialogComponent, {
      data: { /* Pass data to the dialog if needed */ },
      panelClass: 'w-full'
    });
  }
}
