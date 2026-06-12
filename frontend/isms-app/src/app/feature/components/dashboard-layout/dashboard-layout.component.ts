import { MediaMatcher } from '@angular/cdk/layout';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router, ActivatedRoute } from '@angular/router';
import { UserProfile } from '../../../core/models/kc-user-profile';
import { KeycloakService } from '../../../core/services/keycloak/keycloak.service';
import { SnackbarService } from '../../../core/services/snackbar/snackbar.service';


@Component({
  selector: 'app-dashboard-layout',
  standalone: false,
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.css',
})
export class DashboardLayoutComponent implements OnInit, OnDestroy {
  protected _profile = signal<UserProfile | undefined>(undefined);
  protected readonly isMobile = signal(true);

  private readonly _mobileQuery: MediaQueryList;
  private readonly _mobileQueryListener: () => void;

  constructor(
    private keycloakService: KeycloakService,
    private snackbarService: SnackbarService,
    private router: Router,
    private route: ActivatedRoute,
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
