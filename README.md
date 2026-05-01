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

   macOS / Linux:
   ```bash
   cp .env.example .env
   ```
   Windows (Command Prompt):
   ```cmd
   copy .env.example .env
   ```
   Windows (PowerShell):
   ```powershell
   Copy-Item .env.example .env
   ```

## Running

```bash
docker-compose run --rm --service-ports backend
```

- This starts the database automatically, waits for it to be healthy, then launches the backend.
- First run compiles the project inside Docker — this takes a few minutes. Add `--build` to force a rebuild after code changes.
- On first startup the app seeds sample data automatically (4 airports, 4 flights, leg instances, and a passenger).
- The interactive CLI prompt appears after startup and data seeding complete.

The REST API is available while the CLI is running:

| Service  | URL                                         |
|----------|---------------------------------------------|
| API Docs | http://localhost:8080/swagger-ui/index.html |
| Health   | http://localhost:8081/actuator/health       |

## Interactive CLI

The prompt appears automatically on startup:

```
Airline DB — Interactive CLI
Type  help;  for available commands.  Type  exit;  to quit.

prompt>
```

Type commands in `function(args);` style:

```
prompt> trip("DFW", "SFO");
prompt> flight("AA3478");
prompt> availability("AA3478", "2026-05-01");
prompt> passenger(name="Jane Smith");
prompt> passenger(phone="5551234567");
prompt> utilization("2026-05-01", "2026-05-31");
prompt> help;
prompt> exit;
```

## Sample Data

The following data is seeded automatically on first startup.

**Airports**

| Code | Name                    | City          | State |
|------|-------------------------|---------------|-------|
| DFW  | Dallas/Fort Worth Intl  | Dallas        | TX    |
| SFO  | San Francisco Intl      | San Francisco | CA    |
| ATL  | Hartsfield-Jackson      | Atlanta       | GA    |
| JFK  | John F. Kennedy Intl    | New York      | NY    |

**Airplane Type**

| Type | Company | Max Seats |
|------|---------|-----------|
| B737 | Boeing  | 180       |

**Airplanes**

| Registration | Seats | Type |
|--------------|-------|------|
| N101AA       | 150   | B737 |
| N102AA       | 160   | B737 |
| N103AA       | 170   | B737 — idle, no flights assigned |

**Flights & Legs**

| Flight | Airline | Weekdays | Leg | From | To  | Dep   | Arr   |
|--------|---------|----------|-----|------|-----|-------|-------|
| AA3478 | AA      | Daily    | 1   | DFW  | SFO | 08:00 | 10:30 |
| AA1000 | AA      | Daily    | 2   | DFW  | ATL | 09:00 | 12:00 |
| AA2000 | AA      | Daily    | 3   | ATL  | SFO | 13:00 | 16:00 |
| UA9999 | UA      | Mon–Fri  | 4   | SFO  | JFK | 07:00 | 15:00 |

AA1000 (leg 2) + AA2000 (leg 3) form a one-stop DFW → ATL → SFO connection, discoverable via `trip("DFW", "SFO");`.

**Fares**

| Flight | Code | Amount  | Restriction |
|--------|------|---------|-------------|
| AA3478 | Y    | $250.00 |             |
| AA3478 | F    | $550.00 | First class |

**Scheduled Instances (2026-05-01)**

| Flight | Leg | Date       | Airplane |
|--------|-----|------------|----------|
| AA3478 | 1   | 2026-05-01 | N101AA   |
| AA1000 | 2   | 2026-05-01 | N102AA   |

**Passengers**

| Seat | Flight | Leg | Date       | Name       | Phone      |
|------|--------|-----|------------|------------|------------|
| 12A  | AA3478 | 1   | 2026-05-01 | Jane Smith | 5551234567 |

## Stopping

| Situation | Command |
|-----------|---------|
| Exit cleanly | `exit;` |
| Force stop / frozen CLI | `Ctrl+C` |
| Full reset (wipes seeded data) | `docker-compose down -v` |

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
| 1a | Direct + one-connection itinerary (lookup by city OR 3-letter code) | `GET /api/flights/trip?from=<origin>&to=<destination>` | `trip("DFW", "SFO");` | `ItineraryServiceTest` |
| 1b | Flight details by flight number | `GET /api/flights/<number>` | `flight("AA3478");` | `FlightQueryServiceTest` |
| 2  | Aircraft utilization report for date range | `GET /api/reports/aircraft-utilization?start=<date>&end=<date>` | `utilization("2026-05-01", "2026-05-31");` | `AircraftUtilizationServiceTest` |
| 3a | Seat availability for flight + date | `GET /api/bookings/availability?flight=<number>&date=<date>` | `availability("AA3478", "2026-05-01");` | `BookingServiceTest#seatAvailability...` |
| 3b | Passenger itinerary by name or phone | `GET /api/bookings/passenger?name=<name>` (or `?phone=<phone>`) | `passenger(name="Jane Smith");` | `BookingServiceTest#passenger...` |

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

---

## Schema assumptions

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
