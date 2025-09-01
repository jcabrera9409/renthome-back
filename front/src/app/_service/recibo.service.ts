import { Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { EnvService } from './env.service';
import { HttpClient } from '@angular/common/http';
import { Recibo } from '../_model/recibo';
import { APIResponseDTO } from '../_model/dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReciboService extends GenericService<Recibo> {

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
}
