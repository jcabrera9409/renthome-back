import { CasaDTO } from "./dto";

export class UnidadHabitacional {
  id: number;
  nombre: string;
  descripcion: string;
  incluyeAgua: boolean;
  incluyeLuz: boolean;
  tipoUnidad: string;
  estado: string;
  casa: CasaDTO;
}
