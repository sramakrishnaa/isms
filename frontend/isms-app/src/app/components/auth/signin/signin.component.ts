import { Component, signal } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SnackbarService } from '../../../services/snackbar.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-signin',
    templateUrl: './signin.component.html',
    styleUrl: './signin.component.css',
    standalone: false
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
    if (this.signinForm.valid) {
      this.isLoading = true;
      this.signinForm.disable();
      this.hidePassword.set(true);
      setTimeout(() => {
        console.log(this.signinForm.value);
        const credentials = this.signinForm.value;
        this.signinForm.enable();
        this.signinForm.reset();
        this.isLoading = false;
        console.log(this.signinForm.value);
        this.snackbarService.success('Sign in successful! Redirecting...');
        // setTimeout(() => this.router.navigate(['/dashboard']), 1000);
      }, 3000);
    }else{

      this.markFormGroupTouched(this.signinForm);
    }
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
}
