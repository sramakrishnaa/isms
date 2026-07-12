import { Component, computed, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { KeycloakService } from '../../../../core/services/keycloak.service';
import { SnackbarService } from '../../../../core/services/snackbar.service';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  standalone: false,
})
export class HomeComponent implements OnInit {
  constructor(
    private keycloakService: KeycloakService,
    private snackbarService: SnackbarService,
    private router: Router
  ) {}

  isLoggedIn = computed(() => this.keycloakService.keycloak.authenticated);
  ngOnInit(): void {
    const params = new URLSearchParams(window.location.search);

    if (params.get('logout')) {
      this.snackbarService.showWithAction('You have been logged out', 'dengey');
      window.history.replaceState({}, document.title, '/');
    }

    if(this.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  login() {
    if(this.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
    this.keycloakService.login();
  }


  register() {
    this.keycloakService.register();
  }
}
