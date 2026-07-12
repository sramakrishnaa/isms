import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response';
import { UserSearchRequest } from '../models/user-search-request';
import { UserPageResponse } from '../models/user-page-response';
import { UserResponse } from '../models/user-details-response';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = 'http://localhost:8081/api/users';
  constructor(private http: HttpClient) {}

  loadUsers(
    request: UserSearchRequest,
  ): Observable<ApiResponse<UserPageResponse>> {
    return this.http.get<ApiResponse<UserPageResponse>>(
      `${this.baseUrl}?pageIndex=${request.pageIndex}&pageSize=${request.pageSize}&search=${request.searchText}`,
    );
  }

  updateUserStatus(
    userId: string,
    enabled: boolean,
  ): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(
      `${this.baseUrl}/status/${userId}`,
      { enabled: enabled },
    );
  }

  getUser(userId: string) {
    return this.http.get<ApiResponse<UserResponse>>(
      `${this.baseUrl}/${userId}`,
    );
  }

  addUser(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}`, data);
  }

  updateUser(userId: string, data: any): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/${userId}`, data);
    
  }

  deleteUser(userId: string) {
    return this.http.delete<ApiResponse<any>>(`${this.baseUrl}/${userId}`);
  }
}
