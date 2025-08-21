import { Injectable } from '@angular/core';
import { GenericService } from './generic.service';
import { UnidadHabitacional } from '../_model/unidad-habitacional';
import { EnvService } from './env.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APIResponseDTO } from '../_model/dto';

@Injectable({
  providedIn: 'root'
})
export class UnidadHabitacionalService extends GenericService<UnidadHabitacional> {

  constructor(
    protected override http: HttpClient,
    protected envService: EnvService
  ) { 
    super(
      http,
      `${envService.getApiUrl}/unidades`
    );
  }

  /**
   * Lista las unidades habitacionales disponibles de una casa específica
   * @param casaId ID de la casa
   * @returns Observable con las unidades disponibles
   */
  listarDisponibles(casaId: number): Observable<APIResponseDTO<UnidadHabitacional[]>> {
    return this.http.get<APIResponseDTO<UnidadHabitacional[]>>(`${this.url}/casa/${casaId}/disponibles`);
  }
}
