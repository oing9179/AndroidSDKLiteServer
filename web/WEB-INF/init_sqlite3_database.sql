-- Execute the sql to initialize the database.
-- Minimum sqlite version: 3

-- xml repository.
CREATE TABLE "main"."repo_xml" (
	"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	"name"  TEXT NOT NULL,
	"date_creation" INTEGER NOT NULL,
	"date_last_modified" INTEGER
);

-- xml files who in xml repository.
CREATE TABLE "main"."repo_xml_file" (
	"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	"id_repo_xml"  INTEGER NOT NULL,
	"file_name"  TEXT NOT NULL,
	"url"  TEXT NOT NULL,
	CONSTRAINT "fk_repo_xml_file__repo_xml_0" FOREIGN KEY ("id_repo_xml") REFERENCES "repo_xml" ("id")
);

-- zip repository
CREATE TABLE "main"."repo_zip" (
	"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	"id_repo_xml"  INTEGER, -- Zip repo depends on NO xml repo is ok, depends on a EXIST xml repo is better.
	"name"  TEXT NOT NULL,
	"date_creation" INTEGER NOT NULL,
	"date_last_modified" INTEGER,
	CONSTRAINT "fk_repo_zip__repo_xml_0" FOREIGN KEY ("id_repo_xml") REFERENCES "repo_xml" ("id")
) ;

-- Catches the result after parse the xml file.
-- The table name is "sdk_archive" cause an element called "<sdk:archive>" written in the xml file.
CREATE TABLE "main"."sdk_archive" (
	"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	"id_repo_xml_file"  INTEGER NOT NULL,
	"display_name"  TEXT,
	"description"  TEXT,
	"version"  TEXT,
	"revision"  TEXT,
	"api_level"  INTEGER,
	"is_obsolete"  INTEGER NOT NULL,
	"url"  TEXT NOT NULL,
	"checksum_type"  TEXT,
	"checksum"  TEXT,
	"size"  INTEGER NOT NULL,
	"host_os"  TEXT,
	"host_bits"  TEXT,
	CONSTRAINT "fk_sdk_archive__repo_xml_file_0" FOREIGN KEY ("id_repo_xml_file") REFERENCES "repo_xml_file" ("id")
);


