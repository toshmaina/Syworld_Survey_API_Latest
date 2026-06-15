-- =============================================================
--  Seed Questions for sky_survey_db
--  Survey 2: Internship Application Survey
--  Survey 3: Graduate Developer Application Survey
-- =============================================================

-- ─────────────────────────────────────────────────────────────
--  SURVEY 3: Graduate Developer Application Survey
-- ─────────────────────────────────────────────────────────────

INSERT INTO questions (survey_id, name, type, text, description, required, sort_order) VALUES
                                                                                           (3, 'full_name',            'short_text',      'What is your full name?',                                '[Surname] [First Name] [Other Names]',                  TRUE,  1),
                                                                                           (3, 'email_address',        'email',            'What is your email address?',                            'We will use this to contact you',                       TRUE,  2),
                                                                                           (3, 'phone_number',         'short_text',       'What is your phone number?',                             'Include country code e.g. +254712345678',                TRUE,  3),
                                                                                           (3, 'date_of_birth',        'short_text',       'What is your date of birth?',                            'Format: DD/MM/YYYY',                                    TRUE,  4),
                                                                                           (3, 'nationality',          'short_text',       'What is your nationality?',                              NULL,                                                    TRUE,  5),
                                                                                           (3, 'gender',               'single_choice',    'What is your gender?',                                   NULL,                                                    TRUE,  6),
                                                                                           (3, 'university',           'short_text',       'Which university did you graduate from?',                 NULL,                                                    TRUE,  7),
                                                                                           (3, 'degree',               'short_text',       'What is your degree title?',                             'e.g. Bachelor of Science in Computer Science',          TRUE,  8),
                                                                                           (3, 'graduation_year',      'short_text',       'What year did you graduate?',                            'e.g. 2024',                                             TRUE,  9),
                                                                                           (3, 'gpa',                  'short_text',       'What was your GPA or final grade?',                      'e.g. 3.8/4.0 or First Class Honours',                   FALSE, 10),
                                                                                           (3, 'programming_stack',    'multiple_choice',  'Which programming languages are you proficient in?',     'Select all that apply',                                 TRUE,  11),
                                                                                           (3, 'frameworks',           'multiple_choice',  'Which frameworks or technologies have you worked with?', 'Select all that apply',                                 FALSE, 12),
                                                                                           (3, 'databases',            'multiple_choice',  'Which databases have you used?',                         'Select all that apply',                                 FALSE, 13),
                                                                                           (3, 'experience_level',     'single_choice',    'How would you rate your overall development experience?', NULL,                                                   TRUE,  14),
                                                                                           (3, 'github_profile',       'short_text',       'What is your GitHub profile URL?',                       'e.g. https://github.com/yourusername',                  FALSE, 15),
                                                                                           (3, 'linkedin_profile',     'short_text',       'What is your LinkedIn profile URL?',                     'e.g. https://linkedin.com/in/yourprofile',              FALSE, 16),
                                                                                           (3, 'portfolio_url',        'short_text',       'Do you have a portfolio website?',                       'e.g. https://yourportfolio.com',                        FALSE, 17),
                                                                                           (3, 'work_experience',      'single_choice',    'Do you have any prior work experience in software development?', NULL,                                            TRUE,  18),
                                                                                           (3, 'description',          'long_text',        'Tell us about yourself and your passion for software development.', 'Max 500 words',                             TRUE,  19),
                                                                                           (3, 'why_skyworld',         'long_text',        'Why do you want to work at Sky World Limited?',          'Max 300 words',                                         TRUE,  20),
                                                                                           (3, 'availability',         'single_choice',    'When are you available to start?',                       NULL,                                                    TRUE,  21),
                                                                                           (3, 'referral_source',      'single_choice',    'How did you hear about this opportunity?',               NULL,                                                    FALSE, 22),
                                                                                           (3, 'certificates',         'file',             'Upload your academic certificates and transcripts.',     'PDF only · Max 1MB per file · Multiple allowed',        TRUE,  23);

-- ─── Options: gender (survey 3) ──────────────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'MALE'           AS value, 'Male'               AS label, 1 AS sort_order UNION ALL
    SELECT 'FEMALE',                  'Female',                       2               UNION ALL
    SELECT 'OTHER',                   'Other',                        3               UNION ALL
    SELECT 'PREFER_NOT_SAY',          'Prefer not to say',            4
) o
WHERE q.survey_id = 3 AND q.name = 'gender';

-- ─── Options: programming_stack (survey 3) ───────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'JAVA'           AS value, 'Java'           AS label,  1 AS sort_order UNION ALL
    SELECT 'PYTHON',                  'Python',                    2               UNION ALL
    SELECT 'JAVASCRIPT',              'JavaScript',                3               UNION ALL
    SELECT 'TYPESCRIPT',              'TypeScript',                4               UNION ALL
    SELECT 'PHP',                     'PHP',                       5               UNION ALL
    SELECT 'CSHARP',                  'C#',                        6               UNION ALL
    SELECT 'CPP',                     'C++',                       7               UNION ALL
    SELECT 'GO',                      'Go',                        8               UNION ALL
    SELECT 'RUST',                    'Rust',                      9               UNION ALL
    SELECT 'KOTLIN',                  'Kotlin',                   10               UNION ALL
    SELECT 'SWIFT',                   'Swift',                    11               UNION ALL
    SELECT 'DART',                    'Dart',                     12
) o
WHERE q.survey_id = 3 AND q.name = 'programming_stack';

-- ─── Options: frameworks (survey 3) ──────────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'SPRING_BOOT'    AS value, 'Spring Boot'        AS label,  1 AS sort_order UNION ALL
    SELECT 'DJANGO',                  'Django',                        2               UNION ALL
    SELECT 'FASTAPI',                 'FastAPI',                       3               UNION ALL
    SELECT 'REACT',                   'React JS',                      4               UNION ALL
    SELECT 'ANGULAR',                 'Angular',                       5               UNION ALL
    SELECT 'VUE',                     'Vue JS',                        6               UNION ALL
    SELECT 'REACT_NATIVE',            'React Native',                  7               UNION ALL
    SELECT 'FLUTTER',                 'Flutter',                       8               UNION ALL
    SELECT 'NODEJS',                  'Node.js',                       9               UNION ALL
    SELECT 'LARAVEL',                 'Laravel',                      10               UNION ALL
    SELECT 'DOTNET',                  '.NET',                         11               UNION ALL
    SELECT 'NEXTJS',                  'Next.js',                      12
) o
WHERE q.survey_id = 3 AND q.name = 'frameworks';

-- ─── Options: databases (survey 3) ───────────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'MYSQL'          AS value, 'MySQL'                  AS label, 1 AS sort_order UNION ALL
    SELECT 'POSTGRESQL',              'PostgreSQL',                       2               UNION ALL
    SELECT 'MSSQL',                   'Microsoft SQL Server',             3               UNION ALL
    SELECT 'ORACLE',                  'Oracle DB',                        4               UNION ALL
    SELECT 'MONGODB',                 'MongoDB',                          5               UNION ALL
    SELECT 'REDIS',                   'Redis',                            6               UNION ALL
    SELECT 'SQLITE',                  'SQLite',                           7               UNION ALL
    SELECT 'FIREBASE',                'Firebase',                         8
) o
WHERE q.survey_id = 3 AND q.name = 'databases';

-- ─── Options: experience_level (survey 3) ────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'BEGINNER'       AS value, 'Beginner — less than 1 year'  AS label, 1 AS sort_order UNION ALL
    SELECT 'JUNIOR',                  'Junior — 1 to 2 years',                  2               UNION ALL
    SELECT 'MID',                     'Mid-level — 2 to 4 years',               3               UNION ALL
    SELECT 'SENIOR',                  'Senior — 4+ years',                       4
) o
WHERE q.survey_id = 3 AND q.name = 'experience_level';

-- ─── Options: work_experience (survey 3) ─────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'YES_FULLTIME'   AS value, 'Yes — full-time employment'       AS label, 1 AS sort_order UNION ALL
    SELECT 'YES_PARTTIME',            'Yes — part-time or contract',               2               UNION ALL
    SELECT 'YES_INTERNSHIP',          'Yes — internship',                          3               UNION ALL
    SELECT 'NO',                      'No — this would be my first role',          4
) o
WHERE q.survey_id = 3 AND q.name = 'work_experience';

-- ─── Options: availability (survey 3) ────────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'IMMEDIATELY'    AS value, 'Immediately'      AS label, 1 AS sort_order UNION ALL
    SELECT 'TWO_WEEKS',               'Within 2 weeks',             2               UNION ALL
    SELECT 'ONE_MONTH',               'Within 1 month',             3               UNION ALL
    SELECT 'THREE_MONTHS',            'Within 3 months',            4
) o
WHERE q.survey_id = 3 AND q.name = 'availability';

-- ─── Options: referral_source (survey 3) ─────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'LINKEDIN'       AS value, 'LinkedIn'                          AS label, 1 AS sort_order UNION ALL
    SELECT 'WEBSITE',                 'Sky World website',                           2               UNION ALL
    SELECT 'REFERRAL',                'Referred by a friend or colleague',            3               UNION ALL
    SELECT 'UNIVERSITY',              'University career fair or notice board',       4               UNION ALL
    SELECT 'SOCIAL_MEDIA',            'Social media (Twitter/X, Facebook etc.)',      5               UNION ALL
    SELECT 'OTHER',                   'Other',                                        6
) o
WHERE q.survey_id = 3 AND q.name = 'referral_source';

-- ─── File properties: certificates (survey 3) ────────────────
INSERT INTO question_file_properties (question_id, format, max_file_size, max_file_size_unit, multiple)
SELECT id, '.pdf', 1, 'mb', TRUE
FROM questions
WHERE survey_id = 3 AND name = 'certificates';


-- ─────────────────────────────────────────────────────────────
--  SURVEY 2: Internship Application Survey
-- ─────────────────────────────────────────────────────────────

INSERT INTO questions (survey_id, name, type, text, description, required, sort_order) VALUES
                                                                                           (2, 'full_name',            'short_text',      'What is your full name?',                                    '[Surname] [First Name] [Other Names]',                  TRUE,  1),
                                                                                           (2, 'email_address',        'email',            'What is your email address?',                                'We will use this to contact you',                       TRUE,  2),
                                                                                           (2, 'phone_number',         'short_text',       'What is your phone number?',                                 'Include country code e.g. +254712345678',                TRUE,  3),
                                                                                           (2, 'gender',               'single_choice',    'What is your gender?',                                       NULL,                                                    TRUE,  4),
                                                                                           (2, 'university',           'short_text',       'Which university are you currently attending?',               NULL,                                                    TRUE,  5),
                                                                                           (2, 'course',               'short_text',       'What course are you studying?',                              'e.g. Bachelor of Science in Software Engineering',       TRUE,  6),
                                                                                           (2, 'year_of_study',        'single_choice',    'What year of study are you currently in?',                   NULL,                                                    TRUE,  7),
                                                                                           (2, 'expected_graduation',  'short_text',       'What is your expected graduation year?',                     'e.g. 2026',                                             TRUE,  8),
                                                                                           (2, 'internship_duration',  'single_choice',    'How long are you available for the internship?',             NULL,                                                    TRUE,  9),
                                                                                           (2, 'internship_type',      'single_choice',    'What type of internship are you applying for?',              NULL,                                                    TRUE,  10),
                                                                                           (2, 'programming_stack',    'multiple_choice',  'Which programming languages have you used?',                 'Select all that apply',                                 FALSE, 11),
                                                                                           (2, 'tools',                'multiple_choice',  'Which tools and platforms are you familiar with?',           'Select all that apply',                                 FALSE, 12),
                                                                                           (2, 'projects',             'long_text',        'Describe any academic or personal projects you have worked on.', 'Include technologies used and your role',           FALSE, 13),
                                                                                           (2, 'motivation',           'long_text',        'Why are you interested in interning at Sky World Limited?',  'Max 300 words',                                         TRUE,  14),
                                                                                           (2, 'skills',               'long_text',        'What skills or value will you bring to the team?',           'Max 200 words',                                         TRUE,  15),
                                                                                           (2, 'referral_source',      'single_choice',    'How did you hear about this internship?',                    NULL,                                                    FALSE, 16),
                                                                                           (2, 'certificates',         'file',             'Upload your academic transcripts or any relevant certificates.', 'PDF only · Max 1MB per file',                      TRUE,  17);

-- ─── Options: gender (survey 2) ──────────────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'MALE'           AS value, 'Male'               AS label, 1 AS sort_order UNION ALL
    SELECT 'FEMALE',                  'Female',                       2               UNION ALL
    SELECT 'OTHER',                   'Other',                        3               UNION ALL
    SELECT 'PREFER_NOT_SAY',          'Prefer not to say',            4
) o
WHERE q.survey_id = 2 AND q.name = 'gender';

-- ─── Options: year_of_study (survey 2) ───────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'YEAR_1'         AS value, 'First year'     AS label, 1 AS sort_order UNION ALL
    SELECT 'YEAR_2',                  'Second year',             2               UNION ALL
    SELECT 'YEAR_3',                  'Third year',              3               UNION ALL
    SELECT 'YEAR_4',                  'Fourth year',             4               UNION ALL
    SELECT 'YEAR_5',                  'Fifth year',              5               UNION ALL
    SELECT 'POSTGRAD',                'Postgraduate',            6
) o
WHERE q.survey_id = 2 AND q.name = 'year_of_study';

-- ─── Options: internship_duration (survey 2) ─────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'ONE_MONTH'      AS value, '1 month'    AS label, 1 AS sort_order UNION ALL
    SELECT 'THREE_MONTHS',            '3 months',            2               UNION ALL
    SELECT 'SIX_MONTHS',              '6 months',            3               UNION ALL
    SELECT 'ONE_YEAR',                '1 year',              4
) o
WHERE q.survey_id = 2 AND q.name = 'internship_duration';

-- ─── Options: internship_type (survey 2) ─────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'BACKEND'        AS value, 'Backend Development'          AS label, 1 AS sort_order UNION ALL
    SELECT 'FRONTEND',                'Frontend Development',                   2               UNION ALL
    SELECT 'FULLSTACK',               'Full Stack Development',                 3               UNION ALL
    SELECT 'MOBILE',                  'Mobile Development',                     4               UNION ALL
    SELECT 'DATA',                    'Data & Analytics',                       5               UNION ALL
    SELECT 'DEVOPS',                  'DevOps & Cloud',                         6               UNION ALL
    SELECT 'UI_UX',                   'UI/UX Design',                           7               UNION ALL
    SELECT 'QA',                      'Quality Assurance & Testing',            8
) o
WHERE q.survey_id = 2 AND q.name = 'internship_type';

-- ─── Options: programming_stack (survey 2) ───────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'JAVA'           AS value, 'Java'           AS label,  1 AS sort_order UNION ALL
    SELECT 'PYTHON',                  'Python',                    2               UNION ALL
    SELECT 'JAVASCRIPT',              'JavaScript',                3               UNION ALL
    SELECT 'TYPESCRIPT',              'TypeScript',                4               UNION ALL
    SELECT 'PHP',                     'PHP',                       5               UNION ALL
    SELECT 'CSHARP',                  'C#',                        6               UNION ALL
    SELECT 'CPP',                     'C++',                       7               UNION ALL
    SELECT 'KOTLIN',                  'Kotlin',                    8               UNION ALL
    SELECT 'DART',                    'Dart',                      9               UNION ALL
    SELECT 'GO',                      'Go',                       10
) o
WHERE q.survey_id = 2 AND q.name = 'programming_stack';

-- ─── Options: tools (survey 2) ───────────────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'GIT'            AS value, 'Git & GitHub'       AS label,  1 AS sort_order UNION ALL
    SELECT 'DOCKER',                  'Docker',                         2               UNION ALL
    SELECT 'VSCODE',                  'VS Code',                        3               UNION ALL
    SELECT 'INTELLIJ',                'IntelliJ IDEA',                  4               UNION ALL
    SELECT 'POSTMAN',                 'Postman',                        5               UNION ALL
    SELECT 'FIGMA',                   'Figma',                          6               UNION ALL
    SELECT 'JIRA',                    'Jira',                           7               UNION ALL
    SELECT 'LINUX',                   'Linux / Terminal',               8               UNION ALL
    SELECT 'AWS',                     'AWS',                            9               UNION ALL
    SELECT 'FIREBASE',                'Firebase',                      10
) o
WHERE q.survey_id = 2 AND q.name = 'tools';

-- ─── Options: referral_source (survey 2) ─────────────────────
INSERT INTO question_options (question_id, value, label, sort_order)
SELECT q.id, o.value, o.label, o.sort_order
FROM questions q
         CROSS JOIN (
    SELECT 'LINKEDIN'       AS value, 'LinkedIn'                          AS label, 1 AS sort_order UNION ALL
    SELECT 'WEBSITE',                 'Sky World website',                           2               UNION ALL
    SELECT 'REFERRAL',                'Referred by a friend or colleague',            3               UNION ALL
    SELECT 'UNIVERSITY',              'University career fair or notice board',       4               UNION ALL
    SELECT 'SOCIAL_MEDIA',            'Social media (Twitter/X, Facebook etc.)',      5               UNION ALL
    SELECT 'OTHER',                   'Other',                                        6
) o
WHERE q.survey_id = 2 AND q.name = 'referral_source';

-- ─── File properties: certificates (survey 2) ────────────────
INSERT INTO question_file_properties (question_id, format, max_file_size, max_file_size_unit, multiple)
SELECT id, '.pdf', 1, 'mb', TRUE
FROM questions
WHERE survey_id = 2 AND name = 'certificates';