import { Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { Inquilino } from '../_model/inquilino';
import { EnvService } from './env.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APIResponseDTO } from '../_model/dto';

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
}
