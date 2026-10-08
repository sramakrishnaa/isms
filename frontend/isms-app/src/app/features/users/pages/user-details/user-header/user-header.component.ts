import { Component, DestroyRef, inject, Input } from '@angular/core';
import { UserResponse } from '../../../models/user-details-response';
import { MatDialog } from '@angular/material/dialog';
import { EditUserDetailsComponent } from '../../../dialog/edit-user-details/edit-user-details.component';
import { filter, switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ConfirmationDialogComponent } from '../../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { SnackbarService } from '../../../../../core/services/snackbar.service';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-header',
  standalone: false,
  templateUrl: './user-header.component.html',
  styleUrl: './user-header.component.css',
})
export class UserHeaderComponent {
  @Input({ required: true }) user!: UserResponse;

  private readonly destroyRef = inject(DestroyRef);
  private readonly dialog = inject(MatDialog);
  private readonly userService = inject(UserService);
  private readonly snackbarService = inject(SnackbarService);
  private readonly router = inject(Router);

  deleteUser(user: UserResponse): void {
    const action = 'Delete';
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: `${action} ${user.email}`,
        message: `Are you sure you want to permanently ${action.toLowerCase()} ${user.email}?`,
        confirmButtonText: action,
        confirmButtonColor: 'warn',
      },
    });

    dialogRef
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.userService.deleteUser(user.id)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (response) => {
          this.router.navigate(['/users']);
          this.snackbarService.show(response.message);
        },
        error: () => {
          this.snackbarService.show('Failed to delete user');
        },
      });
  }

  toggleUserStatus(user: UserResponse): void {
    const enable = !user.enabled;
    const action = enable ? 'Enable' : 'Disable';
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: `${action} User`,
        message: `Are you sure you want to ${action.toLowerCase()} ${user.email}?`,
        confirmButtonText: action,
        confirmButtonColor: enable ? 'primary' : 'warn',
      },
    });

    dialogRef
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.userService.updateUserStatus(user.id, enable)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (response) => {
          this.user.enabled = enable;
          this.snackbarService.show(response.message);
        },
        error: (err) => {
          this.snackbarService.error(err.message);
        },
      });
  }
}
