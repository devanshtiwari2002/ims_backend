MS Backend: Inventory, Sales & Analytics System

I built this system to solve the core challenges of small-to-medium retail businesses. Most open-source inventory tools are either too simple (basic CRUD) or overly complex. This project bridges that gap by providing a high-performance, secure backend that handles atomic sales transactions and provides real-time profit analytics.

Core Business Logic & Features
    Dynamic Inventory & Soft Management
        - Soft Deletion (Audit Safety)
        - Search & Discovery
        - Smart Pagination
        - Cost-Basis Tracking
        - Custom Low-Stock Thresholds
    Enterprise-Grade Security (RBAC & JWT)
        - Stateless Authentication
        - Ownership-Based Access
             Admin : Full oversight 
             Staff : Operation focusde -> stock viewing , sale creation, own sales history 
    Financial Compliance & Documentation
        - Immutable Invoice Snapshots
        - Zero-Gap Invoice Sequencing
    Business Intelligence (The Analytics Layer)
        - Inventory Valuation
        - day/week/month-Wise Revenue

Tech Stack : 

Framework: Spring Boot 3.x
Security: Spring Security 6 with JWT (Stateless)
Database: PostgreSQL (Production) / H2 (Development)
Reporting: OpenPDF for dynamic receipt generation

Backend Architecture
    - Controllers: Handle DTO validation and HTTP mapping
    - Services: Manage the "Business Brain" and @Transactional boundaries
    - Repositories: Custom JPQL queries for complex data aggregation
    - Security: Global method security for fine-grained permission control

API Refrence 

Authentication :
    - Post /api/auth/register -> onboard new users
        body {
          "username": "mark",
          "email": "mark@staff.com",
          "password": "mark123",
          "role": "STAFF" // ADMIN
        }
    - POSt api/auth/login -> get token
        body {
          "username": "staff",
          "password": "staff123"
        }
Inventory : 
    POST /api/products -> Admin only product creation
    admin token and Body 
    {
          "sku": "009",
          "name": "Earbuds",
          "category": "Electronics",
          "costPrice": 900.00,
          "sellingPrice": 1200.00,
          "quantity": 50
    }
    GET /api/products -> Paginated list of active products
    GET /api/products/search?name=Earbuds&category=Electronics

Sales and Reports

POST /api/sales → Process a new transaction (Atomic).
GET /api/sales/my-sales → Staff-specific sales history.
GET /api/sales/{id}/download → Trigger PDF invoice download.


    
