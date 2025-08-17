import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { JwtModule } from '@auth0/angular-jwt';

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
          allowedDomains: ['localhost:8080'],
          disallowedRoutes: [
            'http://localhost:8080/v1/auth/login',
            'http://localhost:8080/v1/usuarios'
          ]
        }
      })
    )
  ]
};
