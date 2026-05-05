export interface LoginResponse {
    success: boolean;
    message: string;
    timestamp: string;
    data: loginData;
}

export interface loginData {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    user: UserResponse;

}
export interface UserResponse {
    email: string;
    firstName: string;
    lastName: string;
    maskedPhoneNumber: string;
    emailVerified: boolean;
    lastLoginAt: string;
}
