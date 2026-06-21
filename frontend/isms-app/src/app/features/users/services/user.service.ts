import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response';
import { UserSearchRequest } from '../models/user-search-request';
import { UserPageResponse } from '../models/user-page-response';
import { UserDetailsResponse } from '../models/user-details-response';


@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = 'http://localhost:8081/api/users';
  constructor(private http: HttpClient) {}

  getMe(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/me`);
  }

  updateUser(user: any): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.baseUrl}/me`, user);
  }

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
      `${this.baseUrl}/${userId}/status`,
      { enabled: enabled },
    );
  }

  getUser(userId: string) {
    return this.http.get<ApiResponse<UserDetailsResponse>>(
      `${this.baseUrl}/${userId}`,
    );
  }
}
