import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = 'http://localhost:8081/api/users';
  constructor(private http: HttpClient) {}

  getMe(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/me`);
  }

  updateUser(user: any): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.baseUrl}/update/me`, user);
  }

  getAllUsers(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}`);
  }
}
