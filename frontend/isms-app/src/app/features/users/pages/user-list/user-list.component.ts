import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import {
  Subject,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  catchError,
  EMPTY,
  finalize,
  filter,
} from 'rxjs';
import { ApiResponse } from '../../../../core/models/api-response';
import { StatusMessage } from '../../../../core/models/status-message';
import { SnackbarService } from '../../../../core/services/snackbar.service';
import { ConfirmationDialogComponent } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { UserResponse } from '../../models/user-details-response';
import { UserPageResponse } from '../../models/user-page-response';
import { UserSearchRequest } from '../../models/user-search-request';
import { UserService } from '../../services/user.service';
import { UserListItem } from '../../models/user-list-item';

@Component({
  selector: 'app-user-list',
  standalone: false,
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css',
})
export class UserListComponent implements OnInit {
  protected readonly displayedColumns = [
    'username',
    'email',
    'firstName',
    'lastName',
    'createdAt',
    'actions'
  ];
  private readonly destroyRef = inject(DestroyRef);
  private readonly dialog = inject(MatDialog);
  private readonly userService = inject(UserService);
  private readonly snackbarService = inject(SnackbarService);
  private readonly router = inject(Router);

  private readonly pageSize = signal(10);
  private readonly pageIndex = signal(0);
  protected readonly searchText = signal('');

  protected readonly totalUsers = signal(0);

  protected readonly isLoading = signal(false);
  protected readonly statusMsg = signal<StatusMessage | null>(null);

  protected readonly users = signal<UserListItem[]>([]);

  private readonly refreshUsers$ = new Subject<void>();
  private readonly searchUsers$ = new Subject<string>();

  ngOnInit(): void {
    this.initializeSearch();
    this.initializeLoadUsers();
    this.refreshUsers();
  }

  private refreshUsers(): void {
    this.refreshUsers$.next();
  }

  private initializeSearch(): void {
    this.searchUsers$
      .pipe(
        debounceTime(1000),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((value) => {
        this.searchText.set(value.trim());
        this.pageIndex.set(0);
        this.refreshUsers();
      });
  }

  private initializeLoadUsers(): void {
    this.refreshUsers$
      .pipe(
        switchMap(() => {
          this.isLoading.set(true);

          return this.userService.loadUsers(this.buildRequest()).pipe(
            catchError((res) => {
              this.showLoadError(res);
              return EMPTY;
            }),
            finalize(() => this.isLoading.set(false)),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (response: ApiResponse<UserPageResponse>) => {
          this.updateUserTable(response.data);
        },
      });
  }

  applyFilter(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchUsers$.next(value.trim());
  }

  private buildRequest(): UserSearchRequest {
    return {
      pageIndex: this.pageIndex(),
      pageSize: this.pageSize(),
      searchText: this.searchText(),
    };
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.refreshUsers();
  }

  private updateUserTable(response: UserPageResponse): void {
    this.users.set(response.list);
    this.totalUsers.set(response.totalCount);
    this.statusMsg.set(null);
  }

  private showLoadError(res: any): void {
    this.users.set([]);
    this.totalUsers.set(0);
    this.statusMsg.set({
      message: res.message,
      type: 'error',
    });
  }

  viewUserDetails(user: UserResponse): void {
    this.router.navigate([`/users/${user.id}`]);
  }

  navigateCreateUser():void{
    this.router.navigateByUrl('/users/add-user');
  }

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
          console.log(response);

          this.users.update((users) => users.filter((u) => u.id !== user.id));
          this.snackbarService.show(response.message);
        },
        error: () => {
          this.snackbarService.show('Failed to delete user');
        },
      });
  }
}
