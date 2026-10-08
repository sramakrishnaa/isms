import { UserListComponent } from './pages/user-list/user-list.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateUserComponent } from './pages/create-user/create-user.component';

const routes: Routes = [
  {
    path: '',
    data: { breadcrumb: 'Users' },
    component: UserListComponent,
  },
  {
    path: 'add-user',
    data:{breadcrumb: 'Create user'},
    component: CreateUserComponent,
  },
  {
    path: ':id',
    data: { breadcrumb: 'User Details' },
    loadChildren: () =>
      import('./pages/user-details/user-details.module').then(
        (m) => m.UserDetailsModule,
      ),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UsersRoutingModule {}
