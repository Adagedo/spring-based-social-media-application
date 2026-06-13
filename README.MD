# spring-based-social-media-application


[License: MIT (https://shields.io)](https://opensource.org)

[Spring Boot (https://shields.io)](https://spring.io)

[Spring Security (https://shields.io)](https://spring.io)

A high-performance, secure REST API backend for a modern social media application. Built with Spring Boot, featuring automated secure local file system media storage, custom JWT stateful cookie filters, optimized database indexing, and stateful session cross-origin protection.

---

##  Features

* **Multi-Part Media Uploads** - Handles image streaming directly to a local server folder with localized protection against path traversal attacks.
* **Custom Security Architecture** - Utilizes `jwt` coupled with stateful session tracking (`IF_REQUIRED`) for protected origin transfers.
* **Robust Error Handling** - Intercepts unauthorized anonymous interactions cleanly.
* **JPA Integrity Protection** - Implements custom Lombok mappings to neutralize circular referencing stack overflows.
* **Optimized Queries** - Built-in database indexing tables ensuring high-velocity execution pathways across attributes.

---

## Tech Stack

* **Core Platform:** Java 21, Spring Boot 4.x
* **Security:** Spring Security, JWT (JSON Web Tokens via HTTP-only Cookies)
* **Data Layer:** Spring Data JPA, Hibernate ORM, MySQL / PostgreSQL
* **Data Integrity:** Lombok (Explicit exclusions), Jackson JSON Annotations
* **Build Tool:** Maven

---

## Getting Started

Follow these steps to configure, build, and deploy the application server in your local development environment.

### Prerequisites

Ensure you have the following packages installed:
* **Java Development Kit (JDK):** Version 21 or higher
* **Build Tool:** Maven 3.9+
* **Database:** MySQL 8.x or PostgreSQL 16+

### Installation & Setup

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Adagedo/spring-based-social-media-application.git
   cd spring-based-social-media-application
   ```

2. **Configure Application Properties:**
   Open `src/main/resources/application.properties` and provide your environment specifications:
   ```properties
   # Server Port
   server.port=8888

   # Datasource Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/social_media_db?useSSL=false&allowPublicKeyRetrieval=true
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password

   # File Multi-part Upload Configurations (~30MB threshold support)
   spring.servlet.multipart.max-file-size=30MB
   spring.servlet.multipart.max-request-size=30MB

   # Local Media File Storage Path Configurations
   storage.upload-dir=upload_dir
   ```

3. **Build the Package:**
   ```bash
   mvn clean install
   ```

4. **Boot the Application Server:**
   ```bash
   mvn spring-boot:run
   ```
   The REST API service will automatically spin up and listen on `http://localhost:8888`.

---

### Frontend Integration Note
Because the security filter is structured to validate sessions and credentials from third-party client environments, cross-origin communication scripts (such as Axios or Fetch) **must enable credentials passing explicit configurations**:
```javascript
// Axios Global Setup Example
axios.defaults.withCredentials = true;

// Native Fetch API Structure Example
fetch('http://localhost:8888/api/v1/post', {
    method: 'POST',
    credentials: 'include', // <-- MANDATORY FOR COOKIE ACCESS
    body: postFormData
});
```

---

## Testing

Execute localized automated testing pipelines via:
```bash
mvn test
```
