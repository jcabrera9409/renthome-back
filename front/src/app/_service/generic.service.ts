import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { APIResponseDTO, ChangeStatusRequest, PageableResponseDTO } from '../_model/dto';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GenericService<T> {

  private objetoCambio: Subject<T[]> = new Subject<T[]>();
  private mensajeCambio: Subject<string> = new Subject<string>();

  constructor(
    protected http: HttpClient,
    protected url: String
  ) { }

  listar() {
    return this.http.get<APIResponseDTO<T[]>>(`${this.url}`);
  }

  listarPaginado(filtro: string, page: number, size: number) {
    return this.http.get<APIResponseDTO<PageableResponseDTO<T>>>(`${this.url}/filtrar?filtro=${filtro}&page=${page}&size=${size}`);
  }

  listarPorId(id: number) {
    return this.http.get<APIResponseDTO<T>>(`${this.url}/${id}`);
  }

  registrar(t: T) {
    return this.http.post<APIResponseDTO<T>>(`${this.url}`, t);
  }

  modificar(t: T) {
    return this.http.put<APIResponseDTO<T>>(`${this.url}`, t);
  }

  cambiarEstado(changeStatusRequest: ChangeStatusRequest) {
    return this.http.patch<APIResponseDTO<void>>(`${this.url}/cambiar-estado`, changeStatusRequest);
  }

  public getObjetoCambio(): Subject<T[]> {
    return this.objetoCambio;
  }

  public getMensajeCambio(): Subject<string> {
    return this.mensajeCambio;
  }

  public setObjetoCambio(objetos: T[]) {
    this.objetoCambio.next(objetos);
  }

  public setMensajeCambio(mensaje: string) {
    this.mensajeCambio.next(mensaje);
  }
}
