import { Component, OnInit } from '@angular/core';
import { ChevronLeftIcon, ChevronRightIcon, LucideAngularModule, PlusIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TarifaEdicionDialogComponent } from '../../modals/tarifa-edicion-dialog/tarifa-edicion-dialog.component';
import { TarifaService } from '../../_service/tarifa.service';
import { Tarifa } from '../../_model/tarifa';
import { NotificationService } from '../../_service/notification.service';
import { catchError, EMPTY, finalize, switchMap } from 'rxjs';
import { Message } from '../../_model/message';
import { CasaService } from '../../_service/casa.service';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { CommonModule } from '@angular/common';
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';
import { ChangeStatusRequest } from '../../_model/dto';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-tarifa',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule, LoaderComponent, CommonModule, PaginationComponent, ReactiveFormsModule],
  templateUrl: './tarifa.component.html',
  styleUrl: './tarifa.component.css'
})
export class TarifaComponent implements OnInit {
  readonly plusIcon = PlusIcon;
  readonly chevronLeftIcon = ChevronLeftIcon;
  readonly chevronRightIcon = ChevronRightIcon;

  filtro = new FormControl('');

  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 1;

  isLoading = false;
  tarifas: Tarifa[];

  constructor(
    private casaService: CasaService,
    private tarifaService: TarifaService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getAllTarifas();

    this.tarifaService.getObjetoPageableCambio().subscribe({
      next: (response) => {
        if (!response) {
          this.tarifas = [];
          this.totalElements = 0;
          this.totalPages = 1;
          return;
        }
        this.tarifas = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.pageSize = response.size;
      }
    });

    this.casaService.getCasaSeleccionada().subscribe({
      next: () => {
        this.getAllTarifas();
      }
    });
  }

  openDialog(tarifa?: Tarifa) {
    this.dialog.open(TarifaEdicionDialogComponent, {
      data: tarifa,
      panelClass: 'w-full'
    });
  }

  openDialogConfirm(tarifa: Tarifa) {
    const estado = tarifa.activo ? 'inactivar' : 'activar';
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: `${estado.charAt(0).toUpperCase() + estado.slice(1)} Tarifa`,
        message: `¿Estás seguro de que deseas ${estado} esta tarifa?`,
        confirmText: 'Confirmar',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.inactivateTarifa(tarifa);
      }
    });
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.getAllTarifas();
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    this.currentPage = 0;
    this.getAllTarifas();
  }

  applyFilter(event: Event) {
    if(event["keyCode"] === 13) {
      this.currentPage = 0;
      this.getAllTarifas();
    }
  }

  private inactivateTarifa(tarifa: Tarifa) {
    this.isLoading = true;
    const changeStatusRequest: ChangeStatusRequest = {
      id: tarifa.id,
      estado: !tarifa.activo,
      estadoString: 'Inactivo'
    };
    this.tarifaService.cambiarEstado(changeStatusRequest)
      .pipe(
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrio un error al procesar la información.", error)
          );
          return EMPTY;
        }),
        switchMap(() => this.tarifaService.listarPaginado(tarifa.casa.id, this.filtro.value, this.currentPage, this.pageSize)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrio un error al listar las tarifas.", error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if (response.success) {
          this.tarifaService.setObjetoPageableCambio(response.data);
          this.notificationService.setMessageChange(
            Message.success("Tarifa inactivada correctamente")
          );
        } else {
          this.notificationService.setMessageChange(
            Message.error("Error al inactivar la tarifa", response)
          );
        }
      });
  }

  private getAllTarifas() {
    const casaSeleccionada = this.casaService.getCasaStorage();
    if (!casaSeleccionada) {
      this.tarifaService.setObjetoPageableCambio(null);
      return;
    }
    this.isLoading = true;
    this.tarifaService.listarPaginado(casaSeleccionada.id, this.filtro.value, this.currentPage, this.pageSize)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if(response.success) {
          this.tarifaService.setObjetoPageableCambio(response.data);
        } else {
          this.tarifaService.setObjetoPageableCambio(null);
          this.notificationService.setMessageChange(
            Message.error("Error al cargar las tarifas", response)
          );
        }
      })
  }
} 
