import { Component, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { SnackbarService } from '../../../services/snackbar/snackbar.service';
import { Router } from '@angular/router';
import { Login } from '../../../models/login';
import { AuthService } from '../../../services/auth.service';
import { finalize, take } from 'rxjs';
import { LoginResponse } from '../../../models/login-response';
import { HttpErrorResponse } from '@angular/common/http';
import { StatusMessage } from '../../../models/status-message';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css',
  standalone: false,
})
export class SigninComponent implements OnInit {
  signinForm!: FormGroup<{
    email: FormControl<string>;
    password: FormControl<string>;
  }>;

  hidePassword = signal<boolean>(true);
  isLoading = signal<boolean>(false);

  signinStatus = signal<StatusMessage | null>(null);
  isLoginFailed = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private snackbarService: SnackbarService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.resetFormState();
  }

  get f(): { [key: string]: AbstractControl } {
    return this.signinForm.controls;
  }

  togglePassword(event: MouseEvent): void {
    event?.preventDefault();
    event?.stopPropagation();
    this.hidePassword.update((value) => !value);
  }

  private initForm(): void {
    this.signinForm = this.fb.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit(): void {
    if (this.signinForm.invalid || this.isLoading()) {
      this.signinForm.markAllAsTouched();
      return;
    }
    this.hidePassword.set(true);
    this.signinForm.disable();
    this.isLoading.set(true);
    this.performSignin();
  }

  private performSignin(): void {
    const formValue = this.signinForm.getRawValue();

    const credentials: Login = {
      email: formValue.email,
      password: formValue.password,
    };

    this.authService
      .signIn(credentials)
      .pipe(
        finalize(() => {
          this.isLoading.set(false);
          this.signinForm.enable();
        }),
        take(1),
      )
      .subscribe({
        next: (response) => {
          this.handleLoginSuccess(response);
        },
        error: (error) => {
          this.handleLoginError(error);
        },
      });
  }

  private handleLoginSuccess(response: LoginResponse): void {
    localStorage.setItem('access_token', response.data.accessToken);
    localStorage.setItem('refresh_token', response.data.refreshToken);
    localStorage.setItem('user', JSON.stringify(response.data.user));
    this.snackbarService.success(`Welcome ${response.data.user.firstName}`);
    this.resetFormState();
    this.router.navigate(['/dashboard']);
  }

  private handleLoginError(error: HttpErrorResponse): void {
    let message = 'Login failed. Try again';
    if (error.status === 401) {
      message = 'Invalid email or password';
    } else if (error.status === 0) {
      message = 'Unable to connect to server';
    }
    this.signinStatus.set({ message, type: 'error' });
    this.isLoginFailed.set(true);
  }

 resetFormState(): void {
    this.clearError();
    this.signinForm.reset({ email: '', password: '' }, { emitEvent: false });
    this.signinForm.markAsPristine();
    this.signinForm.markAsUntouched();
  }

  clearError(): void {
    this.signinStatus.set(null);
    this.isLoginFailed.set(false);
  }

  routeToSignup(): any {
    this.router.navigate(['/signup'])
  }
}
