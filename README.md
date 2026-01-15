#  MS Backend – Inventory, Sales & Analytics System

A **Spring Boot–based backend system** designed for **small-to-medium retail businesses** that require reliable inventory management, transaction-safe sales processing, and real-time business analytics — without the complexity of full ERP systems.

This project focuses on **data integrity, security, and clean backend architecture**, making it production-ready and recruiter-friendly.

---

##  Project Overview

MS Backend is a **RESTful backend application** built using **Spring Boot 3**, implementing **atomic sales transactions**, **role-based access control (RBAC)**, and **real-time revenue analytics**.  
The system follows a **clean layered architecture** and is designed to handle real-world retail workflows and financial constraints.

---

##  Problem Statement

Most open-source inventory systems fall into two extremes:

- **Too simple** → basic CRUD with no transaction safety
- **Too complex** → ERP-level systems that are difficult to maintain and customize

**MS Backend** bridges this gap by offering:
- Transaction-safe sales handling
- Secure multi-user access
- Real-time analytics
- Clean and maintainable backend design

---

##  Core Business Logic & Features

###  Inventory Management
- Soft deletion for audit-safe product lifecycle
- Advanced search and discovery
- Smart pagination for large datasets
- Cost-basis tracking for accurate profit calculation
- Custom low-stock thresholds per product

---

###  Security & Access Control
- Stateless JWT authentication
- Role-Based Access Control (RBAC)
- Ownership-based data visibility

**Roles**
- **ADMIN**
  - Full system oversight
  - Product creation and management
  - Analytics access
- **STAFF**
  - Inventory viewing
  - Sales creation
  - Personal sales history access

---

###  Financial Compliance
- Immutable invoice snapshots
- Zero-gap invoice sequencing
- Atomic, transaction-safe sales processing

---

###  Business Intelligence (Analytics Layer)
- Inventory valuation
- Revenue analytics:
  - Daily
  - Weekly
  - Monthly

---

##  Tech Stack

| Layer | Technology |
|------|-----------|
| Framework | Spring Boot 3.x |
| Security | Spring Security 6 (JWT, Stateless) |
| Database | PostgreSQL (Production) / H2 (Development) |
| Reporting | OpenPDF (Dynamic invoice generation) |

---

##  Backend Architecture

- **Controllers**
  - Request validation using DTOs
  - HTTP request/response mapping
- **Services**
  - Core business logic
  - `@Transactional` boundaries for data consistency
- **Repositories**
  - Custom JPQL queries
  - Aggregation and analytics queries
- **Security**
  - Method-level security
  - Fine-grained permission control

---

##  API Reference

###  Authentication

#### Register User
#### POST /api/auth/register

```json
{
  "username": "mark",
  "email": "mark@staff.com",
  "password": "mark123",
  "role": "STAFF"
}
```
#### Supported roles: ADMIN, STAFF

#### Login
#### POST /api/auth/login
```
  {
  "username": "staff",
  "password": "staff123"
  }
```
#### Returns a JWT token for authenticated requests.

### Inventory
### Create Product (Admin Only)
#### POST /api/products


```json
{
  "sku": "009",
  "name": "Earbuds",
  "category": "Electronics",
  "costPrice": 900.00,
  "sellingPrice": 1200.00,
  "quantity": 50
}
```

#### Get Products (Paginated)
#### GET /api/products
#### Returns all active (non-deleted) products.

#### Search Products
#### GET /api/products/search?name=Earbuds&category=Electronics

### Sales & Reports

#### Create Sale (Atomic Transaction)
#### POST /api/sales
#### Processes a sale atomically and updates inventory safely.

#### Staff Sales History
#### GET /api/sales/my-sales
#### Returns sales created by the authenticated staff user.

#### Download Invoice (PDF)
#### GET /api/sales/{id}/download
#### Generates and downloads an immutable PDF invoice.


