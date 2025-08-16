import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TarifaComponent } from './tarifa/tarifa.component';
import { UnidadHabitacionalComponent } from './unidad-habitacional/unidad-habitacional.component';
import { ContratoComponent } from './contrato/contrato.component';


export const pagesRoutes: Routes = [
    { path: '', component: DashboardComponent },
    { path: 'tarifa', component: TarifaComponent },
    { path: 'unidad', component: UnidadHabitacionalComponent },
    { path: 'contrato', component: ContratoComponent }
];