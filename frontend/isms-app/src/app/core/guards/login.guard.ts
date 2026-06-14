import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { KeycloakService } from '../services/keycloak/keycloak.service';

export const loginGuard: CanActivateFn = (route, state) => {
  const keycloakService = inject(KeycloakService);
  const router = inject(Router);

  console.log(keycloakService.keycloak);
  
  if (keycloakService.keycloak?.authenticated) {
    router.navigate(['/dashboard']);
    return false;
  }
  return true;
};
