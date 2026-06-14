import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { SnackbarService } from '../snackbar/snackbar.service';
import { UserProfile } from '../../models/kc-user-profile';

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private _keycloak?: Keycloak;
  private _profile?: UserProfile;
  private refreshPromise?: Promise<boolean>;

  get keycloak(): Keycloak {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:8080',
        realm: 'isms',
        clientId: 'isms-ui',
      });
    }
    return this._keycloak;
  }

  async init(): Promise<boolean> {
    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html',
        checkLoginIframe: false,
      });

      if (authenticated) {
        this._profile = (await this.keycloak.loadUserProfile()) as UserProfile;

        this.keycloak.onTokenExpired = () => {
          this.refreshToken().catch(() => this.login());
        };

        this.keycloak.onAuthLogout = () => {
          console.warn('User logged out');
        };

        this.keycloak.onAuthRefreshError = () => {
          this.login();
        };
      }

      return authenticated;
    } catch (error) {
      console.error('Keycloak init failed', error);
      throw error;
    }
  }

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  get token(): string | undefined {
    return this.keycloak.token;
  }

  refreshToken(): Promise<boolean> {
    if (!this.refreshPromise) {
      this.refreshPromise = this.keycloak
        .updateToken(30)
        .finally(() => (this.refreshPromise = undefined));
    }
    return this.refreshPromise;
  }

  login() {
    return this.keycloak.login({
      redirectUri: window.location.href,
    });
  }

  register() {
    return this.keycloak.login({
      action: 'register',
      redirectUri: window.location.origin + '/dashboard',
    });
  }
  logout() {
    return this.keycloak.logout({
      redirectUri: window.location.origin + '?logout=true',
    });
  }
}
