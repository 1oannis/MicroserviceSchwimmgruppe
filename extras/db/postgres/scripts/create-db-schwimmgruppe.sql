-- Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
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

-- https://www.postgresql.org/docs/current/sql-createrole.html
CREATE ROLE schwimmgruppe LOGIN PASSWORD 'p';

-- https://www.postgresql.org/docs/current/sql-createdatabase.html
CREATE DATABASE schwimmgruppe;

GRANT ALL ON DATABASE schwimmgruppe TO schwimmgruppe;

-- https://www.postgresql.org/docs/10/sql-createtablespace.html
CREATE TABLESPACE schwimmgruppespace OWNER schwimmgruppe LOCATION '/var/lib/postgresql/tablespace/schwimmgruppe';
