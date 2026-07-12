import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import { MessageComponent } from '../components/message/message.component';
import { MaterialModule } from './material.module';
import { LogoComponent } from '../components/logo/logo.component';
import { HomeComponent } from '../../features/home/pages/home/home.component';
import { LayoutComponent } from '../../core/layout/layout.component';
import { NavbarComponent } from '../../core/layout/navbar/navbar.component';
import { SidebarComponent } from '../../core/layout/sidebar/sidebar.component';

@NgModule({
  declarations: [
    ConfirmationDialogComponent,
    MessageComponent,
    HomeComponent,
  ],
  imports: [CommonModule, MaterialModule],
  exports: [
    MaterialModule,
    ConfirmationDialogComponent,
    MessageComponent,
  ],
})
export class SharedModule {}
