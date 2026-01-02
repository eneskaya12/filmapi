# Film API

A RESTful API for managing movies, categories, and user interactions built with Spring Boot. Users can browse movies, mark favorites, track watched films, and manage their personal movie lists.

> A case study project for learning Java Spring Boot fundamentals.


## Features

* Secure authentication with JWT
* Role-based authorization (USER, ADMIN)
* Browse, search, and filter movies
* Add movies to favorites and mark as watched
* Category-based movie organization
* User profile management
* Admin panel for user and content management
* Pagination support for large datasets
* API documentation with Swagger/OpenAPI
* Docker and Docker Compose support


## Technology Stack

* Java 21
* Maven
* Spring Boot 3.5
* PostgreSQL
* Spring Security (JWT)
* Spring Data JPA (Hibernate)
* Swagger / OpenAPI
* Docker, Docker Compose
* Lombok
* MapStruct


## Getting Started

### Prerequisites

* Java 21+
* Maven 3.9+
* PostgreSQL 15+

### Local Setup

1. Clone the repository:

```bash
git clone https://github.com/eneskaya12/filmapi.git
cd filmapi
```

2. Create a PostgreSQL database:

```sql
CREATE DATABASE filmapi;
```

3. Configure your database credentials in `src/main/resources/application.yml` or set environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/filmapi
export DATABASE_USERNAME=your_username
export DATABASE_PASSWORD=your_password
```

4. Run the application:

```bash
./mvnw spring-boot:run
```

---

### Docker Setup

1. Copy the environment template and configure:

```bash
cp .env.example .env
# Edit .env with your preferred settings
```

2. Start the application:

```bash
docker-compose up --build
```

3. Access the API:

```
http://localhost:8080
```

4. Stop the containers:

```bash
docker-compose down
```


## API Documentation

Access Swagger UI to explore and test the API:

```
http://localhost:8080/swagger-ui.html
```


## Authentication

Handles user registration and login. Upon successful authentication, a JWT token is returned and must be included in the `Authorization` header for protected endpoints.

| Method | Endpoint             | Description     |
|--------|----------------------|-----------------|
| POST   | `/api/auth/register` | Register a user |
| POST   | `/api/auth/login`    | Login           |


## User Profile

Allows authenticated users to manage their own profile.

| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| GET    | `/api/users/profile` | Get own profile     |
| PATCH  | `/api/users/profile` | Update own profile  |


## User Management (Admin)

Administrative endpoints for managing users.

| Method | Endpoint           | Description         |
|--------|--------------------|---------------------|
| GET    | `/api/users`       | Get all users       |
| GET    | `/api/users/{id}`  | Get user by ID      |
| PATCH  | `/api/users/{id}`  | Update user         |
| DELETE | `/api/users/{id}`  | Delete user         |


## Movie Management

CRUD operations for movies. Public read access, admin-only write operations.

| Method | Endpoint            | Description        | Access |
|--------|---------------------|--------------------|--------|
| GET    | `/api/movies`       | Get all movies     | Public |
| GET    | `/api/movies/{id}`  | Get movie by ID    | Public |
| POST   | `/api/movies`       | Add a new movie    | Admin  |
| PATCH  | `/api/movies/{id}`  | Update a movie     | Admin  |
| DELETE | `/api/movies/{id}`  | Delete a movie     | Admin  |


## Category Management

CRUD operations for categories. Public read access, admin-only write operations.

| Method | Endpoint                | Description          | Access |
|--------|-------------------------|----------------------|--------|
| GET    | `/api/categories`       | Get all categories   | Public |
| GET    | `/api/categories/{id}`  | Get category by ID   | Public |
| POST   | `/api/categories`       | Add a new category   | Admin  |
| PATCH  | `/api/categories/{id}`  | Update a category    | Admin  |
| DELETE | `/api/categories/{id}`  | Delete a category    | Admin  |


## Movie-Category Relations

Manage associations between movies and categories.

| Method | Endpoint                                        | Description                    | Access |
|--------|-------------------------------------------------|--------------------------------|--------|
| GET    | `/api/movies/{movieId}/categories`              | Get categories of a movie      | Public |
| GET    | `/api/categories/{categoryId}/movies`           | Get movies in a category       | Public |
| POST   | `/api/movies/{movieId}/categories/{categoryId}` | Add movie to category          | Admin  |
| DELETE | `/api/movies/{movieId}/categories/{categoryId}` | Remove movie from category     | Admin  |


## User Movie Status

Allows users to track their movie interactions (favorites, watched).

| Method | Endpoint                                  | Description                |
|--------|-------------------------------------------|----------------------------|
| GET    | `/api/users/profile/movies`               | Get all movies with status |
| GET    | `/api/users/profile/movies/favorites`     | Get favorite movies        |
| GET    | `/api/users/profile/movies/watched`       | Get watched movies         |
| GET    | `/api/users/profile/movies/{movieId}/status`  | Get status of a movie  |
| PUT    | `/api/users/profile/movies/{movieId}/status`  | Update movie status    |


## Environment Variables

| Variable            | Description                  | Default                                      |
|---------------------|------------------------------|----------------------------------------------|
| `DATABASE_URL`      | PostgreSQL connection URL    | `jdbc:postgresql://localhost:5432/filmapi`   |
| `DATABASE_USERNAME` | Database username            | `filmapi`                                    |
| `DATABASE_PASSWORD` | Database password            | -                                            |
| `JWT_SECRET_KEY`    | Secret key for JWT signing   | -                                            |
| `JWT_EXPIRATION`    | Token expiration time (ms)   | `3600000` (1 hour)                           |

