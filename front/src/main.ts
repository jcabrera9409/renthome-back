import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { EnvService } from './app/_service/env.service';
import { enableProdMode } from '@angular/core';

const envService = new EnvService(); 

if (envService.isProduction) {
  enableProdMode();
}

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
