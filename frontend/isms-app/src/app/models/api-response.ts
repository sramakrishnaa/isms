export interface ApiResponse {
    success: boolean;
    message: string;
    timestamp: string;
    data: any;
}
export interface UserResponse {
    email: string;
    firstName: string;
    lastName: string;
    username: string;
}

export interface UserListResponse {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  emailVerified: boolean;
  enabled: boolean;
  createdTimestamp: number;
}