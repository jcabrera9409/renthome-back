import { Component, Inject, OnInit, Renderer2 } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ContratoService } from '../../_service/contrato.service';
import { CasaService } from '../../_service/casa.service';
import { NotificationService } from '../../_service/notification.service';
import { Contrato } from '../../_model/contrato';
import { Inquilino } from '../../_model/inquilino';
import { FormMethods } from '../../util/form';
import { UnidadHabitacionalService } from '../../_service/unidad-habitacional.service';
import { InquilinoService } from '../../_service/inquilino.service';
import { UnidadHabitacional } from '../../_model/unidad-habitacional';
import { EMPTY, firstValueFrom } from 'rxjs';
import { switchMap, tap, catchError, finalize } from 'rxjs/operators';
import { Message } from '../../_model/message';
import { CasaDTO } from '../../_model/dto';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/loader/loader.component';

@Component({
  selector: 'app-contrato-edicion-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, LoaderComponent],
  templateUrl: './contrato-edicion-dialog.component.html',
  styleUrl: './contrato-edicion-dialog.component.css'
})
export class ContratoEdicionDialogComponent implements OnInit {

  form: FormGroup;
  tituloDialogo: string = 'Nuevo Contrato';
  isLoading = false;
  casaActual: CasaDTO | null = null;

  inquilinos: Inquilino[] = [];
  unidades: UnidadHabitacional[] = [];

  constructor(
    private dialogRef: MatDialogRef<ContratoEdicionDialogComponent>,
    private contratoService: ContratoService,
    private inquilinoService: InquilinoService,
    private unidadService: UnidadHabitacionalService,
    private casaService: CasaService,
    private notificationService: NotificationService,
    private renderer: Renderer2,
    @Inject(MAT_DIALOG_DATA) private contrato: Contrato | null
  ) {
    this.casaActual = this.casaService.getCasaStorage();

    const fechaInicio = new Date();
    const fechaFin = new Date(fechaInicio);
    fechaFin.setFullYear(fechaFin.getFullYear() + 1);

    this.form = new FormGroup({
      unidad: new FormControl(null, Validators.required),
      inquilino: new FormControl(null, Validators.required),
      fechaInicio: new FormControl(fechaInicio.toISOString().split('T')[0], Validators.required),
      fechaFin: new FormControl(fechaFin.toISOString().split('T')[0], Validators.required),
      montoRentaMensual: new FormControl(0, [Validators.required, Validators.min(1)]),
      garantia: new FormControl(0, [Validators.required])
    });

    FormMethods.addSubscribesForm(this.form, this.renderer);
  }

  ngOnInit(): void {
    this.cargarDatosDisponibles();

    this.form.get('fechaInicio')?.valueChanges.subscribe(value => {
      const fechaInicio = new Date(value);
      const fechaFin = new Date(fechaInicio);
      fechaFin.setFullYear(fechaFin.getFullYear() + 1);
      this.form.get('fechaFin')?.setValue(fechaFin.toISOString().split('T')[0]);
    });

  }

  onSubmit(): void {
    if (this.form.invalid) {
      FormMethods.validateForm(this.form, this.renderer);
      this.notificationService.setMessageChange(
        Message.error('Por favor, complete todos los campos requeridos.')
      );
      return;
    }

    this.isLoading = true;

    let contratoData: Contrato = new Contrato();
    contratoData.id = this.contrato ? this.contrato.id : 0;
    contratoData.fechaInicio = this.form.value.fechaInicio;
    contratoData.fechaFin = this.form.value.fechaFin;
    contratoData.montoRentaMensual = this.form.value.montoRentaMensual;
    contratoData.garantia = this.form.value.garantia;
    contratoData.activo = true;
    contratoData.unidad = new UnidadHabitacional();
    contratoData.unidad.id = this.form.value.unidad;
    contratoData.inquilino = new Inquilino();
    contratoData.inquilino.id = this.form.value.inquilino;

    const operation = (this.contrato) ?
      this.contratoService.modificar(contratoData) :
      this.contratoService.registrar(contratoData);

    operation
      .pipe(
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al procesar la información.', error)
          );
          return EMPTY;
        }),
        switchMap(() => this.contratoService.listarPaginado(this.casaActual.id, '', 0, 10)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al listar los contratos.', error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if (response.success) {
          this.contratoService.setObjetoPageableCambio(response.data);
          this.notificationService.setMessageChange(
            Message.success('El contrato se ha guardado correctamente.')
          );
          this.dialogRef.close();
        } else {
          this.notificationService.setMessageChange(
            Message.error('Ocurrio un error al guardar el contrato.', response)
          );
        }
      });

  }

  onCancel(): void {
    this.dialogRef.close();
  }

  private iniciarFormulario(): void {
    this.form.controls['unidad'].setValue(this.unidades[0]?.id);
    this.form.controls['inquilino'].setValue(this.inquilinos[0]?.id);

    if (this.contrato) {
      this.tituloDialogo = 'Editar Contrato';

      this.unidades.push(this.contrato.unidad);

      this.form.controls['unidad'].setValue(this.contrato.unidad.id);
      this.form.controls['inquilino'].setValue(this.contrato.inquilino.id);
      this.form.controls['fechaInicio'].setValue(this.contrato.fechaInicio);
      this.form.controls['fechaFin'].setValue(this.contrato.fechaFin);
      this.form.controls['montoRentaMensual'].setValue(this.contrato.montoRentaMensual);
      this.form.controls['garantia'].setValue(this.contrato.garantia);
    }
  }

  private cargarDatosDisponibles(): void {
    this.isLoading = true;

    this.inquilinoService.listarDisponibles()
      .pipe(
        tap(inquilinosResponse => {
          if (inquilinosResponse.success) {
            this.inquilinos = inquilinosResponse.data || [];
          }
        }),
        switchMap(() => {
          return this.unidadService.listarDisponibles(this.casaActual.id);
        }),
        tap(unidadesResponse => {
          if (unidadesResponse.success) {
            this.unidades = unidadesResponse.data || [];
          }
        }),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error('Ocurrió un error al cargar los datos.', error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: () => {
          this.iniciarFormulario();
        },
        error: (error) => {
          this.notificationService.setMessageChange(
            Message.error('Error en la carga de datos.', error)
          );
        }
      });
  }

}
