# TenaLink
# PLEASE NOTE: THIS IS NOT COMPLETE, IT'S IN-PROGRESS. SO ANY INCONSITENCIES YOU MIGHT ENCOUNTER ARE WITHIN MY EXPECTATIONS.

┌─────────────────────────────────────────────────────────────────┐
│                      PROPTECH PLATFORM                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                   
│  ┌─────────────────┐    ┌─────────────────┐                     
│  │   LANDLORDS     │    │    TENANTS      │                     
│  │ • Portfolio Mgmt│    │ • Rent Payment  │                     
│  │ • Unit Tracking │    │ • Maintenance   │                     
│  │ • Revenue View  │    │ • Communication │                     
│  └────────┬────────┘    └────────┬────────┘                     
│           │                      │                               
│           └──────────┬───────────┘                               
│                      │                                           
│         ┌────────────┴────────────┐                              
│         │   REAL-TIME MESSAGING    │                              
│         │   Landlord ↔ Tenant      │                              
│         │   Tenant ↔ Tenant        │                              
│         └────────────┬────────────┘                              
│                      │                                           
│         ┌────────────┴────────────┐                              
│         │   FINANCIAL LAYER        │                              
│         │   • Fiat Payments        │                              
│         │   • Crypto Transactions  │                              
│         │   • Payment Tracking     │                              
│         └────────────┬────────────┘                              
│                      │                                           
│         ┌────────────┴────────────┐                              
│         │   AUTOMATION ENGINE      │                              
│         │   • Rent Reminders       │                              
│         │   • Scheduled Tracking   │                              
│         │   • Smart Contracts (F)  │                              
│         └──────────────────────────┘                              
│                                                                   
└─────────────────────────────────────────────────────────────────┘

# STACK
# FRONTEND
React 18                 - UI library
TypeScript              - Type safety
Redux Toolkit/Context   - State management
Material-UI/Tailwind    - Component library
Axios                   - HTTP client
React Router            - Navigation
Socket.io-client        - WebSocket integration
React Hook Form         - Form management

# BACKEND
Java 17                  - Core language
Spring Boot 3.x          - Application framework
Spring Security          - Authentication & authorization
Spring Data JPA          - Database operations
JWT                      - Token-based authentication
WebSocket/STOMP         - Real-time messaging
PostgreSQL/MySQL        - Primary database
Maven                    - Dependency management
JUnit/Mockito           - Testing

# Infrastructure & DevOps
Docker                  - Containerization
AWS/GCP/Azure           - Cloud deployment (planned)
GitHub Actions          - CI/CD (planned)
Nginx                   - Reverse proxy
Let's Encrypt           - SSL/TLS
