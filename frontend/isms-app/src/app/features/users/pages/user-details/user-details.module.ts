import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserDetailsRoutingModule } from './user-details-routing.module';
import { SettingsComponent } from './settings/settings.component';
import { CredentialsComponent } from './credentials/credentials.component';
import { RoleMappingComponent } from './role-mapping/role-mapping.component';
import {MatGridListModule} from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';


@NgModule({
  declarations: [SettingsComponent, CredentialsComponent, RoleMappingComponent],
  imports: [CommonModule, UserDetailsRoutingModule, MatGridListModule, MatCardModule,MatFormFieldModule],
})
export class UserDetailsModule {}
