# Installation Guide

## Requirements

| Requirement | Version |
|-------------|---------|
| JDK | 21 or higher |
| MySQL | 8.0 |
| Maven | 3.x (bundled inside Apache NetBeans, or a standalone install) |

---

## Step 1 — Database Setup

Start your MySQL service, then create the database, apply the schema, and load the seed data.

### Windows (full path)

```powershell
# Create schema (tables, indexes, FKs)
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < src\main\resources\db\schema.sql

# Seed lookup data, sample employees, and default user accounts
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < src\main\resources\db\seed.sql
```

### Unix / macOS

```bash
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p < src/main/resources/db/seed.sql
```

Both scripts are idempotent (`CREATE DATABASE IF NOT EXISTS`, `INSERT IGNORE`) and safe to re-run.

---

## Step 2 — Configure `config.properties`

Open `src/main/resources/config.properties` and fill in the connection details for your MySQL instance.

```properties
db.host=localhost
db.port=3306
db.name=payrollsystem_db
db.user=root
db.pass=your_password_here
```

| Key | Description |
|-----|-------------|
| `db.host` | Hostname or IP of the MySQL server |
| `db.port` | MySQL port (default: `3306`) |
| `db.name` | Database name — must match `payrollsystem_db` unless you changed the schema |
| `db.user` | MySQL username |
| `db.pass` | MySQL password for the above user |

The file is loaded from the classpath at startup. If it is missing, the application will throw an `IllegalStateException` immediately.

---

## Step 3 — Build

### Using NetBeans (recommended on Windows)

NetBeans ships with a bundled Maven. Open the project (`File → Open Project`), then select **Run → Clean and Build Project** (or press **Shift+F11**).

### Using the NetBeans-bundled Maven from the terminal

```powershell
& "C:\Program Files\NetBeans-25\netbeans\java\maven\bin\mvn.cmd" clean package
```

> Adjust the NetBeans installation path to match your actual install directory. Typical locations include `C:\Program Files\NetBeans-25\` or `C:\Program Files\Apache NetBeans 21\`.

### Using a standalone Maven install

```bash
mvn clean package
```

A successful build produces `target/MPHPayrollSystem-1.0-SNAPSHOT.jar`.

---

## Step 4 — Run

The simplest way to launch the application is via the Maven exec plugin, which places all dependencies on the classpath automatically:

```bash
mvn exec:java
```

Alternatively, if you have copied the dependencies to `target/lib/` (e.g., via `mvn dependency:copy-dependencies -DoutputDirectory=target/lib`), you can launch the JAR directly:

```bash
java -jar target\MPHPayrollSystem-1.0-SNAPSHOT.jar
```

The Login window will appear.

---

## Default Credentials

The seed script creates the following accounts. All passwords are `password123`.

| Username | Role | Linked Employee |
|----------|------|----------------|
| `admin` | ADMIN | E001 — Manuel Garcia |
| `hr` | HR | E002 — Antonio Lim |
| `finance` | FINANCE | E003 — Bianca Aquino |
| `it` | IT | E004 — Isabella Reyes |
| `employee` | EMPLOYEE | E005 — Maria Santos |

> **Security note:** Change all default passwords before using the system with real data.
