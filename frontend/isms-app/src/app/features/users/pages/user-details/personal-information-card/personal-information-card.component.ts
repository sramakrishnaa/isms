import { Component, Input } from '@angular/core';
import { UserResponse } from '../../../models/user-details-response';

@Component({
  selector: 'app-personal-information-card',
  standalone: false,
  templateUrl: './personal-information-card.component.html',
  styleUrl: './personal-information-card.component.css'
})
export class PersonalInformationCardComponent {
@Input({ required: true }) user!: UserResponse;}
