import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { UserProfile } from '../../features/profile/models/kc-user-profile';
import { KeycloakService } from '../services/keycloak.service';
import { Router } from '@angular/router';
import { MediaMatcher } from '@angular/cdk/layout';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-layout',
  standalone: false,
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css',
})
export class LayoutComponent implements OnInit, OnDestroy {
  protected _profile = signal<UserProfile | undefined>(undefined);
  protected readonly isMobile = signal(true);

  private readonly _mobileQuery: MediaQueryList;
  private readonly _mobileQueryListener: () => void;

  constructor(
    private keycloakService: KeycloakService,
    private router: Router,
  ) {
    const media = inject(MediaMatcher);

    this._mobileQuery = media.matchMedia('(max-width: 1280px)');

    this.isMobile.set(this._mobileQuery.matches);

    this._mobileQueryListener = () => {
      this.isMobile.set(this._mobileQuery.matches);
    };

    this._mobileQuery.addEventListener('change', this._mobileQueryListener);
  }

  ngOnInit(): void {
    this._profile.set(this.keycloakService.profile);
  }

  ngOnDestroy(): void {
    this._mobileQuery.removeEventListener('change', this._mobileQueryListener);
  }

  closeIfMobile(sidenav: MatSidenav): void {
    if (this.isMobile()) {
      sidenav.close();
    }
  }

  openProfile(): void {
    this.router.navigate(['/dashboard/profile']);
  }

  logout(): void {
    this.keycloakService.logout();
  }
}
