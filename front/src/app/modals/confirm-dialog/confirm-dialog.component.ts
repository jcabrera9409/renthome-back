import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmDataDTO } from '../../_model/dto';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.css'
})
export class ConfirmDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDataDTO
  ) {
    // Valores por defecto
    this.data = {
      title: data?.title || 'Confirmar Inactivación',
      message: data?.message || '¿Estás seguro de que deseas inactivar este elemento?',
      confirmText: data?.confirmText || 'Confirmar',
      cancelText: data?.cancelText || 'Cancelar'
    };
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
