import { Component, OnInit } from '@angular/core';
import { IdCardIcon, LucideAngularModule, MailIcon, PhoneIcon, PlusIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { InquilinoEdicionDialogComponent } from '../../modals/inquilino-edicion-dialog/inquilino-edicion-dialog.component';
import { FormControl } from '@angular/forms';
import { Inquilino } from '../../_model/inquilino';
import { InquilinoService } from '../../_service/inquilino.service';
import { NotificationService } from '../../_service/notification.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { Message } from '../../_model/message';
import { ChangeStatusRequest } from '../../_model/dto';
import { catchError } from 'rxjs/internal/operators/catchError';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { switchMap } from 'rxjs/internal/operators/switchMap';
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PaginationComponent } from '../../shared/pagination/pagination.component';

@Component({
  selector: 'app-inquilino',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule, LoaderComponent, CommonModule, ReactiveFormsModule, PaginationComponent],
  templateUrl: './inquilino.component.html',
  styleUrl: './inquilino.component.css'
})
export class InquilinoComponent implements OnInit {
  readonly plusIcon = PlusIcon;
  readonly idCardIcon = IdCardIcon;
  readonly phoneIcon = PhoneIcon;
  readonly mailIcon = MailIcon;

  filtro = new FormControl('');
  
  currentPage = 0;
  pageSize = 9;
  totalElements = 0;
  totalPages = 1;

  isLoading = false;
  inquilinos: Inquilino[] = [];

  constructor(
    private inquilinoService: InquilinoService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getAllInquilinos();

    this.inquilinoService.getObjetoPageableCambio().subscribe({
      next: (response) => {
        if (!response) {
          this.inquilinos = [];
          this.totalElements = 0;
          this.totalPages = 1;
          return;
        }
        this.inquilinos = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.pageSize = response.size;
      }
    });
  }

  openDialog(inquilino?: Inquilino): void {
    this.dialog.open(InquilinoEdicionDialogComponent, {
      data: inquilino,
      panelClass: 'w-full'
    });
  }

  openDialogConfirm(inquilino: Inquilino) {
    const estado = inquilino.activo ? 'inactivar' : 'activar';
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: `${estado.charAt(0).toUpperCase() + estado.slice(1)} Inquilino`,
        message: `¿Estás seguro de que deseas ${estado} este inquilino?`,
        confirmText: 'Confirmar',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cambiarEstadoInquilino(inquilino);
      }
    });
  }

  cambiarEstadoInquilino(inquilino: Inquilino) {
    this.isLoading = true;
    const changeStatusRequest: ChangeStatusRequest = {
      id: inquilino.id,
      estado: !inquilino.activo,
      estadoString: ""
    }

    this.inquilinoService.cambiarEstado(changeStatusRequest)
      .pipe(
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrió un error al procesar la información.", error)
          );
          return EMPTY;
        }),
        switchMap(() => this.inquilinoService.listarPaginadoInquilinos(this.filtro.value, this.currentPage, this.pageSize)),
        catchError(error => {
          this.notificationService.setMessageChange(
            Message.error("Ocurrió un error al listar los inquilinos", error)
          );
          return EMPTY;
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if(response.success) {
          this.inquilinoService.setObjetoPageableCambio(response.data);
          this.notificationService.setMessageChange(
            Message.success("Estado del inquilino actualizado correctamente")
          );
        } else {
          this.notificationService.setMessageChange(
            Message.error("Ocurrió un error al actualizar el estado del inquilino", response)
          );
        }
      })
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.getAllInquilinos();
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    this.currentPage = 0;
    this.getAllInquilinos();
  }

  applyFilter(event: Event) {
    if (event["keyCode"] === 13) {
      this.currentPage = 0;
      this.getAllInquilinos();
    }
  }

  private getAllInquilinos() {
    this.isLoading = true;
    this.inquilinoService.listarPaginadoInquilinos(this.filtro.value, this.currentPage, this.pageSize)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if (response.success) {
          this.inquilinoService.setObjetoPageableCambio(response.data);
        } else {
          this.inquilinoService.setObjetoPageableCambio(null);
          this.notificationService.setMessageChange(
            Message.error("Error al cargar los inquilinos", response)
          );
        }
      })
  }
}
