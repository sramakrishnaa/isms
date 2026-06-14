import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './feature/components/dashboard/dashboard.component';
import { MessageComponent } from './components/common/message/message.component';
import { authGuard } from './core/guards/auth.guard';
import { HomeComponent } from './feature/components/home/home.component';


const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'msg',
    component: MessageComponent,
  },

  {
    path: 'dashboard',
    data: { breadcrumb: 'Dashboard' },
    loadChildren: () =>
      import('./feature/modules/dashboard-layout/dashboard-layout.module').then(
        (m) => m.DashboardLayoutModule,
      ),
    canActivate: [authGuard],
    
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
