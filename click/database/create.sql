CREATE SCHEMA Trackman;

SET SCHEMA Trackman;

CREATE USER tracker SET PASSWORD 'compass';

DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
	username	VARCHAR(12)		NOT NULL UNIQUE,
	password	VARCHAR(12)		NOT NULL,
	email		VARCHAR(30),
	telephone	VARCHAR(15),
	mobile		VARCHAR(15),
	fax			VARCHAR(15),
	admin_user	BOOLEAN			DEFAULT false NOT NULL,
	active		BOOLEAN			DEFAULT true NOT NULL,
	created_by	VARCHAR(30)		NOT NULL,
	created_at	TIMESTAMP		NOT NULL,
	updated_by	VARCHAR(30)		NOT NULL,
	updated_at	TIMESTAMP		NOT NULL
);
GRANT SELECT, INSERT, UPDATE ON Users TO tracker;

DROP TABLE IF EXISTS Types;
CREATE TABLE Types (
	type_id		INTEGER			NOT NULL UNIQUE,
	sort_order	INTEGER			NOT NULL UNIQUE,
	description	VARCHAR(30)		NOT NULL,
	active		BOOLEAN			DEFAULT true NOT NULL,
	
	CONSTRAINT types_pk PRIMARY KEY (type_id)
);
GRANT SELECT, INSERT, UPDATE ON Types TO tracker;

DROP TABLE IF EXISTS Sub_Types;
CREATE TABLE Sub_Types (
	type_id		INTEGER			NOT NULL,
	sub_type_id	INTEGER			NOT NULL,
	sort_order	INTEGER			NOT NULL,
	description	VARCHAR(30)		NOT NULL,
	active		BOOLEAN			DEFAULT true NOT NULL,
	
	CONSTRAINT sub_types_pk PRIMARY KEY (type_id, sub_type_id),
	CONSTRAINT sub_types_fk FOREIGN KEY (type_id) REFERENCES Types (type_id)
);
GRANT SELECT, INSERT, UPDATE ON Sub_Types TO tracker;

DROP TABLE IF EXISTS Issues;
CREATE TABLE Issues (
	issue_id 	INTEGER 		NOT NULL UNIQUE,
	status 		INTEGER 		DEFAULT 0 NOT NULL,
	type_id		INTEGER			DEFAULT 0 NOT NULL,
	sub_type	INTEGER			DEFAULT 0 NOT NULL,
	summary		VARCHAR(80)		NOT NULL,
	description	VARCHAR(1000)	NULL,
	created_by	VARCHAR(30)		NOT NULL,
	created_at	TIMESTAMP		NOT NULL,
	updated_by	VARCHAR(30)		NOT NULL,
	updated_at	TIMESTAMP		NOT NULL,	
	
	CONSTRAINT issues_pk PRIMARY KEY (issue_id),
	CONSTRAINT issues_fk1 FOREIGN KEY (type_id) REFERENCES Types (type_id),
	CONSTRAINT issues_fk2 FOREIGN KEY (created_by) REFERENCES Users (username),
	CONSTRAINT issues_fk3 FOREIGN KEY (updated_by) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON Issues TO tracker;

DROP TABLE IF EXISTS Resolutions;
CREATE TABLE Resolutions (
	resolution_id	INTEGER			NOT NULL UNIQUE,
	issue_id		INTEGER			NOT NULL,
	description		VARCHAR(1000)	NOT NULL,
	created_by		VARCHAR(30)		NOT NULL,
	created_at		TIMESTAMP		NOT NULL,	
	
	CONSTRAINT resolutions_pk PRIMARY KEY (resolution_id),
	CONSTRAINT resolutions_fk1 FOREIGN KEY (issue_id) REFERENCES Issues (issue_id),
	CONSTRAINT resolutions_fk2 FOREIGN KEY (created_by) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON Resolutions TO tracker;

DROP TABLE IF EXISTS Notes;
CREATE TABLE Notes (
	note_id		INTEGER			NOT NULL UNIQUE,
	issue_id	INTEGER			NOT NULL,
	description	VARCHAR(1000)	NOT NULL,
	created_by	VARCHAR(30)		NOT NULL,
	created_at	TIMESTAMP		NOT NULL,	
	
	CONSTRAINT notes_pk PRIMARY KEY (note_id),
	CONSTRAINT notes_fk1 FOREIGN KEY (issue_id) REFERENCES Issues (issue_id),
	CONSTRAINT notes_fk2 FOREIGN KEY (created_by) REFERENCES Users (username)
);
GRANT SELECT, INSERT, UPDATE ON Notes TO tracker;




