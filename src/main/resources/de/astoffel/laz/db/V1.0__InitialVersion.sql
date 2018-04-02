
-- Copyright (C) 2018 astoffel
-- 
--  This program is free software: you can redistribute it and/or modify
--  it under the terms of the GNU General Public License as published by
--  the Free Software Foundation, either version 3 of the License, or
--  (at your option) any later version.
--  
--  This program is distributed in the hope that it will be useful,
--  but WITHOUT ANY WARRANTY; without even the implied warranty of
--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--  GNU General Public License for more details.
--  
--  You should have received a copy of the GNU General Public License
--  along with this program.  If not, see <http://www.gnu.org/licenses/>.

-- 
-- Author:  astoffel
-- Created: 25.03.2018
-- 

CREATE TABLE Grade (
	id LONG NOT NULL,
	version LONG NOT NULL,
	name VARCHAR(255) NOT NULL,
	displayName VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Category (
	id LONG NOT NULL,
	version LONG NOT NULL,
	name VARCHAR(255) NOT NULL,
	displayName VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Exam (
	id LONG NOT NULL,
	version LONG NOT NULL,
	sort INTEGER NOT NULL,
	name VARCHAR(255) NOT NULL,
	displayName VARCHAR(255) NOT NULL,
	displayShortName VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Exam_Description (
	exam_id LONG NOT NULL,
	category_id LONG NOT NULL,
	description VARCHAR(4096) NOT NULL,
	PRIMARY KEY (exam_id, category_id),
	FOREIGN KEY (exam_id) REFERENCES Exam (id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (category_id) REFERENCES Category (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Instrument (
	id LONG NOT NULL,
	version LONG NOT NULL,
	name VARCHAR(255) NOT NULL,
	displayName VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Jury (
	id LONG NOT NULL,
	version LONG NOT NULL,
	name VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Meta (
	id LONG NOT NULL,
	version LONG NOT NULL,
	location VARCHAR(255) NOT NULL,
	eventDate VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Participant (
	id LONG NOT NULL,
	version LONG NOT NULL,
	name VARCHAR(255) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (name)
);

CREATE TABLE Participation (
	id LONG NOT NULL,
	version LONG NOT NULL,
	participant_id LONG NOT NULL,
	category_id LONG NOT NULL,
	instrument_id LONG NOT NULL,
	jury_id LONG NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (participant_id, category_id, instrument_id),
	FOREIGN KEY (participant_id) REFERENCES Participant (id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (category_id) REFERENCES Category (id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (instrument_id) REFERENCES Instrument (id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (jury_id) REFERENCES Jury (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Assessment (
	id LONG NOT NULL,
	version LONG NOT NULL,
	participation_id LONG NOT NULL,
	exam_id LONG NULL,
	grade_id LONG NULL,
	PRIMARY KEY (id),
	UNIQUE (participation_id, exam_id),
	FOREIGN KEY (participation_id) REFERENCES Participation (id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (exam_id) REFERENCES Exam (id) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (grade_id) REFERENCES Grade (id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE SEQUENCE hibernate_sequence;

