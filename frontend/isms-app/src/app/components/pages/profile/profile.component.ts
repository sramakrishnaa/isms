import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { UserResponse } from '../../../models/api-response';
import { EditProfileComponent } from '../../dialogs/edit-profile/edit-profile.component';
import { SnackbarService } from '../../../services/snackbar/snackbar.service';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent implements OnInit {
  user!: UserResponse;
  readonly editProfileDialog = inject(MatDialog);

  constructor(
    private userService: UserService,
    private snackbarService: SnackbarService,
  ) {}
  ngOnInit(): void {
    this.getProfile();
  }

  getProfile() {
    this.userService.getMe().subscribe({
      next: (response) => {
        this.user = response.data;
      },
      error: (err) => {
        console.error('Failed to load profile', err);
      },
    });
  }

  changePassword(): void {
    window.open(
      'http://localhost:8080/realms/isms/account/account-security/signing-in',
      '_blank',
    );
  }

  manageSessions(): void {
    window.open(
      'http://localhost:8080/realms/isms/account/account-security/device-activity',
      '_blank',
    );
  }
  enableTFA(): void {
    window.open(
      'http://localhost:8080/realms/isms/account/account-security/signing-in',
      '_blank',
    );
  }

  async editProfile(): Promise<void> {
    const { EditProfileComponent } =
      await import('./../../dialogs/edit-profile/edit-profile.component');
    const dialogRef = this.editProfileDialog.open(EditProfileComponent, {
      data: this.user,
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.userService.updateUser(result).subscribe({
          next: (response) => {
            this.snackbarService.showWithAction(
              'Profile details updated successfully',
              'close',
            );
            this.getProfile();
          },
          error: (err) => {
            console.error('Failed to update profile', err);

            this.snackbarService.showWithAction(
              'Failed to update profile details',
              'close',
            );
          },
        });
      }
    });
  }
}
