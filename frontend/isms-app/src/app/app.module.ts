import { inject, NgModule, provideAppInitializer } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';

import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { DashboardComponent } from './features/dashboard/pages/dashboard/dashboard.component';
import { ErrorStateMatcher } from '@angular/material/core';
import { ConfigurableErrorStateMatcher } from './core/validators/configurable-error-state-matcher';

import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { SharedModule } from './shared/modules/shared.module';
import { KeycloakService } from './core/services/keycloak.service';
import { HomeComponent } from './features/home/pages/home/home.component';

@NgModule({
  declarations: [AppComponent, DashboardComponent, HomeComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    AppRoutingModule,
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
