# SE-4347.501-Database-System-Project

Airline / Airport management system built with Spring Boot and PostgreSQL.
Current version includes Milestone 2 backend features (services, REST, CLI, and tests).

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Docker Compose)

That's it. Java and Maven are not required locally — they run inside Docker during the build.
The build uses **Java 21** (LTS) for portability across local toolchains and CI.

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

| Service  | URL                                          |
|----------|----------------------------------------------|
| API      | http://localhost:8080                        |
| API Docs | http://localhost:8080/swagger-ui/index.html  |
| Health   | http://localhost:8081/actuator/health        |

## Stopping

```
docker-compose down
```

To also delete the database volume:
```
docker-compose down -v
```

## Tests

```
cd backend
./mvnw test
```

Tests use an in-memory H2 database (PostgreSQL compatibility mode) so no Postgres is required.

---

## Milestone 2 — Feature Checklist

| # | Requirement | Endpoint | CLI | Tests |
|---|---|---|---|---|
| 1a | Direct + one-connection itinerary (lookup by city OR 3-letter code) | `GET /api/flights/trip?from=&to=` | `--cli trip <ORIGIN> <DEST>` | `ItineraryServiceTest` |
| 1b | Flight details by flight number | `GET /api/flights/{number}` | `--cli flight <NUMBER>` | `FlightQueryServiceTest` |
| 2  | Aircraft utilization report for date range | `GET /api/reports/aircraft-utilization?start=&end=` | `--cli utilization <START> <END>` | `AircraftUtilizationServiceTest` |
| 3a | Seat availability for flight + date | `GET /api/bookings/availability?flight=&date=` | `--cli availability <NUMBER> <DATE>` | `BookingServiceTest#seatAvailability...` |
| 3b | Passenger itinerary by name or phone | `GET /api/bookings/passenger?name=` (or `?phone=`) | `--cli passenger --name=` (or `--phone=`) | `BookingServiceTest#passenger...` |

## REST examples

```bash
# Flight details
curl http://localhost:8080/api/flights/AA3478

# Itinerary search (airport code)
curl "http://localhost:8080/api/flights/trip?from=DFW&to=SFO"

# Itinerary search (city name)
curl "http://localhost:8080/api/flights/trip?from=Dallas&to=San%20Francisco"

# Seat availability
curl "http://localhost:8080/api/bookings/availability?flight=AA3478&date=2026-05-01"

# Passenger itinerary
curl "http://localhost:8080/api/bookings/passenger?name=Jane%20Smith"
curl "http://localhost:8080/api/bookings/passenger?phone=5551234567"

# Aircraft utilization report
curl "http://localhost:8080/api/reports/aircraft-utilization?start=2026-05-01&end=2026-05-31"
```

Sample response — `GET /api/flights/AA3478`:

```json
{
  "number": "AA3478",
  "airline": "AA",
  "weekdays": "MTWHFSU",
  "legs": [
    { "legNo": 1,
      "depAirport": { "airportCode": "DFW", "name": "...", "city": "Dallas", "state": "TX" },
      "arrAirport": { "airportCode": "SFO", "name": "...", "city": "San Francisco", "state": "CA" },
      "scheduledDepTime": "08:00:00",
      "scheduledArrTime": "10:30:00" }
  ],
  "fares": [
    { "code": "Y", "amount": 250.00, "restriction": null },
    { "code": "F", "amount": 550.00, "restriction": "First class" }
  ]
}
```

## CLI mode

The same logic is reachable from the command line via the `--cli` flag — the embedded
web server still starts, so you can hit either interface in the same process:

```bash
# Inside the running container (or via java -jar app.jar)
java -jar app.jar --cli flight AA3478
java -jar app.jar --cli trip DFW SFO
java -jar app.jar --cli availability AA3478 2026-05-01
java -jar app.jar --cli passenger --name="Jane Smith"
java -jar app.jar --cli passenger --phone=5551234567
java -jar app.jar --cli utilization 2026-05-01 2026-05-31
```

---

## Schema assumptions (documented)

To avoid ambiguous leg joins when different flights reuse the same leg number, this implementation
stores flight number alongside leg number in runtime identifiers:

- `FLIGHT_LEG` PK is `(NUMBER, LEG_NO)`
- `LEG_INSTANCE` identity is treated as `(DATE, NUMBER, LEG_NO)`
- `SEAT` identity is treated as `(SEAT_NO, DATE, NUMBER, LEG_NO)`

That keeps seat availability and passenger itinerary queries unambiguous even when `LEG_NO` is
not globally unique.

## Indexing notes

For production-scale data, the following columns are query-heavy and would benefit from indexes
(PKs are already indexed; these are the additional ones):

- `FLIGHT_LEG (DEP_AIRPORT_CODE)`, `FLIGHT_LEG (ARR_AIRPORT_CODE)` — itinerary searches
- `SEAT (CUSTOMER_NAME)`, `SEAT (CPHONE)` — passenger lookups
- `LEG_INSTANCE (AIRPLANE_ID)` — utilization report
- `AIRPORT (CITY)` — case-insensitive city resolution

## Project layout (backend)

```
backend/src/main/java/com/se4347/database_system_project/
├── DatabaseSystemProjectApplication.java   # Spring Boot entry point
├── api/
│   ├── dto/                                # Java records (response shapes)
│   └── rest/                               # REST controllers + ControllerAdvice
├── cli/                                    # ApplicationRunner for CLI mode
├── dao/jpa/                                # Spring Data repositories (queries)
├── domain/                                 # JPA entities (unchanged)
├── exception/                              # NotFoundException, InvalidInputException
└── service/                                # Business logic (read-only @Transactional)
```
