import { inject, NgModule, provideAppInitializer } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';

import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { DashboardComponent } from './feature/components/dashboard/dashboard.component';
import { ErrorStateMatcher } from '@angular/material/core';
import { ConfigurableErrorStateMatcher } from './shared/configurable-error-state-matcher';

import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { KeycloakService } from './core/services/keycloak/keycloak.service';
import { SharedModule } from './shared/modules/shared.module';
import { MessageComponent } from './components/common/message/message.component';
import { HomeComponent } from './feature/components/home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    MessageComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    SharedModule,
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
