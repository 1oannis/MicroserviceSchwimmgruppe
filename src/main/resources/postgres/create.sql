-- Copyright (C) 2022 - present Ioannis Theodosiadis, Hochschule Karlsruhe
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.

-- docker compose exec postgres bash
-- psql --dbname=schwimmgruppe --username=schwimgruppe --file=/scripts/create.sql

-- https://www.postgresql.org/docs/devel/app-psql.html
-- https://www.postgresql.org/docs/current/ddl-schemas.html
-- https://www.postgresql.org/docs/current/ddl-schemas.html#DDL-SCHEMAS-CREATE
-- "user-private schema" (Default-Schema: public)
CREATE SCHEMA IF NOT EXISTS AUTHORIZATION schwimmgruppe;

ALTER ROLE schwimmgruppe SET search_path = 'schwimmgruppe';

-- https://www.postgresql.org/docs/current/sql-createtable.html
-- https://www.postgresql.org/docs/current/datatype.html
-- BEACHTE: user ist ein Schluesselwort
CREATE TABLE IF NOT EXISTS login (
             -- https://www.postgresql.org/docs/current/datatype-uuid.html
             -- https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-PRIMARY-KEYS
             -- impliziter Index fuer Primary Key
    id       uuid PRIMARY KEY USING INDEX TABLESPACE schwimmgruppespace,
    username varchar(20) UNIQUE NOT NULL,
    password varchar(150) NOT NULL
) TABLESPACE schwimmgruppespace;

CREATE TABLE IF NOT EXISTS login_rollen (
             -- https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK
    login_id uuid NOT NULL REFERENCES login,
             -- https://www.postgresql.org/docs/current/ddl-constraints.html#id-1.5.4.6.6
             -- https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-REGEXP
    rolle    varchar(20) NOT NULL CHECK (rolle ~ 'ADMIN|SCHWIMMGRUPPE|ACTUATOR'),

    PRIMARY KEY (login_id, rolle) USING INDEX TABLESPACE schwimmgruppespace
) TABLESPACE schwimmgruppespace;

-- https://www.postgresql.org/docs/docs/sql-createindex.html
CREATE INDEX IF NOT EXISTS login_rollen_idx ON login_rollen(login_id) TABLESPACE schwimmgruppespace;

CREATE TABLE IF NOT EXISTS schwimmhalle (
    id            uuid PRIMARY KEY USING INDEX TABLESPACE schwimmgruppespace,
    bezeichnung   varchar(40) NOT NULL,
    plz           char(5) NOT NULL CHECK (plz ~ '\d{5}'),
    ort           varchar(40) NOT NULL
) TABLESPACE schwimmgruppespace;

CREATE INDEX IF NOT EXISTS schwimmhalle_plz_idx ON schwimmhalle(plz) TABLESPACE schwimmgruppespace;

CREATE TABLE IF NOT EXISTS schwimmgruppe (
    id                uuid PRIMARY KEY USING INDEX TABLESPACE schwimmgruppespace,
                      -- https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-INT
    name              varchar(20) NOT NULL,
                      -- impliziter Index als B-Baum durch UNIQUE
                      -- https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-CHECK-CONSTRAINTS
                      -- https://www.postgresql.org/docs/current/datatype-boolean.html
    aktiv             boolean,
                      -- https://www.postgresql.org/docs/current/datatype-datetime.html
    trainingstermin1  timestamp CHECK (trainingstermin1 > current_timestamp),
    trainingstermin2  timestamp CHECK (trainingstermin2 > current_timestamp),
    ligaklasse        varchar(5) CHECK (ligaklasse ~ 'KEINE|SL|LL|2.BL|1.BL'),
    schwimmhalle_id   uuid REFERENCES schwimmhalle,
    erzeugt           timestamp NOT NULL,
    aktualisiert      timestamp NOT NULL
) TABLESPACE schwimmgruppespace;

CREATE INDEX IF NOT EXISTS schwimmgruppe_name_idx ON schwimmgruppe(name) TABLESPACE schwimmgruppespace;
