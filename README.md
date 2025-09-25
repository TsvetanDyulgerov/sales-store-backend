# Sales Store Backend

## Table of Contents

- [About](#about)
- [Core Features](#core-features)
- [Technologies Used](#technologies-used)
- [Database Schema](#database-schema)
- [API Endpoints + Swagger](#api-endpoints)
  - [Auth](#auth)
  - [User](#user)
  - [Product](#product)
  - [Order](#order)
  - [Report](#report)
- [Getting Started](#getting-started)


## About

**sales-store-backend**  is a backend system for a web-based e-commerce application which serves to manage users, products, and orders efficiently.
It is build using Java with Spring Boot framework, ensuring a robust and scalable architecture.

## Core Features

-   **User Management:**  Supports different user roles (`admin`  and  `user`) with secure authentication via JWT tokens. Admins can manage users and products, while regular users can place orders.
    
-   **Product Inventory:**  Maintains detailed product information including pricing, stock availability, and descriptions.
    
-   **Order Processing:**  Allows users to submit orders, validating product availability and tracking order status (`Pending`,  `In Progress`,  `Done`).
    
-   **Reporting:**  Generates customizable reports filtered by product name, username, and order date range, providing insights into orders and user activities.
    

The backend uses a relational database (PostgreSQL) with the following schema:

-   `users`  — (id, username, first_name, last_name, email, role, password (hashed)).
    
-   `products`  — (id, name, description, selling_price, actual_price, available_stock).
    
-   `orders`  — (id, user_id, order_date, total_cost, status).
    
-   `order_products`  — (order_id, product_id, quantity).
    

## Technologies Used
- Java version: 17
- Framework: Spring Boot
-  Security: Spring Security
-  Authentication: JWT header tokens
-  Database: PostgreSQL
-  Entity management: JPA/Hibernate
-  Build tool: Maven


## API Endpoints:

### Swagger
A more detailed API documentation is available via Swagger UI at: `http://backend-server-url/swagger-ui/`
Make sure that the backend server is running to access the Swagger UI.

### Auth:
| HTTP Method | Endpoint           | Description                              | Authentication Required | Roles Allowed |
|-------------|--------------------|------------------------------------------|--------------------------|---------------|
| POST        | /api/auth/register | Register a new user                      | No                       | N/A           |
| POST        | /api/auth/login    | Authenticate a user and return a JWT     | No                       | N/A           |
### User:
| HTTP Method | Endpoint        | Description                      | Authentication Required | Roles Allowed |
|-------------|-----------------|----------------------------------|--------------------------|---------------|
| GET         | /api/users      | Retrieve all users               | Yes                      | Admin         |
| GET         | /api/users/{id} | Retrieve a specific user by ID   | Yes                      | Admin         |
| PUT         | /api/users/{id} | Update user details              | Yes                      | Admin         |
| DELETE      | /api/users/{id} | Delete a user                    | Yes                      | Admin         |
### Product:
| HTTP Method | Endpoint            | Description                      | Authentication Required | Roles Allowed |
|-------------|---------------------|----------------------------------|--------------------------|---------------|
| GET         | /api/products       | Retrieve all products            | No                       | N/A           |
| GET         | /api/products/{id}  | Retrieve a specific product by ID| No                       | N/A           |
| POST        | /api/products       | Add a new product                | Yes                      | Admin         |
| PUT         | /api/products/{id}  | Update product details           | Yes                      | Admin         |
| DELETE      | /api/products/{id}  | Delete a product                 | Yes                      | Admin         |
### Order:
| HTTP Method | Endpoint               | Description                              | Authentication Required | Roles Allowed       |
|-------------|------------------------|------------------------------------------|--------------------------|---------------------|
| POST        | /api/orders            | Place a new order                        | Yes                      | User                |
| GET         | /api/orders            | Retrieve all orders for the user         | Yes                      | User, Admin         |
| GET         | /api/orders/{id}       | Retrieve a specific order by ID          | Yes                      | User (own orders), Admin |
| PUT         | /api/orders/{id}/status| Update the status of an order            | Yes                      | Admin               |
### Report:
| HTTP Method | Endpoint      | Description                      | Authentication Required | Roles Allowed |
|-------------|---------------|----------------------------------|--------------------------|---------------|
| GET         | /api/reports  | Generate reports based on filters| Yes                      | Admin         |


## Getting Started

The full project is hosted on GitHub: [sales-store-deployment](https://github.com/TsvetanDyulgerov/sales-store-deployment)

1. **To use this backend on its own, clone the repository:**
   ```bash
   git clone https://github.com/TsvetanDyulgerov/sales-store-backend
   
2. **Navigate to the project directory:**
   ```bash
   cd sales-store-backend
   ```
   
3. **Configure the application:**
 - Rename the .env.example file to .env and update the environment variables as needed.
 - You must set a 265 bit secret key for JWT. You can generate one using online tools or libraries.

4. **Use Docker Compose to start the app**
    ```bash
    docker-compose up --build
    ```
