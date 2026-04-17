import { Component, signal } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CustomValidators } from '../../../validators/custom-validators';
import { SnackbarService } from '../../../services/snackbar.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {

  signupForm: FormGroup;
  hidePassword = signal(true);
  hideConfirmPassword = signal(true);
  isLoading = false;

  constructor(private fb: FormBuilder,
    private snackbarService: SnackbarService,
    private router: Router
  ) {
    this.signupForm = this.fb.group(
      {
        firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
        phoneNumber: ['', [Validators.required, CustomValidators.phoneNumber()]],
        password: ['', [Validators.required, Validators.minLength(8), CustomValidators.passwordPattern()]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: CustomValidators.passwordMatch() }
    );
  }

  get f(): { [key: string]: AbstractControl } {
    return this.signupForm.controls;
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

    if (this.signupForm.invalid) {
      this.markFormGroupTouched(this.signupForm);
      return;
    }

    this.isLoading = true;
    this.signupForm.disable();
    this.hidePassword.set(true);
    this.hideConfirmPassword.set(true);

    setTimeout(() => {
      this.isLoading = false;
      this.snackbarService.success('Account created successfully! Redirecting to login...');
      this.signupForm.reset();
      setTimeout(() => this.router.navigate(['/login']), 2000);
    }, 2000);
  }


  private markFormGroupTouched(fg: FormGroup): void {
    Object.keys(fg.controls).forEach(k => {
      const control = fg.get(k);
      control?.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}
