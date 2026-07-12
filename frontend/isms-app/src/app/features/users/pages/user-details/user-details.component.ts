import {
  Component,
  DestroyRef,
  inject,
  Input,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../services/user.service';
import { UserResponse } from '../../models/user-details-response';
import { finalize } from 'rxjs';
import { StatusMessage } from '../../../../core/models/status-message';

@Component({
  selector: 'app-user-details',
  standalone: false,
  templateUrl: './user-details.component.html',
  styleUrl: './user-details.component.css',
})
export class UserDetailsComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);
  private readonly userService = inject(UserService);
  protected readonly userId = signal('');
  protected readonly isLoading = signal(true);
  protected readonly statusMessage = signal<StatusMessage | null>(null);
  protected readonly userDetails = signal<UserResponse | null>(null);

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((params) => {
        this.userId.set(params['id']);
        this.loadUser();
      });
  }

  private loadUser(): void {
    this.isLoading.set(true);

    this.userService
      .getUser(this.userId())
      .pipe(
        finalize(() => this.isLoading.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (response) => {
          this.userDetails.set(response.data);
        },
        error: (error) => {
          let message = 'Something went wrong while loading user details.';
          if (error.status === 0) {
            message =
              'Unable to connect to the server. Please check your internet connection.';
          } else if (error.status === 400) {
            message = error.error?.message ?? 'Invalid user request.';
          } else if (error.status === 401) {
            message = 'You are not authorized to view this user.';
          } else if (error.status === 403) {
            message = 'You do not have permission to access this user.';
          } else if (error.status === 404) {
            message = 'User not found.';
          } else if (error.status >= 500) {
            message = 'Server error occurred. Please try again later.';
          } else if (error.error?.message) {
            message = error.error.message;
          }

          this.statusMessage.set({
            type: 'error',
            message,
          });
        },
      });
  }
}
