import { CasaDTO } from "./dto";

export class Tarifa {
    id: number;
    tipoServicio: string;
    rangoInicio: number;
    rangoFin: number;
    unidad: string;
    precioUnidad: number;
    activo: boolean;
    casa: CasaDTO
}