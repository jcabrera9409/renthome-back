import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class EnvService {

  private production: boolean;
  private apiUrl: string;
  private tokenName: string;
  private domains: string[];

  constructor() { 
    const env = (window as any).__env || {};

    this.production = env.production || false;
    this.apiUrl = env.apiUrl || 'http://localhost:8080';
    this.tokenName = env.token_name || 'access_token';
    this.domains = env.domains || ['localhost:8080'];
  }

  get isProduction(): boolean {
    return this.production;
  }

  get getApiUrl(): string {
    return this.apiUrl;
  }

  get getTokenName(): string {
    return this.tokenName;
  }

  get getDomains(): string[] {
    return this.domains;
  }
}
