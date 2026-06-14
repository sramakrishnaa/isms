import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardLayoutRoutingModule } from './dashboard-layout-routing.module';
import { DashboardLayoutComponent } from '../../components/dashboard-layout/dashboard-layout.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { SharedModule } from "../../../shared/modules/shared.module";
import { NavbarComponent } from '../../components/dashboard-layout/navbar/navbar.component';
import { SidebarComponent } from '../../components/dashboard-layout/sidebar/sidebar.component';
import {MatExpansionModule} from '@angular/material/expansion';

@NgModule({
  declarations: [DashboardLayoutComponent,NavbarComponent,SidebarComponent],
  imports: [
    CommonModule,
    MatMenuModule,
    MatListModule,
    MatIconModule,
    MatToolbarModule,
    MatSidenavModule,
    MatSlideToggleModule,
    DashboardLayoutRoutingModule,
    MatIconModule,
    SharedModule,
    MatExpansionModule

],
  exports: [
    MatMenuModule,
    MatListModule,
    MatIconModule,
    MatToolbarModule,
    MatSidenavModule,
    MatSlideToggleModule,
    SharedModule
  ],
})
export class DashboardLayoutModule {}
