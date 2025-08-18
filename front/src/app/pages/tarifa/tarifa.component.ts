import { Component, OnInit } from '@angular/core';
import { LucideAngularModule, PlusIcon } from 'lucide-angular';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TarifaEdicionDialogComponent } from '../../modals/tarifa-edicion-dialog/tarifa-edicion-dialog.component';
import { TarifaService } from '../../_service/tarifa.service';
import { Tarifa } from '../../_model/tarifa';
import { NotificationService } from '../../_service/notification.service';
import { finalize } from 'rxjs';
import { Message } from '../../_model/message';
import { CasaService } from '../../_service/casa.service';
import { LoaderComponent } from '../../shared/loader/loader.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tarifa',
  standalone: true,
  imports: [LucideAngularModule, MatDialogModule, LoaderComponent, CommonModule],
  templateUrl: './tarifa.component.html',
  styleUrl: './tarifa.component.css'
})
export class TarifaComponent implements OnInit {
  readonly plusIcon = PlusIcon;

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

    this.tarifaService.getObjetoCambio().subscribe({
      next: (tarifas) => {
        this.tarifas = tarifas;
      }
    });

    this.casaService.getCasaSeleccionada().subscribe({
      next: () => {
        this.getAllTarifas();
      }
    });
  }

  openDialog(tarifa?: Tarifa) {
    const dialogRef = this.dialog.open(TarifaEdicionDialogComponent, {
      data: tarifa,
      panelClass: 'w-full'
    });
  }

  private getAllTarifas() {
    const casaSeleccionada = this.casaService.getCasaStorage();
    if (!casaSeleccionada) {
      this.tarifaService.setObjetoCambio([]);
      return;
    }
    this.isLoading = true;
    this.tarifaService.listar(casaSeleccionada.id)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((response) => {
        if(response.success) {
          this.tarifaService.setObjetoCambio(response.data);
        } else {
          this.tarifaService.setObjetoCambio([]);
          this.notificationService.setMessageChange(
            Message.error("Error al cargar las tarifas", response)
          );
        }
      })
  }
} 
