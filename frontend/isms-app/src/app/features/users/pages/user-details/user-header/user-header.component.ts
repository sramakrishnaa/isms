import { Component, Input } from '@angular/core';
import { UserResponse } from '../../../models/user-details-response';

@Component({
  selector: 'app-user-header',
  standalone: false,
  templateUrl: './user-header.component.html',
  styleUrl: './user-header.component.css',
})
export class UserHeaderComponent {
  @Input({ required: true }) user!: UserResponse;
}
