export interface CreateUserRequest {
  email: string;
  firstName: string;
  lastName: string;
  enabled: boolean;
  emailVerified: boolean;
  requiredActions: string[];
}


