import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Recibo } from '../../_model/recibo';
import { UtilMethods } from '../../util/util';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recibo-ver-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './recibo-ver-dialog.component.html',
  styleUrl: './recibo-ver-dialog.component.css'
})
export class ReciboVerDialogComponent {

  utilMethods = UtilMethods.getInstance();

  constructor(
    private dialogRef: MatDialogRef<ReciboVerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public recibo: Recibo
  ) { 
    console.log(recibo);
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onPrint(): void {
    // Alternativa 1: Ventana nueva con contenido personalizado (implementación actual)
    const printContent = document.getElementById('modalContent');
    
    if (printContent) {
      const printWindow = window.open('', '', 'height=600,width=800');
      
      if (printWindow) {
        printWindow.document.write(`
          <html>
            <head>
              <title>Recibo de Alquiler</title>
              <style>
                body {
                  font-family: Arial, sans-serif;
                  margin: 20px;
                  color: #000;
                }
                .text-center { text-align: center; }
                .text-xl { font-size: 1.25rem; }
                .font-bold { font-weight: bold; }
                .text-gray-900 { color: #111827; }
                .text-gray-600 { color: #4b5563; }
                .grid { display: grid; }
                .grid-cols-2 { grid-template-columns: repeat(2, minmax(0, 1fr)); }
                .gap-6 { gap: 1.5rem; }
                .mb-6 { margin-bottom: 1.5rem; }
                .mb-2 { margin-bottom: 0.5rem; }
                .mb-3 { margin-bottom: 0.75rem; }
                .font-semibold { font-weight: 600; }
                .text-sm { font-size: 0.875rem; }
                .border-t { border-top: 1px solid #e5e7eb; }
                .pt-4 { padding-top: 1rem; }
                .space-y-2 > * + * { margin-top: 0.5rem; }
                .flex { display: flex; }
                .justify-between { justify-content: space-between; }
                .font-medium { font-weight: 500; }
                .mt-4 { margin-top: 1rem; }
                .text-lg { font-size: 1.125rem; }
              </style>
            </head>
            <body>
              ${printContent.innerHTML}
            </body>
          </html>
        `);
        
        printWindow.document.close();
        
        setTimeout(() => {
          printWindow.print();
          printWindow.close();
        }, 250);
      }
    }

    // Alternativa 2: Usar window.print() directamente con CSS @media print
    // Descomenta estas líneas si prefieres esta opción:
    // window.print();
  }
}
