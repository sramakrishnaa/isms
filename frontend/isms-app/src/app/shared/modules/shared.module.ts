import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { LogoComponent } from '../components/logo.component';

@NgModule({
  declarations: [LogoComponent],
  imports: [CommonModule, FormsModule, MatIconModule, MatButtonModule],
  exports: [MatButtonModule, MatIconModule, LogoComponent],
})
export class SharedModule {}
