# Frontier Companion Backend

---

## Overview

---
Frontier Companion Backend is a Spring Boot-based REST API that serves as the backbone for the Frontier Companion ecosystem. It provides AI-powered search, narrative generation, and analytics capabilities using Spring AI and PGVector.

## Tech Stack

---
- **Language:** Java 17
- **Framework:** Spring Boot 3.5.x
- **AI Integration:** Spring AI (OpenAI/OpenRouter)
- **Database:** PostgreSQL with PGVector
- **Security:** Spring Security (API Key & Basic Auth)
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Build Tool:** Maven

## Requirements

---
- Java 17 or higher
- PostgreSQL with [PGVector extension](https://github.com/pgvector/pgvector) installed
- Maven 3.x (or use the provided `mvnw`)
- Docker (optional, for containerized deployment)

## Setup & Installation

---
### 1. Database Setup
Ensure you have a PostgreSQL instance running with the pgvector extension enabled.
```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

### 2. Environment Variables
The application requires several environment variables to function correctly. You can set these in your OS or create an `.env` file (if using a tool that supports it) or pass them via command line.

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL (e.g., `jdbc:postgresql://localhost:5432/frontier`) | *Required* |
| `DATABASE_USER` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `OPEN_AI_KEY` | OpenAI/OpenRouter API Key | *Required* |
| `ANDROID_API_KEY` | API Key for Android client authentication | `MISSING_ANDROID_KEY` |
| `SWAGGER_USER` | Admin username for Swagger UI access | `MISSING_SWAGGER_USERNAME` |
| `SWAGGER_PASSWORD` | Admin password for Swagger UI access | `MISSING_SWAGGER_PASSWORD` |
| `PORT` | Server port | `8080` |

### 3. Build the Application
```bash
./mvnw clean install
```

## Running the Application

---
### Local Development
```bash
./mvnw spring-boot:run
```

### Using Docker
To build and run the application using Docker:
```bash
# Build the image
docker build -t frontier-companion-backend .

# Run the container
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/frontier \
  -e OPEN_AI_KEY=your_key \
  -e ANDROID_API_KEY=your_android_key \
  -e SWAGGER_USER=admin \
  -e SWAGGER_PASSWORD=password \
  frontier-companion-backend
```

## Scripts & Commands

---
- `./mvnw clean package`: Build the JAR file.
- `./mvnw test`: Run unit and integration tests.
- `./mvnw spring-boot:run`: Run the application locally.

## Project Structure

---
- `src/main/java/.../config`: Configuration classes (Security, OpenAPI, Vector store).
- `src/main/java/.../controller`: REST controllers for Search, Analytics, Narrative, and Articles.
- `src/main/java/.../dto`: Data Transfer Objects for API requests/responses.
- `src/main/java/.../entity`: JPA entities.
- `src/main/java/.../repository`: Spring Data JPA repositories.
- `src/main/java/.../service`: Business logic (AI embeddings, Search logic, Report generation).
- `src/main/java/.../security`: Custom security filters (API Key authentication).
- `src/main/resources/application.properties`: Main configuration file.

## API Documentation

---
Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

*Note: Accessing Swagger UI requires Admin credentials configured via `SWAGGER_USER` and `SWAGGER_PASSWORD`.*

## Maintenance & Deployment

---
- **Database Migrations:** Currently uses Hibernate `validate`. Ensure schema matches the entities.
- **Deployment:** The application is Docker-ready and can be deployed to any platform supporting containers (e.g., Render, AWS ECS, Heroku).
- **Security:** Ensure `ANDROID_API_KEY` is kept secret and rotated periodically.

