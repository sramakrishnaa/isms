import { inject, NgModule, provideAppInitializer } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './modules/material.module';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './components/home/home.component';
import { LogoComponent } from './components/common/logo.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { DashboardComponent } from './components/pages/dashboard/dashboard.component';
import { ErrorMessageComponent } from './components/common/error.component';
import { ErrorStateMatcher } from '@angular/material/core';
import { ConfigurableErrorStateMatcher } from './components/common/configurable-error-state-matcher';
import { MessageComponent } from './components/common/message/message.component';
import { KeycloakService } from './services/keycloak/keycloak.service';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { ProfileComponent } from './components/pages/profile/profile.component';
import { DashboardLayoutComponent } from './components/dashboard-layout/dashboard-layout.component';
import { SidebarComponent } from './components/dashboard-layout/sidebar/sidebar.component';
import { NavbarComponent } from './components/dashboard-layout/navbar/navbar.component';
import { UsersComponent } from './components/pages/users/users.component';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LogoComponent,
    DashboardComponent,
    ErrorMessageComponent,
    ProfileComponent,
    NavbarComponent,
    DashboardLayoutComponent,
    SidebarComponent,
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
      useFactory: () => new ConfigurableErrorStateMatcher('dirtyOrSubmitted'),
      multi: false,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
    provideAppInitializer(() => {
      const keycloak = inject(KeycloakService);
      return keycloak.init();
    }),
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
