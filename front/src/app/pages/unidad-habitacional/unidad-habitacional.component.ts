import { Component, OnInit } from '@angular/core';
import { DropletsIcon, HouseIcon, LucideAngularModule, PlusIcon, ZapIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UnidadEdicionDialogComponent } from '../../modals/unidad-edicion-dialog/unidad-edicion-dialog.component';
import { UnidadHabitacional } from '../../_model/unidad-habitacional';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CasaService } from '../../_service/casa.service';
import { UnidadHabitacionalService } from '../../_service/unidad-habitacional.service';
import { NotificationService } from '../../_service/notification.service';
import { catchError, EMPTY, finalize, switchMap } from 'rxjs';
import { Message } from '../../_model/message';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';
import { ChangeStatusRequest } from '../../_model/dto';

@Component({
  selector: 'app-unidad-habitacional',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule, LoaderComponent, CommonModule, ReactiveFormsModule, PaginationComponent],
  templateUrl: './unidad-habitacional.component.html',
  styleUrl: './unidad-habitacional.component.css'
})
export class UnidadHabitacionalComponent implements OnInit {
  readonly plusIcon = PlusIcon;
  readonly dropletsIcon = DropletsIcon;
  readonly zapIcon = ZapIcon;
  readonly houseIcon = HouseIcon;

  filtro = new FormControl('');

  currentPage = 0;
  pageSize = 9;
  totalElements = 0;
  totalPages = 1;

  isLoading = false;
  unidades: UnidadHabitacional[] = [];

  constructor(
    private casaService: CasaService,
    private unidadHabitacionalService: UnidadHabitacionalService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getAllUnidadesHabitacionales();

    this.unidadHabitacionalService.getObjetoPageableCambio().subscribe({
      next: (response) => {
        if (!response) {
          this.unidades = [];
          this.totalElements = 0;
          this.totalPages = 1;
          return;
        }
        this.unidades = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.pageSize = response.size;
      }
    });

    this.casaService.getCasaSeleccionada().subscribe({
      next: () => {
        this.getAllUnidadesHabitacionales();
      }
    });
  }

  openDialog(unidad?: UnidadHabitacional) {
    this.dialog.open(UnidadEdicionDialogComponent, {
      data: unidad,
      panelClass: 'w-full'
    });
  }

  getTextoBotonEstado(unidad: UnidadHabitacional) {
    if (unidad.estado === 'Disponible') {
      return "Mantenimiento";
    } else if (unidad.estado === "Mantenimiento") {
      return "Disponible";
    }
    return "";
  }

  openDialogConfirm(unidad: UnidadHabitacional) {
    const estado = this.getTextoBotonEstado(unidad);
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: `Cambiar el estado de la unidad ${unidad.nombre} a estado ${estado}`,
        message: `¿Estás seguro de que deseas actualizar el estado de la unidad a ${estado}?`,
        confirmText: 'Confirmar',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cambiarEstadoUnidad(unidad, estado);
      }
    });
  }

  cambiarEstadoUnidad(unidad: UnidadHabitacional, estado: string) {
    this.isLoading = true;
    const changeStatusRequest: ChangeStatusRequest = {
      id: unidad.id,
      estado: false,
      estadoString: estado
    }

    this.unidadHabitacionalService.cambiarEstado(changeStatusRequest)
      .pipe(
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrió un error al procesar la información.", error)
          );
          return EMPTY;
        }),
        switchMap(() => this.unidadHabitacionalService.listarPaginado(unidad.casa.id, this.filtro.value, this.currentPage, this.pageSize)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrió un error al listar las unidades habitacionales", error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if(response.success) {
          this.unidadHabitacionalService.setObjetoPageableCambio(response.data);
          this.notificationService.setMessageChange(
            Message.success("Estado de la unidad habitacional actualizado correctamente")
          );
        } else {
          this.notificationService.setMessageChange(
            Message.error("Ocurrió un error al actualizar el estado de la unidad habitacional", response)
          );
        }
      })
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.getAllUnidadesHabitacionales();
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    this.currentPage = 0;
    this.getAllUnidadesHabitacionales();
  }

  applyFilter(event: Event) {
    if (event["keyCode"] === 13) {
      this.currentPage = 0;
      this.getAllUnidadesHabitacionales();
    }
  }

  private getAllUnidadesHabitacionales() {
    const casaSeleccionada = this.casaService.getCasaStorage();
    if (!casaSeleccionada) {
      this.unidadHabitacionalService.setObjetoPageableCambio(null);
      return;
    }

    this.isLoading = true;
    this.unidadHabitacionalService.listarPaginado(casaSeleccionada.id, this.filtro.value, this.currentPage, this.pageSize)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if (response.success) {
          this.unidadHabitacionalService.setObjetoPageableCambio(response.data);
        } else {
          this.unidadHabitacionalService.setObjetoPageableCambio(null);
          this.notificationService.setMessageChange(
            Message.error("Error al cargar las unidades habitacionales", response)
          );
        }
      })
  }
}
