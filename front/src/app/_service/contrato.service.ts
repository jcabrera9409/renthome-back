import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { Contrato } from '../_model/contrato';
import { EnvService } from './env.service';

@Injectable({
  providedIn: 'root'
})
export class ContratoService extends GenericService<Contrato> {

  constructor(
    protected override http: HttpClient,
    protected envService: EnvService
  ) {
    super(
      http,
      `${envService.getApiUrl}/contratos`
    );
   }
}
