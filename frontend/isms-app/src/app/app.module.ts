import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './modules/material.module';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SignupComponent } from './components/auth/signup/signup.component';
import { SigninComponent } from './components/auth/signin/signin.component';
import { HomeComponent } from './components/home/home.component';
import { LogoComponent } from './logo.component';

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    SigninComponent,
    HomeComponent,
    LogoComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    AppRoutingModule,
    MaterialModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
