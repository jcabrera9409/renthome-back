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

  getCasaSeleccionada(): Observable<CasaDTO | null> {
    return this.casaSeleccionada.asObservable();
  }

  setCasaSeleccionada(casa: CasaDTO | null) {
    this.saveCasaStorage(casa);
    this.casaSeleccionada.next(casa);
  }

  getCasas(): Observable<CasaDTO[]> {
    return this.casas.asObservable();
  }

  setCasas(casas: CasaDTO[]) {
    if (casas.length == 0) {
      this.casas.next(casas);
      this.casaSeleccionada.next(null);
      return;
    }

    const casaStorage = this.getCasaStorage();
    if (casaStorage) {
      const index = casas.findIndex(c => c.id === casaStorage.id);

      if (index !== -1) {
        this.setCasaSeleccionada(casas[index]);
      } else {
        this.setCasaSeleccionada(casas[0]);
      }
    } else {
      this.setCasaSeleccionada(casas[0]);
    }

    this.casas.next(casas);
  }

  saveCasaStorage(casa: CasaDTO | null) {
    if (casa) {
      sessionStorage.setItem('casaSeleccionada', JSON.stringify(casa));
    } else {
      sessionStorage.removeItem('casaSeleccionada');
    }
  }

  getCasaStorage(): CasaDTO | null {
    const casa = sessionStorage.getItem('casaSeleccionada');
    return casa ? JSON.parse(casa) : null;
  }
}
