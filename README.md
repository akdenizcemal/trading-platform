# Stock Trading Platform

A Java backend application built with Spring Boot that provides REST APIs for stock trading, user management, and portfolio tracking.

## Tech Stack

- Java
- Spring Boot
- Spring Security and JWT authentication
- Spring Data JPA and Hibernate
- PostgreSQL
- Maven
- JUnit and Spring Boot integration testing
- Docker Compose

## Features

- User registration and authentication
- JWT-protected API endpoints
- Stock creation, retrieval, updating, and deletion
- Buying and selling shares
- Portfolio holdings and current-value tracking
- Request validation and structured error responses
- Integration tests for trading operations and security
- ## API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/users` | Register a new user | Not required |
| POST | `/login` | Authenticate and obtain a JWT | Not required |
| GET | `/stocks` | Retrieve stocks | JWT required |
| POST | `/stocks` | Create a stock | JWT required |
| PUT | `/stocks/{id}` | Update a stock | JWT required |
| DELETE | `/stocks/{id}` | Delete a stock | JWT required |
| GET | `/trades` | Retrieve trades | JWT required |
| POST | `/trades` | Execute a buy or sell trade | JWT required |
| GET | `/holdings` | Retrieve the authenticated user's holdings | JWT required |

Protected endpoints require a JWT access token sent in the `Authorization` header:

`Authorization: Bearer <your-token>`

## Example Requests

### Register a user

`POST /users`

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "your-password"
}
```

### Log in

`POST /login`

Use the username and password to obtain a JWT. Send that token with subsequent requests to protected endpoints.

### Execute a trade

`POST /trades`

```json
{
  "userId": 1,
  "stockId": 1,
  "shares": 5,
  "tradeType": "BUY"
}
```

Use `"SELL"` for a sell order. The trade request uses a user ID, stock ID, share quantity and trade type.

## Testing

Run the integration test suite from the project root:

```bash
mvn clean test
```

Latest verified result: 34 tests passed, with 0 failures and 0 errors.

## Project Structure

- `trading-platform-app/` — Spring Boot application
- `TradingPlatformTest/` — integration tests
- `docker-compose.yml` — Docker Compose configuration
- `pom.xml` — Maven parent project configuration

## Purpose

Built as a backend development project to practise REST API design, authentication, database persistence, validation, and automated testing.
