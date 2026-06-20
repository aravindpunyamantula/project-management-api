# Project Management API

A robust, containerized RESTful API for project and task management built with **Spring Boot 3**, **PostgreSQL**, **JWT Authentication**, and **Docker**.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.5 | Application framework |
| Spring Security | Authentication & Authorization |
| Spring Data JPA (Hibernate) | ORM & Data access |
| PostgreSQL 16 | Relational database |
| JWT (jjwt 0.12) | Token-based authentication |
| Docker & Docker Compose | Containerization |
| Testcontainers | Integration testing |
| Maven | Build tool |
| Lombok | Boilerplate reduction |

## Architecture

The application follows a **layered architecture** with the **Repository Pattern**:

```
Controller → Service → Repository → Database
```

- **Controllers**: Handle HTTP requests, delegate to services. No direct DB access.
- **Services**: Business logic, ownership verification, data transformation.
- **Repositories**: Data access layer via Spring Data JPA interfaces.
- **DTOs**: Separate request/response objects from entities.
- **Security**: JWT filter chain with stateless session management.

## Quick Start

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) & [Docker Compose](https://docs.docker.com/compose/install/) installed

### 1. Clone the repository

```bash
git clone https://github.com/aravindpunyamantula/project-management-api
cd project-management-api
```

### 2. Configure environment (optional)

```bash
cp .env.example .env
# Edit .env with your preferred values (defaults work out of the box)
```

### 3. Start the application

```bash
docker-compose up --build
```

This single command will:
- Build the API Docker image (multi-stage Maven build)
- Start the PostgreSQL 16 container with persistent volume
- Wait for PostgreSQL to be healthy (via healthcheck)
- Start the API container connected to PostgreSQL
- Automatically provision all database tables on startup

The API will be available at: **http://localhost:8080**

### 4. Stop the application

```bash
docker-compose down
# To also remove the database volume:
docker-compose down -v
```

## Test User Credentials

You can register any user via the API. Here's a quick start:

```bash
# Register a test user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'

# Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
```

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and get JWT | No |

### User Endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/users/me` | Get current user profile | Yes |

### Project Endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/projects` | Create a project | Yes |
| GET | `/api/projects` | List user's projects | Yes |
| GET | `/api/projects/{id}` | Get a project | Yes |
| PUT | `/api/projects/{id}` | Update a project | Yes |
| DELETE | `/api/projects/{id}` | Delete a project (cascades tasks) | Yes |

### Task Endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/projects/{projectId}/tasks` | Create a task | Yes |
| GET | `/api/projects/{projectId}/tasks` | List project's tasks | Yes |
| GET | `/api/tasks/{id}` | Get a task | Yes |
| PUT | `/api/tasks/{id}` | Update a task | Yes |
| DELETE | `/api/tasks/{id}` | Delete a task | Yes |

### Request/Response Examples

#### Register
```bash
# Request
POST /api/auth/register
{
  "email": "user@example.com",
  "password": "securepass123"
}

# Response: 201 Created
{
  "id": 1,
  "email": "user@example.com",
  "message": "User registered successfully"
}
```

#### Login
```bash
# Request
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "securepass123"
}

# Response: 200 OK
{
  "access_token": "eyJhbGciOiJIUzM4NCJ9...",
  "token_type": "bearer"
}
```

#### Create Project
```bash
# Request
POST /api/projects
Authorization: Bearer <token>
{
  "name": "My Project",
  "description": "Project description"
}

# Response: 201 Created
{
  "id": 1,
  "name": "My Project",
  "description": "Project description",
  "owner_id": 1,
  "created_at": "2026-06-20T12:00:00"
}
```

#### Create Task
```bash
# Request
POST /api/projects/1/tasks
Authorization: Bearer <token>
{
  "title": "Implement feature",
  "description": "Build the login page",
  "status": "TODO"
}

# Response: 201 Created
{
  "id": 1,
  "title": "Implement feature",
  "description": "Build the login page",
  "status": "TODO",
  "project_id": 1,
  "created_at": "2026-06-20T12:00:00"
}
```

### Task Status Values

| Status | Description |
|---|---|
| `TODO` | Task not started |
| `IN_PROGRESS` | Task in progress |
| `DONE` | Task completed |

### Error Responses

All errors follow a consistent JSON format:

```json
{
  "timestamp": "2026-06-20T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "email": "must not be blank",
    "password": "size must be between 6 and 2147483647"
  }
}
```

| Status Code | Meaning |
|---|---|
| 400 | Validation error / Bad request |
| 401 | Missing or invalid JWT token |
| 403 | Ownership verification failed |
| 404 | Resource not found |
| 409 | Duplicate resource (e.g., email) |
| 500 | Internal server error |

## Running Tests

Tests use **Testcontainers** with a real PostgreSQL instance. Requires Docker running.

```bash
# Run all integration tests
./mvnw test
```

### Test Coverage

- **Auth Tests**: Register, duplicate email, missing fields, login, invalid credentials
- **User Tests**: Profile retrieval, unauthorized access
- **Project Tests**: CRUD operations, ownership filtering, forbidden access, cascade delete
- **Task Tests**: CRUD operations, project-scoped access, ownership via parent project

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `project_management_db` | Database name |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | (built-in default) | JWT signing secret (min 32 chars) |
| `JWT_EXPIRATION_MS` | `86400000` | Token expiration (24h default) |
| `SERVER_PORT` | `8080` | API server port |

## Database Schema

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    users     │     │   projects   │     │    tasks     │
├──────────────┤     ├──────────────┤     ├──────────────┤
│ id (PK)      │◄───┐│ id (PK)      │◄───┐│ id (PK)      │
│ email        │    ││ name         │    ││ title        │
│ password     │    ││ description  │    ││ description  │
│ created_at   │    ││ created_at   │    ││ status       │
└──────────────┘    ││ owner_id(FK) │────┘│ created_at   │
                    │└──────────────┘     │ project_id(FK)│───┘
                    │                     └──────────────┘
                    └─────────────────────────────────────┘
```

- **User → Projects**: One-to-Many (cascade delete)
- **Project → Tasks**: One-to-Many (cascade delete)

## Project Structure

```
src/
├── main/java/com/aravind/projectmanagementapi/
│   ├── config/              # Security configuration
│   ├── controller/          # REST controllers
│   ├── dto/                 # Request/Response DTOs
│   │   ├── auth/
│   │   ├── project/
│   │   ├── task/
│   │   └── user/
│   ├── entity/              # JPA entities
│   │   └── enums/
│   ├── exception/           # Custom exceptions & global handler
│   ├── respository/         # Spring Data JPA repositories
│   ├── security/            # JWT service, filter, UserDetails
│   └── service/             # Business logic layer
│       └── imp/
├── main/resources/
│   └── application.yaml
└── test/java/               # Integration tests
    └── com/aravind/projectmanagementapi/
        ├── BaseIntegrationTest.java
        ├── AuthIntegrationTest.java
        ├── UserIntegrationTest.java
        ├── ProjectIntegrationTest.java
        └── TaskIntegrationTest.java
```
