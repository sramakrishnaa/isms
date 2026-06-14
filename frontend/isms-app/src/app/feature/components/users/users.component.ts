import { Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { UserListResponse } from '../../../core/models/api-response';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { StatusMessage } from '../../../core/models/status-message';
import { UserService } from '../../../core/services/user/user.service';
import { MatDialog } from '@angular/material/dialog';
import { AddUserComponent } from '../add-user/add-user.component';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-users',
  standalone: false,
  templateUrl: './users.component.html',
  styleUrl: './users.component.css',
})
export class UsersComponent implements OnInit {
  displayedColumns: string[] = ['email', 'name','status'];

  private pageSize = 5;
  private pageIndex = 0;
  protected totalUsers = 0;
  private searchText = '';
  private searchSubject = new Subject<string>();

  dataSource!: MatTableDataSource<UserListResponse>;
  statusMsg = signal<StatusMessage | null>(null);
  readonly addUserDialog = inject(MatDialog);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  // @ViewChild(MatSort) sort!: MatSort;

  constructor(private userService: UserService) {}
  ngOnInit(): void {
    this.searchSubject
      .pipe(debounceTime(1000), distinctUntilChanged())
      .subscribe((value) => {
        this.pageIndex = 0;
        this.searchText = value;
        this.loadUsers();
      });
    this.loadUsers();
  }

  loadUsers() {
    this.userService
      .loadUsers(this.pageIndex, this.pageSize, this.searchText)
      .subscribe({
        next: (response) => {
          this.dataSource = new MatTableDataSource(response.data.list);
          this.totalUsers = response.data.totalCount;
        },
        error: (err) => {
          console.error('Failed to load users', err);
          this.statusMsg.set({
            type: 'error',
            message: 'Failed to load users',
          });
        },
      });
  }

  applyFilter(event: Event) {
    const value = (event.target as HTMLInputElement).value;

    this.searchSubject.next(value.trim());
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadUsers();
  }

  openAddUserDialog() {
    this.addUserDialog.open(AddUserComponent, {
      width: '500px',
    });
  }
}
