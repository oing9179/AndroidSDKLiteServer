-- Execute the sql to initialize the database.
-- Minimum sqlite version: 3.

-- xml repository.
CREATE TABLE "repo_xml" (
  "id"                 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "name"               TEXT                              NOT NULL UNIQUE,
  "date_creation"      INTEGER                           NOT NULL,
  "date_last_modified" INTEGER
);

-- xml files who in xml repository.
CREATE TABLE "repo_xml_file" (
  "id"                INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "id_repo_xml"       INTEGER                           NOT NULL,
  "file_name"         TEXT                              NOT NULL,
  "url"               TEXT                              NOT NULL,
  "zip_sub_directory" TEXT,
  CONSTRAINT "fk_repo_xml_file__to__repo_xml_0" FOREIGN KEY ("id_repo_xml") REFERENCES "repo_xml" ("id")
);

-- zip repository
CREATE TABLE "repo_zip" (
  "id"                 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "id_repo_xml"        INTEGER, -- Zip repo depends on NO xml repo is ok, depends on a EXIST xml repo is better.
  "name"               TEXT                              NOT NULL UNIQUE,
  "date_creation"      INTEGER                           NOT NULL,
  "date_last_modified" INTEGER,
  CONSTRAINT "fk_repo_xml_file__to__repo_xml_0" FOREIGN KEY ("id_repo_xml") REFERENCES "repo_xml" ("id")
);
