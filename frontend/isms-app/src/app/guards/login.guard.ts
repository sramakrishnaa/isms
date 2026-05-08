import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from '../services/keycloak/keycloak.service';
import { inject } from '@angular/core';

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
