import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsersRoutingModule } from './users-routing.module';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CardModule } from '../../../shared/modules/card.module';
import { TableModule } from '../../../shared/modules/table.module';
import { UsersComponent } from '../../components/users/users.component';
import { DashboardLayoutModule } from "../dashboard-layout/dashboard-layout.module";
import { AddUserComponent } from '../../components/add-user/add-user.component';
import { DialogModule } from '../../../shared/modules/dialog.module';

@NgModule({
  declarations: [UsersComponent, AddUserComponent],
  imports: [
    CommonModule,
    UsersRoutingModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    CardModule,
    TableModule,
    DashboardLayoutModule,
    DialogModule
],
  exports: [
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    CardModule,
    TableModule,DialogModule
  ],
})
export class UsersModule {}
