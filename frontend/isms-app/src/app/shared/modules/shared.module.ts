import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import { MessageComponent } from '../components/message/message.component';
import { MaterialModule } from './material.module';
import { LogoComponent } from '../components/logo/logo.component';

@NgModule({
  declarations: [
    ConfirmationDialogComponent,
    MessageComponent,
    LogoComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    MaterialModule,
    ConfirmationDialogComponent,
    MessageComponent, 
    LogoComponent
  ]
})
export class SharedModule {}
