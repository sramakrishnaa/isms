import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormGroup,
  FormBuilder,
  Validators,
  AbstractControl,
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { catchError, EMPTY, filter } from 'rxjs';
import { StatusMessage } from '../../../../core/models/status-message';
import { SnackbarService } from '../../../../core/services/snackbar.service';
import { ConfirmationDialogComponent } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/user-details-response';

@Component({
  selector: 'app-edit-user-details',
  standalone: false,
  templateUrl: './edit-user-details.component.html',
  styleUrl: './edit-user-details.component.css',
})
export class EditUserDetailsComponent {
  protected editUserForm!: FormGroup;
  private readonly dialog = inject(MatDialog);
  private readonly dialogRef = inject(MatDialogRef<EditUserDetailsComponent>);
  protected readonly data = inject<UserResponse>(MAT_DIALOG_DATA);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly snackbarService = inject(SnackbarService);
  protected readonly userUpdateStatus = signal<StatusMessage | null>(null);

  ngOnInit(): void {
    this.initUserForm();
  }

  private initUserForm(): void {
    this.editUserForm = this.fb.nonNullable.group({
      email: [this.data.email, [Validators.required, Validators.email]],

      firstName: [
        this.data.firstName,
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(100),
        ],
      ],

      lastName: [
        this.data.lastName,
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(100),
        ],
      ],
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.editUserForm.controls;
  }

  saveChanges(): void {
    if (this.editUserForm.invalid) {
      this.editUserForm.markAllAsTouched();
      return;
    }

    this.userService
      .updateUser(this.data.id, this.editUserForm.getRawValue())
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError((error) => {
          this.showError(error);
          return EMPTY;
        }),
      )
      .subscribe((response) => {
        this.snackbarService.success(response.message);
        this.dialogRef.close(true);
      });
  }

  private showError(err: any): void {
    this.userUpdateStatus.set({
      message: err.message,
      type: 'error',
    });
  }

  cancel(): void {
    if (this.editUserForm.dirty) {
      this.openCancelConfirmationDialog();
      return;
    }
    this.dialogRef.close(false);
  }

  private openCancelConfirmationDialog(): void {
    const ref = this.dialog.open(ConfirmationDialogComponent, {
      width: '500px',
      disableClose: true,
      data: {
        title: 'Unsaved Changes',
        message:
          'You have unsaved changes. Are you sure you want to leave without saving?',
        confirmButtonText: 'Leave',
        confirmButtonColor: 'warn',
        cancelButtonText: 'Stay',
      },
    });

    ref
      .afterClosed()
      .pipe(filter(Boolean), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.dialogRef.close(false);
      });
  }
}
