# SalesStoreAPI

## About

**Sales Store**  is an online store management backend.

This Java-based RESTful web service, implemented using Spring Boot, offers a public API to manage users, products, and orders with the following core features:

-   **User Management:**  Supports different user roles (`admin`  and  `user`) with secure authentication via JWT tokens. Admins can manage users and products, while regular users can place orders.
    
-   **Product Inventory:**  Maintains detailed product information including pricing, stock availability, and descriptions.
    
-   **Order Processing:**  Allows users to submit orders, validating product availability and tracking order status (`Pending`,  `In Progress`,  `Done`).
    
-   **Reporting:**  Generates customizable reports filtered by product name, username, and order date range, providing insights into orders and user activities.
    

The backend uses a relational database (PostgreSQL) with clearly defined tables and relationships:

-   `users`  — stores user information and roles.
    
-   `products`  — contains product specifications and stock details.
    
-   `orders`  — tracks order metadata and statuses.
    
-   `order_products`  — records products and quantities within each order.
    

All communication with the system is done through JSON-formatted messages via secure RESTful endpoints, ensuring ease of integration and use.


## Before use
Before deploying this web service, use global/project wide search for `CHANGE_THIS_BEFORE_USE`. These are comments I've added for on everything that needs to be configured before deploying this web service. This includes changing the Spring configuration from `dev` mode to `prod` mode, configuring the database details, and removing access to the to the H2 DB console endpoint.
