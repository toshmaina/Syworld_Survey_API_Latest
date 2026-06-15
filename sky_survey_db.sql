-- =============================================================
--  sky_survey_db  –  Database Creation Script
--  Compatible with: MySQL 8+ / PostgreSQL 14+
--  Notes:
--    • Enums are defined inline for MySQL.
--      For PostgreSQL, replace ENUM(...) with VARCHAR(20)
--      and add CHECK constraints (see comments).
--    • All timestamps default to CURRENT_TIMESTAMP.
--    • Soft-delete is NOT used; use application-level archiving
--      if required in future.
-- =============================================================

CREATE DATABASE IF NOT EXISTS sky_survey_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sky_survey_db;

-- =============================================================
--  1. surveys
-- =============================================================
CREATE TABLE surveys (
  id BIGINT             NOT NULL AUTO_INCREMENT,
  name        VARCHAR(255)    NOT NULL,
  description TEXT,
  created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id)
);


-- =============================================================
--  2. questions
--     type ENUM covers all six required question types.
--     sort_order drives the stepped-form ordering on the UI.
-- =============================================================
CREATE TABLE questions (
  id BIGINT             NOT NULL AUTO_INCREMENT,
  survey_id BIGINT             NOT NULL,
  name        VARCHAR(100)    NOT NULL,
  type        ENUM(
                'short_text',
                'long_text',
                'email',
                'single_choice',
                'multiple_choice',
                'file'
              )               NOT NULL,
  text        TEXT            NOT NULL,
  description TEXT,
  required    BOOLEAN         NOT NULL DEFAULT FALSE,
  sort_order  INT             NOT NULL DEFAULT 0,
  created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  CONSTRAINT fk_questions_survey
    FOREIGN KEY (survey_id)
    REFERENCES surveys (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  -- Enforce unique machine-name per survey
  UNIQUE KEY uq_question_name_per_survey (survey_id, name)
);


-- =============================================================
--  3. question_options
--     Only relevant for single_choice / multiple_choice questions.
--     value  = machine key  (e.g. "REACT")
--     label  = display text (e.g. "React JS")
-- =============================================================
CREATE TABLE question_options (
  id BIGINT             NOT NULL AUTO_INCREMENT,
  question_id BIGINT             NOT NULL,
  value       VARCHAR(100)    NOT NULL,
  label       VARCHAR(255)    NOT NULL,
  sort_order  INT             NOT NULL DEFAULT 0,

  PRIMARY KEY (id),
  CONSTRAINT fk_options_question
    FOREIGN KEY (question_id)
    REFERENCES questions (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  -- Enforce unique option value per question
  UNIQUE KEY uq_option_value_per_question (question_id, value)
);


-- =============================================================
--  4. question_file_properties
--     One-to-one with a question of type 'file'.
--     Stores upload constraints returned in <file_properties/>.
-- =============================================================
CREATE TABLE question_file_properties (
  id BIGINT             NOT NULL AUTO_INCREMENT,
  question_id BIGINT             NOT NULL,
  format              VARCHAR(20)     NOT NULL DEFAULT '.pdf',
  max_file_size       INT             NOT NULL DEFAULT 1,
  max_file_size_unit  VARCHAR(10)     NOT NULL DEFAULT 'mb',
  multiple            BOOLEAN         NOT NULL DEFAULT FALSE,

  PRIMARY KEY (id),
  CONSTRAINT fk_file_props_question
    FOREIGN KEY (question_id)
    REFERENCES questions (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  -- One set of file properties per question
  UNIQUE KEY uq_file_props_per_question (question_id)
);


-- =============================================================
--  5. responses
--     One record per survey submission.
--     respondent_email is denormalised here to support the
--     required email-filter query without extra joins.
-- =============================================================
CREATE TABLE responses (
  id BIGINT             NOT NULL AUTO_INCREMENT,
  survey_id BIGINT             NOT NULL,
  respondent_email  VARCHAR(255)    NOT NULL,
  date_responded    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  CONSTRAINT fk_responses_survey
    FOREIGN KEY (survey_id)
    REFERENCES surveys (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  -- Index for the required email-filter endpoint
  INDEX idx_responses_email (respondent_email),
  INDEX idx_responses_survey (survey_id)
);


-- =============================================================
--  6. response_answers
--     Stores text/choice answers for every non-file question.
--     Multiple-choice answers are stored as comma-separated
--     values (e.g. "REACT,VUE") matching the XML contract.
-- =============================================================
CREATE TABLE response_answers (
  id BIGINT   NOT NULL AUTO_INCREMENT,
  response_id BIGINT   NOT NULL,
  question_id BIGINT   NOT NULL,
  answer_value TEXT,

  PRIMARY KEY (id),
  CONSTRAINT fk_answers_response
    FOREIGN KEY (response_id)
    REFERENCES responses (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_answers_question
    FOREIGN KEY (question_id)
    REFERENCES questions (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  -- One answer per question per response
  UNIQUE KEY uq_answer_per_question_response (response_id, question_id),

  INDEX idx_answers_response (response_id),
  INDEX idx_answers_question (question_id)
);


-- =============================================================
--  7. certificates
--     File uploads linked to both a response and the specific
--     file-type question that requested the upload.
--     stored_filename is the UUID-based name on disk to avoid
--     collisions; original_filename is shown in the UI.
--     Supports GET /api/certificates/{id} download endpoint.
-- =============================================================
CREATE TABLE certificates (
  id BIGINT             NOT NULL AUTO_INCREMENT,
  response_id BIGINT             NOT NULL,
  question_id BIGINT             NOT NULL,
  original_filename VARCHAR(255)    NOT NULL,
  stored_filename   VARCHAR(255)    NOT NULL,
  file_path         VARCHAR(500)    NOT NULL,
  file_size         BIGINT             NOT NULL COMMENT 'Size in bytes',
  uploaded_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  CONSTRAINT fk_certificates_response
    FOREIGN KEY (response_id)
    REFERENCES responses (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_certificates_question
    FOREIGN KEY (question_id)
    REFERENCES questions (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  INDEX idx_certificates_response (response_id),
  INDEX idx_certificates_question (question_id)
);


-- =============================================================
--  SEED DATA  –  Sample survey matching the task specification
-- =============================================================

INSERT INTO surveys (name, description) VALUES
  ('Graduate Developer Application Survey', 'Initial candidate screening survey'),
  ('Internship Application Survey',         'Internship application survey');

-- Questions for survey 1
INSERT INTO questions (survey_id, name, type, text, description, required, sort_order) VALUES
  (1, 'full_name',         'short_text',      'What is your full name?',             '[Surname] [First Name] [Other Names]', TRUE,  1),
  (1, 'email_address',     'email',            'What is your email address?',         NULL,                                   TRUE,  2),
  (1, 'description',       'long_text',        'Tell us about yourself.',             'Briefly describe your experience.',    FALSE, 3),
  (1, 'gender',            'single_choice',    'What is your gender?',                NULL,                                   TRUE,  4),
  (1, 'programming_stack', 'multiple_choice',  'What programming stack are you familiar with?', 'You can select multiple',   TRUE,  5),
  (1, 'certificates',      'file',             'Upload any of your certificates.',    'You can upload multiple (.pdf)',       TRUE,  6);

-- Options for gender (single choice)
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, opt.value, opt.label, opt.sort_order
FROM questions q
JOIN (
  SELECT 'MALE'   AS value, 'Male'   AS label, 1 AS sort_order UNION ALL
  SELECT 'FEMALE',           'Female',          2               UNION ALL
  SELECT 'OTHER',            'Other',           3
) opt ON TRUE
WHERE q.survey_id = 1 AND q.name = 'gender';

-- Options for programming_stack (multiple choice)
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, opt.value, opt.label, opt.sort_order
FROM questions q
JOIN (
  SELECT 'REACT'    AS value, 'React JS'             AS label,  1 AS sort_order UNION ALL
  SELECT 'ANGULAR',           'Angular JS',                      2               UNION ALL
  SELECT 'VUE',               'Vue JS',                          3               UNION ALL
  SELECT 'SQL',               'SQL',                             4               UNION ALL
  SELECT 'POSTGRES',          'Postgres',                        5               UNION ALL
  SELECT 'MYSQL',             'MySQL',                           6               UNION ALL
  SELECT 'MSSQL',             'Microsoft SQL Server',            7               UNION ALL
  SELECT 'JAVA',              'Java',                            8               UNION ALL
  SELECT 'PHP',               'PHP',                             9               UNION ALL
  SELECT 'GO',                'Go',                             10               UNION ALL
  SELECT 'RUST',              'Rust',                           11
) opt ON TRUE
WHERE q.survey_id = 1 AND q.name = 'programming_stack';

-- File properties for certificates question
INSERT INTO question_file_properties (question_id, format, max_file_size, max_file_size_unit, multiple)
SELECT id, '.pdf', 1, 'mb', TRUE
FROM questions
WHERE survey_id = 1 AND name = 'certificates';
