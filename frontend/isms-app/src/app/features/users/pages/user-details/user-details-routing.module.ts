import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserDetailsComponent } from './user-details.component';
import { SettingsComponent } from './settings/settings.component';
import { CredentialsComponent } from './credentials/credentials.component';
import { RoleMappingComponent } from './role-mapping/role-mapping.component';

const routes: Routes = [
  {
    path: '',
    component: UserDetailsComponent,
    children: [
      {
        path: '',
        redirectTo: 'settings',
        pathMatch: 'full',
      },
      {
        path: 'settings',
        component: SettingsComponent,
        data: {
          breadcrumb: {
            skip: true,
          },
        },
      },
      {
        path: 'credentials',
        component: CredentialsComponent,
        data: {
          breadcrumb: {
            skip: true,
          },
        },
      },
      {
        path: 'role-mapping',
        component: RoleMappingComponent,
        data: {
          breadcrumb: {
            skip: true,
          },
        },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserDetailsRoutingModule {}
