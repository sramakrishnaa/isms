import { Component, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { CustomValidators } from '../../../validators/custom-validators';
import { SnackbarService } from '../../../services/snackbar/snackbar.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { Register } from '../../../models/register';
import { finalize, single, take } from 'rxjs';
import { RegistrationResponse } from '../../../models/registration-response';
import { HttpErrorResponse } from '@angular/common/http';
import { StatusMessage } from '../../../models/status-message';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
  standalone: false,
})
export class SignupComponent implements OnInit {
  signupForm!: FormGroup<{
    firstName: FormControl<string>;
    lastName: FormControl<string>;
    email: FormControl<string>;
    phoneNumber: FormControl<string>;
    password: FormControl<string>;
    confirmPassword: FormControl<string>;
  }>;

  hidePassword = signal(true);
  hideConfirmPassword = signal<boolean>(true);
  isLoading = signal<boolean>(false);
  signupStatus = signal<StatusMessage | null>(null);
  isSignupFailed = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private snackbarService: SnackbarService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initSignupForm();
    this.resetFormState();
  }

  get f(): { [key: string]: AbstractControl } {
    return this.signupForm.controls;
  }

  private initSignupForm(): void {
    this.signupForm = this.fb.nonNullable.group(
      {
        firstName: [
          '',
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(100),
          ],
        ],
        lastName: [
          '',
          [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(100),
          ],
        ],
        email: [
          '',
          [Validators.required, Validators.email, Validators.maxLength(255)],
        ],
        phoneNumber: [
          '',
          [Validators.required, CustomValidators.phoneNumber()],
        ],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(8),
            CustomValidators.passwordPattern(),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: CustomValidators.passwordMatch() },
    );
  }

  togglePassword(event: MouseEvent) {
    this.hidePassword.set(!this.hidePassword());
    event.stopPropagation();
  }

  toggleConfirmPassword(event: MouseEvent) {
    this.hideConfirmPassword.set(!this.hideConfirmPassword());
    event.stopPropagation();
  }

  onSubmit(): void {
    if (this.signupForm.invalid || this.isLoading()) {
      this.signupForm.markAllAsTouched();
      return;
    }

    this.hidePassword.set(true);
    this.hideConfirmPassword.set(true);
    this.isLoading.set(true);
    this.signupForm.disable();
    this.performSignup();
  }

  private performSignup(): void {
    const formValue = this.signupForm.getRawValue();

    const signUpDetails: Register = {
      email: formValue.email,
      password: formValue.password,
      phoneNumber: formValue.phoneNumber,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
    };

    this.authService
      .signUp(signUpDetails)
      .pipe(
        finalize(() => {
          this.isLoading.set(false);
          this.signupForm.enable();
        }),
        take(1),
      )
      .subscribe({
        next: (response) => {
          this.handleSignupSuccess(response);
        },
        error: (error) => {
          this.handleSignupError(error);
        },
      });
  }

  private handleSignupSuccess(response: RegistrationResponse): void {
    this.snackbarService.success('Account created successfully.');
    setTimeout(() => {
      this.resetFormState();
      this.router.navigate(['/login']);
    }, 1500);
  }

  private handleSignupError(error: HttpErrorResponse): void {
    let message = 'Failed to create account. Try again';

    if (error.status === 409) {
      message = 'Account already exists with this email';
    } else if (error.status === 0) {
      message = 'Unable to connect to server';
    }
    this.isSignupFailed.set(true);
    this.signupStatus.set({ message, type: 'error' });
  }
  private resetFormState(): void {
    this.signupForm.reset(
      {
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        password: '',
        confirmPassword: '',
      },
      { emitEvent: false },
    );

    this.signupForm.markAsPristine();
    this.signupForm.markAsUntouched();
  }

  clearError(): void {
    this.signupStatus.set(null);
    this.isSignupFailed.set(false);
  }
}
