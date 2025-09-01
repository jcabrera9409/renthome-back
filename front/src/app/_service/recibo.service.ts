import { Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { EnvService } from './env.service';
import { HttpClient } from '@angular/common/http';
import { Recibo } from '../_model/recibo';
import { APIResponseDTO, PageableResponseDTO } from '../_model/dto';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReciboService extends GenericService<Recibo> {

  private periodoCambio: Subject<string> = new Subject<string>();

  constructor(
      protected override http: HttpClient,
      protected envService: EnvService
    ) {
      super(
        http,
        `${envService.getApiUrl}/recibos`
      );
     }

  generarRecibos(casaId: number, year: number, month: number): Observable<APIResponseDTO<Recibo[]>> {
    return this.http.get<APIResponseDTO<Recibo[]>>(`${this.url}/casa/${casaId}/generar/${year}/${month}`);
  }

  obtenerPeriodos(casaId: number): Observable<APIResponseDTO<string[]>> {
    return this.http.get<APIResponseDTO<string[]>>(`${this.url}/casa/${casaId}/periodos`);
  }

  listarPaginadoPeriodo(casaId: number, year: number, month: number, filtro: string, page: number, size: number): Observable<APIResponseDTO<PageableResponseDTO<Recibo>>> {
    return this.http.get<APIResponseDTO<PageableResponseDTO<Recibo>>>(
      `${this.url}/casa/${casaId}/filtrar/${year}/${month}?filtro=${filtro}&page=${page}&size=${size}`
    );
  }

  public getPeriodoCambio(): Subject<string> {
    return this.periodoCambio;
  }

  public setPeriodoCambio(periodo: string): void {
    this.periodoCambio.next(periodo);
  }
}
