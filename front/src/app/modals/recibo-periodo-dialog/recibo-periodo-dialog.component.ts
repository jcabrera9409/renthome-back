import { Component, Inject, OnInit, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { FormMethods } from '../../util/form';
import { NotificationService } from '../../_service/notification.service';
import { Message } from '../../_model/message';
import { ReciboService } from '../../_service/recibo.service';
import { CasaService } from '../../_service/casa.service';
import { catchError, EMPTY, finalize, switchMap } from 'rxjs';

export interface PeriodoData {
  mes: number;
  anio: number;
}

@Component({
  selector: 'app-recibo-periodo-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoaderComponent],
  templateUrl: './recibo-periodo-dialog.component.html',
  styleUrl: './recibo-periodo-dialog.component.css'
})
export class ReciboPeriodoDialogComponent implements OnInit {
  form: FormGroup;
  isLoading = false;
  availableYears: number[] = [];

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ReciboPeriodoDialogComponent>,
    private renderer: Renderer2,
    private reciboService: ReciboService,
    private notificationService: NotificationService,
    private casaService: CasaService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.fb.group({
      mes: ['', Validators.required],
      anio: ['', Validators.required]
    });

    FormMethods.addSubscribesForm(this.form, this.renderer);
  }

  ngOnInit() {
    this.generateAvailableYears();
    this.setDefaultValues();
  }

  private generateAvailableYears() {
    const currentYear = new Date().getFullYear();
    // Generar años desde 2020 hasta el año actual + 1
    for (let year = 2020; year <= currentYear + 1; year++) {
      this.availableYears.push(year);
    }
  }

  private setDefaultValues() {
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth() + 1;
    const currentYear = currentDate.getFullYear();
    
    this.form.patchValue({
      mes: currentMonth < 10 ? '0' + currentMonth : currentMonth,
      anio: currentYear
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      FormMethods.validateForm(this.form, this.renderer);
      this.notificationService.setMessageChange(
        Message.error("Por favor, complete todos los campos requeridos.")
      )
      return;
    }

    this.generarRecibos();
  }

  onCancel() {
    this.dialogRef.close();
  }

  private generarRecibos() {
    this.isLoading = true;

    const casaId = this.casaService.getCasaStorage().id;
    const year = parseInt(this.form.value.anio);
    const month = parseInt(this.form.value.mes);

    const operation = this.reciboService.generarRecibos(casaId, year, month);

    operation
      .pipe(
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al procesar la información.', error)
          )
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if (response.success) {
          this.reciboService.setPeriodoCambio(`${this.form.value.anio}-${this.form.value.mes}`);
          this.notificationService.setMessageChange(
            Message.success('Recibos generados exitosamente.')
          );
          this.dialogRef.close();
        } else {
          this.notificationService.setMessageChange(
            Message.error('Ocurrió un error al generar los recibos.', response)
          );
        }
      })
  }
}
