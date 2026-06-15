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
- [API Reference](#api-reference)
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
| Runtime | Node.js 20+ |
| Framework | Express.js |
| Database | MySQL 8.0 |
| ORM / Query Builder | mysql2 |
| XML Serialization | xml2js |
| File Uploads | Multer |
| Containerization | Docker + Docker Compose |
| API Documentation | Postman |

---

## Prerequisites

Ensure the following are installed on your machine before proceeding:

| Tool | Version | Download |
|---|---|---|
| Node.js | 20 or higher | https://nodejs.org |
| npm | 9 or higher | Bundled with Node.js |
| Docker Desktop | Latest stable | https://www.docker.com/products/docker-desktop |
| Git | Any recent version | https://git-scm.com |
| Postman | Latest stable | https://www.postman.com/downloads |

Verify your installations:

```bash
node --version    # should print v20.x.x or higher
npm --version     # should print 9.x.x or higher
docker --version  # should print Docker version 24.x.x or higher
git --version     # should print git version 2.x.x or higher
```

---

## Project Structure

```
simple-survey-api/
├── docker-compose.yml          # MySQL 8 container definition
├── .env                        # Local environment variables (never committed)
├── .env.example                # Template — copy this to .env
├── .gitignore
├── sky_survey_db.sql           # Full database schema + seed data
├── docs/
│   └── erd.png                 # Entity Relationship Diagram
├── postman/
│   └── simple-survey-api.json  # Postman collection
├── src/
│   ├── config/
│   │   └── db.js               # MySQL connection pool
│   ├── controllers/
│   │   ├── surveyController.js
│   │   ├── questionController.js
│   │   ├── responseController.js
│   │   └── certificateController.js
│   ├── routes/
│   │   ├── surveys.js
│   │   ├── questions.js
│   │   ├── responses.js
│   │   └── certificates.js
│   ├── middleware/
│   │   └── upload.js           # Multer config for PDF uploads
│   ├── utils/
│   │   └── xml.js              # XML serialization helpers
│   └── app.js                  # Express app entry point
├── package.json
└── README.md
```

---

## Environment Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-username/simple-survey-api.git
cd simple-survey-api
```

### 2. Create your environment file

Copy the provided template and fill in your values:

```bash
cp .env.example .env
```

Open `.env` and set your credentials:

```env
DB_ROOT_PASSWORD=root_secret_change_me
DB_NAME=sky_survey_db
DB_USER=sky_user
DB_PASSWORD=sky_pass_change_me
DB_PORT=3306
DB_HOST=localhost
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

You can also watch the startup logs:

```bash
docker compose logs -f db
```

Wait until you see a line similar to:

```
[System] [MY-010931] [Server] /usr/sbin/mysqld: ready for connections.
```

Then press `Ctrl + C` to stop following the logs.

### Connecting to the Database

#### Option A — MySQL shell inside the container

```bash
docker exec -it sky_survey_db mysql -u sky_user -psky_pass_change_me sky_survey_db
```

Run a quick sanity check:

```sql
SHOW TABLES;
```

Expected output:

```
+----------------------------+
| Tables_in_sky_survey_db    |
+----------------------------+
| certificates               |
| question_file_properties   |
| question_options           |
| questions                  |
| response_answers           |
| responses                  |
| surveys                    |
+----------------------------+
7 rows in set
```

Confirm seed data:

```sql
SELECT id, name FROM surveys;
```

Expected output:

```
+----+----------------------------------------+
| id | name                                   |
+----+----------------------------------------+
|  1 | Graduate Developer Application Survey  |
|  2 | Internship Application Survey          |
+----+----------------------------------------+
```

Exit the shell:

```sql
EXIT;
```

#### Option B — GUI client (TablePlus, DBeaver, MySQL Workbench)

Use these connection details (matching your `.env`):

| Field | Value |
|---|---|
| Host | `127.0.0.1` |
| Port | `3306` |
| Database | `sky_survey_db` |
| Username | `sky_user` |
| Password | `sky_pass_change_me` |

### Stopping the Container

Stop the container while keeping all data:

```bash
docker compose down
```

### Resetting the Database

To wipe all data and re-run the SQL script from scratch:

```bash
docker compose down -v     # removes the named volume
docker compose up -d       # recreates and re-seeds the database
```

> Use this whenever you need a clean slate during development.

---

## Running the API Locally

> The API source code setup steps will be added here once the Express.js application is scaffolded. The database must be running before starting the API.

### 1. Install dependencies

```bash
npm install
```

### 2. Ensure the database container is running

```bash
docker compose up -d
docker compose ps   # confirm STATUS is healthy
```

### 3. Start the development server

```bash
npm run dev
```

The API will be available at `http://localhost:3000`.

---

## API Reference

All endpoints accept and return **XML**. Set the following headers on every request:

```
Content-Type: application/xml
Accept: application/xml
```

File upload endpoints use `multipart/form-data` instead.

---

### Surveys

#### Create a survey

```
POST /api/surveys
```

Request body:

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

---

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

---

#### Fetch a single survey

```
GET /api/surveys/:id
```

Response `200 OK`:

```xml
<survey id="1">
  <name>Graduate Developer Application Survey</name>
  <description>Initial candidate screening survey</description>
</survey>
```

---

#### Update a survey

```
PUT /api/surveys/:id
```

Request body:

```xml
<survey>
  <name>Updated Survey Name</name>
  <description>Updated description</description>
</survey>
```

Response `200 OK`: updated survey XML.

---

#### Delete a survey

```
DELETE /api/surveys/:id
```

Response `204 No Content`.

> Deleting a survey cascades to all its questions, options, and responses.

---

### Questions

#### Create a question

```
POST /api/surveys/:surveyId/questions
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
    <option value="VUE">Vue JS</option>
    <option value="JAVA">Java</option>
  </options>
</question>
```

File question:

```xml
<question name="certificates" type="file" required="yes">
  <text>Upload any of your certificates.</text>
  <description>You can upload multiple (.pdf)</description>
  <file_properties
    format=".pdf"
    max_file_size="1"
    max_file_size_unit="mb"
    multiple="yes"
  />
</question>
```

Response `201 Created`: created question XML with assigned `id`.

---

#### Fetch all questions for a survey

```
GET /api/surveys/:surveyId/questions
```

Response `200 OK`:

```xml
<questions>
  <question id="1" name="full_name" type="short_text" required="yes">
    <text>What is your full name?</text>
    <description>[Surname] [First Name] [Other Names]</description>
  </question>
  <question id="2" name="email_address" type="email" required="yes">
    <text>What is your email address?</text>
    <description/>
  </question>
  <question id="3" name="gender" type="single_choice" required="yes">
    <text>What is your gender?</text>
    <description/>
    <options multiple="no">
      <option value="MALE">Male</option>
      <option value="FEMALE">Female</option>
      <option value="OTHER">Other</option>
    </options>
  </question>
</questions>
```

---

#### Update a question

```
PUT /api/surveys/:surveyId/questions/:questionId
```

Request body: same structure as create. Response `200 OK`: updated question XML.

---

#### Delete a question

```
DELETE /api/surveys/:surveyId/questions/:questionId
```

Response `204 No Content`.

---

### Responses

#### Submit a survey response

```
POST /api/surveys/:surveyId/responses
Content-Type: multipart/form-data
```

Form fields mirror the question `name` attributes defined in the survey. File fields accept one or more `.pdf` files.

Example form fields:

| Field | Value |
|---|---|
| `full_name` | `Jane Doe` |
| `email_address` | `janedoe@gmail.com` |
| `description` | `I am an experienced Frontend Engineer.` |
| `gender` | `FEMALE` |
| `programming_stack` | `REACT,VUE` |
| `certificates` | _(attach one or more PDF files)_ |

Response `201 Created`:

```xml
<question_response>
  <full_name>Jane Doe</full_name>
  <email_address>janedoe@gmail.com</email_address>
  <description>I am an experienced Frontend Engineer.</description>
  <gender>FEMALE</gender>
  <programming_stack>REACT,VUE</programming_stack>
  <certificates>
    <certificate>Adobe Certification.pdf</certificate>
    <certificate>Figma Fundamentals.pdf</certificate>
  </certificates>
  <date_responded>2026-06-08 12:30:12</date_responded>
</question_response>
```

---

#### Fetch responses for a survey

```
GET /api/surveys/:surveyId/responses
```

Query parameters:

| Parameter | Type | Required | Description |
|---|---|---|---|
| `page` | integer | No | Page number (default: 1) |
| `pageSize` | integer | No | Results per page (default: 10) |
| `email` | string | No | Filter by respondent email address |

Example:

```
GET /api/surveys/1/responses?page=1&pageSize=10&email=jane@gmail.com
```

Response `200 OK`:

```xml
<question_responses current_page="1" last_page="1" page_size="10" total_count="2">
  <question_response>
    <response_id>1</response_id>
    <full_name>John Doe</full_name>
    <email_address>johndoe@gmail.com</email_address>
    <description>I am an experienced FullStack Engineer.</description>
    <gender>MALE</gender>
    <programming_stack>REACT,JAVA,SQL,POSTGRES</programming_stack>
    <certificates>
      <certificate id="1">Oracle Java Certification.pdf</certificate>
      <certificate id="2">Oracle SQL Certification.pdf</certificate>
    </certificates>
    <date_responded>2026-06-08 12:30:12</date_responded>
  </question_response>
</question_responses>
```

---

### Certificates

#### Download a certificate

```
GET /api/certificates/:id
```

Returns the PDF file as a binary download with headers:

```
Content-Type: application/pdf
Content-Disposition: attachment; filename="original_filename.pdf"
```

---

## Postman Collection

A full Postman collection is included at `postman/simple-survey-api.json`. It contains:

- All endpoints with sample requests
- Pre-set `Content-Type` headers
- Saved example responses for every endpoint
- A `base_url` collection variable (default: `http://localhost:3000`)

### Importing the collection

1. Open Postman.
2. Click **Import** in the top-left.
3. Select `postman/simple-survey-api.json`.
4. Set the `base_url` variable to your API URL if different from the default.

---

## Assumptions Made

1. **Authentication is out of scope.** All endpoints are public. There is no distinction enforced between administrator and user roles at the API level — this is assumed to be handled by the consuming frontend.

2. **Multiple-choice answers are stored as comma-separated values** (e.g. `REACT,VUE,JAVA`) to match the XML response contract specified in the task. They are not normalised into a junction table.

3. **File uploads are stored on the local filesystem** under an `uploads/` directory during development. The `certificates` table stores the file path. For deployment, this would be replaced with cloud object storage (e.g. AWS S3, Cloudflare R2).

4. **Only `.pdf` files are accepted** for certificate uploads, with a maximum size of 1 MB per file, as specified in the seed survey's file question properties.

5. **Cascading deletes are intentional.** Deleting a survey removes all its questions, options, file properties, responses, answers, and certificates. This is consistent with expected admin behaviour.

6. **Soft deletes are not implemented.** Records are permanently deleted. Archiving functionality can be added in a future iteration.

7. **The `name` field on a question acts as the response field key.** It must be unique within a survey and is used to map submitted form fields to their corresponding questions.

8. **Pagination defaults** are page `1` with `10` results per page when not specified in the query string.

---

## Deployment

> Optional — additional credit is awarded for a live deployment.

### Recommended stack

| Component | Service |
|---|---|
| API hosting | [Railway](https://railway.app) or [Render](https://render.com) |
| Database | [Railway MySQL](https://railway.app) or [PlanetScale](https://planetscale.com) |
| File storage | [Cloudflare R2](https://developers.cloudflare.com/r2) (S3-compatible, free tier) |

### Steps (Railway)

1. Push your code to GitHub.
2. Create a new Railway project and connect your `simple-survey-api` repository.
3. Add a MySQL plugin inside the Railway project.
4. Set the environment variables from `.env.example` using Railway's variable editor, pointing `DB_HOST` to the Railway MySQL internal hostname.
5. Run `sky_survey_db.sql` against the Railway database using the Railway shell or a GUI client connected to the external URL.
6. Deploy — Railway auto-detects Node.js and runs `npm start`.

Once deployed, update the `base_url` variable in your Postman collection to the live URL.
