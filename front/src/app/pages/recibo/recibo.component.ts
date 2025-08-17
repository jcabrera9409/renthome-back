import { Component } from '@angular/core';
import { LucideAngularModule, ReceiptIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReciboEdicionDialogComponent } from '../../modals/recibo-edicion-dialog/recibo-edicion-dialog.component';
import { ReciboVerDialogComponent } from '../../modals/recibo-ver-dialog/recibo-ver-dialog.component';

@Component({
  selector: 'app-recibo',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule],
  templateUrl: './recibo.component.html',
  styleUrl: './recibo.component.css'
})
export class ReciboComponent {

  readonly receiptIcon = ReceiptIcon;

  constructor(private dialog: MatDialog) {}

  openEditDialog() {
    const dialogRef = this.dialog.open(ReciboEdicionDialogComponent, {
      data: { /* Pass any data you need here */ },
      panelClass: 'w-full'
    });
  }

  openViewDialog() {
    const dialogRef = this.dialog.open(ReciboVerDialogComponent, {
      data: { /* Pass any data you need here */ },
      panelClass: 'w-full'
    });
  }
}
