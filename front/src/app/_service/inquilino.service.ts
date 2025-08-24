import { Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { Inquilino } from '../_model/inquilino';
import { EnvService } from './env.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APIResponseDTO, PageableResponseDTO } from '../_model/dto';

@Injectable({
  providedIn: 'root'
})
export class InquilinoService extends GenericService<Inquilino> {

  constructor(
    protected override http: HttpClient,
    protected envService: EnvService
  ) {
    super(
      http,
      `${envService.getApiUrl}/inquilinos`
    );
   }

  listarDisponibles(): Observable<APIResponseDTO<Inquilino[]>> {
    return this.http.get<APIResponseDTO<Inquilino[]>>(`${this.url}/disponibles`);
  }

  listarPaginadoInquilinos(filtro: string, page: number, size: number) {
    return this.http.get<APIResponseDTO<PageableResponseDTO<Inquilino>>>(`${this.url}/filtrar?filtro=${filtro}&page=${page}&size=${size}`);
  }
}
