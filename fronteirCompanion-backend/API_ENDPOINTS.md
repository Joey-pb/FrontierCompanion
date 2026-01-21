# API Endpoints Documentation

This document provides a detailed overview of the REST API endpoints available in the Frontier Companion Backend.

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

### Simple Search
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
      "title": "New Article",
      "description": "Description",
      "url": "https://unique-url.com",
      "thumbnailUrl": "https://img.com",
      "author": "Author",
      "source": "Source",
      "content": "Full article content...",
      "publishedDate": "2023-10-27"
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
