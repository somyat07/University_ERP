SET FOREIGN_KEY_CHECKS = 0;

------------------------------------------------
-- CREATE & USE ERP DATABASE
------------------------------------------------
DROP DATABASE IF EXISTS erp_db;
CREATE DATABASE erp_db;
USE erp_db;

-- -----------------------------
-- TABLE: courses
-- -----------------------------
CREATE TABLE courses (
  course_id INT NOT NULL AUTO_INCREMENT,
  code VARCHAR(10) NOT NULL,
  title VARCHAR(100) NOT NULL,
  credits INT NOT NULL,
  PRIMARY KEY (course_id),
  UNIQUE KEY (code)
);

-- -----------------------------
-- TABLE: instructors
-- -----------------------------
CREATE TABLE instructors (
  user_id INT NOT NULL,
  department VARCHAR(50),
  PRIMARY KEY (user_id)
);

-- -----------------------------
-- TABLE: sections
-- -----------------------------
CREATE TABLE sections (
  section_id INT NOT NULL AUTO_INCREMENT,
  course_id INT NOT NULL,
  instructor_id INT,
  day_time VARCHAR(50),
  room VARCHAR(20),
  capacity INT NOT NULL,
  semester VARCHAR(20) NOT NULL,
  year INT NOT NULL,
  PRIMARY KEY (section_id),
  FOREIGN KEY (course_id) REFERENCES courses(course_id),
  FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);

-- -----------------------------
-- TABLE: students
-- -----------------------------
CREATE TABLE students (
  user_id INT NOT NULL,
  roll_no VARCHAR(20) NOT NULL,
  program VARCHAR(50),
  year INT,
  PRIMARY KEY (user_id),
  UNIQUE (roll_no)
);

-- -----------------------------
-- TABLE: enrollments
-- -----------------------------
CREATE TABLE enrollments (
  enrollment_id INT NOT NULL AUTO_INCREMENT,
  student_id INT NOT NULL,
  section_id INT NOT NULL,
  status VARCHAR(20) DEFAULT 'enrolled',
  PRIMARY KEY (enrollment_id),
  UNIQUE KEY uk_student_section (student_id, section_id),
  FOREIGN KEY (student_id) REFERENCES students(user_id),
  FOREIGN KEY (section_id) REFERENCES sections(section_id)
);

-- -----------------------------
-- TABLE: grades
-- -----------------------------
CREATE TABLE grades (
  grade_id INT NOT NULL AUTO_INCREMENT,
  enrollment_id INT NOT NULL,
  component VARCHAR(50) NOT NULL,
  score DECIMAL(5,2),
  final_grade VARCHAR(2),
  PRIMARY KEY (grade_id),
  UNIQUE KEY uk_enrollment_component (enrollment_id, component),
  FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

-- -----------------------------
-- TABLE: settings
-- -----------------------------
CREATE TABLE settings (
  setting_key VARCHAR(50) NOT NULL,
  setting_value VARCHAR(100),
  PRIMARY KEY (setting_key)
);

------------------------------------------------
-- AUTH DATABASE
------------------------------------------------
DROP DATABASE IF EXISTS auth_db;
CREATE DATABASE auth_db;
USE auth_db;

CREATE TABLE users_auth (
  user_id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  role VARCHAR(20) NOT NULL,
  password_hash VARCHAR(72) NOT NULL,
  status ENUM('active','locked') DEFAULT 'active',
  last_login TIMESTAMP NULL DEFAULT NULL,
  failed_attempts INT DEFAULT 0,
  PRIMARY KEY (user_id),
  UNIQUE (username)
);

SET FOREIGN_KEY_CHECKS = 1;
