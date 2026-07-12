import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsersRoutingModule } from './users-routing.module';
import { BreadcrumbComponent } from 'xng-breadcrumb';
import { UserDetailsComponent } from './pages/user-details/user-details.component';
import { UsersComponent } from './pages/users/users.component';
import { SharedModule } from '../../shared/modules/shared.module';
import { AvatarModule } from 'ngx-avatars';
import { UserHeaderComponent } from './pages/user-details/user-header/user-header.component';
import { PersonalInformationCardComponent } from './pages/user-details/personal-information-card/personal-information-card.component';
import { AccountInformationCardComponent } from './pages/user-details/account-information-card/account-information-card.component';
import { RolesCardComponent } from './pages/user-details/roles-card/roles-card.component';
import { DangerZoneCardComponent } from './pages/user-details/danger-zone-card/danger-zone-card.component';
import { AddUserComponent } from './dialog/add-user/add-user.component';
import { EditUserDetailsComponent } from './dialog/edit-user-details/edit-user-details.component';

@NgModule({
  declarations: [
    UsersComponent,
    UserDetailsComponent,
    UserHeaderComponent,
    PersonalInformationCardComponent,
    AccountInformationCardComponent,
    RolesCardComponent,
    DangerZoneCardComponent,
    AddUserComponent,
    
    EditUserDetailsComponent,
  ],
  imports: [
    CommonModule,
    UsersRoutingModule,
    SharedModule,
    BreadcrumbComponent,
    AvatarModule,
  ],
  exports: [],
})
export class UsersModule {}
