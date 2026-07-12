import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, from, Observable, switchMap, throwError } from 'rxjs';
import { KeycloakService } from '../services/keycloak.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private keycloakService: KeycloakService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    return from(this.keycloakService.refreshToken()).pipe(
      switchMap(() => {
        const token = this.keycloakService.token;
        let authReq = req;
        if (token) {
          authReq = req.clone({
            setHeaders: { Authorization: `Bearer ${token}` },
          });
        }
        return next.handle(authReq);
      }),
      catchError((error) => {
        if (error.status === 401) this.keycloakService.logout();
        return throwError(() => error);
      }),
    );
  }
}
