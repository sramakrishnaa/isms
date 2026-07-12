import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../services/user.service';
import { ConfirmationDialogComponent } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { catchError, EMPTY, filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SnackbarService } from '../../../../core/services/snackbar.service';
import { StatusMessage } from '../../../../core/models/status-message';

@Component({
  selector: 'app-add-user',
  standalone: false,
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css',
})
export class AddUserComponent implements OnInit {
  protected userForm!: FormGroup;
  private readonly dialog = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly dialogRef = inject(MatDialogRef<AddUserComponent>);
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly snackbarService = inject(SnackbarService);
  protected readonly userCreateStatus = signal<StatusMessage | null>(null);

  ngOnInit(): void {
    this.initUserForm();
  }

  private initUserForm(): void {
    this.userForm = this.fb.nonNullable.group({
      username: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(100),
        ],
      ],

      email: ['', [Validators.required, Validators.email]],

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
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.userForm.controls;
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.userService
      .addUser(this.userForm.getRawValue())
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
    this.userCreateStatus.set({
      message: err.message,
      type: 'error',
    });
  }

  cancel(): void {
    if (this.userForm.dirty) {
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
