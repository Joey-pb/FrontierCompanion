# Frontier Companion Backend User Manual

---

## Overview


Frontier Companion Backend is a Spring Boot-based REST API that serves as the backbone for the Frontier Companion ecosystem. It provides AI-powered search, narrative generation, and analytics capabilities using Spring AI and PGVector.

---
## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.5.x
- **AI Integration:** Spring AI (OpenAI/OpenRouter)
- **Database:** PostgreSQL with PGVector
- **Security:** Spring Security (API Key & Basic Auth)
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Build Tool:** Maven

---
## Requirements


- Java 17 or higher
- PostgreSQL with [PGVector extension](https://github.com/pgvector/pgvector) installed
- Maven 3.x (or use the provided `mvnw`)
- Docker (optional, for containerized deployment)

---
## Setup & Installation

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
---
## Running the Application

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

---
## Scripts & Commands

- `./mvnw clean package`: Build the JAR file.
- `./mvnw test`: Run unit and integration tests.
- `./mvnw spring-boot:run`: Run the application locally.

---
## Project Structure


- `src/main/java/.../config`: Configuration classes (Security, OpenAPI, Vector store).
- `src/main/java/.../controller`: REST controllers for Search, Analytics, Narrative, and Articles.
- `src/main/java/.../dto`: Data Transfer Objects for API requests/responses.
- `src/main/java/.../entity`: JPA entities.
- `src/main/java/.../repository`: Spring Data JPA repositories.
- `src/main/java/.../service`: Business logic (AI embeddings, Search logic, Report generation).
- `src/main/java/.../security`: Custom security filters (API Key authentication).
- `src/main/resources/application.properties`: Main configuration file.

---
## Maintenance & Deployment

- **Database Migrations:** Currently uses Hibernate `validate`. Ensure schema matches the entities.
- **Deployment:** The application is Docker-ready and can be deployed to any platform supporting containers (e.g., Render, AWS ECS, Heroku).
- **Security:** Ensure `ANDROID_API_KEY` is kept secret and rotated periodically.

---

---
# API Documentation

This document provides a detailed overview of the REST API endpoints available in the Frontier Companion Backend.

Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

*Note: Accessing Swagger UI requires Admin credentials configured via `SWAGGER_USER` and `SWAGGER_PASSWORD`.*

## Authentication

The API uses two types of authentication:

1.  **API Key Authentication**: Used primarily by the Android application.
    *   **Header**: `X-API-KEY`
    *   **Required for**: All `/api/**` endpoints.
2.  **Basic Authentication**: Used for administrative access (e.g., Swagger UI).
    *   **Required for**: `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`.

Endpoints under `/api/**` require either a valid API key (matching `ANDROID_API_KEY`) or Basic Auth credentials for a user with the `ADMIN` role.

---

## Search APIs

### Semantic Search
Performs a semantic search across articles and narratives.

*   **URL**: `/api/search`
*   **Method**: `GET`
*   **Authentication**: Required (API Key or Basic Auth)
*   **Parameters**:

| Parameter | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `query` | String | Yes | - | The search query text. |
| `limit` | Integer | No | `20` | Maximum number of results to return. |
| `threshold` | Double | No | `0.3` | Similarity threshold for results (0.0 to 1.0). |

*   **Success Response**:
    *   **Code**: `200 OK`
    *   **Body**: `SearchResultDTO`

```json
{
  "articles": [
    {
      "id": 0,
      "title": "string",
      "description": "string",
      "url": "string",
      "thumbnailUrl": "string",
      "author": "string",
      "source": "string",
      "publishedDate": "string"
    }
  ],
  "narratives": [
    {
      "id": 0,
      "exhibitId": 0,
      "title": "string",
      "content": "string",
      "sectionName": "string"
    }
  ],
  "totalResults": 0
}
```

---

## Analytics APIs

### Download Search Analytics PDF
Generates and downloads a PDF report of recent search queries.

*   **URL**: `/api/analytics/search-analytics`
*   **Method**: `GET`
*   **Authentication**: Required (API Key or Basic Auth)
*   **Parameters**:
*
| Parameter | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `limit` | Integer | No | `10` | Number of recent queries to include. |

*   **Success Response**:
    *   **Code**: `200 OK`
    *   **Body**: Binary PDF file.
    *   **Headers**: `Content-Type: application/pdf`, `Content-Disposition: attachment; filename=search-analytics-report.pdf`

### Get Popular Queries
Retrieves a list of the most frequent search terms.

*   **URL**: `/api/analytics/popular`
*   **Method**: `GET`
*   **Authentication**: Required (API Key or Basic Auth)
*   **Parameters**:

| Parameter | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `limit` | Integer | No | `10` | Maximum number of queries to return. |

*   **Success Response**:
    *   **Code**: `200 OK`
    *   **Body**: `List<String>` (e.g., `["query1", "query2"]`)

### Get Recent Queries
Retrieves a list of the most recent search queries with metadata.

*   **URL**: `/api/analytics/recent`
*   **Method**: `GET`
*   **Authentication**: Required (API Key or Basic Auth)
*   **Parameters**:
*
| Parameter | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `limit` | Integer | No | `10` | Maximum number of queries to return. |

*   **Success Response**:
    *   **Code**: `200 OK`
    *   **Body**: `List<MostRecentQueryDTO>`

```json
[
  {
    "queryText": "string",
    "resultCount": 0,
    "clickedArticleId": 0,
    "searchTimestamp": "2026-01-21T01:39:48.801Z"
  }
]
```

---

## Article APIs

### Get All Articles
*   **URL**: `/api/articles`
*   **Method**: `GET`
*   **Success Response**: `200 OK`, `List<ArticleDTO>`

### Get Article by ID
*   **URL**: `/api/articles/{id}`
*   **Method**: `GET`
*   **Success Response**: `200 OK`, `ArticleDTO`

### Get Articles by Exhibit
*   **URL**: `/api/articles/exhibit/{exhibitId}`
*   **Method**: `GET`
*   **Success Response**: `200 OK`, `List<ArticleDTO>`

### Create Article
*   **URL**: `/api/articles`
*   **Method**: `POST`
*   **Body**: `Article` object

```json
{
  "id": 0,
  "title": "string",
  "description": "string",
  "url": "string",
  "thumbnailUrl": "string",
  "author": "string",
  "source": "string",
  "content": "string",
  "publishedDate": "2026-01-23",
  "createdAt": "2026-01-23T22:36:26.828Z",
  "updatedAt": "2026-01-23T22:36:26.828Z",
  "embedding": {
    "type": "string",
    "value": "string",
    "null": true
  },
  "exhibitMappings": [
    {
      "id": 0,
      "exhibitId": 0,
      "article": "string",
      "displayOrder": 0,
      "createdAt": "2026-01-23T22:36:26.828Z"
    }
  ]
}
```
*   **Success Response**: `200 OK`, `Article` (created entity)

### Update Article
*   **URL**: `/api/articles/{id}`
*   **Method**: `PUT`
*   **Body**: `Article` object
*   **Success Response**: `200 OK`, `Article` (updated entity)

---

## Narrative APIs

### Upload Narrative Document
Uploads a text file and processes it into narrative chunks for an exhibit.

*   **URL**: `/api/narratives/upload`
*   **Method**: `POST`
*   **Body**: `multipart/form-data`
    *   `file`: Text file (Required)
    *   `exhibitId`: Long (Required)
    *   `sectionName`: String (Optional)
*   **Success Response**: `200 OK`, `List<Narrative>`

### Get Narratives by Exhibit
*   **URL**: `/api/narratives/exhibit/{exhibitId}`
*   **Method**: `GET`
*   **Success Response**: `200 OK`, `List<Narrative>`

### Soft Delete Narratives for Exhibit
Soft deletes all narratives for a specific exhibit (usually before a re-upload).

*   **URL**: `/api/narratives/exhibit/{exhibitId}`
*   **Method**: `DELETE`
*   **Success Response**: `200 OK`, `String` (status message)

### Restore Narratives for Exhibit
Restores soft-deleted narratives for a specific exhibit.

*   **URL**: `/api/narratives/exhibit/{id}/restore`
*   **Method**: `PATCH`
*   **Success Response**: `200 OK`, `String` (status message)

---

## Data Models

### ArticleDTO
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique identifier. |
| `title` | String | Title of the article. |
| `description` | String | Short summary. |
| `url` | String | Link to full article. |
| `thumbnailUrl` | String | Link to image. |
| `author` | String | Author name. |
| `source` | String | Original source. |
| `publishedDate` | String | Date published (ISO-8601). |

### Narrative
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique identifier. |
| `exhibitId` | Long | Associated exhibit ID. |
| `title` | String | Title of the narrative. |
| `content` | String | Narrative text content. |
| `sectionName` | String | Categorization (e.g., 'Migration'). |
| `deleted` | Boolean | Soft delete status. |


