import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersComponent } from './pages/users/users.component';
import { UserDetailsComponent } from './pages/user-details/user-details.component';
import { ErrorStateMatcher } from '@angular/material/core';
import { ConfigurableErrorStateMatcher } from '../../core/validators/configurable-error-state-matcher';

const routes: Routes = [
  {
    path: '',
    data: { breadcrumb: 'Users'  },
    component: UsersComponent,
  },
  {
    path: ':id',  
    data: { breadcrumb: 'User Details'  },
    component: UserDetailsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UsersRoutingModule {}
