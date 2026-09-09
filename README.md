# ElderCare Connect - Specialist Senior Healthcare & Consultations

![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen?style=flat-square&logo=springboot)
![Bootstrap 5](https://img.shields.io/badge/Bootstrap-5.3.3-purple?style=flat-square&logo=bootstrap)
![Spring Security & JWT](https://img.shields.io/badge/Security-JWT%20%2B%20Spring%20Security-blue?style=flat-square)
![Database](https://img.shields.io/badge/Database-H2%20%2F%20MySQL-lightgrey?style=flat-square)

**ElderCare Connect** is a senior-friendly digital healthcare web application designed specifically for elderly citizens and their families. It connects seniors with verified geriatric specialists, enables simple appointment booking, generates digital prescriptions, and features 1-click caregiver emergency coordination.

---

## 🌟 Core Features

- **Senior-Friendly User Interface**: Clean, spacious, uncluttered design using native Bootstrap 5 typography and high-contrast color standards.
- **Elder Accessibility Controls**: Real-time text scaling (`A-`, `A`, `A+`) and high-contrast mode for seniors with low vision.
- **Specialist Directory & Filtering**: Search and filter geriatricians, cardiologists, orthopedic specialists, and neurologists by city and hospital.
- **Real-Time Slot Booking**: Visual time slot selection pills with automatic conflict avoidance.
- **Patient Portal**: Track upcoming visits, view past consultations, and download digital prescriptions.
- **Doctor Portal**: Physicians can view scheduled visits, accept/reject consultations, and write digital prescriptions (Rx) with medicine dosages and duration.
- **Admin Console**: Monitor platform metrics (total patients, doctors, appointments) and review/approve doctor registrations.
- **Caregiver Coordination**: Emergency contacts and national emergency hotline dialing (`1800-ELDER-CARE`, `108`, `14567`).

---

## 💻 Technical Stack

### Backend
| Technology | Version | Purpose |
| :--- | :--- | :--- |
| **Java** | 21 (LTS) | Core application programming language |
| **Spring Boot** | 3.3.0 | Application framework and dependency injection |
| **Spring Data JPA / Hibernate** | 6.5.2 | Object-relational mapping and database persistence |
| **Spring Security** | 6.x | Authentication, authorization, and endpoint protection |
| **JJWT (Java JWT)** | 0.11.5 | Stateless JSON Web Token authentication |
| **Jakarta Validation** | 3.0 | Request payload and constraint validation |
| **Lombok** | Latest | Boilerplate code reduction (`@Getter`, `@Setter`, `@Builder`) |

### Frontend
| Technology | Version | Purpose |
| :--- | :--- | :--- |
| **Bootstrap** | 5.3.3 | Responsive grid, cards, forms, modals, and utilities |
| **Bootstrap Icons** | 1.11.3 | Healthcare and UI iconography |
| **Bootstrap System Fonts** | Native | High-readability native typography stack (`var(--bs-font-sans-serif)`) |
| **Vanilla JavaScript** | ES6+ | Asynchronous client logic (`fetch` API), view routing, state management |
| **CSS3** | Modern | Clean healthcare design tokens, elevation effects, and accessibility scaling |

### Database & Build Tools
| Technology | Purpose |
| :--- | :--- |
| **H2 Database** | Embedded, in-memory SQL database for instant development and testing |
| **MySQL 8.x** | Production-ready relational database (fully supported via `mysql-connector-j`) |
| **Apache Maven** | Dependency management and build packaging |

---

## 📁 System Architecture & Project Structure

```
ElderCare/
├── pom.xml                               # Maven project dependencies & build config
├── README.md                             # Project documentation
├── src/
│   ├── main/
│   │   ├── java/com/eldercare/
│   │   │   ├── EldercareConnectApplication.java   # Spring Boot entry point
│   │   │   ├── config/                            # Security, CORS, DataInitializer
│   │   │   ├── controller/                        # REST API Controllers
│   │   │   ├── dto/                               # Request/Response Data Transfer Objects
│   │   │   ├── entity/                            # JPA Database Entities (User, Doctor, etc.)
│   │   │   ├── exception/                         # Global error handling
│   │   │   ├── repository/                        # Spring Data JPA Repositories
│   │   │   ├── security/                          # JWT Authentication Filter & Token Provider
│   │   │   └── service/                           # Business logic services
│   │   └── resources/
│   │       ├── application.properties             # App configuration & DB settings
│   │       └── static/                            # Frontend static assets
│   │           ├── index.html                     # Single-Page Application HTML
│   │           ├── css/
│   │           │   └── style.css                  # Bootstrap healthcare styling & variables
│   │           └── js/
│   │               └── app.js                     # REST API client & view rendering
└── target/                                        # Compiled artifacts & executable JAR
```

---

## ⚙️ Prerequisites

Ensure the following tools are installed on your machine:
1. **Java Development Kit (JDK) 21**:
   - Verify by running: `java -version`
2. **Apache Maven 3.8+**:
   - Verify by running: `mvn -version`
3. *(Optional)* **MySQL 8.0+** (if running with MySQL instead of H2).

---

## 🚀 How to Run the Project (Step-by-Step)

### Option 1: Quick Run in PowerShell (Recommended)

1. In PowerShell, run the launcher script:
   ```powershell
   .\run.ps1
   ```
   *(Or in Command Prompt / double-click: `run.bat`)*
   - Automatically sets up Java 21 & Maven paths.
   - Cleans up any old process using port 8080.
   - Launches the application.
2. Open your browser and navigate to:
   ```
   http://localhost:8080/
   ```

---

### Option 2: Build and Run with Maven / build.ps1

If `mvn` is not yet added to your system environment variables, use the included **`build.ps1`** PowerShell script:

1. Build the project and package into a JAR:
   ```powershell
   .\build.ps1 clean package -DskipTests
   ```
2. Run using the Spring Boot Maven Plugin:
   ```powershell
   .\build.ps1 spring-boot:run
   ```
   *(Or if Maven is in your system PATH: `mvn spring-boot:run`)*
3. Access the web app at: `http://localhost:8080/`

---

### Option 3: Run inside an IDE (IntelliJ IDEA / Eclipse / VS Code)

1. Open the project folder (`ElderCare`) in your IDE.
2. Ensure Project SDK is set to **Java 21**.
3. Locate `src/main/java/com/eldercare/EldercareConnectApplication.java`.
4. Right-click and choose **Run 'EldercareConnectApplication'**.
5. The application starts on port `8080`.

---

## 🔑 Demo User Credentials

The application automatically seeds realistic demo data on startup:

| Role | Email | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **Senior Patient** | `patient@eldercare.com` | `patient123` | Book doctor appointments, view consultations & prescriptions |
| **Doctor** | `doctor@eldercare.com` | `doctor123` | Manage schedule, accept/reject visits, write prescriptions |
| **Admin** | `admin@eldercare.com` | `admin123` | Platform KPI analytics, doctor verification queue |

> 💡 **Quick Switcher**: On the top utility bar of the homepage, click **Patient**, **Doctor**, or **Admin** to instantly sign in without typing passwords.

---

## 🗄️ Database Setup & Configuration

The application is configured in `src/main/resources/application.properties` with **MySQL** as the active database.

### Active: MySQL Database
- **Host**: `localhost:3306`
- **Database**: `eldercare_db` (automatically created if not present)
- **Username**: `root`
- **Password**: [PASSWORD]
- **JDBC URL**:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/eldercare_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
  spring.datasource.username=root
  spring.datasource.password=[PASSWORD]
  spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
  spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
  spring.jpa.hibernate.ddl-auto=update
  ```

### Optional: Switching to In-Memory H2 Database
If you ever want to run without MySQL, edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:eldercare_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

---

## 🌐 Deployment Guide

### 1. Docker Deployment (Recommended)

Create a `Dockerfile` in the project root:
```dockerfile
# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/eldercare-connect-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run with Docker:
```bash
# Build image
docker build -t eldercare-connect:latest .

# Run container
docker run -d -p 8080:8080 --name eldercare-app eldercare-connect:latest
```

---

### 2. Linux Server Deployment (Systemd Service)

1. Upload `target/eldercare-connect-0.0.1-SNAPSHOT.jar` to `/opt/eldercare/eldercare-connect.jar`.
2. Create systemd service file `/etc/systemd/system/eldercare.service`:
   ```ini
   [Unit]
   Description=ElderCare Connect Web Application
   After=syslog.target network.target

   [Service]
   User=eldercare
   ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/eldercare/eldercare-connect.jar
   SuccessExitStatus=143
   Restart=always
   RestartSec=10

   [Install]
   WantedBy=multi-user.target
   ```
3. Enable and start service:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable eldercare
   sudo systemctl start eldercare
   ```

---

### 3. Cloud Deployment (AWS / Render / Railway / Heroku)

1. **Render / Railway**:
   - Connect your GitHub repository.
   - Set Build Command: `mvn clean package -DskipTests`
   - Set Start Command: `java -jar target/eldercare-connect-0.0.1-SNAPSHOT.jar`
   - Set Environment Variable: `PORT=8080`
2. **AWS Elastic Beanstalk**:
   - Create a Web App environment selecting **Java 21**.
   - Upload the `eldercare-connect-0.0.1-SNAPSHOT.jar` file and deploy.

---

## 📡 REST API Reference

All protected endpoints require an `Authorization: Bearer <jwt_token>` header.

### Authentication & Registration
- `POST /api/auth/login` - Authenticate and receive JWT token
- `POST /api/auth/register/patient` - Register new senior patient
- `POST /api/auth/register/doctor` - Register new doctor (pending admin approval)

### Specialists & Directory
- `GET /api/departments` - List medical departments (Cardiology, Geriatrics, etc.)
- `GET /api/hospitals` - List hospital network branches
- `GET /api/doctors` - Search and filter doctors (params: `specializationId`, `city`, `name`)
- `GET /api/doctors/{id}` - Retrieve doctor profile
- `GET /api/doctors/{id}/slots?date=YYYY-MM-DD` - Get available consultation slots

### Appointments
- `POST /api/appointments` - Book a consultation slot (`ROLE_PATIENT`)
- `GET /api/appointments/patient/my` - View patient appointments (`ROLE_PATIENT`)
- `GET /api/appointments/doctor/my` - View doctor appointment queue (`ROLE_DOCTOR`)
- `PATCH /api/appointments/{id}/status` - Update status (`CONFIRMED`, `REJECTED`, `CANCELLED`)

### Medical Records & Prescriptions
- `POST /api/medical-records` - Record diagnosis & generate prescription (`ROLE_DOCTOR`)
- `GET /api/medical-records/appointment/{id}` - Fetch prescription for appointment

### Admin Governance
- `GET /api/admin/stats` - Platform counts (patients, doctors, appointments)
- `GET /api/admin/doctors/pending` - Review unapproved doctor registrations
- `PATCH /api/admin/doctors/{id}/approve` - Approve doctor registration
- `PATCH /api/admin/doctors/{id}/reject` - Reject doctor registration

---

## 📄 License & Compliance

- **Accessibility**: Standardized with WCAG AAA contrast and high-legibility fonts for senior citizens.
- **License**: MIT License.
