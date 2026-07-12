import { inject, NgModule, provideAppInitializer } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';

import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ErrorStateMatcher } from '@angular/material/core';
import { ConfigurableErrorStateMatcher } from './core/validators/configurable-error-state-matcher';

import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { KeycloakService } from './core/services/keycloak.service';
import { LayoutComponent } from './core/layout/layout.component';
import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { SidebarComponent } from './core/layout/sidebar/sidebar.component';
import { MaterialModule } from './shared/modules/material.module';
import { LogoComponent } from './shared/components/logo/logo.component';
import { AvatarModule } from 'ngx-avatars';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';

@NgModule({
  declarations: [AppComponent, LayoutComponent, NavbarComponent, SidebarComponent,LogoComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    MaterialModule,
    AvatarModule
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
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true,
    },
    {
      provide: MAT_DIALOG_DEFAULT_OPTIONS,
      useValue: {
        width: '500px',
        maxWidth: '95vw',
        minHeight: '150px',
        disableClose: true,
        autoFocus: false
      }
    },
    provideAppInitializer(() => {
      const keycloak = inject(KeycloakService);
      return keycloak.init();
    }),
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
