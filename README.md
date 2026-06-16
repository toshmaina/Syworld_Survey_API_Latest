# simple-survey-api

A RESTful API for the Sky World Survey Platform. Enables administrators to create and manage surveys with dynamic question types, and allows users to submit responses including file uploads. All API responses are served in XML format.

---

## Table of Contents

- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Environment Setup](#environment-setup)
- [Database Setup](#database-setup)
  - [ERD Diagram](#erd-diagram)
  - [Running with Docker](#running-with-docker)
  - [Connecting to the Database](#connecting-to-the-database)
  - [Resetting the Database](#resetting-the-database)
- [Running the API Locally](#running-the-api-locally)
- [Authentication](#authentication)
- [API Reference](#api-reference)
  - [Auth](#auth)
  - [Surveys](#surveys)
  - [Questions](#questions)
  - [Responses](#responses)
  - [Certificates](#certificates)
- [Postman Collection](#postman-collection)
- [Assumptions Made](#assumptions-made)
- [Deployment](#deployment)

---

## Technologies Used

| Layer | Technology |
|---|---|
| Language | Java 17 (LTS) |
| Framework | Spring Boot 3.3 |
| ORM | Spring Data JPA + Hibernate 6 |
| Database | MySQL 8.0 |
| Security | Spring Security 6 + JWT (JJWT 0.11.5) |
| XML Serialization | Jackson Dataformat XML + DOM API |
| File Uploads | Spring Multipart (built-in) |
| Build Tool | Maven 3.8+ |
| Containerization | Docker + Docker Compose |
| API Documentation | Postman |

---

## Prerequisites

Ensure the following are installed on your machine before proceeding:

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17 (LTS) | https://www.azul.com/downloads/ |
| Maven | 3.8 or higher | https://maven.apache.org/download.cgi |
| Docker Desktop | Latest stable | https://www.docker.com/products/docker-desktop |
| Git | Any recent version | https://git-scm.com |
| Postman | Latest stable | https://www.postman.com/downloads |

Verify your installations:

```bash
java -version     # should print openjdk version "17.x.x"
mvn -version      # should print Apache Maven 3.x.x
docker --version  # should print Docker version 24.x.x or higher
git --version     # should print git version 2.x.x or higher
```

---

## Project Structure

```
simple-survey-api/
├── docker-compose.yml                    # MySQL 8 container definition
├── .env                                  # Local environment variables (never committed)
├── .env.example                          # Template — copy this to .env
├── .gitignore
├── sky_survey_db.sql                     # Full database schema + seed data
├── pom.xml                               # Maven build + dependency config
├── docs/
│   └── erd.png                           # Entity Relationship Diagram
├── postman/
│   └── simple-survey-api.json            # Postman collection
├── uploads/                              # Uploaded PDF files (gitignored)
└── src/
    └── main/
        ├── java/com/skyworld/survey/
        │   ├── SurveyApplication.java    # Spring Boot entry point
        │   ├── config/
        │   │   └── WebConfig.java        # CORS configuration
        │   ├── controller/               # REST controllers (XML in/out)
        │   │   ├── AuthController.java
        │   │   ├── SurveyController.java
        │   │   ├── QuestionController.java
        │   │   ├── ResponseController.java
        │   │   └── CertificateController.java
        │   ├── dto/                      # Request/Response DTOs (JAXB annotated)
        │   ├── entity/                   # JPA entities (map to DB tables)
        │   ├── exception/                # Global error handler + custom exceptions
        │   ├── repository/               # Spring Data JPA interfaces
        │   ├── security/                 # JWT filter + Spring Security config
        │   │   ├── JwtUtil.java
        │   │   ├── JwtAuthFilter.java
        │   │   └── SecurityConfig.java
        │   ├── service/                  # Business logic layer
        │   └── util/                     # File storage + XML serializer
        └── resources/
            ├── application.properties    # All configuration
            └── db/                       # SQL migration scripts
```

---

## Environment Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-username/simple-survey-api.git
cd simple-survey-api
```

### 2. Create your environment file

```bash
cp .env.example .env
```

Open `.env` and set your credentials:

```env
DB_ROOT_PASSWORD=your_root_password
DB_NAME=sky_survey_db
DB_USER=your_db_user
DB_PASSWORD=your_db_password
DB_PORT=3306
DB_HOST=localhost
JWT_SECRET=your_jwt_secret_min_32_characters_long
```

> **Important:** Never commit the `.env` file. It is already listed in `.gitignore`.

---

## Database Setup

### ERD Diagram

The full Entity Relationship Diagram is located at `docs/erd.png`.

The database consists of 7 tables:

| Table | Purpose |
|---|---|
| `surveys` | Top-level survey records |
| `questions` | Questions belonging to a survey |
| `question_options` | Choice options for single/multiple choice questions |
| `question_file_properties` | Upload constraints for file-type questions |
| `responses` | One record per survey submission |
| `response_answers` | Text/choice answers per question per response |
| `certificates` | Uploaded PDF files linked to a response |

### Running with Docker

The database runs inside a Docker container. Docker Compose handles everything — no local MySQL installation is required.

#### Start the database

```bash
docker compose up -d
```

This command will:

1. Pull the `mysql:8.0` image if not already cached locally.
2. Create a named volume `sky_survey_data` for persistent storage.
3. Mount `sky_survey_db.sql` into the container's init directory — MySQL runs this script automatically on the very first startup, creating all tables and inserting seed data.
4. Expose MySQL on the port defined in `.env` (default: `3306`).

#### Verify the container is healthy

```bash
docker compose ps
```

The `STATUS` column should show `healthy`. This may take 20–30 seconds on first run while MySQL initialises.

Watch the startup logs:

```bash
docker compose logs -f db
```

Wait until you see:

```
[System] [MY-010931] [Server] /usr/sbin/mysqld: ready for connections.
```

### Connecting to the Database

#### Option A — MySQL shell inside the container

```bash
docker exec -i sky_survey_db mysql -u your_db_user -pyour_db_password sky_survey_db
```

Run a quick sanity check:

```sql
SHOW TABLES;
SELECT id, name FROM surveys;
EXIT;
```

#### Option B — GUI client (TablePlus, DBeaver, MySQL Workbench)

| Field | Value |
|---|---|
| Host | `127.0.0.1` |
| Port | `3306` |
| Database | `sky_survey_db` |
| Username | `your_db_user` |
| Password | `your_db_password` |

### Stopping the Container

```bash
docker compose down
```

### Resetting the Database

To wipe all data and re-run the SQL script from scratch:

```bash
docker compose down -v     # removes the named volume
docker compose up -d       # recreates and re-seeds the database
```

---

## Running the API Locally

### 1. Ensure the database container is running

```bash
docker compose up -d
docker compose ps   # confirm STATUS is healthy
```

### 2. Load environment variables into your shell

Maven does not read `.env` files automatically. Export the variables first:

```bash
export $(cat .env | grep -v '^#' | xargs)
```

Verify:

```bash
echo $DB_USER    # should print your db username
echo $DB_NAME    # should print sky_survey_db
```

### 3. Build and run

```bash
mvn spring-boot:run
```

Or build a JAR and run it:

```bash
mvn clean package -DskipTests
java -jar target/simple-survey-api-1.0.0.jar
```

The API will be available at `http://localhost:8080`.

### application.properties key settings

| Property | Value | Description |
|---|---|---|
| `server.port` | `8080` | Embedded Tomcat port |
| `spring.jpa.hibernate.ddl-auto` | `validate` | Validates schema against entities on startup |
| `spring.servlet.multipart.max-file-size` | `1MB` | Max PDF upload size |
| `app.upload.dir` | `uploads` | Directory for uploaded PDFs |
| `app.jwt.expiration-ms` | `86400000` | Token expiry — 24 hours |

---

## Authentication

The API uses **JWT (JSON Web Token)** authentication via Spring Security.

### How it works

1. Call `POST /api/auth/login` with your credentials — receive a JWT token.
2. Include the token in every subsequent request as `Authorization: Bearer <token>`.
3. The `JwtAuthFilter` intercepts every request, validates the token, and sets the security context.

### Roles

| Role | Permissions |
|---|---|
| `ADMIN` | Full access — create/edit/delete surveys, questions; view all responses and download certificates |
| `USER` | Read surveys and questions; submit responses |

### Default accounts

| Role | Email | Password |
|---|---|---|
| Admin | `admin@skyworld.com` | `Admin@1234` |
| User | `user@skyworld.com` | `User@1234` |

> Passwords are BCrypt hashed in the database. Change them in production.

---

## API Reference

All endpoints accept and return **XML**. Set the following headers on every request:

```
Content-Type: application/xml
Accept: application/xml
```

File upload endpoints use `multipart/form-data` instead.

---

### Auth

#### Login

```
POST /api/auth/login
```

Request body (XML):

```xml
<login_request>
  <email>admin@skyworld.com</email>
  <password>Admin@1234</password>
</login_request>
```

Response `200 OK`:

```xml
<auth_response>
  <token>eyJhbGciOiJIUzI1NiJ9...</token>
  <type>Bearer</type>
  <username>admin</username>
  <email>admin@skyworld.com</email>
  <role>ADMIN</role>
</auth_response>
```

#### Register

```
POST /api/auth/register
```

Request body (JSON or XML):

```json
{
  "username": "johndoe",
  "email": "johndoe@gmail.com",
  "password": "Password@123",
  "role": "USER"
}
```

Response `201 Created`: same structure as login response.

---

### Surveys

#### Create a survey

```
POST /api/surveys
```

Request:

```xml
<survey>
  <name>Graduate Developer Application Survey</name>
  <description>Initial candidate screening survey</description>
</survey>
```

Response `201 Created`:

```xml
<survey id="1">
  <name>Graduate Developer Application Survey</name>
  <description>Initial candidate screening survey</description>
</survey>
```

#### Fetch all surveys

```
GET /api/surveys
```

Response `200 OK`:

```xml
<surveys>
  <survey id="1">
    <name>Graduate Developer Application Survey</name>
    <description>Initial candidate screening survey</description>
  </survey>
  <survey id="2">
    <name>Internship Application Survey</name>
    <description>Internship application survey</description>
  </survey>
</surveys>
```

#### Fetch a single survey

```
GET /api/surveys/{id}
```

#### Update a survey

```
PUT /api/surveys/{id}
```

Request body: same structure as create.

#### Delete a survey

```
DELETE /api/surveys/{id}
```

Response `204 No Content`. Cascades to all questions, options, responses and certificates.

---

### Questions

#### Create a question

```
POST /api/surveys/{surveyId}/questions
```

Text question:

```xml
<question name="full_name" type="short_text" required="yes">
  <text>What is your full name?</text>
  <description>[Surname] [First Name] [Other Names]</description>
</question>
```

Choice question:

```xml
<question name="programming_stack" type="multiple_choice" required="yes">
  <text>What programming stack are you familiar with?</text>
  <description>You can select multiple</description>
  <options multiple="yes">
    <option value="REACT">React JS</option>
    <option value="JAVA">Java</option>
  </options>
</question>
```

File question:

```xml
<question name="certificates" type="file" required="yes">
  <text>Upload your certificates.</text>
  <description>PDF only</description>
  <file_properties format=".pdf" max_file_size="1" max_file_size_unit="mb" multiple="yes"/>
</question>
```

#### Fetch all questions for a survey

```
GET /api/surveys/{surveyId}/questions
```

#### Update a question

```
PUT /api/surveys/{surveyId}/questions/{questionId}
```

#### Delete a question

```
DELETE /api/surveys/{surveyId}/questions/{questionId}
```

Response `204 No Content`.

---

### Responses

#### Submit a survey response

```
POST /api/surveys/{surveyId}/responses
Content-Type: multipart/form-data
```

| Form field | Type | Description |
|---|---|---|
| `full_name` | text | Maps to the question with `name="full_name"` |
| `email_address` | text | Respondent email (also stored on the response record) |
| `gender` | text | Single choice value e.g. `FEMALE` |
| `programming_stack` | text | Multiple choice as comma-separated values e.g. `REACT,JAVA` |
| `certificates` | file(s) | One or more PDF files |

Response `201 Created`:

```xml
<question_response>
  <response_id>1</response_id>
  <full_name>Jane Doe</full_name>
  <email_address>janedoe@gmail.com</email_address>
  <gender>FEMALE</gender>
  <programming_stack>REACT,VUE</programming_stack>
  <certificates>
    <certificate id="1">Adobe Certification.pdf</certificate>
  </certificates>
  <date_responded>2026-06-09 12:30:12</date_responded>
</question_response>
```

#### Fetch responses for a survey

```
GET /api/surveys/{surveyId}/responses?page=1&pageSize=10&email=jane@gmail.com
```

| Parameter | Type | Required | Description |
|---|---|---|---|
| `page` | integer | No | Page number (default: 1) |
| `pageSize` | integer | No | Results per page (default: 10) |
| `email` | string | No | Filter by respondent email |

---

### Certificates

#### Download a certificate

```
GET /api/certificates/{id}
```

Returns the PDF file as a binary download:

```
Content-Type: application/pdf
Content-Disposition: attachment; filename="original_filename.pdf"
```

---

## Postman Collection

A full Postman collection is included at `postman/simple-survey-api.json`. It contains all endpoints with sample requests and saved responses.

### Importing the collection

1. Open Postman.
2. Click **Import** in the top-left.
3. Select `postman/simple-survey-api.json`.
4. Set the `base_url` collection variable to your API URL (default: `http://localhost:8080`).

---

## Assumptions Made

1. **Authentication is role-based.** ADMIN users have full access. USER role can read surveys, read questions, and submit responses — but cannot manage surveys or view other users' responses.
2. **JWT subject is the user's email address.** The email is used as the principal identifier throughout — it is stored as the JWT `sub` claim and is what `UserDetailsService` loads by.
3. **Multiple-choice answers are stored as comma-separated values** (e.g. `REACT,VUE,JAVA`) to match the XML response contract. They are not normalised into a junction table.
4. **File uploads are stored on the local filesystem** under the `uploads/` directory during development. The `certificates` table stores both the original filename and a UUID-based stored filename to prevent collisions. For production, replace with cloud object storage (AWS S3, Cloudflare R2).
5. **Only `.pdf` files are accepted** for certificate uploads, with a maximum of 1 MB per file as specified in the survey's file question properties.
6. **Cascading deletes are intentional.** Deleting a survey removes all its questions, options, responses, answers, and certificates.
7. **Soft deletes are not implemented.** Records are permanently deleted.
8. **Pagination defaults** to page `1` with `10` results per page when not specified in the query string.
9. **The `ddl-auto` is set to `validate`** in production mode — Hibernate checks the schema matches the entities on startup and fails fast if there is a mismatch. Use `update` during development if you make entity changes.

---

## Deployment

### Recommended stack

| Component | Service |
|---|---|
| API hosting | [Railway](https://railway.app) or [Render](https://render.com) |
| Database | Railway MySQL or [PlanetScale](https://planetscale.com) |
| File storage | [Cloudflare R2](https://developers.cloudflare.com/r2) (S3-compatible, free tier) |

### Steps (Railway)

1. Push your code to GitHub.
2. Create a new Railway project and connect your `simple-survey-api` repository.
3. Add a MySQL plugin inside the Railway project.
4. Set environment variables from `.env.example` in Railway's variable editor, pointing `DB_HOST` to the Railway MySQL internal hostname.
5. Run `sky_survey_db.sql` against the Railway database using the Railway shell or a GUI client.
6. Railway auto-detects the `pom.xml` and builds with Maven. Set the start command to:
   ```
   java -jar target/simple-survey-api-1.0.0.jar
   ```
7. Set `VITE_API_BASE_URL` (or equivalent) in your web frontend to the Railway deployment URL.

Once deployed, update the `base_url` variable in your Postman collection to the live URL.
