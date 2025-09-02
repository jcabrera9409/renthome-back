import { EnvService } from "../_service/env.service";
import { JwtHelperService } from "@auth0/angular-jwt";

export class UtilMethods {

    private static utilMethods: UtilMethods = undefined;
    private static envService: EnvService = undefined;
    static getInstance(): UtilMethods {
        if (!UtilMethods.utilMethods) {
            UtilMethods.utilMethods = new UtilMethods();
            UtilMethods.envService = new EnvService();
        }
        return UtilMethods.utilMethods;
    }

    convertPeriodoToString(periodo: string): string {
        let data = '';
        const parts = periodo.split('-');
        const year = parts[0];
        const month = parts[1];

        switch (month) {
            case '01':
                data = `Enero ${year}`;
                break;
            case '02':
                data = `Febrero ${year}`;
                break;
            case '03':
                data = `Marzo ${year}`;
                break;
            case '04':
                data = `Abril ${year}`;
                break;
            case '05':
                data = `Mayo ${year}`;
                break;
            case '06':
                data = `Junio ${year}`;
                break;
            case '07':
                data = `Julio ${year}`;
                break;
            case '08':
                data = `Agosto ${year}`;
                break;
            case '09':
                data = `Septiembre ${year}`;
                break;
            case '10':
                data = `Octubre ${year}`;
                break;
            case '11':
                data = `Noviembre ${year}`;
                break;
            case '12':
                data = `Diciembre ${year}`;
                break;
        }

        return data;
    }

    public getJwtToken(): string {
        let token = sessionStorage.getItem(UtilMethods.envService.getTokenName);
        return token;
    }

    public getUsernameFieldJwtToken(): string {
        const username = this.getFieldJwtToken('username');
        return username ? username : '';
    }

    public getFieldJwtToken(field: string): string {
        const decodeToken = this.getDecodedJwtToken();
        if (decodeToken) {
            if (field in decodeToken) {
                return decodeToken[field];
            }
            return "";
        }
        else {
            return null;
        }
    }

    public isTokenExpired(): boolean {
        const helper = this.getHelper();
        let token = this.getJwtToken();

        return helper.isTokenExpired(token);
    }

    private getDecodedJwtToken(): any {
        let token = this.getJwtToken();
        return this.extractJwtPayload(token);
    }

    private extractJwtPayload(token: string): any {
        if (!token) {
            return null;
        }

        const parts = token.split('.');
        if (parts.length !== 3) {
            return null;
        }

        try {
            const helper = this.getHelper();
            const decodedToken = helper.decodeToken(token);

            return decodedToken;
        } catch (e) {
            return null;
        }
    }

    public getHelper(): JwtHelperService {
        return new JwtHelperService();
    }

    public setJwtToken(token: string): void {
        sessionStorage.setItem(UtilMethods.envService.getTokenName, token);
    }

    public removeJwtToken(): void {
        sessionStorage.removeItem(UtilMethods.envService.getTokenName);
    }
}
