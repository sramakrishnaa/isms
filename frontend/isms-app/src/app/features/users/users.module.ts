import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsersRoutingModule } from './users-routing.module';
import { BreadcrumbComponent } from 'xng-breadcrumb';
import { UserDetailsComponent } from './pages/user-details/user-details.component';
import { SharedModule } from '../../shared/modules/shared.module';
import { AvatarModule } from 'ngx-avatars';
import { UserHeaderComponent } from './pages/user-details/user-header/user-header.component';
import { PersonalInformationCardComponent } from './pages/user-details/personal-information-card/personal-information-card.component';
import { AddUserComponent } from './dialog/add-user/add-user.component';
import { EditUserDetailsComponent } from './dialog/edit-user-details/edit-user-details.component';
import { MatTabsModule } from '@angular/material/tabs';
import { UserListComponent } from './pages/user-list/user-list.component';
import { CreateUserComponent } from './pages/create-user/create-user.component';
import {MatSelectModule} from '@angular/material/select';

@NgModule({
  declarations: [
    UserDetailsComponent,
    UserHeaderComponent,
    PersonalInformationCardComponent,
    AddUserComponent,
    EditUserDetailsComponent,
    UserListComponent,
    CreateUserComponent,
  ],
  imports: [
    CommonModule,
    UsersRoutingModule,
    SharedModule,
    BreadcrumbComponent,
    AvatarModule,
    MatTabsModule,
    MatSelectModule
  ],
  exports: [],
})
export class UsersModule {}
