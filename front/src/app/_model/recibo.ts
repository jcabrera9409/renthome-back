import { Contrato } from "./contrato";

export class Recibo {
    id: number;
    periodo: string;
    montoTotal: number;
    pagado: boolean;
    contrato: Contrato
    detalle: DetalleRecibo[] = [];
}

export class DetalleRecibo {
    id: number;
    descripcion: string;
    monto: number;
}
