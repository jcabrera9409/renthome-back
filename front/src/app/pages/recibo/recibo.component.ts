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
  periodo = new FormControl('');

  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 1;

  isLoading = false;
  recibos: Recibo[] = [];
  periodos: string[] = [];

  constructor(
    private casaService: CasaService,
    private reciboService: ReciboService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.cargarDatosDisponibles();

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

    this.reciboService.getPeriodoCambio().subscribe({
      next: (data) => {
        if (this.periodos.includes(data)) {
          this.periodo.setValue(data);
          this.getAllRecibos();
        } else {
          this.cargarDatosDisponibles(data);
        }
      }
    });

    this.casaService.getCasaSeleccionada().subscribe({
      next: () => {
        this.cargarDatosDisponibles();
      }
    });
    
    this.periodo.valueChanges.subscribe({
      next: () => {
        this.currentPage = 0;
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
    if (event["keyCode"] === 13) {
      this.currentPage = 0;
      this.getAllRecibos();
    }
  }

  private getAllRecibos() {
    const casaSeleccionada = this.casaService.getCasaStorage();
    const periodoSeleccionado = this.periodo.value;
    const year: number = +periodoSeleccionado.split('-')[0];
    const month: number = +periodoSeleccionado.split('-')[1];
    if (!casaSeleccionada) {
      this.reciboService.setObjetoPageableCambio(null);
      return;
    }

    this.isLoading = true;
    this.reciboService.listarPaginadoPeriodo(casaSeleccionada.id, year, month, this.filtro.value, this.currentPage, this.pageSize)
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

  private cargarDatosDisponibles(dataPeriodo?: string): void {
    const casaId = this.casaService.getCasaStorage().id;
    this.isLoading = true;

    this.reciboService.obtenerPeriodos(casaId)
      .subscribe({
        next: (response) => {
          this.periodos = response.data;
          if (this.periodos.length > 0) {
            if (dataPeriodo && this.periodos.includes(dataPeriodo)) {
              this.periodo.setValue(dataPeriodo);
            } else {
              this.periodo.setValue(this.periodos[0]);
            }
            this.getAllRecibos();
          }
        },
        error: (error) => {
          this.notificationService.setMessageChange(
            Message.error("Error al cargar los periodos", error)
          );
        },
        complete: () => {
          this.isLoading = false;
        }
      });
  }

  convertPeriodoToString(periodo: string): string {
    let data = '';
    const parts = periodo.split('-');
    const year = parts[0];
    const month = parts[1];

    switch (month) {
      case '01':
        data = `Enero ${year}`;
        break;
      case '02':
        data = `Febrero ${year}`;
        break;
      case '03':
        data = `Marzo ${year}`;
        break;
      case '04':
        data = `Abril ${year}`;
        break;
      case '05':
        data = `Mayo ${year}`;
        break;
      case '06':
        data = `Junio ${year}`;
        break;
      case '07':
        data = `Julio ${year}`;
        break;
      case '08':
        data = `Agosto ${year}`;
        break;
      case '09':
        data = `Septiembre ${year}`;
        break;
      case '10':
        data = `Octubre ${year}`;
        break;
      case '11':
        data = `Noviembre ${year}`;
        break;
      case '12':
        data = `Diciembre ${year}`;
        break;
    }

    return data;
  }

}
