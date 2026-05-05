import { Injectable } from '@angular/core';
import { Login } from '../models/login';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginResponse } from '../models/login-response';
import { Register } from '../models/register';
import { RegistrationResponse } from '../models/registration-response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  signIn(credentials: Login):Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, credentials);
  }

  signUp(registerDetails: Register):Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(`${this.baseUrl}/signup`, registerDetails);
  }
}
