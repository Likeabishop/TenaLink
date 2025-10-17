CREATE table IF NOT EXISTS users 
(
    userId BINARY(16) PRIMARY KEY,
    name VARCHAR(35) NOT NULL,
    middleNames VARCHAR(35),
    surname VARCHAR(35) NOT NULL,
    identificationNumber VARCHAR(13) UNIQUE,
    email VARCHAR(25) UNIQUE NOT NULL,
    password VARCHAR(255) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'SUPER_ADMIN', 'TENANT') NOT NULL DEFAULT 'TENANT',
    createdDate DATETIME NOT NULL,
    createdBy VARCHAR(36) NOT NULL,
    updatedDate DATETIME,
    updatedBy VARCHAR(36),
    deletedBy VARCHAR(36),
    deletedDate DATETIME,
    suspendedBy VARCHAR(36),
    suspendedDate DATETIME,
    status ENUM('IS_ACTIVE', 'IS_UNACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'IS_UNACTIVE',
    verificationToken VARCHAR(255),
    verificationTokenExpiry DATETIME,
    resetPasswordToken VARCHAR(255),
    resetPasswordTokenExpiry DATETIME,
    tokenInvalidBefore DATETIME
);

CREATE table IF NOT EXISTS refresh_tokens 
(
    refreshId BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    token VARCHAR(255),
    expiryDate DATETIME NOT NULL,
    revoked BOOLEAN NOT NULL
);