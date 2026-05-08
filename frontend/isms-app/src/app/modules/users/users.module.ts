import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from '../../components/pages/users/users.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';

import { MatPaginatorModule } from '@angular/material/paginator';

import { MatSortModule } from '@angular/material/sort';
import { UsersRoutingModule } from './users-routing.module';
import { MatIconModule } from '@angular/material/icon';
import { MessageComponent } from '../../components/common/message/message.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCard, MatCardHeader, MatCardTitle, MatCardContent, MatCardSubtitle } from "@angular/material/card";

@NgModule({
  declarations: [UsersComponent, MessageComponent],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    UsersRoutingModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatCardSubtitle
],
})
export class UsersModule {}
