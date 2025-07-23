# Artflow (README is out-of-date/WIP) 🎨

Artflow is a full-stack web application for uploading and organizing works-in-progress, final art pieces, and project timelines. It allows artists to keep their work organized, private or public, and optionally tagged by medium, style, or other categories.

---

## ✨ Features

- ✅ User authentication with Spring Security
- ✅ Image uploads (WIP, final product, sketches)
- ✅ Tagging system for projects
- ✅ Public/private visibility options
- ✅ Projects grouped by creation date and art type
- ✅ Clean, responsive UI (to be designed)
- ✅ TDD (Test-Driven Development) for backend services

---

## 📁 Project Structure

    artflow/
    ├── src/
    │ ├── main/
    │ │ ├── java/com/artflow/artflow/
    │ │ │ ├── controller/
    │ │ │ ├── model/
    │ │ │ ├── repository/
    │ │ │ ├── service/
    │ │ │ └── ArtflowApplication.java
    │ │ └── resources/
    │ │ ├── application.properties
    │ │ └── static/ # For static frontend files (if applicable)
    │ └── test/java/com/artflow/artflow/
    │ ├── controller/
    │ ├── service/
    │ └── ArtflowApplicationTests.java
    ├── .env # Local dev secrets (ignored by Git)
    ├── .gitignore
    ├── README.md
    ├── pom.xml
    └── seed/ # DB seed and test data
    ├── test_data.sql
    └── schema.sql


---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 20+
- Maven
- Git
- [Neon](https://neon.tech) for PostgreSQL

### 🛠️ Setup

1. **Clone the repo:**

   ```bash
   git clone https://github.com/your-username/artflow.git
   cd artflow

2. Create .env in the root of the project:

    ```text
    DB_URL=jdbc:postgresql://your-neon-db-url
    DB_USERNAME=your_db_user
    DB_PASSWORD=your_db_password

3. Run the app:

    ```bash
    ./mvnw spring-boot:run

Or from IntelliJ: run ArtflowApplication.java

4. Access it at:

http://localhost:8080

### 🛠️ Testing
Run all tests:

    ./mvnw test

Tests live under src/test/java.

## 🛣️ Roadmap
### ✅ v1.0 MVP
-[ ] Spring Boot backend
-[ ] Security (basic auth)
-[ ] Neon integration
-[ ] Image upload support (via external service or database)
-[ ] Project model: multiple images, tags, visibility
-[ ] Public/private viewing permissions
-[ ] Responsive-friendly backend endpoints

### ⏳ v1.1 (Next)
-[ ] Implement UI with modern frontend (React, Next.js, etc.)
-[ ] OAuth 2.0 login (Google, GitHub)
-[ ] Frontend testing (Jest, Cypress)
-[ ] Form validation and error handling

### 🛡️ Environment and Security
- .env is excluded from Git to protect secrets
- Use dotenv-java to load environment variables

### 🧰 Tech Stack
- Backend: Java, Spring Boot, Spring Security, JPA
- Database: PostgreSQL (hosted via Neon)
- Frontend (planned): React (or similar)
- Testing: JUnit, Mockito
- Deployment: TBD (Netlify, Vercel, Render, or Railway)

🖼️ Image Hosting
- Images may be stored in:
- A cloud file service (Cloudinary, Firebase Storage, S3-compatible API)
- Or in PostgreSQL as base64 blobs (not preferred for large files)
