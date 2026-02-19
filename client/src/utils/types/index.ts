export enum Role {
    SUPER_ADMIN,
    ADMIN,
    TENANT
  }

  export enum UserStatus {
    IS_ACTIVE = "IS_ACTIVE",
    IS_UNACTIVE = "IS_UNACTIVE",
    SUSPENDED = "SUSPENDED"
  }

  export interface AuthStoreState {
    user: Users | null;
    accessToken: string | null;
    refreshToken: string | null;
    isLoading: boolean;
    error: string | null;
  }

  export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType : string;
    expiresIn: string;
  }

  export interface RefreshTokenResponse {
    accessToken: string;
    refreshToken: string;
  }

  export interface LoginCredentials {
    email: string;
    password: string;
  }

  export type Organization = {
    organizationId: string;
    name: string; // Array of member userIds
    users: Users[]; // DateTime
    properties: Property[]; // DateTime
  };

  export type Property = {
    propertyId: string;
    name: string;
    address?: string;
    organization_id?: Organization;
    monthlyRent?: number;
    dueDay: number;
    isActive: boolean;
    createdDate?: string; // DateTime
    createdBy?: string;
    updatedDate?: string; // DateTime
    updatedBy?: string;
  };

  export type Users = {
    profilePic?: string | undefined;
    userId: string;
    firstname: string;
    lastname: string;
    email: string;
    password: string;
    identificationNumber: string;
    role: Role;
    organizationId?: string;
    resetPasswordToken?: string;
    resetPasswordExpiresAt?: string; // DateTime
    verificationToken?: string;
    verificationTokenExpiresAt?: string; // DateTime
    status: UserStatus;
    createdDate: string; // DateTime
    createdBy: string;
    updatedDate?: string; // DateTime
    updatedBy?: string;
    deletedDate?: string; // DateTime
    deletedBy?: string;
  };

  export type Chats = {
    chatId: string;
    members: string[]; // Array of member userIds
    createdAt: string; // DateTime
    updatedAt: string; // DateTime
  };

  export type AuditTrails = {
    auditTrailsId: string;
    userId: string;
    auditTrail?: string;
    loginMachine?: string;
    errorMessage?: string;
    isActive: boolean;
    createdDate?: string; // DateTime
    createdBy?: string;
    updatedDate?: string; // DateTime
    updatedBy?: string;
  };