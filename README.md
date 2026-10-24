# Task Management Application

A Spring Boot web application for planning a day: create activities, add them to a time-ordered to-do list, track their status and progress, and see the free time between them as automatically inserted breaks.

## Features

- **Activity CRUD** - create, edit, delete and search activities (name, description, start time, duration in minutes) from a Thymeleaf UI or a REST API.
- **To-do schedule** - add activities to a to-do list sorted by start time; breaks are inserted wherever there is a gap between two activities.
- **Overlap detection** - an activity cannot be added to the to-do list if its time interval overlaps one already in the list.
- **Status workflow** - each to-do activity is *Not Started*, *In Progress* or *Completed*; an activity can only be started or completed once all earlier activities are completed.
- **Progress tracking** - per-activity progress (0-100%) and an overall progress bar weighted by duration.
- **Validation** - required fields, `HH:MM` start time, numeric duration between 1 and 600 minutes, and rejection of duplicate activities, with errors shown in a modal.
- **Confirmation overlays** for delete, add/remove from the to-do list and "remove all".

## Tech Stack

- Java 17, Spring Boot 3.4 (Spring MVC, Spring Data JPA / Hibernate)
- Thymeleaf, HTML/CSS
- MySQL (H2 is also on the runtime classpath)
- JUnit 5 / Spring Boot Test
- Maven, packaged as a WAR (runnable standalone or deployable to Tomcat)

## Architecture

Classic layered MVC:

`ActivityController` (Thymeleaf pages) / `ActivityRestController` (JSON) -> `ActivityService` (validation, scheduling, progress) -> `ActivityRepository` (Spring Data JPA) -> MySQL.

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.9+
- A running MySQL server with a database named `BACKEND` (or any name, set through `DB_URL`)

### Configuration

Database settings are read from environment variables (see [`.env.example`](.env.example)):

| Variable      | Default                                           | Description    |
|---------------|---------------------------------------------------|----------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/BACKEND?useSSL=false` | JDBC URL       |
| `DB_USERNAME` | `root`                                            | MySQL user     |
| `DB_PASSWORD` | *(none, required)*                                | MySQL password |

The schema is created/updated automatically by Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

### Run

```bash
export DB_PASSWORD=your-password   # plus DB_URL / DB_USERNAME if they differ from the defaults
mvn spring-boot:run
```

Then open http://localhost:8080.

### Build and test

```bash
mvn clean package    # builds target/Backend-0.0.1-SNAPSHOT.war
mvn test             # @SpringBootTest: needs the configured database to be reachable
```

The tests use `@SpringBootTest`, so they need a reachable database. To run them without MySQL, point the app at the in-memory H2 database that is already on the classpath:

```bash
DB_URL=jdbc:h2:mem:testdb DB_USERNAME=sa DB_PASSWORD= mvn test
```

## REST API

Base path: `/api/activities`

| Method   | Path                   | Description                                  |
|----------|------------------------|----------------------------------------------|
| `GET`    | `/api/activities`      | List all activities                          |
| `GET`    | `/api/activities/{id}` | Get one activity (404 if not found)          |
| `POST`   | `/api/activities`      | Create an activity (201 Created)             |
| `PUT`    | `/api/activities/{id}` | Update an activity (404 if not found)        |
| `DELETE` | `/api/activities/{id}` | Delete an activity (204, or 404 if not found)|

Example:

```bash
curl -X POST http://localhost:8080/api/activities \
  -H "Content-Type: application/json" \
  -d '{"name":"Run","description":"Morning run","startTime":"08:00","duration":"30"}'
```

## Project Structure

```
src/
├── main/
│   ├── java/com/backend/
│   │   ├── BackendApplication.java      # Spring Boot entry point
│   │   ├── ServletInitializer.java      # WAR deployment support
│   │   ├── controller/                  # MVC controller (UI) and REST controller
│   │   ├── dto/TodoItemDTO.java         # to-do row: activity or break
│   │   ├── entity/Activity.java         # JPA entity
│   │   ├── enums/                       # ActivityStatus + JPA converter
│   │   ├── repository/                  # Spring Data JPA repository
│   │   └── service/ActivityService.java # business logic
│   └── resources/
│       ├── application.properties
│       ├── templates/index.html         # Thymeleaf UI
│       └── static/styles.css
└── test/java/com/backend/BackendApplicationTests.java
```

## Author

George-Armand Ursei - [GitHub](https://github.com/armandursei)
