import { Component, inject } from '@angular/core';
import { UserResponse } from '../../../core/models/api-response';
import { MatDialog } from '@angular/material/dialog';
import { SnackbarService } from '../../../core/services/snackbar/snackbar.service';
import { UserService } from '../../../core/services/user/user.service';

@Component({
  selector: 'app-user-profile',
  standalone: false,
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent {
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

  editProfile(){
    window.open('http://localhost:8080/realms/isms/account/', '_blank');
  }

  // async editProfile(): Promise<void> {
  //   const { EditUserProfileComponent } =
  //     await import('./../edit-user-profile/edit-user-profile.component');
  //   const dialogRef = this.editProfileDialog.open(EditUserProfileComponent, {
  //     data: this.user,
  //     width: '500px',
  //   });
  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this.userService.updateUser(result).subscribe({
  //         next: (response) => {
  //           this.snackbarService.showWithAction(
  //             'Profile details updated successfully',
  //             'close',
  //           );
  //           this.getProfile();
  //         },
  //         error: (err) => {
  //           console.error('Failed to update profile', err);

  //           this.snackbarService.showWithAction(
  //             'Failed to update profile details',
  //             'close',
  //           );
  //         },
  //       });
  //     }
  //   });
  // }
}
