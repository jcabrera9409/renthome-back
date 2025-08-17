import { Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { Tarifa } from '../_model/tarifa';
import { EnvService } from './env.service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TarifaService extends GenericService<Tarifa> {

  constructor(
    protected override http: HttpClient,
    protected envService: EnvService
  ) { 
    super(
      http,
      `${envService.getApiUrl}/tarifas`
    );
  }
}
