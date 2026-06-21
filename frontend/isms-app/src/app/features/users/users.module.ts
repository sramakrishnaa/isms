import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsersRoutingModule } from './users-routing.module';

import { UserDetailsComponent } from './pages/user-details/user-details.component';
import { UsersComponent } from './pages/users/users.component';
import { MaterialModule } from '../../shared/modules/material.module';
import { SharedModule } from '../../shared/modules/shared.module';

@NgModule({
  declarations: [UsersComponent, UserDetailsComponent],
  imports: [CommonModule, UsersRoutingModule, SharedModule],
  exports: [],
})
export class UsersModule {}
