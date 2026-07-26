# 🚀 Rozgar Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-336791?style=for-the-badge&logo=postgresql)
![JWT](https://img.shields.io/badge/Security-JWT-blue?style=for-the-badge)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange?style=for-the-badge&logo=rabbitmq)
![WebSocket](https://img.shields.io/badge/WebSocket-Real--Time-green?style=for-the-badge)

**Backend service powering Rozgar — a B2B marketplace connecting buyers and verified suppliers across India.**

</div>

---

# 📖 Overview

Rozgar Backend is a scalable REST API built using **Spring Boot** that enables businesses to discover suppliers, negotiate quotations, communicate in real time, and securely complete transactions.

The backend follows modern enterprise development practices including authentication, role-based authorization, caching, messaging queues, payment integration, cloud storage, and rate limiting.

---

# ✨ Features

### 👤 Authentication & Authorization

- JWT Authentication
- Refresh Token support
- Role Based Access Control (RBAC)
- Secure password encryption
- Email verification
- Forgot Password support

---

### 🏢 Business Management

- Business registration
- GST verification support
- PAN verification
- Business profile management
- Company document uploads

---

### 📦 Product Management

- Product catalog
- Product categories
- Inventory management
- Product images
- Search & filtering

---

### 📄 RFQ (Request For Quotation)

Buyers can:

- Create RFQs
- Specify quantity
- Set delivery requirements
- Receive supplier quotations

Suppliers can:

- Submit quotations
- Modify quotations
- Track quotation status

---

### 💬 Real-Time Chat

- WebSocket based messaging
- Buyer ↔ Seller communication
- Instant notifications
- Conversation history

---

### 💳 Payment Integration

- Razorpay integration
- Secure payment verification
- Order payment support

---

### 📁 File Storage

Supports cloud object storage using:

- MinIO
- Amazon S3 compatible APIs

Used for:

- Business documents
- Product images
- Profile pictures

---

### 📲 Notifications

Integrated with Twilio for:

- SMS alerts
- OTP verification
- Important order updates

---

### ⚡ Performance Optimizations

- Redis caching
- Bucket4j Rate Limiting
- Connection pooling
- Pagination support

---

### 📈 Monitoring

- Spring Boot Actuator
- Health endpoints
- Metrics
- Logging

---

# 🛠 Tech Stack

| Category | Technologies |
|-----------|--------------|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Migration | Flyway |
| Cache | Redis |
| Messaging | RabbitMQ |
| API Docs | Swagger / OpenAPI |
| Storage | MinIO / AWS S3 |
| Mapping | MapStruct |
| Payment | Razorpay |
| SMS | Twilio |
| WebSocket | STOMP + SockJS |
| Build Tool | Maven |

---

# 🏗 System Architecture

```text
                 Client (React)

                        │
                        ▼

              Spring Boot REST API

        ┌────────────────────────────────┐
        │                                │
        │ JWT Authentication             │
        │ Spring Security                │
        │ REST Controllers               │
        │ Business Services              │
        │ WebSocket Messaging            │
        │ Razorpay Payments              │
        │ Twilio Notifications           │
        │ Redis Cache                    │
        │ RabbitMQ Queue                 │
        └────────────────────────────────┘

            │             │

            ▼             ▼

      PostgreSQL      Object Storage
                        (MinIO/S3)
```

---

# 📂 Project Structure

```
src
├── controller
├── service
├── repository
├── entity
├── dto
├── mapper
├── security
├── websocket
├── payment
├── notification
├── config
├── exception
└── util
```

---

# 🚀 Getting Started

## Clone Repository

```bash
git clone https://github.com/SarthakBhushan/Rozgar-Backend.git

cd Rozgar-Backend
```

---

## Requirements

- Java 21
- Maven
- PostgreSQL
- Redis
- RabbitMQ

---

## Configure Environment

Update your application configuration.

Example:

```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

jwt.secret=

redis.host=

rabbitmq.host=

razorpay.key=
razorpay.secret=

twilio.account.sid=
twilio.auth.token=

aws.access.key=
aws.secret.key=
```

---

## Build Project

```bash
mvn clean install
```

---

## Run

```bash
mvn spring-boot:run
```

Server starts at

```
http://localhost:8080
```

---

# 📚 API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI

```
http://localhost:8080/v3/api-docs
```

---

# 🔐 Authentication Flow

```text
User Login
      │
      ▼

Validate Credentials

      │
      ▼

Generate JWT Token

      │
      ▼

Client Stores Token

      │
      ▼

Every Request

      │
      ▼

JWT Filter

      │
      ▼

Authorized Endpoint
```

---

# 📡 Core Modules

- Authentication
- User Management
- Business Management
- Product Management
- RFQ Management
- Quotation Management
- Chat Module
- Payment Module
- Notification Module
- File Upload Module
- Admin Module

---

# 🔄 Request Lifecycle

```text
Client Request

      │

Authentication

      │

Controller

      │

Service Layer

      │

Repository

      │

PostgreSQL
```

---

# 🔒 Security Features

- JWT Authentication
- BCrypt Password Encoding
- Role Based Authorization
- Rate Limiting
- Input Validation
- CORS Configuration
- Secure Exception Handling

---

# 📈 Future Improvements

- AI-powered supplier recommendations
- Elasticsearch integration
- Docker & Kubernetes deployment
- Microservice architecture
- Recommendation engine
- Email notification service
- Analytics dashboard
- Multi-language support

---

# 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch

```bash
git checkout -b feature/new-feature
```

3. Commit changes

```bash
git commit -m "Add new feature"
```

4. Push

```bash
git push origin feature/new-feature
```

5. Create a Pull Request

---

# 📄 License

This project is licensed under the MIT License.

---

# 👨‍💻 Author

**Sarthak Bhushan**

- GitHub: https://github.com/SarthakBhushan
- LinkedIn: https://linkedin.com/in/sarthakbhushan

---

<div align="center">

⭐ If you found this project useful, consider giving it a star!

</div>
