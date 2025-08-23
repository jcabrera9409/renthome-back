import { Inquilino } from "./inquilino";
import { UnidadHabitacional } from "./unidad-habitacional";

export class Contrato {
    id: number;
    fechaInicio: string;
    fechaFin: string;
    montoRentaMensual: number;
    garantia: number;
    activo: boolean;
    inquilino: Inquilino;
    unidad: UnidadHabitacional;
}