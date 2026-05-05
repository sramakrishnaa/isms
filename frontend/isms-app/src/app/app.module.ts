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
import { LogoComponent } from './components/common/logo.component';
import { HttpClientModule } from '@angular/common/http';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ErrorMessageComponent } from './components/common/error.component';
import { ErrorStateMatcher } from '@angular/material/core';
import { ConfigurableErrorStateMatcher } from './components/common/configurable-error-state-matcher';
import { MessageComponent } from './components/common/message/message.component';

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    SigninComponent,
    HomeComponent,
    LogoComponent,
    DashboardComponent,
    ErrorMessageComponent,
    MessageComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    AppRoutingModule,
    MaterialModule,
    HttpClientModule,
],
  providers: [
    {
      provide: ErrorStateMatcher,
      useFactory: ()=> new ConfigurableErrorStateMatcher("dirtyOrSubmitted")
      // useClass: GlobalErrorStateMatcher  
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
