# SE-4347.501-Database-System-Project

Airline / Airport management system built with Spring Boot and PostgreSQL.

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Docker Compose)

That's it. Java and Maven are not required locally — they run inside Docker during the build.

## Setup

1. Clone the repository
2. Copy the example env file and fill in your values:
   ```
   cp .env.example .env
   ```

## Running

```
docker-compose up --build
```

- First run downloads dependencies and compiles the project inside Docker — this takes a few minutes.
- Subsequent runs are fast due to Docker layer caching.
- The app is ready when you see `Started DatabaseSystemProjectApplication` in the logs.

| Service  | URL                          |
|----------|------------------------------|
| API      | http://localhost:8080        |
| API Docs | http://localhost:8080/swagger-ui/index.html |
| Health   | http://localhost:8081/actuator/health       |

## Stopping

```
docker-compose down
```

To also delete the database volume:
```
docker-compose down -v
```
