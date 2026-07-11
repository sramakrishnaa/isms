import { Component, Input } from '@angular/core';
import { UserResponse } from '../../../models/user-details-response';

@Component({
  selector: 'app-account-information-card',
  standalone: false,
  templateUrl: './account-information-card.component.html',
  styleUrl: './account-information-card.component.css'
})
export class AccountInformationCardComponent {

  @Input({ required: true }) user!: UserResponse;
}
