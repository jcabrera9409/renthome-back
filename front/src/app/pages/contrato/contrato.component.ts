import { Component, OnInit } from '@angular/core';
import { LucideAngularModule, PlusIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ContratoEdicionDialogComponent } from '../../modals/contrato-edicion-dialog/contrato-edicion-dialog.component';
import { Contrato } from '../../_model/contrato';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CasaService } from '../../_service/casa.service';
import { ContratoService } from '../../_service/contrato.service';
import { NotificationService } from '../../_service/notification.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { Message } from '../../_model/message';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';
import { ChangeStatusRequest } from '../../_model/dto';
import { catchError } from 'rxjs/internal/operators/catchError';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-contrato',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule, ReactiveFormsModule, CommonModule, LoaderComponent, PaginationComponent],
  templateUrl: './contrato.component.html',
  styleUrl: './contrato.component.css'
})
export class ContratoComponent implements OnInit {
  readonly plusIcon = PlusIcon;

  filtro = new FormControl('');

  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 1;

  isLoading = false;
  contratos: Contrato[] = [];

  constructor(
    private casaService: CasaService,
    private contratoService: ContratoService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getAllContratos();

    this.contratoService.getObjetoPageableCambio().subscribe({
      next: (response) => {
        if (!response) {
          this.contratos = [];
          this.totalElements = 0;
          this.totalPages = 1;
          return;
        }
        this.contratos = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.pageSize = response.size;
      }
    });

    this.casaService.getCasaSeleccionada().subscribe({
      next: () => {
        this.getAllContratos();
      }
    });
  }

  openDialog(contrato?: Contrato) {
    this.dialog.open(ContratoEdicionDialogComponent, {
      data: contrato,
      panelClass: 'w-full'
    });
  }

  openDialogConfirm(contrato: Contrato) {
      const estado = contrato.activo ? 'inactivar' : 'activar';
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        data: {
          title: `${estado.charAt(0).toUpperCase() + estado.slice(1)} Contrato`,
          message: `¿Estás seguro de que deseas ${estado} este contrato?`,
          confirmText: 'Confirmar',
          cancelText: 'Cancelar'
        }
      });
  
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.inactivateContrato(contrato);
        }
      });
    }

  onPageChange(page: number) {
    this.currentPage = page;
    this.getAllContratos();
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    this.currentPage = 0;
    this.getAllContratos();
  }

  applyFilter(event: Event) {
    if(event["keyCode"] === 13) {
      this.currentPage = 0;
      this.getAllContratos();
    }
  }

  private inactivateContrato(contrato: Contrato): void {
    this.isLoading = true;
    const casaActual = this.casaService.getCasaStorage();
    const changeStatusRequest: ChangeStatusRequest = {
      id: contrato.id,
      estado: !contrato.activo,
      estadoString: 'Inactivo'
    };
    this.contratoService.cambiarEstado(changeStatusRequest)
      .pipe(
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrio un error al procesar la información.", error)
          );
          return EMPTY;
        }),
        switchMap(() => this.contratoService.listarPaginado(casaActual.id, this.filtro.value, this.currentPage, this.pageSize)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrio un error al listar los contratos.", error)
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
            Message.success("Contrato inactivado correctamente")
          );
        } else {
          this.notificationService.setMessageChange(
            Message.error("Error al inactivar el contrato", response)
          );
        }
      });
  }

  private getAllContratos(): void {
    const casaSeleccionada = this.casaService.getCasaStorage();
    if (!casaSeleccionada) {
      this.contratoService.setObjetoPageableCambio(null);
      return;
    }
    this.isLoading = true;
    this.contratoService.listarPaginado(casaSeleccionada.id, this.filtro.value, this.currentPage, this.pageSize)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if(response.success) {
          this.contratoService.setObjetoPageableCambio(response.data);
        } else {
          this.contratoService.setObjetoPageableCambio(null);
          this.notificationService.setMessageChange(
            Message.error("Error al cargar los contratos", response)
          );
        }
      });
  }
}
