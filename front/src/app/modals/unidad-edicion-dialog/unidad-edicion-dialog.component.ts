import { Component, Inject, Renderer2 } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UnidadHabitacionalService } from '../../_service/unidad-habitacional.service';
import { CasaService } from '../../_service/casa.service';
import { NotificationService } from '../../_service/notification.service';
import { UnidadHabitacional } from '../../_model/unidad-habitacional';
import { FormMethods } from '../../util/form';
import { Message } from '../../_model/message';
import { catchError } from 'rxjs/internal/operators/catchError';
import { EMPTY, finalize, switchMap } from 'rxjs';

@Component({
  selector: 'app-unidad-edicion-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, LoaderComponent],
  templateUrl: './unidad-edicion-dialog.component.html',
  styleUrl: './unidad-edicion-dialog.component.css'
})
export class UnidadEdicionDialogComponent {

  form: FormGroup;
  tituloDialogo: string = 'Nueva Unidad Habitacional';
  isLoading: boolean = false;

  constructor(
    private dialogRef: MatDialogRef<UnidadEdicionDialogComponent>,
    private unidadHabitacionalService: UnidadHabitacionalService,
    private casaService: CasaService,
    private notificationService: NotificationService,
    private renderer: Renderer2,
    @Inject(MAT_DIALOG_DATA) private unidad: UnidadHabitacional | null
  ) {
    this.form = new FormGroup({
      "nombre": new FormControl(this.unidad?.nombre || '', Validators.required),
      "descripcion": new FormControl(this.unidad?.descripcion || '', Validators.required),
      "incluyeAgua": new FormControl(this.unidad?.incluyeAgua || false),
      "incluyeLuz": new FormControl(this.unidad?.incluyeLuz || false),
      "tipoUnidad": new FormControl(this.unidad?.tipoUnidad || 'Cuarto', Validators.required)
    });

    if (this.unidad) {
      this.tituloDialogo = 'Editar Unidad Habitacional';
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

    let unidadData: UnidadHabitacional = new UnidadHabitacional();
    unidadData.nombre = this.form.value.nombre;
    unidadData.descripcion = this.form.value.descripcion;
    unidadData.incluyeAgua = this.form.value.incluyeAgua;
    unidadData.incluyeLuz = this.form.value.incluyeLuz;
    unidadData.tipoUnidad = this.form.value.tipoUnidad;
    unidadData.id = this.unidad ? this.unidad.id : 0;
    unidadData.estado = this.unidad ? this.unidad.estado : 'Disponible';
    unidadData.casa = this.casaService.getCasaStorage();

    const operation = (this.unidad) ?
      this.unidadHabitacionalService.modificar(unidadData) :
      this.unidadHabitacionalService.registrar(unidadData);

    operation
      .pipe(
        catchError((error) => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al procesar la solicitud.')
          );
          return EMPTY;
        }),
        switchMap(() => this.unidadHabitacionalService.listarPaginado(unidadData.casa.id, '', 0, 9)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al listar las unidades habitacionales.', error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        }) 
      )
      .subscribe((response) => {
        if (response.success) {
          this.unidadHabitacionalService.setObjetoPageableCambio(response.data);
          this.notificationService.setMessageChange(
            Message.success('La unidad habitacional se ha guardado correctamente.')
          );
          this.dialogRef.close(true);
        } else {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al guardar la unidad habitacional.')
          );
        }
      })
  }

  onCancel() {
    this.dialogRef.close(false);
  }

}
