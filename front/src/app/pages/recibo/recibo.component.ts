import { Component } from '@angular/core';
import { LucideAngularModule, ReceiptIcon } from 'lucide-angular';

@Component({
  selector: 'app-recibo',
  standalone: true,
  imports: [LucideAngularModule],
  templateUrl: './recibo.component.html',
  styleUrl: './recibo.component.css'
})
export class ReciboComponent {

  readonly receiptIcon = ReceiptIcon;
  
}
