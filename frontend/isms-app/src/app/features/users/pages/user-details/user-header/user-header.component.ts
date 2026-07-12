import { Component, DestroyRef, inject, Input } from '@angular/core';
import { UserResponse } from '../../../models/user-details-response';
import { MatDialog } from '@angular/material/dialog';
import { EditUserDetailsComponent } from '../../../dialog/edit-user-details/edit-user-details.component';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

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

  openEditUserDialog(): void {
    const dialogRef = this.dialog.open(EditUserDetailsComponent, {
      data: this.user,
    });
    dialogRef
      .afterClosed()
      .pipe(filter(Boolean), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {});
  }
}
