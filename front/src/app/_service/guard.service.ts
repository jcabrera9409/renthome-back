import { inject } from "@angular/core";
import { Observable } from "rxjs";
import { AuthService } from "./auth.service";
import { UtilMethods } from "../util/util";

export const AuthGuard = (): Observable<boolean> | boolean => {
    const authService = inject(AuthService);
    const utilMethods = inject(UtilMethods);

    if (!authService.isLogged()) {
        authService.logout();
        return false;
    }
    else {
        if (!utilMethods.isTokenExpired()) {
            return true;
        } else {
            authService.logout();
            return false;
        }
    }

}
