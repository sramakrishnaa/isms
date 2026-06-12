import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserProfileRoutingModule } from './user-profile-routing.module';
import { UserProfileComponent } from '../../components/user-profile/user-profile.component';
import { CardModule } from '../../../shared/modules/card.module';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { DialogModule } from '../../../shared/modules/dialog.module';
import { EditUserProfileComponent } from '../../components/edit-user-profile/edit-user-profile.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { SharedModule } from '../../../shared/modules/shared.module';
import { MatInputModule } from '@angular/material/input';

@NgModule({
  declarations: [UserProfileComponent, EditUserProfileComponent],
  imports: [
    CardModule,
    CommonModule,
    DialogModule,
    SharedModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    UserProfileRoutingModule,
  ],
  exports: [
    CardModule,
    DialogModule,
    SharedModule,
    CommonModule,
    MatInputModule,
    MatIconModule,
    MatFormFieldModule,
    ReactiveFormsModule,
  ],
})
export class UserProfileModule {}
