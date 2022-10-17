# Copyright (C) 2021 -  Juergen Zimmermann, Hochschule Karlsruhe
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

# Aufruf:   .\delete-pvc.ps1

Set-StrictMode -Version Latest

$versionMinimum = [Version]'7.3.0'
$versionCurrent = $PSVersionTable.PSVersion
if ($versionMinimum -gt $versionCurrent) {
    throw "PowerShell $versionMinimum statt $versionCurrent erforderlich"
}

# Titel setzen
$host.ui.RawUI.WindowTitle = 'postgres delete pvc'

$namespace = 'acme'
kubectl delete pvc/postgres-data-volume-postgres-0 --namespace $namespace
kubectl delete pvc/postgres-conf-volume-postgres-0 --namespace $namespace
kubectl delete pvc/postgres-tablespace-volume-postgres-0 --namespace $namespace
kubectl delete pvc/pgadmin-pgadmin-volume-pgadmin-0 --namespace $namespace
kubectl delete pvc/pgadmin-pgadmin4-volume-pgadmin-0 --namespace $namespace
