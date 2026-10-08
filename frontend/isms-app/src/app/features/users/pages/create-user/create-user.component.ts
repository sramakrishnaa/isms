import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormGroup,
  FormBuilder,
  Validators,
  AbstractControl,
  FormControl,
} from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { catchError, EMPTY, filter, finalize } from 'rxjs';
import { StatusMessage } from '../../../../core/models/status-message';
import { SnackbarService } from '../../../../core/services/snackbar.service';
import { ConfirmationDialogComponent } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { UserService } from '../../services/user.service';
import { CreateUserRequest } from '../../models/create-user-request';
import { Router } from '@angular/router';

export interface RequiredUserAction {
  value: string;
  label: string;
  description: string;
}

@Component({
  selector: 'app-create-user',
  standalone: false,
  templateUrl: './create-user.component.html',
  styleUrl: './create-user.component.css',
})
export class CreateUserComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly snackbarService = inject(SnackbarService);

  protected readonly userCreateStatus = signal<StatusMessage | null>(null);
  protected readonly isSubmitting = signal(false);
  protected readonly router = inject(Router);
  protected readonly requiredUserActions: RequiredUserAction[] = [
    {
      value: 'VERIFY_EMAIL',
      label: 'Verify email',
      description: 'User must verify their email address.',
    },
    {
      value: 'UPDATE_PASSWORD',
      label: 'Update password',
      description: 'User must set a new password.',
    },
    {
      value: 'UPDATE_PROFILE',
      label: 'Update profile',
      description: 'User must complete/update their profile.',
    },
    {
      value: 'CONFIGURE_TOTP',
      label: 'Configure OTP',
      description: 'User must configure OTP authentication.',
    },
    {
      value: 'WEBAUTHN_REGISTER',
      label: 'Register WebAuthn',
      description: 'User must register a WebAuthn credential.',
    },
  ];

  protected userForm!: FormGroup<{
    email: FormControl<string>;
    firstName: FormControl<string>;
    lastName: FormControl<string>;
    enabled: FormControl<boolean>;
    emailVerified: FormControl<boolean>;
    requiredActions: FormControl<string[]>;
  }>;

  ngOnInit(): void {
    this.initUserForm();
  }

  private initUserForm(): void {
    this.userForm = this.fb.nonNullable.group({
      email: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(255)],
      ],

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

      enabled: this.fb.nonNullable.control(true),

      emailVerified: this.fb.nonNullable.control(false),

      requiredActions: this.fb.nonNullable.control<string[]>([
        'VERIFY_EMAIL',
        'UPDATE_PASSWORD',
      ]),
    });
  }

  protected get f() {
    return this.userForm.controls;
  }

  protected saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.userCreateStatus.set(null);
    this.isSubmitting.set(true);

    const request: CreateUserRequest = this.userForm.getRawValue();

    this.userService
      .addUser(request)
      .pipe(
        takeUntilDestroyed(this.destroyRef),

        catchError((error) => {
          this.showError(error);
          return EMPTY;
        }),

        finalize(() => {
          this.isSubmitting.set(false);
        }),
      )
      .subscribe((response) => {
        this.snackbarService.success(response.message);
        this.resetUserForm();
        this.router.navigate([`/users/${response.data}`]);
      });
  }

  protected resetUserForm(): void {
    this.userForm.reset({
      email: '',
      firstName: '',
      lastName: '',
      enabled: true,
      emailVerified: false,
      requiredActions: ['VERIFY_EMAIL', 'UPDATE_PASSWORD'],
    });

    this.userCreateStatus.set(null);
  }

  protected cancel(): void {
    if (this.userForm.dirty) {
      return;
    }

    this.resetUserForm();
  }

  private showError(error: unknown): void {
    const message =
      this.extractErrorMessage(error) ??
      'Unable to create user. Please try again.';

    this.userCreateStatus.set({
      message,
      type: 'error',
    });
  }

  private extractErrorMessage(error: any): string | null {
    return error?.error?.message ?? error?.message ?? null;
  }

  protected onEmailVerifiedChange(verified: boolean): void {
    const actions = this.f.requiredActions.value;

    if (verified) {
      this.f.requiredActions.setValue(
        actions.filter((action) => action !== 'VERIFY_EMAIL'),
      );
      return;
    }

    if (!actions.includes('VERIFY_EMAIL')) {
      this.f.requiredActions.setValue([...actions, 'VERIFY_EMAIL']);
    }
  }
  // cancel(): void {
  //   if (this.userForm.dirty) {
  //     this.openCancelConfirmationDialog();
  //     return;
  //   }
  //   this.dialogRef.close(false);
  // }

  // private openCancelConfirmationDialog(): void {
  //   const ref = this.dialog.open(ConfirmationDialogComponent, {
  //     width: '500px',
  //     disableClose: true,
  //     data: {
  //       title: 'Unsaved Changes',
  //       message:
  //         'You have unsaved changes. Are you sure you want to leave without saving?',
  //       confirmButtonText: 'Leave',
  //       confirmButtonColor: 'warn',
  //       cancelButtonText: 'Stay',
  //     },
  //   });

  //   ref
  //     .afterClosed()
  //     .pipe(filter(Boolean), takeUntilDestroyed(this.destroyRef))
  //     .subscribe(() => {
  //       this.dialogRef.close(false);
  //     });
  // }
}
