import {
  AfterViewInit,
  Component,
  OnInit,
  signal,
  ViewChild,
} from '@angular/core';
import { UserListResponse } from '../../../models/api-response';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { UserService } from '../../../services/user.service';
import { StatusMessage } from '../../../models/status-message';

@Component({
  selector: 'app-users',
  standalone: false,
  templateUrl: './users.component.html',
  styleUrl: './users.component.css',
})
export class UsersComponent implements OnInit {
  displayedColumns: string[] = [
    'id',
    'email',
    // 'username',
    'firstName',
    'lastName',
    // 'emailVerified',
    // 'enabled',
    'createdTimestamp',
  ];

  dataSource!: MatTableDataSource<UserListResponse>;
  statusMsg = signal<StatusMessage | null>(null);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private userService: UserService) {
    // this.dataSource = new MatTableDataSource(SAMPLE_USERS);
  }
  ngOnInit(): void {
    this.getAllUsers();
  }


  getAllUsers() {
    this.userService.getAllUsers().subscribe({
      next: (response) => {
        this.dataSource = new MatTableDataSource(response.data);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      },
      error: (err) => {
        console.error('Failed to load users', err);
        this.statusMsg.set({ type: 'error', message: 'Failed to load users' });
      },
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
