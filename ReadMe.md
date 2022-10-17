# Hinweise zum Programmierbeispiel

[Juergen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)

> Diese Datei ist in Markdown geschrieben und kann z.B. mit IntelliJ IDEA
> gelesen werden. Näheres zu Markdown gibt es z.B. bei
> [Markdown Guide](https://www.markdownguide.org/) oder
> [JetBrains](https://www.jetbrains.com/help/hub/Markdown-Syntax.html)

> Bevor man mit der Projektarbeit an der 2. Abgabe beginnt, sichert man sich
> die 1. Abgabe, u.a. weil für die 2. Abgabe auch die Original-Implementierung
> aus der 1. Abgabe benötigt wird.

Inhalt

- [Eigener Namespace in Kubernetes](#eigener-Namespace-in-Kubernetes)
- [Relationale Datenbanksysteme](#relationale-Datenbanksysteme)
  - [PostgreSQL](#postgreSQL)
  - [MySQL](#mySQL)
  - [Oracle](#oracle)
- [Administration des Kubernetes-Clusters](#administration-des-Kubernetes-Clusters)
- [Übersetzung und lokale Ausführung](#übersetzung-und-lokale-Ausführung)
  - [Ausführung in IntelliJ IDEA](#ausführung-in-IntelliJ-IDEA)
  - [Start und Stop des Servers in der Kommandozeile](#start-und-Stop-des-Servers-in-der-Kommandozeile)
- [HTTP Client von IntelliJ IDEA](#hTTP-Client-von-IntelliJ-IDEA)
- [OpenAPI mit Swagger](#openAPI-mit-Swagger)
- [Unit Tests und Integrationstests](#unit-Tests-und-Integrationstests)
- [Rechnername in der Datei hosts](#rechnername-in-der-Datei-hosts)
- [Microservice mit Kubernetes, Kustomize und Skaffold](#microservice-mit-Kubernetes,-Kustomize-und-Skaffold)
  - [WICHTIG: Schreibrechte für die Log Datei](#wICHTIG:-Schreibrechte-für-die-Log-Datei)
  - [Image für Kubernetes erstellen](#image-für-Kubernetes-erstellen)
  - [Deployment mit Kustomize](#deployment-mit-Kustomize)
  - [Port Forwarding](#port-Forwarding)
  - [Continuous Deployment mit Skaffold](#continuous-Deployment-mit-Skaffold)
- [Istio](#istio)
  - [Installation von Istio](#installation-von-Istio)
  - [Namespace für Istio aufbereiten](#namespace-für-Istio-aufbereiten)
  - [Gateway, VirtualService und Routing](#gateway,-VirtualService-und-Routing)
  - [Gateway mit HTTP](#gateway-mit-HTTP)
  - [Gateway mit HTTPS](#gateway-mit-HTTPS)
- [Codeanalyse durch ktlint und detekt](#codeanalyse-durch-ktlint-und-detekt)
- [Codeanalyse durch SonarQube](#codeanalyse-durch-SonarQube)
- [Dokumentation durch AsciiDoctor und PlantUML](#dokumentation-durch-AsciiDoctor-und-PlantUML)
- [API Dokumentation durch Dokka](#aPI-Dokumentation-durch-Dokka)
- [Einfache Lasttests mit hey und  Fortio](#einfache-Lasttests-mit-hey-und-Fortio)

---

## Eigener Namespace in Kubernetes

Ein eigener Namespace in Kubernetes, z.B. `acme`, wird durch folgendes Kommando
angelegt:

```PowerShell
    kubectl create namespace acme
```

---

## Relationale Datenbanksysteme

### PostgreSQL

#### Docker Compose für PostgreSQL und pgadmin

Wenn man den eigenen Microservice direkt mit Windows - nicht mit Kubernetes -
laufen lässt, kann man PostgreSQL und das Administrationswerkzeug pgadmin
einfach mit _Docker Compose_ starten und später auch herunterfahren.

> ❗ Vor dem 1. Start von PostgreSQL muss man in `docker-compose.yaml` im
> Verzeichnis extras\db\postgres die Zeile mit dem (eingeschränkten) Linux-User
> "postgres:postgres" auskommentieren, damit die Initialisierung von PostgreSQL
> als Linux-User `root` ausgeführt werden kann. Danach kopiert man die beiden
> Skripte `create-db-schwimmgruppe.sh` und `create-db-schwimmgruppe.sql` aus dem Verzeichnis
> `extras\db\postgres\scripts` nach `C:\Zimmermann\volumes\postgres\scripts`.
> Für die Windows-Verzeichnisse `C:\Zimmermann\volumes\postgres\data`,
> `C:\Zimmermann\volumes\postgres\tablespace` und
> `C:\Zimmermann\volumes\postgres\tablespace\schwimmgruppe` muss außerdem Vollzugriff
> gewährt werden, was über das Kontextmenü mit _Eigenschaften_ und den
> Karteireiter _Sicherheit_ für die Windows-Gruppe _Benutzer_ eingerichtet
> werden kann. Nun kann man das Auskommentieren des eingeschränkten Linux-Users
> in `docker-compose.yaml` wieder rückgängig machen, damit der DB-Server fortan
> nicht als Superuser "root" läuft. Übrigens ist das Emoji für das Ausrufezeichen
> von https://emojipedia.org.

```PowerShell
    cd extras\db\postgres
    docker compose up

    # Herunterfahren in einer 2. Shell:
    cd extras\db\postgres
    docker compose down
```

Der Name des Docker-Containers lautet `postgres` und ebenso lautet der
_virtuelle Rechnername_ `postgres`. Der virtuelle Rechnername `postgres`
wird später auch als Service-Name für PostgreSQL in Kubernetes verwendet.

> ❗ Nach dem 1. Start des PostgreSQL-Servers muss man einmalig den
> Datenbank-User `schwimmgruppe` und dessen Datenbank `schwimmgruppe` anlegen, d.h. der neue
> Datenbank-User `schwimmgruppe` wird zum Owner der Datenbank `schwimmgruppe`. Dazu muss man
> sich mit dem Docker-Container mit Namen `postgres` verbinden und im
> Docker-Container das `bash`-Skript ausführen:

```PowerShell
    docker compose exec postgres bash /scripts/create-db-schwimmgruppe.sh
```

Statt eine PowerShell zu verwenden, kann man Docker Compose auch direkt in
IntelliJ aufrufen, indem man über das Kontextmenü ("rechte Maustaste") den
Unterpunkt _Run 'docker-compose.yaml:...'_ aufruft. Im Tool-Window _Services_
sieht man dann unterhalb von _Docker_ den Eintrag _Docker-compose: postgres_ mit
dem Service _postgres_.

Jetzt läuft der PostgreSQL- bzw. DB-Server. Die Datenbank-URL für den eigenen
Microservice als DB-Client lautet: `postgresql://localhost/schwimmgruppe`, dabei ist
`localhost` aus Windows-Sicht der Rechnername, der Port defaultmäßig `5432`
und der Datenbankname `schwimmgruppe`.

Außerdem kann _pgadmin_ zur Administration verwendet werden. pgadmin läuft
ebenfalls als Docker-Container und ist über ein virtuelles Netzwerk mit dem
Docker-Container des DB-Servers verbunden. Deshalb muss beim Verbinden mit dem
DB-Server auch der virtuelle Rechnername `postgres` verwendet werden. Man ruft
also pgadmin mit Chrome und der URL `http://localhost:8888` auf.
Die Emailadresse `pgadmin@acme.com` und das Passwort `p` sind voreingestellt.
Da pgadmin mit Chromium implementiert ist, empfiehlt es sich, Chrome als
Webbrowser zu verwenden.

Beim 1. Einloggen konfiguriert man einen Server-Eintrag mit z.B. dem Namen
`postgres-container` und verwendet folgende Werte:

- Host: `postgres` (virtueller Rechnername des DB-Servers im Docker-Netzwerk.
  **BEACHTE**: `localhost` ist im virtuellen Netzwerk der Name des
  pgadmin-Containers selbst !!!)
- Port: `5432` (Defaultwert)
- Username: `postgres` (Superuser beim DB-Server)
- Password: `p`

Es empfiehlt sich, das Passwort abzuspeichern, damit man es künftig nicht jedes
Mal beim Einloggen eingeben muss.

#### Skaffold für PostgreSQL und pgadmin

Wenn der eigene Microservice in Kubernetes gestartet werden soll (s.u.), muss
_PostgreSQL_ zuvor in Kubernetes gestartet werden, was mit _Skaffold_ gemacht
werden kann. Wenn die Umgebungsvariable `SKAFFOLD_PROFILE` auf den Wert `dev`
gesetzt ist, dann wird das Profile `dev` verwendet, welches das
Kustomize-Overlay `dev` aufruft. Bis das Port-Forwarding, das in
`skaffold.yaml` konfiguriert ist, auch ausgeführt wird, muss man ein bisschen
warten.

```PowerShell
    cd extras\db\postgres\kubernetes
    skaffold dev --no-prune=false --cache-artifacts=false
```

Dabei wurde auch das Administrationswerkzeug _pgadmin_ innerhalb von Kubernetes
gestartet und kann wegen Port-Forwarding mit `http://localhost:8888` aufgerufen
werden.

Mit `<Strg>C` kann das Deployment wieder zurückgerollt werden. Ohne die beiden
Optionen muss man noch manuell die 4 _PersistentVolumeClaim_ mit den Namen
`postgres-data-volume-postgres-0`, `postgres-conf-volume-postgres-0`,
`pgadmin-pgadmin-volume-pgadmin-0` und `pgadmin-pgadmin4-volume-pgadmin-0`
löschen, die durch die _StatefulSet_ `postgres` und `pgadmin` erstellt wurden.
Dazu gibt es das PowerShell-Skript `delete-pvc.ps1` im Verzeichnis
`extras\db\postgres\kubernetes`.

---

### MySQL

#### Docker Compose für MySQL und phpMyAdmin

Wenn man den eigenen Microservice direkt mit Windows - nicht mit Kubernetes -
laufen lässt, kann man MySQL und das Administrationswerkzeug phpMyAdmin einfach
mit _Docker Compose_ starten und später auch herunterfahren.

> ❗ Vor dem 1. Start von MySQL muss man die Skripte `create-db-schwimmgruppe.sh` und
> `create-db-schwimmgruppe.sql` aus dem Projektverzeichnis
> `extras\mysql\scripts` nach `C:\Zimmermann\volumes\mysql\scripts` kopieren.

```PowerShell
    cd extras\db\mysql
    docker compose up

    # Herunterfahren in einer 2. Shell:
    cd extras\db\mysql
    docker compose down
```

Der Name des Docker-Containers und des _virtuellen Rechners_ lautet `mysql`.
Der virtuelle Rechnername wird später auch als Service-Name für MySQL in
Kubernetes verwendet.

> ❗ Nach dem 1. Start des DB-Servers muss man einmalig den Datenbank-User
> `schwimmgruppe` und dessen Datenbank `schwimmgruppe` anlegen, d.h. der neue Datenbank-User
> `schwimmgruppe` wird zum Owner der Datenbank `schwimmgruppe`. Dazu muss man sich mit dem
> Docker-Container mit Namen `mysql` verbinden und im Docker-Container das
> `bash`-Skript ausführen:

```PowerShell
    docker compose exec mysql bash /scripts/create-db-schwimmgruppe.sh
```

Statt eine PowerShell zu verwenden, kann man Docker Compose auch direkt in
IntelliJ aufrufen, indem man über das Kontextmenü ("rechte Maustaste") den
Unterpunkt _Run 'docker-compose.yaml:...'_ aufruft. Im Tool-Window _Services_
sieht man dann unterhalb von _Docker_ den Eintrag _Docker-compose: mysql_ mit
dem Service _mysql_.

Jetzt läuft der DB-Server. Die Datenbank-URL für den eigenen Microservice als
DB-Client lautet: `mysql://localhost/schwimmgruppe`. Dabei ist `localhost` aus
Windows-Sicht der Rechnername, der Port defaultmäßig `3306` und der
Datenbankname `schwimmgruppe`.

Außerdem kann _phpMyAdmin_ zur Administration verwendet werden. phpMyAdmin läuft
ebenfalls als Docker-Container und ist über ein virtuelles Netzwerk mit dem
Docker-Container des DB-Servers verbunden. Deshalb muss beim Verbinden mit dem
DB-Server auch der virtuelle Rechnername `mysql` verwendet werden.
Man ruft also phpMyAdmin mit einem Webbrowser und der URL `http://localhost:8889`
auf. Zum Einloggen verwendet folgende Werte:

- Server: `mysql` (virtueller Rechnername des DB-Servers im Docker-Netzwerk.
  **BEACHTE**: `localhost` ist im virtuellen Netzwerk der Name des
  phpMyAdmin-Containers selbst !!!)
- Benutzername: `root` (Superuser beim DB-Server)
- Password: `p`

#### Skaffold für MySQL und phpMyAdmin

Wenn der eigene Microservice in Kubernetes gestartet werden soll (s.u.), muss
_MySQL_ zuvor in Kubernetes gestartet werden, was mit _Skaffold_ gemacht werden
kann. Wenn die Umgebungsvariable `SKAFFOLD_PROFILE` auf den Wert `dev` gesetzt
ist, wird das Profile `dev` verwendet, welches das Kustomize-Overlay `dev`
aufruft. Bis das Port-Forwarding, das in `skaffold.yaml` konfiguriert ist, auch
ausgeführt wird, muss man ein bisschen warten.

```PowerShell
    cd extras\db\mysql\kubernetes
    skaffold dev --no-prune=false --cache-artifacts=false
```

Dabei wurde auch das Administrationswerkzeug _phpMyAdmin_ innerhalb von Kubernetes
gestartet und kann wegen Port-Forwarding mit `http://localhost:8889` aufgerufen
werden.

Mit `<Strg>C` kann das Deployment wieder zurückgerollt werden. Ohne die beiden
Optionen muss man noch manuell das _PersistentVolumeClaim_ mit den Namen
`mysql-db-volume-mysql-0` löschen, das durch das _StatefulSet_ `mysql` erstellt
wurde. Dazu gibt es das PowerShell-Skript `delete-pvc.ps1` im Verzeichnis
`extras\db\mysql\kubernetes`.

---

### Oracle

#### Docker Compose für Oracle

Wenn man den eigenen Microservice direkt mit Windows - nicht mit Kubernetes -
laufen lässt, kann man Oracle einfach mit _Docker Compose_ starten und später
auch herunterfahren.

> ❗ Das erstmalige Hochfahren von Oracle XE kann bis zu 10 Minuten dauern.
> Dabei werden auch die beiden üblichen Oracle-User `SYS` und `SYSTEM` jeweils
> mit dem Passwort `p` angelegt.

```PowerShell
    cd extras\db\oracle
    docker compose up

    # Herunterfahren in einer 2. Shell:
    cd extras\db\oracle
    docker compose down
```

Der Name des Docker-Containers und des _virtuellen Rechners_ lautet `oracle`.
Der virtuelle Rechnername wird später auch als Service-Name für
Oracle in Kubernetes verwendet.

> ❗ Nach dem 1. Start des DB-Servers muss man einmalig den Datenbank-User
> `schwimmgruppe`, den Tablespace `schwimmgruppespace` und das Schema `schwimmgruppe` für den gleichnamigen
> User anlegen. Dazu muss man ggf. _SQLcl_ von https://www.oracle.com/de/tools/downloads/sqlcl-downloads.html
> herunterladen und die ZIP-Datei in `C:\Zimmermann` auspacken, so dass es die
> Datei `C:\Zimmermann\sqlcl\bin\sql.exe` gibt. Außerdem muss man die Umgebungsvariable
> `PATH` um `C:\Zimmermann\sqlcl\bin` ergänzen. Danach kann man folgendes PowerShell-Skript
> ausführen:

```PowerShell
    cd extras\db\oracle\scripts
    .\create-schwimmgruppe.ps1
```

Statt Docker Compose (s.o.) über die PowerShell zu starten, kann man Docker Compose
auch direkt in IntelliJ aufrufen, indem man über das Kontextmenü ("rechte Maustaste")
den Unterpunkt _Run 'docker-compose.yaml:...'_ aufruft. Im Tool-Window _Services_
sieht man dann unterhalb von _Docker_ den Eintrag _Docker-compose: oracle_ mit
dem Service _oracle_.

Die Datenbank-URL für den eigenen Microservice und auch für _SQL Developer_
als grafischen DB-Client lautet: `oracle:thin:schwimmgruppe/p@localhost/XEPDB1`.
Dabei ist

- `schwimmgruppe` der Benutzername,
- `p` das Passwort
- `localhost` aus Windows-Sicht der Rechnername
- der Port defaultmäßig `1521` und
- `XEPDB1` (XE Portable Database) der Name der Default-Datenbank nach dem 1. Start.

#### Skaffold für Oracle

Wenn der eigene Microservice in Kubernetes gestartet werden soll (s.u.), muss
_Oracle_ zuvor in Kubernetes gestartet werden, was mit _Skaffold_
gemacht werden kann. Wenn die Umgebungsvariable `SKAFFOLD_PROFILE` auf den Wert
`dev` gesetzt ist, wird das Profile `dev` verwendet, welches das Kustomize-Overlay
`dev` aufruft. Bis das Port-Forwarding, das in `skaffold.yaml` konfiguriert ist,
auch ausgeführt wird, muss man ein bisschen warten.

```PowerShell
    cd extras\db\oracle\kubernetes
    skaffold dev --no-prune=false --cache-artifacts=false
```

Mit `<Strg>C` kann das Deployment wieder zurückgerollt werden. Ohne die beiden
Optionen muss man noch manuell das _PersistentVolumeClaim_ mit den Namen
`oracle-oradata-volume-oracle-0` löschen, das durch
das _StatefulSet_ `oracle` erstellt wurde. Dazu gibt es das
PowerShell-Skript `delete-pvc.ps1` im Verzeichnis `extras\db\oracle\kubernetes`.

---

## Administration des Kubernetes Clusters

Zur Administration des Kubernetes-Clusters ist für Entwickler*innen m.E. _Lens_
oder _Octant_ von VMware Tanzu oder _Kui_ von IBM gut geeignet.

---

## Übersetzung und lokale Ausführung

### Ausführung in IntelliJ IDEA

In der Menüleiste am rechten Rand ist ein Auswahlmenü, in dem i.a. _Application_
zu sehen ist. In diesem Auswahlmenü wählt man den ersten Menüpunkt
`Edit Configurations ...` aus.

Falls das Label _Program Arguments_ nicht sichtbar ist, kann man es
aktivieren, indem man auf _Modify options_ klickt und im Abschnitt _Java_
_Program Arguments_ auswählt. Dann trägt man folgendes ein:

```Text
  --spring.output.ansi.enabled=ALWAYS --spring.config.location=classpath:/application.yml --spring.profiles.default=dev --spring.profiles.active=dev
```

Falls das Label _Environment variables_ nicht sichtbar ist, kann man es
aktivieren, indem man auf _Modify options_ klickt und im Abschnitt _Operating
System_ auswählt. Dann trägt man folgenden String ein:

```Text
    LOG_PATH=./build/log;APPLICATION_LOGLEVEL=trace;HIBERNATE_LOGLEVEL=debug
```

Wenn man für z.B. die Interaktion mit dem 2. Microservice "bestellung"
TLS deaktivieren möchte, dann gibt man im Eingabefeld _Program Arguments_ noch
zusätzlich folgenden String ein:

```Text
    --server.http2.enabled=false --server.ssl.enabled=false
```

Falls man den Port z.B. von `8080` auf `8081` beim Microservice "bestellung"
ändern möchte, dann ergänzt man außerdem noch ` --server.port=8081`.

### Start und Stop des Servers in der Kommandozeile

Nachdem das Port-Forwarding für den DB-Server aufgerufen wurde, kann man in einer
Powershell den Server mit dem Profil `dev` starten:

```PowerShell
    .\gradlew bootRun
```

Mit `<Strg>C` kann man den Server herunterfahren, weil in der Datei
`application.yml` im Verzeichnis `src\main\resources` _graceful shutdown_
konfiguriert ist.

Mit dem Kommando `.\gradlew bootRun -Dport=8081` kann man den Server auf Port
`8081` statt auf Port `8080` laufen lassen.

Mit dem Kommando `.\gradlew bootRun -DnoTls=true` kann man den Server ohne TLS
laufen lassen. Das ist insbesondere dann notwendig, wenn ein 2. Server getestet
werden soll und dieser ohne TLS läuft.

---

## Ausführung in IntelliJ IDEA statt in der Kommandozeile

In der Menüleiste am rechten Rand ist ein Auswahlmenü, in dem i.a. _Application_
zu sehen ist. In diesem Auswahlmenü wählt man den ersten Menüpunkt
`Edit Configurations ...` aus.

Falls das Label _Program Arguments_ nicht sichtbar ist, kann man es
aktivieren, indem man auf _Modify options_ klickt und im Abschnitt `Java`
auswählt. Dann trägt man folgendes ein:

```Text
  --spring.output.ansi.enabled=ALWAYS --spring.config.location=classpath:/application.yml --spring.profiles.default=dev --spring.profiles.active=dev
```

Falls das Label _Environment variables_ nicht sichtbar ist, kann man es
aktivieren, indem man auf _Modify options_ klickt und im Abschnitt `Operating
System` auswählt. Dann trägt man die Variablen mit ihren Werten ein:

```Text
  `LOG_PATH=./build/log;APPLICATION_LOGLEVEL=trace;HIBERNATE_LOGLEVEL=debug`
```

---

## HTTP Client von IntelliJ IDEA

Im Verzeichnis `extras\http-client` gibt es Dateien mit der Endung `.http`, in
denen  HTTP-Requests vordefiniert sind. Diese kann man mit verschiedenen
Umgebungen ("environment") ausführen, z.B. für https oder für http.

---

### OpenAPI mit Swagger

Mit der URL `https://localhost:8080/swagger-ui.html` kann man in einem
Webbrowser den RESTful Web Service über eine Weboberfläche nutzen, die
von _Swagger_ auf der Basis von der Spezifikation _OpenAPI_ generiert wurde.
Die _Swagger JSON Datei_ kann man mit `https://localhost:8080/v3/api-docs`
abrufen.

## Unit Tests und Integrationstests

Wenn der [PostgreSQL-Server](#postgreSQL) erfolgreich gestartet ist und das
Port-Forwarding aktiv ist, können auch die Unit-und Integrationstests
gestartet werden.

```PowerShell
    .\gradlew test
```

**WICHTIGER** Hinweis zu den Tests für den zweiten Microservice, der den ersten
Microservice aufruft:

- Da die Tests direkt mit Windows laufen, muss Port-Forwarding für den
  aufzurufenden, ersten Microservice gestartet sein.
- Außerdem muss in `build.gradle.kts` innerhalb von `tasks.test` der Name der
  Umgebungsvariable `SCHWIMMGRUPPE_SERVICE_HOST` auf den Namen des eigenen ersten
  Microservice angepasst werden, z.B. `SPORTVEREIN_SERVICE_HOST`.

Um das Testergebnis mit _Allure_ zu inspizieren, ruft man einmalig
`.\gradlew downloadAllure` auf. Fortan kann man den generierten Webauftritt mit
den Testergebnissen folgendermaßen aufrufen:

```PowerShell
    .\gradlew allureServe
```

---

## Rechnername in der Datei hosts

Wenn man mit Kubernetes arbeitet, bedeutet das auch, dass man i.a. über TCP
kommuniziert. Deshalb sollte man überprüfen, ob in der Datei
`C:\Windows\System32\drivers\etc\hosts` der eigene Rechnername mit seiner
IP-Adresse eingetragen ist. Zum Editieren dieser Datei sind Administrator-Rechte
notwendig.

---

## Microservice mit Kubernetes, Kustomize und Skaffold

### WICHTIG: Schreibrechte für die Log Datei

Wenn die Anwendung in Kubernetes läuft, ist die Log-Datei `application.log` im
Verzeichnis `C:\Zimmermann\volumes\schwimmgruppe-v2`. Das bedeutet auch zwangsläufig,
dass diese Datei durch den _Linux-User_ vom (Kubernetes-) Pod angelegt und
geschrieben wird, wozu die erforderlichen Berechtigungen in Windows gegeben
sein müssen.

Wenn man z.B. die Anwendung zuerst mittels _Cloud Native Buildpacks_ laufen
lässt, dann wird `application.log` vom Linux-User `cnb` erstellt. Wenn man
ein späteres Deployment mit _Jib_ vornehmen möchte, wird der Linux-User
`nonroot` verwendet, der dann **KEINE** Schreibrechte für `application.log`
hat.

Deshalb muss man vor einem solchen Wechsel des Deployments und damit der
Benutzerkennungen unbedingt die Dateien in
**C:\Zimmermann\volumes\schwimmgruppe-v1 LÖSCHEN** !

### Image für Kubernetes erstellen

Zunächst wird ein Docker-Image benötigt, das z.B. mit dem Gradle-Plugin von
_Spring Boot_ auf der Basis von _Cloud Native Buildpacks_ oder mit _Jib_
als optimiertes und geschichtetes Image erstellt wird.

Buildpacks durch Spring Boot unterstützen Java 17. Bei Verwendung der
Buildpacks werden ggf.  einige Archive von Github heruntergeladen, wofür es
leider kein  Caching gibt. Ein solches Image kann mit dem Linux-User `cnb`
gestartet werden.

Bei Jib basiert das resultierende Image auf einem _distroless_ Image für Java
17 und enthält deshalb _keine_ Package Manager, Shell usw., sondern nur in
der Variante _debug_. Das Image kann mit dem Linux-User `nonroot` gestartet
werden.

```PowerShell
    .\gradlew bootBuildImage -Dtag='2.0.0'

    # oder mit Jib:
    .\gradlew jibDockerBuild -Dtag='2.0.0-jib' -Djava=17 --rerun-tasks
```

Mit denselben Kommandos kann man im Verzeichnis für das Projekt "bestellung"
auch dort ein Docker-Image erstellen.

Ein _distroless_ Image für Jib gibt es auch in der Variante _debug_ und enthält
Package Manager, Shell usw. Die Shell in der Variante _debug_ ist _ash_
(= Almquist shell, A Shell, ash and sh), die leichtgewichtiger als bash, tcsh
und zsh ist. Das Image hat dann eine Größe von ca. 450 MB statt 200 MB. Um ein
solches Image zu verwenden, lautet der Aufruf:
`.\gradlew jibDockerBuild -Dtag='2.0.0' -Djava=17 -Ddebug=true`

Mit _dive_ kann man dann ein Docker-Image und die einzelnen Layer inspizieren:

```PowerShell
    cd \Zimmermann\dive
    .\dive ioannistheodosiadis\schwimmgruppe:2.0.0
```

Alternativ kann man auch das Tool Window _Services_ von IntelliJ IDEA verwenden.

Wenn ein Docker-Image mit Buildpacks gebaut wurde, kann man mit folgendem
Kommando inspizieren, mit welchen Software-Paketen es gebaut wurde:

```PowerSkell
    pack inspect ioannistheodosiadis/schwimmgruppe:2.0.0
```

Mit der PowerShell kann man Docker-Images folgendermaßen auflisten und löschen,
wobei das Unterkommando `rmi` die Kurzform für `image rm` ist:

```PowerShell
    docker images | sort
    docker rmi myImage:myTag
```

Im Laufe der Zeit kann es immer wieder Images geben, bei denen der Name
und/oder das Tag `<none>` ist, so dass das Image nicht mehr verwendbar und
deshalb nutzlos ist. Solche Images kann man mit dem nachfolgenden Kommando
herausfiltern und dann unter Verwendung ihrer Image-ID, z.B. `9dd7541706f0`
löschen:

```PowerShell
    docker images | Select-String -Pattern '<none>'
    docker rmi 9dd7541706f0
```

### Deployment mit Kustomize

Voraussetzung für das Deployment des Microservice ist, dass der
[PostgreSQL-Server](#postgreSQL) erfolgreich gestartet ist. Im Verzeichnis
`extras\kubernetes\dev` ist eine Konfiguration für die Entwicklung des
Microservice "schwimmgruppe", wobei der Einfachheit halber auf HTTPS verzichtet wird.

Zunächst muss man mit dem
[Gradle-Plugin von Spring Boot](#image-für-Kubernetes-erstellen) oder
[Jib](#image-für-Kubernetes-erstellen) ein Docker-Image erstellen.
Bei Jib basiert das resultierende Image auf einem _distroless_ Image für Java
17 und enthält deshalb _keine_ Package Manager, Shell usw., sondern nur in
der Variante _debug_.

Das Deployment in Kubernetes wird dann folgendermaßen durchgeführt, was man
z.B. mit _Lens_ oder _Octant_ inspizieren kann. Dabei wird die Logdatei im
internen Verzeichnis `/var/log/spring` angelegt, welches  durch _Mounting_ dem
Windows-Verzeichnis `C:\Zimmermann\volumes\schwimmgruppe-v2` entspricht und mit
_Schreibberechtigung_ existieren muss.

```PowerShell
    cd extras\kubernetes\dev
    kustomize build | kubectl apply -f -
```

Das Deployment kann durch `kubectl` wieder aus Kubernetes entfernt werden:

```PowerShell
    kustomize build | kubectl delete -f -
```

Falls es beim Deployment-Vorgang zum Fehler _CrashLoopBackOff_ kann es z.B.
eine dieser Ursachen haben:

* Fehler beim Deployment-Vorgang selbst
* Fehler bei "Liveness Probe"

### Port Forwarding

Um beim Entwickeln von localhost (und damit von außen) auf einen
Kubernetes-Service zuzugreifen, ist _Port-Forwarding_ die einfachste
Möglichkeit, indem das nachfolgende Kommando für den installierten Service mit
Name _schwimmgruppe_ aufgerufen wird:

```PowerShell
    kubectl port-forward service/schwimmgruppe 8080 --namespace acme
```

Alternativ kann auch das Skript `port-forward.ps1` im Unterverzeichnis `extras`
aufgerufen werden.

### Continuous Deployment mit Skaffold

Um das Image mit dem Tag `2.0.0` zu bauen, muss die Umgebungsvariable `TAG` auf
den Wert `2.0.0` gesetzt werden. Dabei ist auf die Großschreibung bei der
Umgebungsvariablen zu achten.

Das Deployment wird mit Skaffold nun folgendermaßen durchgeführt und kann mit
`<Strg>C` abgebrochen bzw. zurückgerollt werden. Bis das Port-Forwarding, das
in `skaffold.yaml` konfiguriert ist und nicht manuell eingerichtet werden muss,
auch ausgeführt wird, muss man ggf. ein bisschen warten.

```PowerShell
    $env:TAG = '2.0.0'
    skaffold dev
```

In `skaffold.yaml` ist konfiguriert, dass das Image mit _Cloud Native
Buildpacks_ gebaut wird. In Kommentar gibt es auch die Konfiguration für
Jib_. Wenn man zwischen den beiden Varianten wechselt, beispielsweise um sie
auszuprobieren, so muss man auf die Schreibrechte in
`C:\Zimmermann\volumes\schwimmgruppe-v2`achten (siehe
[WICHTIG: Schreibrechte für die Log Datei](#wICHTIG:-Schreibrechte-für-die-Log-Datei)
).

Aufgrund der Einstellungen für _Liveness_ und _Readiness_ kann es einige
Minuten dauern, bis in der PowerShell angezeigt wird, dass das Deployment
erfolgreich war. Mit _Lens_ oder _Octant_ kann man jedoch die Log-Einträge
inspizieren und so vorher sehen, ob das Deployment erfolgreich war.

Außerdem generiert Skaffold noch ein künstliches Tag zusatäzlich zu `2.0.0`.
Das kann man mit `docker images | sort` sehen und sollte von Zeit zu Zeit
mittels `docker rmi <image:tag>` aufräumen.

---

## Istio

### Installation von Istio

Mit den folgenden Kommandos wird die Version von Istio überprüft und dann
wird Istio mit dem Profil _demo_ installiert:

```
    C:\Zimmermann\istioctl\istioctl version --remote
    C:\Zimmermann\istioctl\istioctl install --verify --set profile=demo `
        --set values.global.jwtPolicy=first-party-jwt --skip-confirmation
```

### Namespace für Istio aufbereiten

Mit dem nachfolgenden Kommando wird der Namespace `acme` für Istio und die
Envoy-Proxies aufbereitet und überprüft:

```PowerShell
    kubectl label namespace acme istio-injection=enabled
    C:\Zimmermann\istioctl\istioctl analyze --namespace acme
```

### Gateway, VirtualService und Routing

Ein API-_Gateway_ `acme-gateway` in Istio wird mit einem (Kubernetes-)
_Service_ verbunden, indem ein _VirtualService_ eingerichtet wird, d.h. ein
VirtualService ist ein Adapter zwischen Gateway und Service. Ein solcher
VirtualService wird jeweils für den Service _schwimmgruppe_ und für _bestellung_ aus
dem anderen Projekt benötigt.

Der VirtualService `schwimmgruppe-virtual` leitet die Requests mit dem Pfad-Präfix
`/schwimmgruppen` vom Gateway an den Service `schwimmgruppe` per `HTTP` weiter – also ohne
TLS. Bei der Weiterleitung gibt es eine Gewichtung, so dass 75 % der
`HTTP`-Requests an das Deployment `schwimmgruppe-v2` aus dem aktuellen Projekt und 25 %
der `HTTP`-Requests an das Deployment `schwimmgruppe-v1` aus dem ersten Beispiel
weitergeleitet werden.

Das Deployment erfolgt dann folgendermaßen:

```PowerShell
    cd extras\istio\dev
    kustomize build | kubectl apply -f -
```

Innerhalb von _Lens_ findet man Gateway und VirtualService über
- _Custom Resources_ > _networking.istio.io_ > _Gateways_ bzw.
- _Custom Resources_ > _networking.istio.io_ > _VirtualService_.

### Gateway mit HTTP

Das Gateway kann mit den beiden URIs `http://kubernetes.docker.internal/schwimmgruppen/...`
und `http://kubernetes.docker.internal/bestellungen/...` verwendet werden, um
auf die beiden (Micro-) Services _schwimmgruppe_ und _bestellung_ zuzugreifen. Der
Rechnername `kubernetes.docker.internal` wurde bei der Installation von _Docker
Desktop_ in der Datei `C:\Windows\System32\drivers\etc\hosts` auf die
IP-Adresse `127.0.0.1` gesetzt und wird in `extras\istio\dev` verwendet.

Für den _HTTP Client_ von IntelliJ gibt es vorgefertigte HTTP-Requests im
Unterverzeichnis `extras\http-client` und der Umgebung `istio-schwimmgruppen` bzw.
`istio-bestellungen` im Projekt "bestellung". In der jeweiligen Umgebung
wird auch als Rechnername `kubernetes.docker.internal` verwendet.

Log-Ausgaben zum Gateway findet man im Namespace `istio-system` im Pod
`istio-ingressgateway-...`.

### Gateway mit HTTPS

Das API-Gateway soll auch _HTTPS_ bzw. _TLS_ unterstützen, weshalb _PKI_
(= public key infrastructure) bereitgestellt werden muss, d.h. beim Deployment
des Gateways müssen _private Schlüssel_ und das _Zertifikat_ für TLS vorhanden
sein.

Im Unterverzeichnis `extras\istio\tls` gibt es:
- Die Datei `kubernetes.docker.internal.private-key` im Format _PEM_ mit dem
  privaten Schlüssel für den Rechner `kubernetes.docker.internal`, der bei der
  Installation von _Docker Desktop_ in der Datei
  `C:\Windows\System32\drivers\etc\hosts` mit der IP-Adresse `127.0.0.1`
  eingetragen wurde.
- Das Zertifikat `kubernetes.docker.internal.crt` für denselben Rechner
  `kubernetes.docker.internal`,

Diese beiden Binärdateien müssen in Kubernetes eingelesen und dort als
sogenanntes _Secret_ im Namespace `istio-system` abgespeichert werden:

```PowerShell
    cd <IJ-Projekt>\extras\istio\tls
    kubectl create secret tls acme-credential `
        --key=kubernetes.docker.internal.private-key --cert=kubernetes.docker.internal.crt `
        --namespace istio-system
```

Ein _Secret_ ist ähnlich wie eine _ConfigMap_ und verwaltet Schlüssel-Wert-Paare,
wobei die Schlüssel mit _Base64_ codiert sind, was für die Speicherung von
Binärdaten geeignet ist. Durch das obige Kommando wird das _Secret_
`acme-credential` für _TLS_ angelegt und es hat 2 Einträge:
- Zum Schlüssel `key` gibt es den Wert mit dem Inhalt der Binärdatei
  `kubernetes.docker.internal.private-key`, der mit _Base64_ codiert ist.
- Zum Schlüssel `cert` gibt es den Wert mit dem Inhalt der Binärdatei
  `kubernetes.docker.internal.crt`, der ebenfalls mit _Base64_ codiert ist.

Für HTTPS bzw. TLS benötigen die Clients des Gateways ein Zertifikat,
welches auf den Rechnernamen [`kubernetes.docker.internal`](#rechnername-in-der-Datei-hosts)
ausgestellt ist. Wenn mit dem _HTTP Client_ von IntelliJ die Umgebung für das
Gateway mit HTTPS ausgewählt und ein Request abgeschickt wird, dann muss man
lediglich das Zertifikat akzeptieren.

Falls man _cURL_ installiert hat, kann man eine Sequenz von z.B. 5 Requests
in einer PowerShell folgendermaßen absetzen und in der Server-Konsole beobachten:

```PowerShell
    for ($i = 1; $i -le 5; $i++) {
        Write-Output '';
        Write-Output $i;
        curl --silent --output NUL --basic --user admin:p --header "Accept: application/hal+json" --http2 --tlsv1.3 --insecure https://kubernetes.docker.internal/schwimmgruppen/api/00000000-0000-0000-0000-000000000001;
    }
```

---

## Codeanalyse durch ktlint und detekt

Eine statische Codeanalyse ist durch die beiden Werkzeuge _ktlint_ und _detekt_
möglich, indem man die folgenden Gradle-Tasks aufruft:

```PowerShell
    .\gradlew ktlint detekt
```

---

## Codeanalyse durch SonarQube

Für eine statische Codeanalyse durch _SonarQube_ muss zunächst der
SonarQube-Server mit _Docker Compose_ als Docker-Container gestartet werden,
wozu die Konfigurationsdatei `sonar.yaml` verwendet wird:

```PowerShell
    docker compose -f sonar.yaml up
```

Wenn der Server zum ersten Mal gestartet wird, ruft man in einem Webbrowser die
URL `http://localhost:9000` auf. In der Startseite muss man sich einloggen und
verwendet dazu als Loginname `admin` und ebenso als Password `admin`. Danach
wird man weitergeleitet, um das initiale Passwort zu ändern. Den Loginnamen und
das neue Passwort trägt man dann in der Datei `sonar-project.properties` im
Wurzelverzeichnis bei den Properties `sonar.login` und `sonar.password` ein.

Nachdem der Server gestartet ist, wird der SonarQube-Scanner in einer zweiten
PowerShell ebenfalls mit _Docker Compose_ gestartet, wozu dieselbe
Konfigurationsdatei mit dem Profile `scan` verwendet wird. Der Scan-Vorgang kann
evtl. **lange** dauern. Abschließend wird der oben gestartete Server
heruntergefahren.

```PowerShell
    docker compose -f sonar.yaml --profile scan up
    docker compose -f sonar.yaml down
```

---

## Dokumentation durch AsciiDoctor und PlantUML

Eine HTML- und PDF-Dokumentation aus AsciiDoctor-Dateien, die ggf. UML-Diagramme
mit PlantUML enthalten, wird durch folgende Gradle-Tasks erstellt:

```PowerShell
    .\gradlew asciidoctor asciidoctorPdf
```

---

## API Dokumentation durch Dokka

Eine API-Dokumentation in Form von HTML-Seiten kann man durch das Gradle-Plugin
Dokka_ erstellen, und zwar wahlweise mit einem Dokka-eigenen Layout oder dem
Layout von javadoc:

```PowerShell
    .\gradlew dokkaHtml
    .\gradlew dokkaJavadoc
```
---

## Einfache Lasttests mit hey und Fortio

Ein einfacher Lasttest mit _hey_ ist im PowerShell-Skript `hey.ps1` im
Unterverzeichnis `extras` implementiert.

_Fortio_ als Werkzeug für Lasttests ist als eigenständiges Projekt aus _Istio_
hervorgegangen. Zunächst startet man den Fortio-Server:

```PowerShell
    cd C:\Zimmermann\fortio
    .\fortio server -http-port 8088
```

Nachdem der Fortio-Server gestartet ist, ruft man in einem Webbrowser die URL
`http://localhost:8088/fortio` auf. Dort kann man einen einfachen Lasttest
durch die Eingabe von z.B. folgenden Werten konfigurieren:

- _URL_: `http://kubernetes.docker.internal/schwimmgruppen/api/00000000-0000-0000-0000-000000000001`
- _QPS_ (queries per second): `30`
- _Duration_: `5s`
- _Headers_: `Authorization: Basic YWRtaW46cA`

Abschließend klickt man auf den Button `Start`, um den einfachen Lasttest zu
starten.
