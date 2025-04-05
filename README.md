# Pay My Buddy – Prototype

This repository contains the database and application prototype for **Pay My Buddy**, developed using Java and Spring Boot with an updated database structure.

## Contents

- Updated Physical Data Model (see below)
- Spring Boot backend (Java 21)
- Thymeleaf-based web frontend
- REST and HTML controllers
- Secure session-based authentication (Spring Security)
- Integration tests using JUnit & MockMvc

---

## Updated Physical Data Model (MPD)

The following diagram represents the physical data model for the Pay My Buddy prototype, aligned with the current JPA entity structure.

### MPD Diagram

![Physical Data Model](mpd-diagram.png)

### Tables and Attributes

#### `users`

| Column     | Type          | Constraints                  |
|------------|---------------|------------------------------|
| id         | BIGINT        | PK, AUTO_INCREMENT           |
| username   | VARCHAR(255)  | NOT NULL                     |
| email      | VARCHAR(255)  | UNIQUE, NOT NULL             |
| password   | VARCHAR(255)  | NOT NULL                     |
| balance    | DECIMAL(10,2) | DEFAULT 0.00, NOT NULL       |
| created_at | DATETIME      | DEFAULT CURRENT_TIMESTAMP    |

---

#### `transactions`

| Column       | Type           | Constraints                          |
|--------------|----------------|--------------------------------------|
| id           | BIGINT         | PK, AUTO_INCREMENT                   |
| sender_id    | BIGINT         | FK → users(id), NOT NULL             |
| receiver_id  | BIGINT         | FK → users(id), NOT NULL             |
| description  | VARCHAR(500)   | NULL                                 |
| amount       | DECIMAL(10,2)  | NOT NULL                             |
| created_at   | DATETIME       | DEFAULT CURRENT_TIMESTAMP            |

---

#### `user_connections`

| Column      | Type      | Constraints                                |
|-------------|-----------|--------------------------------------------|
| user_id     | BIGINT    | FK → users(id), NOT NULL                   |
| friend_id   | BIGINT    | FK → users(id), NOT NULL                   |
| UNIQUE(user_id, friend_id) – prevents duplicate friendships           |

---

## Relationships

- A user can send and receive many transactions.
- Users can connect to each other (many-to-many) via `user_connections`.
- All foreign keys are defined using JPA with referential integrity.

---

## Authentication & Security

- Custom `LoginSuccessHandler` stores authenticated user in session.
- Access control via `SecurityConfig` using Spring Security.
- Public pages: `/login`, `/register`
- All other routes require authentication

---

## Integration Testing

- Written using Spring Boot Test + MockMvc
- Test classes:
  - `HtmlAuthControllerIntegrationTest`
  - `HomeIntegrationTest`
  - `FriendIntegrationTest`
  - `TransactionIntegrationTest`
- Sample data loaded with `@Sql(test-data.sql)`

---

## Database Backup & Restore

To create a full backup of the `paymybuddy` database:

```bash
mysqldump -u root -p paymybuddy > backup.sql
```

To restore the database from the backup file:

```bash
mysql -u root -p paymybuddy < backup.sql
```
