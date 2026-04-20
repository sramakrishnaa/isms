import { Component, signal } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SnackbarService } from '../../../services/snackbar.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css'
})
export class SigninComponent {
  signinForm: FormGroup;
  hidePassword = signal(true);
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private snackbarService: SnackbarService,
    private router: Router
  ) {
    this.signinForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      password: ['', [Validators.required]],
      rememberMe: [false]
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.signinForm.controls;
  }

  togglePassword(event: MouseEvent): void {
    this.hidePassword.set(!this.hidePassword());
    event.stopPropagation();
  }

  onSubmit(): void {
    if (this.signinForm.invalid) {
      this.markFormGroupTouched(this.signinForm);
      return;
    }

    this.isLoading = true;
    this.signinForm.disable();
    this.hidePassword.set(true);
    setTimeout(() => {
      const credentials = this.signinForm.value;
      this.isLoading = false;
      this.snackbarService.success('Sign in successful! Redirecting...');
      setTimeout(() => this.router.navigate(['/dashboard']), 1000);
    }, 3000);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  showSb(){

    this.snackbarService.info('Sign in successful! Redirecting...');
  }
}
