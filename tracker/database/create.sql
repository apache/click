
CREATE SCHEMA Tracker;

SET SCHEMA Tracker;

CREATE USER scout SET PASSWORD 'compass';

CREATE TABLE Users (
	username	VARCHAR(12)		NOT NULL UNIQUE,
	password	VARCHAR(12)		NOT NULL,
	email		VARCHAR(30),
	telephone	VARCHAR(15),
	mobile		VARCHAR(15),
	fax			VARCHAR(15),
	active		BOOLEAN			DEFAULT true NOT NULL,
	created_by	VARCHAR(30)		NOT NULL,
	created_at	TIMESTAMP		NOT NULL,
	updated_by	VARCHAR(30)		NOT NULL,
	updated_at	TIMESTAMP		NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (username)
);
GRANT SELECT, INSERT, UPDATE ON Users TO scout;

CREATE TABLE User_Roles (
	username	VARCHAR(12)		NOT NULL,
	rolename	VARCHAR(12)		NOT NULL,
	CONSTRAINT user_roles_pk PRIMARY KEY (username, rolename),
	CONSTRAINT user_roles_fk1 FOREIGN KEY (username) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON User_Roles TO scout;

CREATE TABLE Category (
	category_id	INTEGER			NOT NULL UNIQUE,
	sort_order	INTEGER			NOT NULL UNIQUE,
	description	VARCHAR(30)		NOT NULL,
	active		BOOLEAN			DEFAULT true NOT NULL,	
	CONSTRAINT category_pk PRIMARY KEY (category_id)
);
GRANT SELECT, INSERT, UPDATE ON Category TO scout;
CREATE SEQUENCE category_seq;
GRANT SELECT ON category_seq TO scout;

DROP TABLE IF EXISTS Priority;
CREATE TABLE Priority (
	priority_id	INTEGER			NOT NULL UNIQUE,
	sort_order	INTEGER			NOT NULL UNIQUE,
	description	VARCHAR(30)		NOT NULL,
	active		BOOLEAN			DEFAULT true NOT NULL,
	CONSTRAINT priority_pk PRIMARY KEY (priority_id)
);
GRANT SELECT, INSERT, UPDATE ON Priority TO scout;
CREATE SEQUENCE priority_seq;
GRANT SELECT ON priority_seq TO scout;

CREATE TABLE Severity (
	severity_id	INTEGER			NOT NULL UNIQUE,
	sort_order	INTEGER			NOT NULL UNIQUE,
	description	VARCHAR(30)		NOT NULL,
	active		BOOLEAN			DEFAULT true NOT NULL,
	CONSTRAINT severity_pk PRIMARY KEY (severity_id)
);
GRANT SELECT, INSERT, UPDATE ON Severity TO scout;
CREATE SEQUENCE severity_seq;
GRANT SELECT ON severity_seq TO scout;

CREATE TABLE Status (
	status_id	INTEGER			NOT NULL UNIQUE,
	sort_order	INTEGER			NOT NULL UNIQUE,
	description	VARCHAR(30)		NOT NULL,
	active		BOOLEAN			DEFAULT true NOT NULL,
	CONSTRAINT status_pk PRIMARY KEY (status_id)
);
GRANT SELECT, INSERT, UPDATE ON Status TO scout;
CREATE SEQUENCE status_seq;
GRANT SELECT ON status_seq TO scout;

CREATE TABLE Version (
	version_id	INTEGER			NOT NULL UNIQUE,
	sort_order	INTEGER			NOT NULL UNIQUE,
	description	VARCHAR(30)		NOT NULL UNIQUE,
	active		BOOLEAN			DEFAULT true NOT NULL,
	CONSTRAINT version_pk PRIMARY KEY (version_id)
);
GRANT SELECT, INSERT, UPDATE ON Version TO scout;
CREATE SEQUENCE version_seq;
GRANT SELECT ON version_seq TO scout;

CREATE TABLE Issues (
	issue_id 	INTEGER 		NOT NULL UNIQUE,
	status_id	INTEGER 		DEFAULT 0 NOT NULL,
	severity_id	INTEGER			DEFAULT 0 NOT NULL,
	priority_id	INTEGER			DEFAULT 0 NOT NULL,
	category_id	INTEGER			DEFAULT 0 NOT NULL,
	version_id	INTEGER			DEFAULT 0 NOT NULL,
	summary		VARCHAR(80)		NOT NULL,
	description	VARCHAR(1000)	NULL,
	created_by	VARCHAR(12)		NOT NULL,
	created_at	TIMESTAMP		NOT NULL,
	updated_by	VARCHAR(12)		NOT NULL,
	updated_at	TIMESTAMP		NOT NULL,	
	CONSTRAINT issues_pk PRIMARY KEY (issue_id),
	CONSTRAINT issues_fk1 FOREIGN KEY (status_id) REFERENCES Status (status_id),
	CONSTRAINT issues_fk2 FOREIGN KEY (severity_id) REFERENCES Severity (severity_id),
	CONSTRAINT issues_fk3 FOREIGN KEY (priority_id) REFERENCES Priority (priority_id),
	CONSTRAINT issues_fk4 FOREIGN KEY (category_id) REFERENCES Category (category_id),
	CONSTRAINT issues_fk5 FOREIGN KEY (version_id) REFERENCES Version (version_id),
	CONSTRAINT issues_fk6 FOREIGN KEY (created_by) REFERENCES Users (username),
	CONSTRAINT issues_fk7 FOREIGN KEY (updated_by) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON Issues TO scout;
CREATE SEQUENCE issues_seq;
GRANT SELECT ON issues_seq TO scout;

CREATE TABLE Changes (
	change_id	INTEGER			NOT NULL UNIQUE,
	issue_id	INTEGER			NOT NULL,
	description	VARCHAR(1000)	NOT NULL,
	created_by	VARCHAR(12)		NOT NULL,
	created_at	TIMESTAMP		NOT NULL,	
	CONSTRAINT changes_pk PRIMARY KEY (change_id),
	CONSTRAINT changes_fk1 FOREIGN KEY (issue_id) REFERENCES Issues (issue_id),
	CONSTRAINT changes_fk2 FOREIGN KEY (created_by) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON Changes TO scout;
CREATE SEQUENCE changes_seq;
GRANT SELECT ON changes_seq TO scout;

CREATE TABLE Notes (
	note_id		INTEGER			NOT NULL UNIQUE,
	issue_id	INTEGER			NOT NULL,
	description	VARCHAR(1000)	NOT NULL,
	created_by	VARCHAR(12)		NOT NULL,
	created_at	TIMESTAMP		NOT NULL,	
	CONSTRAINT notes_pk PRIMARY KEY (note_id),
	CONSTRAINT notes_fk1 FOREIGN KEY (issue_id) REFERENCES Issues (issue_id),
	CONSTRAINT notes_fk2 FOREIGN KEY (created_by) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON Notes TO scout;
CREATE SEQUENCE notes_seq;
GRANT SELECT ON notes_seq TO scout;

CREATE TABLE Resolutions (
	resolution_id	INTEGER			NOT NULL UNIQUE,
	issue_id		INTEGER			NOT NULL,
	description		VARCHAR(1000)	NOT NULL,
	created_by		VARCHAR(12)		NOT NULL,
	created_at		TIMESTAMP		NOT NULL,	
	CONSTRAINT resolutions_pk PRIMARY KEY (resolution_id),
	CONSTRAINT resolutions_fk1 FOREIGN KEY (issue_id) REFERENCES Issues (issue_id),
	CONSTRAINT resolutions_fk2 FOREIGN KEY (created_by) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON Resolutions TO scout;
CREATE SEQUENCE resolutions_seq;
GRANT SELECT ON resolutions_seq TO scout;


INSERT INTO Status (status_id, sort_order, description, active) values (0, 0, 'Open', true);
INSERT INTO Status (status_id, sort_order, description, active) values (1, 1, 'Assigned', true);
INSERT INTO Status (status_id, sort_order, description, active) values (2, 2, 'Resolved', true);
INSERT INTO Status (status_id, sort_order, description, active) values (3, 3, 'Closed', true);
INSERT INTO Status (status_id, sort_order, description, active) values (4, 4, 'Duplicate', true);

INSERT INTO Severity (severity_id, sort_order, description, active) values (0, 0, '', true);
INSERT INTO Severity (severity_id, sort_order, description, active) values (1, 1, 'Major', true);
INSERT INTO Severity (severity_id, sort_order, description, active) values (2, 2, 'Moderate', true);
INSERT INTO Severity (severity_id, sort_order, description, active) values (3, 3, 'Minor', true);
INSERT INTO Severity (severity_id, sort_order, description, active) values (4, 4, 'Blocking', true);
INSERT INTO Severity (severity_id, sort_order, description, active) values (5, 5, 'Tweak', true);
INSERT INTO Severity (severity_id, sort_order, description, active) values (6, 6, 'Note', true);

INSERT INTO Priority (priority_id, sort_order, description, active) values (0, 0, '', true);
INSERT INTO Priority (priority_id, sort_order, description, active) values (1, 1, 'High', true);
INSERT INTO Priority (priority_id, sort_order, description, active) values (2, 2, 'Medium', true);
INSERT INTO Priority (priority_id, sort_order, description, active) values (3, 3, 'Low', true);

INSERT INTO Category (category_id, sort_order, description, active) values (0, 0, '', true);

INSERT INTO Version (version_id, sort_order, description, active) values (0, 0, '', true);





