import { Component, OnInit } from '@angular/core';
import { LucideAngularModule, ReceiptIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReciboEdicionDialogComponent } from '../../modals/recibo-edicion-dialog/recibo-edicion-dialog.component';
import { ReciboVerDialogComponent } from '../../modals/recibo-ver-dialog/recibo-ver-dialog.component';
import { ReciboPeriodoDialogComponent } from '../../modals/recibo-periodo-dialog/recibo-periodo-dialog.component';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { Recibo } from '../../_model/recibo';
import { CasaService } from '../../_service/casa.service';
import { NotificationService } from '../../_service/notification.service';
import { ReciboService } from '../../_service/recibo.service';
import { finalize } from 'rxjs';
import { Message } from '../../_model/message';

@Component({
  selector: 'app-recibo',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule, CommonModule, ReactiveFormsModule, LoaderComponent, PaginationComponent],
  templateUrl: './recibo.component.html',
  styleUrl: './recibo.component.css'
})
export class ReciboComponent implements OnInit {

  readonly receiptIcon = ReceiptIcon;

  filtro = new FormControl('');

  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 1;

  isLoading = false;
  recibos: Recibo[] = [];

  constructor(
    private casaService: CasaService,
    private reciboService: ReciboService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getAllRecibos();

    this.reciboService.getObjetoPageableCambio().subscribe({
      next: (response) => {
        if (!response) {
          this.recibos = [];
          this.totalElements = 0;
          this.totalPages = 1;
          return;
        }
        this.recibos = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        this.pageSize = response.size;
      }
    });

    this.casaService.getCasaSeleccionada().subscribe({
      next: () => {
        this.getAllRecibos();
      }
    });
  }

  openEditDialog(recibo?: Recibo) {
    const dialogRef = this.dialog.open(ReciboEdicionDialogComponent, {
      data: recibo,
      panelClass: 'w-full'
    });
  }

  openViewDialog(recibo: Recibo) {
    const dialogRef = this.dialog.open(ReciboVerDialogComponent, {
      data: recibo,
      panelClass: 'w-full'
    });
  }

  openGenerateDialog() {
    const dialogRef = this.dialog.open(ReciboPeriodoDialogComponent, {
      data: null,
    });
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.getAllRecibos();
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    this.currentPage = 0;
    this.getAllRecibos();
  }

  applyFilter(event: Event) {
    if(event["keyCode"] === 13) {
      this.currentPage = 0;
      this.getAllRecibos();
    }
  }

  private getAllRecibos() {
    const casaSeleccionada = this.casaService.getCasaStorage();
    if (!casaSeleccionada) {
      this.reciboService.setObjetoPageableCambio(null);
      return;
    }

    this.isLoading = true;
    this.reciboService.listarPaginado(casaSeleccionada.id, this.filtro.value, this.currentPage, this.pageSize)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if (response.success) {
          this.reciboService.setObjetoPageableCambio(response.data);
        } else {
          this.reciboService.setObjetoPageableCambio(null);
          this.notificationService.setMessageChange(
            Message.error("Error al cargar los recibos", response)
          );
        }
      })
  }

}
