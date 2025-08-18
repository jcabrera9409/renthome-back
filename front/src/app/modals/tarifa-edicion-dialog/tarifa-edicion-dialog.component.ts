import { Component, Inject, Renderer2 } from '@angular/core';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TarifaService } from '../../_service/tarifa.service';
import { NotificationService } from '../../_service/notification.service';
import { CasaService } from '../../_service/casa.service';
import { Tarifa } from '../../_model/tarifa';
import { FormMethods } from '../../util/form';
import { Message } from '../../_model/message';
import { catchError, EMPTY, finalize, switchMap } from 'rxjs';

@Component({
  selector: 'app-tarifa-edicion-dialog',
  standalone: true,
  imports: [CommonModule, LoaderComponent, ReactiveFormsModule],
  templateUrl: './tarifa-edicion-dialog.component.html',
  styleUrl: './tarifa-edicion-dialog.component.css'
})
export class TarifaEdicionDialogComponent {

  form: FormGroup;
  tituloDialogo: string = 'Nueva Tarifa de Servicio';
  isLoading = false;

  constructor(
    private dialogRef: MatDialogRef<TarifaEdicionDialogComponent>,
    private tarifaService: TarifaService,
    private casaService: CasaService,
    private notificationService: NotificationService,
    private renderer: Renderer2,
    @Inject(MAT_DIALOG_DATA) private tarifa: Tarifa | null
  ) {
    this.form = new FormGroup({
      "tipo": new FormControl(this.tarifa?.tipoServicio || '', Validators.required),
      "unidad": new FormControl(this.tarifa?.unidad || '', Validators.required),
      "precio": new FormControl(this.tarifa?.precioUnidad || 0, [Validators.required, Validators.min(0.1)]),
    });

    if (this.tarifa) {
      this.tituloDialogo = 'Editar Tarifa de Servicio';
    }

    FormMethods.addSubscribesForm(this.form, this.renderer);
  }

  onSubmit() {
    if (this.form.invalid) {
      FormMethods.validateForm(this.form, this.renderer);
      this.notificationService.setMessageChange(
        Message.error('Por favor, complete todos los campos requeridos.')
      );
      return;
    }

    this.isLoading = true;

    let tarifaData: Tarifa = new Tarifa();
    tarifaData.tipoServicio = this.form.value.tipo;
    tarifaData.id = this.tarifa ? this.tarifa.id : 0;
    tarifaData.unidad = this.form.value.unidad;
    tarifaData.precioUnidad = this.form.value.precio;
    tarifaData.activo = true;
    tarifaData.rangoInicio = 0;
    tarifaData.rangoFin = 0;
    tarifaData.casa = this.casaService.getCasaStorage();

    const operation = (this.tarifa) ?
      this.tarifaService.modificar(tarifaData) :
      this.tarifaService.registrar(tarifaData);

    operation
      .pipe(
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al procesar la información.', error)
          );
          return EMPTY;
        }),
        switchMap(() => this.tarifaService.listar(tarifaData.casa.id)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al listar las tarifas.', error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if (response.success) {
          this.tarifaService.setObjetoCambio(response.data);
          this.notificationService.setMessageChange(
            Message.success('La tarifa se ha guardado correctamente.')
          );
          this.dialogRef.close();
        } else {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al guardar la tarifa.', response)
          );
        }
      });
  }

  onCancel() {
    this.dialogRef.close();
  }
}
