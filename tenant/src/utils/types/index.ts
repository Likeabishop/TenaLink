export enum Role {
    Admin = 'Admin',
    User = 'User',
    Doctor = 'Doctor',
    Nurse = 'Nurse',
    Paramedic = 'Paramedic',
  }
  
  export enum Gender {
    Male = 'Male',
    Female = 'Female',
  }

  export type Users = {
    profilePic?: string | undefined;
    userId: string;
    firstname: string;
    lastname: string;
    email: string;
    password: string;
    role: Role;
    companyId?: string;
    resetPasswordToken?: string;
    resetPasswordExpiresAt?: string; // DateTime
    verificationToken?: string;
    verificationTokenExpiresAt?: string; // DateTime
    isActive: boolean;
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