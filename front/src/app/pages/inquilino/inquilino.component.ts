import { Component } from '@angular/core';
import { IdCardIcon, LucideAngularModule, MailIcon, PhoneIcon, PlusIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { InquilinoEdicionDialogComponent } from '../../modals/inquilino-edicion-dialog/inquilino-edicion-dialog.component';

@Component({
  selector: 'app-inquilino',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule],
  templateUrl: './inquilino.component.html',
  styleUrl: './inquilino.component.css'
})
export class InquilinoComponent {
  readonly plusIcon = PlusIcon;
  readonly idCardIcon = IdCardIcon;
  readonly phoneIcon = PhoneIcon;
  readonly mailIcon = MailIcon;

  constructor(private dialog: MatDialog) {}

  openDialog(): void {
    const dialogRef = this.dialog.open(InquilinoEdicionDialogComponent, {
      data: { /* Pass data to the dialog if needed */ },
      panelClass: 'w-full'
    });
  }
}
