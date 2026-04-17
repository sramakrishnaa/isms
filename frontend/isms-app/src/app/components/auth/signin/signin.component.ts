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
    // Mark all fields as touched to show validation errors
    if (this.signinForm.invalid) {
      this.markFormGroupTouched(this.signinForm);
      return;
    }
 
    this.isLoading = true;
    this.signinForm.disable();
    
    // Hide password during submission for security
    this.hidePassword.set(true);
 
    // Simulate API call - replace with actual authentication service
    setTimeout(() => {
      const credentials = this.signinForm.value;
      
      // TODO: Replace with actual authentication logic
      // Example: this.authService.login(credentials.email, credentials.password, credentials.rememberMe)
      
      this.isLoading = false;
      this.snackbarService.success('Sign in successful! Redirecting...');
      
      // Navigate to dashboard or home page
      setTimeout(() => this.router.navigate(['/dashboard']), 1000);
    }, 2000);
  }
 
  /**
   * Recursively marks all controls in a form group as touched
   * This triggers validation messages to display
   */
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
