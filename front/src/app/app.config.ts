import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { JwtModule } from '@auth0/angular-jwt';
import { EnvService } from './_service/env.service';

const envService = new EnvService();  

export function jwtTokenGetter(): string {
  return sessionStorage.getItem('access_token') || '';
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), 
    provideAnimationsAsync(),
    provideHttpClient(withInterceptorsFromDi()),
    importProvidersFrom(
      JwtModule.forRoot({
        config: {
          tokenGetter: jwtTokenGetter,
          allowedDomains: envService.getDomains,
          disallowedRoutes: [
            `${envService.getApiUrl}/v1/auth/login`,
            `${envService.getApiUrl}/v1/usuarios`
          ]
        }
      })
    )
  ]
};
