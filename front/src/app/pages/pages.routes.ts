import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TarifaComponent } from './tarifa/tarifa.component';
import { UnidadHabitacionalComponent } from './unidad-habitacional/unidad-habitacional.component';
import { ContratoComponent } from './contrato/contrato.component';
import { InquilinoComponent } from './inquilino/inquilino.component';
import { ReciboComponent } from './recibo/recibo.component';
import { AuthGuard } from '../_service/guard.service';


export const pagesRoutes: Routes = [
    { path: '', component: DashboardComponent, canActivate: [AuthGuard] },
    { path: 'tarifa', component: TarifaComponent, canActivate: [AuthGuard] },
    { path: 'unidad', component: UnidadHabitacionalComponent, canActivate: [AuthGuard] },
    { path: 'contrato', component: ContratoComponent, canActivate: [AuthGuard] },
    { path: 'inquilino', component: InquilinoComponent, canActivate: [AuthGuard] },
    { path: 'recibo', component: ReciboComponent, canActivate: [AuthGuard] }
];