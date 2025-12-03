CREATE table IF NOT EXISTS organization 
(
    organizationId BINARY(16) PRIMARY KEY,
    name VARCHAR(255)
);

CREATE table IF NOT EXISTS users 
(
    userId BINARY(16) PRIMARY KEY,
    organization_id BINARY(16) NULL,
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
    tokenInvalidBefore DATETIME,

    FOREIGN KEY (organization_id) REFERENCES organization(organizationId)
);

CREATE table IF NOT EXISTS refresh_tokens 
(
    refreshId BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    token VARCHAR(255),
    expiryDate DATETIME NOT NULL,
    revoked BOOLEAN NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(userId)
);

CREATE table IF NOT EXISTS property 
(
    propertyId BINARY(16) PRIMARY KEY,
    organization_id BINARY(16),
    name VARCHAR(25),
    address VARCHAR(50),
    monthlyRent DECIMAL(16, 4),
    dueDay INT(2),
    status ENUM('VACANT', 'OCCUPIED', 'MAINTENANCE', 'IS_UNACTIVE') NOT NULL DEFAULT 'VACANT',
    createdDate DATETIME NOT NULL,
    createdBy VARCHAR(36) NOT NULL,
    updatedDate DATETIME,
    updatedBy VARCHAR(36),
    deletedBy VARCHAR(36),
    deletedDate DATETIME,

    FOREIGN KEY (organization_id) REFERENCES organization(organizationId)
);

CREATE table IF NOT EXISTS unit 
(
    unitId BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    property_id BINARY(16) NOT NULL,
    unitNumber VARCHAR(15),
    monthlyRent DECIMAL(8, 4),
    status ENUM('VACANT', 'OCCUPIED', 'MAINTENANCE', 'IS_UNACTIVE') NOT NULL DEFAULT 'VACANT',
    createdDate DATETIME NOT NULL,
    createdBy VARCHAR(36) NOT NULL,
    updatedDate DATETIME,
    updatedBy VARCHAR(36),
    deletedBy VARCHAR(36),
    deletedDate DATETIME,

    FOREIGN KEY (user_id) REFERENCES users(userId),
    FOREIGN KEY (property_id) REFERENCES property(propertyId)
);

CREATE table IF NOT EXISTS invoice 
(
    invoiceId BINARY(16) PRIMARY KEY,
    unit_id BINARY(16) NOT NULL,
    tenant BINARY(16) NOT NULL,
    invoiceNumber VARCHAR(255),
    amount DECIMAL(8, 4) NOT NULL,
    amountPaid DECIMAL(8, 4) NOT NULL,
    dueDate DATE,
    description VARCHAR(255),
    status ENUM('PENDING', 'PARTIAL', 'PAID', 'OVERDUE', 'IS_UNACTIVE') NOT NULL DEFAULT 'PENDING',
    createdDate DATETIME NOT NULL,
    createdBy VARCHAR(36) NOT NULL,
    updatedDate DATETIME,
    updatedBy VARCHAR(36),
    deletedBy VARCHAR(36),
    deletedDate DATETIME,

    FOREIGN KEY (unit_id) REFERENCES unit(unitId),
    FOREIGN KEY (tenant) REFERENCES users(userId)
);

CREATE table IF NOT EXISTS payment 
(
    paymentId BINARY(16) PRIMARY KEY,
    invoice_id BINARY(16) NOT NULL,
    paymentReference VARCHAR(255),
    paidBy BINARY(16) NOT NULL,
    amount DECIMAL(8, 4),
    method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'MOBILE_MONEY', 'CASH') NOT NULL DEFAULT 'MOBILE_MONEY',
    gateWayResponse VARCHAR(255),
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED', 'CANCELLED', 'IS_UNACTIVE') NOT NULL DEFAULT 'PENDING',
    createdDate DATETIME NOT NULL,
    createdBy VARCHAR(36) NOT NULL,
    updatedDate DATETIME,
    updatedBy VARCHAR(36),
    deletedBy VARCHAR(36),
    deletedDate DATETIME,

    FOREIGN KEY (invoice_id) REFERENCES invoice(invoiceId),
    FOREIGN KEY (paidBy) REFERENCES users(userId)
);

CREATE TABLE IF NOT EXISTS chatroom 
(
    roomId BINARY(16) PRIMARY KEY,
    name VARCHAR(255),
    type ENUM('PROPERTY_TENANTS', 'ADMIN_SUPPPORT', 'BROADCAST', 'DIRECT') NOT NULL DEFAULT 'ADMIN_SUPPPORT',
    organization_id BINARY(16),
    status ENUM('IS_ACTIVE', 'IS_UNACTIVE') NOT NULL DEFAULT 'IS_ACTIVE',
    createdDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdBy VARCHAR(36) NOT NULL,
    updatedDate DATETIME,
    updatedBy VARCHAR(36),
    deletedBy VARCHAR(36),
    deletedDate DATETIME,

    FOREIGN KEY (organization_id) REFERENCES organization(organizationId)
);

CREATE TABLE IF NOT EXISTS chat_room_users 
(
    room_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    joinedDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES chatroom(roomId),
    FOREIGN KEY (user_id) REFERENCES users(userId)
);

CREATE table IF NOT EXISTS chatmessage 
(
    messageId BINARY(16) PRIMARY KEY,
    room_id BINARY(16),
    sender BINARY(16),
    content VARCHAR(255),
    messageType ENUM('TEXT', 'IMAGE', 'FILE', 'SYSTEM'),
    fileUrl varchar(255),
    isRead BOOLEAN DEFAULT FALSE,
    status ENUM('IS_ACTIVE', 'IS_UNACTIVE') NOT NULL DEFAULT 'IS_UNACTIVE',
    createdDate DATETIME NOT NULL,
    createdBy VARCHAR(36) NOT NULL,
    updatedDate DATETIME,
    updatedBy VARCHAR(36),
    deletedBy VARCHAR(36),
    deletedDate DATETIME,

    FOREIGN KEY (room_id) REFERENCES chatroom(roomId),
    FOREIGN KEY (sender) REFERENCES users(userId)
);

CREATE table IF NOT EXISTS analytics_snapshot 
(
    snapshotId BINARY(16) PRIMARY KEY,
    organization_id BINARY(16),
    snapshotDate DATE,
    totalTenants INT(6),
    activeTenants INT(6),
    newTenantsThisMonth INT(6),
    totalInvoices INT(6),
    paidInvoices INT(6),
    overdueInvoices INT(6),
    pendingInvoices INT(6),
    totalRevenue DECIMAL(8, 4),
    collectedRevenue DECIMAL(8, 4),
    pendingRevenue DECIMAL(8, 4),
    totalUnits INT(6),
    occupiedUnits INT(6),
    vacantUnits INT(6),
    maintenanceUnits INT(6),
    collectionRate DECIMAL(8, 4),

    FOREIGN KEY (organization_id) REFERENCES organization(organizationId)
);