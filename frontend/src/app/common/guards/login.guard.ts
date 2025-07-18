import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const loginGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  if(authService.isAuthenticated()){
    try{
      return authService.logout();
    }catch(e){
      return true;
    }
  }
  return true;
};
