import { Component, Input } from '@angular/core';
import { UserResponse } from '../../../models/user-details-response';

@Component({
  selector: 'app-roles-card',
  standalone: false,
  templateUrl: './roles-card.component.html',
  styleUrl: './roles-card.component.css'
})
export class RolesCardComponent {

@Input({ required: true }) user!: UserResponse;}
