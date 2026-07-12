export interface UserResponse {
  id: string;
  username?: string;
  email?: string;
  emailVerified?: boolean;
  firstName?: string;
  lastName?: string;
  enabled?: boolean;
  createdTimestamp?: number;
  roles?: string[];
}
