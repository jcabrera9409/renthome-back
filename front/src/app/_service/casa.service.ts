import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EnvService } from './env.service';
import { APIResponseDTO, CasaDTO } from '../_model/dto';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CasaService {

  private casaSeleccionada: Subject<CasaDTO> = new Subject<CasaDTO>();
  private casas: Subject<CasaDTO[]> = new Subject<CasaDTO[]>();

  private url: string = `${this.envService.getApiUrl}/casas`;
  
  constructor(
    private http: HttpClient,
    private envService: EnvService
  ) { }

  listarMisCasas() {
    return this.http.get<APIResponseDTO<CasaDTO[]>>(`${this.url}/mis-casas`);
  }

  getCasaSeleccionada(): CasaDTO | null {
    return JSON.parse(sessionStorage.getItem('casaSeleccionada') || 'null');
  }

  setCasaSeleccionada(casa: CasaDTO) {
    if (casa) {
      sessionStorage.setItem('casaSeleccionada', JSON.stringify(casa));
    } else {
      sessionStorage.removeItem('casaSeleccionada');
    }
    this.casaSeleccionada.next(casa);
  }

  getCasas(): Observable<CasaDTO[]> {
    return this.casas.asObservable();
  }

  setCasas(casas: CasaDTO[]) {
    if (casas.length == 0) {
      this.setCasaSeleccionada(null);
      this.casas.next(casas);
      return;
    }
    
    const casaActual: CasaDTO = this.getCasaSeleccionada();
    if(casaActual) {
      const index = casas.findIndex(c => c.id === casaActual.id);
      if(index !== -1) {
        this.setCasaSeleccionada(casas[index]);
      }
    }
    this.setCasaSeleccionada(casas[0]);
    this.casas.next(casas);
  }

}
