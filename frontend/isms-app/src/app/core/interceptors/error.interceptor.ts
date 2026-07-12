import {
  HttpErrorResponse,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { ErrorResponse } from '../models/error-response.model';
import { Injectable } from '@angular/core';
import { SnackbarService } from '../services/snackbar.service';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private snackbarService: SnackbarService,
    private router: Router,
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler) {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        switch (error.status) {
          case 401:
            this.snackbarService.warning('Session expired. Please login again');
            this.router.navigate(['/login']);
            break;

          case 403:
            this.snackbarService.warning(
              'You do not have permission for this action',
            );
            break;

          case 500:
            this.snackbarService.warning('Server error. Please try again later');
            break;

          case 0:
            this.snackbarService.warning('Unable to connect to server');
            break;
        }

        return throwError(() => error.error);
      }),
    );
  }
}
