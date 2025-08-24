import { Component, Inject, Renderer2 } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { InquilinoService } from '../../_service/inquilino.service';
import { NotificationService } from '../../_service/notification.service';
import { Inquilino } from '../../_model/inquilino';
import { FormMethods } from '../../util/form';
import { Message } from '../../_model/message';
import { catchError } from 'rxjs/internal/operators/catchError';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { finalize, switchMap } from 'rxjs';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/loader/loader.component';


@Component({
  selector: 'app-inquilino-edicion-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, LoaderComponent],
  templateUrl: './inquilino-edicion-dialog.component.html',
  styleUrl: './inquilino-edicion-dialog.component.css'
})
export class InquilinoEdicionDialogComponent {

  form: FormGroup;
  tituloDialogo: string = 'Nuevo Inquilino';
  isLoading: boolean = false;

  constructor(
    private dialogRef: MatDialogRef<InquilinoEdicionDialogComponent>,
    private inquilinoService: InquilinoService,
    private notificationService: NotificationService,
    private renderer: Renderer2,
    @Inject(MAT_DIALOG_DATA) private inquilino: Inquilino | null
  ) {
    this.form = new FormGroup({
      nombre: new FormControl(this.inquilino?.nombreCompleto || '', Validators.required),
      documento: new FormControl(this.inquilino?.documentoIdentidad || '', Validators.required),
      correo: new FormControl(this.inquilino?.correo || '', [Validators.required, Validators.email]),
      telefono: new FormControl(this.inquilino?.telefono || '', Validators.required)
    }); 

    if (this.inquilino) {
      this.tituloDialogo = 'Editar Inquilino';
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

    let inquilinoData = new Inquilino();
    inquilinoData.nombreCompleto = this.form.value['nombre'];
    inquilinoData.documentoIdentidad = this.form.value['documento'];
    inquilinoData.correo = this.form.value['correo'];
    inquilinoData.telefono = this.form.value['telefono'];
    inquilinoData.id = this.inquilino?.id || 0;
    inquilinoData.activo = true;
    
    const operation = (this.inquilino) ?
      this.inquilinoService.modificar(inquilinoData) :
      this.inquilinoService.registrar(inquilinoData);

    operation
      .pipe(
        catchError((error) => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al procesar la solicitud.')
          );
          return EMPTY;
        }),
        switchMap(() => this.inquilinoService.listarPaginadoInquilinos('', 0, 9)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al listar los inquilinos.', error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        }) 
      )
      .subscribe((response) => {
        if (response.success) {
          this.inquilinoService.setObjetoPageableCambio(response.data);
          this.notificationService.setMessageChange(
            Message.success('El inquilino se ha guardado correctamente.')
          );
          this.dialogRef.close(true);
        } else {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al guardar el inquilino.')
          );
        }
      })
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
