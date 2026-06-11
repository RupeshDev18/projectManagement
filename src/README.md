# 🗂️ Project Management API — 7-Day Spring Boot Learning Project

> Build a real-world, production-like REST API for managing workspaces, projects, tasks, and teams — covering every topic you've studied: Spring Boot, Spring Security (JWT), JPA + PostgreSQL, and @Transactional.

---

## 🧠 What You'll Build

A backend API that lets:
- **Users** register, log in, and manage their profile
- **Workspace owners** create workspaces and invite members with roles
- **Project managers** create projects inside workspaces
- **Team members** create tasks, assign them, change their status, and comment on them
- **Admins** manage everything via elevated access

This touches every Spring Boot topic in a meaningful, non-trivial way.

---

## 🗃️ Domain Model (What Tables Will Exist)

```
User
  ├── id, name, email, password, role (ADMIN / USER), createdAt

Workspace
  ├── id, name, description, owner (→ User), createdAt

WorkspaceMember
  ├── id, workspace (→ Workspace), user (→ User), role (OWNER / MANAGER / MEMBER)

Project
  ├── id, name, description, status (ACTIVE / ARCHIVED), workspace (→ Workspace), createdAt

Task
  ├── id, title, description, status (TODO / IN_PROGRESS / IN_REVIEW / DONE)
  ├── priority (LOW / MEDIUM / HIGH / CRITICAL)
  ├── project (→ Project), assignee (→ User), createdBy (→ User)
  ├── dueDate, createdAt, updatedAt

Comment
  ├── id, content, task (→ Task), author (→ User), createdAt

AuditLog
  ├── id, action, entityType, entityId, performedBy (→ User), timestamp
```

---

## 📦 Tech Stack & Libraries to Add (in pom.xml / build.gradle)

| Library | Purpose | Dependency name |
|---|---|---|
| Spring Web | REST controllers, HTTP | `spring-boot-starter-web` |
| Spring Security | Auth, route protection | `spring-boot-starter-security` |
| Spring Data JPA | ORM, repository layer | `spring-boot-starter-data-jpa` |
| PostgreSQL Driver | DB connection | `postgresql` |
| JJWT (io.jsonwebtoken) | Create & validate JWT tokens | `jjwt-api`, `jjwt-impl`, `jjwt-jackson` |
| Lombok | Reduce boilerplate (@Getter etc.) | `lombok` |
| Validation | @Valid, @NotBlank, @Email | `spring-boot-starter-validation` |
| Flyway (optional but recommended) | DB migrations | `flyway-core` |

**JJWT version to use:** `0.12.x` (latest stable as of 2024)

---

## 🗂️ Package Structure to Follow

```
com.yourname.projectmanager
  ├── config/
  │    ├── SecurityConfig.java          ← Spring Security configuration
  │    ├── JwtAuthenticationFilter.java ← JWT request filter
  │    └── AppConfig.java               ← PasswordEncoder, other beans
  ├── controller/
  │    ├── AuthController.java
  │    ├── WorkspaceController.java
  │    ├── ProjectController.java
  │    ├── TaskController.java
  │    └── CommentController.java
  ├── service/
  │    ├── AuthService.java
  │    ├── WorkspaceService.java
  │    ├── ProjectService.java
  │    ├── TaskService.java
  │    └── CommentService.java
  ├── repository/
  │    ├── UserRepository.java
  │    ├── WorkspaceRepository.java
  │    ├── WorkspaceMemberRepository.java
  │    ├── ProjectRepository.java
  │    ├── TaskRepository.java
  │    └── CommentRepository.java
  ├── entity/
  │    ├── User.java
  │    ├── Workspace.java
  │    ├── WorkspaceMember.java
  │    ├── Project.java
  │    ├── Task.java
  │    ├── Comment.java
  │    └── AuditLog.java
  ├── dto/
  │    ├── request/   ← What comes IN (RegisterRequest, CreateTaskRequest, etc.)
  │    └── response/  ← What goes OUT (UserResponse, TaskResponse, etc.)
  ├── enums/
  │    ├── TaskStatus.java
  │    ├── TaskPriority.java
  │    ├── WorkspaceRole.java
  │    └── UserRole.java
  ├── exception/
  │    ├── GlobalExceptionHandler.java  ← @RestControllerAdvice
  │    ├── ResourceNotFoundException.java
  │    ├── UnauthorizedException.java
  │    └── BusinessException.java
  └── util/
       └── JwtUtil.java                 ← Token generation & parsing
```

---

## 🔐 Authentication Strategy — JWT (Stateless)

You will use **JWT (JSON Web Token)** with a **stateless** setup. No sessions, no cookies (unless you want to add them later).

### How the flow works:
1. User sends `POST /api/auth/register` with name, email, password
2. Password is **hashed using BCrypt** before saving — never store plain text
3. User sends `POST /api/auth/login` with email, password
4. Your app verifies the password, then **generates a JWT** signed with a secret key
5. The JWT is returned in the response
6. For every subsequent request, the client sends the token in the header: `Authorization: Bearer <token>`
7. Your `JwtAuthenticationFilter` intercepts every request, extracts the token, validates it, and sets the security context

### What to put inside the JWT payload (claims):
- `sub` → user's email or ID
- `role` → USER or ADMIN
- `iat` → issued at (auto)
- `exp` → expiry time (set to 24 hours or 7 days)

### Spring Security configuration decisions:
- Use `SecurityFilterChain` bean (not the old `WebSecurityConfigurerAdapter` — it's deprecated)
- Permit: `POST /api/auth/**` (register, login — public)
- Authenticate: everything else
- Set session policy to `STATELESS` — critical for JWT
- Add your `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`
- Implement `UserDetailsService` to load user by email from DB
- Register a `BCryptPasswordEncoder` bean and use it everywhere

---

## 🛡️ Authorization Strategy — Role-Based + Membership-Based

Two layers of authorization:

**Layer 1 — Global Role (stored in User table)**
- `ADMIN` → can see all workspaces, all users, all tasks
- `USER` → can only see what they're a member of

**Layer 2 — Workspace Role (stored in WorkspaceMember table)**
- `OWNER` → can delete workspace, archive projects, remove members
- `MANAGER` → can create projects, assign tasks, change any task status
- `MEMBER` → can create tasks, change only their own task status, add comments

How to enforce this:
- Use `@PreAuthorize("hasRole('ADMIN')")` on admin-only endpoints
- For membership checks, write a helper method in your service: `validateWorkspaceMembership(userId, workspaceId)` — throw `UnauthorizedException` if not a member
- Don't forget to enable method security: add `@EnableMethodSecurity` to your security config

---

## 💾 JPA Relationships to Configure

| Relationship | Annotation to use | Notes |
|---|---|---|
| Workspace → User (owner) | `@ManyToOne` | FetchType.LAZY |
| WorkspaceMember → Workspace | `@ManyToOne` | |
| WorkspaceMember → User | `@ManyToOne` | |
| Project → Workspace | `@ManyToOne` | FetchType.LAZY |
| Task → Project | `@ManyToOne` | FetchType.LAZY |
| Task → User (assignee) | `@ManyToOne` | Nullable, FetchType.LAZY |
| Comment → Task | `@ManyToOne` | FetchType.LAZY |
| Comment → User | `@ManyToOne` | FetchType.LAZY |

**Important JPA rules to follow:**
- Always use `FetchType.LAZY` on `@ManyToOne` and `@OneToMany` — eager loading kills performance
- Never expose JPA entities directly from controllers — always map to DTOs
- Use `@JsonIgnore` or avoid bidirectional relationships in entities to prevent infinite recursion
- Add `@Column(nullable = false)` on required fields
- Use `@Enumerated(EnumType.STRING)` for all enums — never use ORDINAL (breaks if you reorder enum values)
- Add `@CreationTimestamp` and `@UpdateTimestamp` from Hibernate on timestamp fields

---

## 🔄 Where to Use @Transactional and Why

This is one of the most important topics. Here's exactly where and why:

**Rule of thumb:** Put `@Transactional` on your **service methods**, not repositories or controllers.

### Specific cases in this project:

**Task status change** → `@Transactional`
When a task moves to DONE, you might want to: update the task status + write an audit log entry. Both must succeed or both must fail. This is a transaction.

**Invite member to workspace** → `@Transactional`
You check if user exists, check if already a member, then insert a WorkspaceMember row. If anything fails midway, you don't want a half-inserted record.

**Archive project** → `@Transactional`
Archiving a project sets the project status to ARCHIVED and may bulk-update all open tasks to a cancelled/frozen state. Multi-entity update = transaction.

**Delete workspace** → `@Transactional`
Deleting a workspace should cascade: delete all projects, all tasks in those projects, all comments, all members. Use this with `CascadeType.ALL` + `orphanRemoval = true` in your JPA entities, and wrap in a transaction.

**Register user** → `@Transactional`
Save the user, maybe write a "welcome" audit log. Atomic operation.

**Read-only queries** → `@Transactional(readOnly = true)`
Use this on service methods that only read data. It gives a performance hint to Hibernate and prevents accidental writes.

---

## 📋 All API Endpoints to Build

### Auth
```
POST   /api/auth/register        → Register new user
POST   /api/auth/login           → Login, returns JWT
GET    /api/auth/me              → Get current user profile (requires token)
```

### Workspaces
```
POST   /api/workspaces                        → Create workspace
GET    /api/workspaces                        → List my workspaces
GET    /api/workspaces/{id}                   → Get workspace details
PUT    /api/workspaces/{id}                   → Update workspace (OWNER only)
DELETE /api/workspaces/{id}                   → Delete workspace (OWNER only)
POST   /api/workspaces/{id}/members           → Invite member
GET    /api/workspaces/{id}/members           → List members
DELETE /api/workspaces/{id}/members/{userId}  → Remove member (OWNER only)
```

### Projects
```
POST   /api/workspaces/{workspaceId}/projects         → Create project
GET    /api/workspaces/{workspaceId}/projects         → List projects
GET    /api/workspaces/{workspaceId}/projects/{id}    → Get project
PUT    /api/workspaces/{workspaceId}/projects/{id}    → Update project
DELETE /api/workspaces/{workspaceId}/projects/{id}    → Delete project (MANAGER+)
PATCH  /api/workspaces/{workspaceId}/projects/{id}/archive → Archive project
```

### Tasks
```
POST   /api/projects/{projectId}/tasks              → Create task
GET    /api/projects/{projectId}/tasks              → List tasks (with filters: status, priority, assignee)
GET    /api/projects/{projectId}/tasks/{id}         → Get task
PUT    /api/projects/{projectId}/tasks/{id}         → Update task
DELETE /api/projects/{projectId}/tasks/{id}         → Delete task
PATCH  /api/projects/{projectId}/tasks/{id}/status  → Change task status
PATCH  /api/projects/{projectId}/tasks/{id}/assign  → Assign task to user
```

### Comments
```
POST   /api/tasks/{taskId}/comments       → Add comment
GET    /api/tasks/{taskId}/comments       → List comments
DELETE /api/tasks/{taskId}/comments/{id} → Delete comment (own only)
```

### Admin (ADMIN role required)
```
GET    /api/admin/users            → List all users
PATCH  /api/admin/users/{id}/role  → Change user role
GET    /api/admin/workspaces       → List all workspaces
```

---

## 🗓️ 7-Day Plan (2 Hours/Day)

---

### ✅ Day 1 — Project Skeleton + Database Setup
**Goal:** Get a running Spring Boot app connected to PostgreSQL.

**What to do:**
- Create a new Spring Boot project using Spring Initializr (start.spring.io)
- Add all dependencies listed in the Tech Stack section
- Set up `application.properties` or `application.yml` with:
    - PostgreSQL datasource URL, username, password
    - JPA setting: `ddl-auto=update` (for now; switch to Flyway later if ambitious)
    - JPA setting: `show-sql=true` so you can see queries in the console
    - A `jwt.secret` property (generate a 256-bit random string and paste it)
    - A `jwt.expiration` property (e.g., 86400000 for 24 hours in ms)
- Create all your `@Entity` classes with correct annotations and relationships
- Run the app — if tables are auto-created in Postgres, Day 1 is done

**Key learning:** How Spring Boot auto-configures DataSource from properties. How JPA creates tables from entities.

---

### ✅ Day 2 — Auth System (Register + Login + JWT)
**Goal:** Users can register and log in, get a JWT back.

**What to do:**
- Create `User` entity and `UserRepository` with `findByEmail(String email)`
- Create `AuthService` with `register()` and `login()` methods
    - `register()`: validate email not taken, hash password with BCrypt, save user, return response
    - `login()`: load user by email, verify password, generate JWT, return token
- Create `JwtUtil` class: methods for `generateToken(UserDetails)`, `extractUsername(token)`, `isTokenValid(token, userDetails)`
- Create `JwtAuthenticationFilter` extending `OncePerRequestFilter`
    - Extract `Authorization` header, parse token, set `SecurityContextHolder`
- Create `SecurityConfig`:
    - Permit `/api/auth/**`, authenticate all else
    - `STATELESS` session policy
    - Register `JwtAuthenticationFilter`
    - `BCryptPasswordEncoder` bean
    - `AuthenticationManager` bean
- Create `AuthController` with `POST /register` and `POST /login`
- Test with Postman: register → login → get token → use token on a protected route

**Key learning:** How Spring Security filter chain works. What SecurityContextHolder is. How JWT replaces sessions.

---

### ✅ Day 3 — Workspace Module + Membership
**Goal:** Create workspaces, invite members, enforce workspace roles.

**What to do:**
- Create `Workspace`, `WorkspaceMember` entities and their repositories
- Create `WorkspaceService`:
    - `createWorkspace()`: save workspace, also create a WorkspaceMember entry for the creator with role OWNER — both in one `@Transactional` method
    - `getMyWorkspaces()`: find all workspaces where current user is a member
    - `inviteMember()`: check workspace exists, check user exists, check not already a member, save new WorkspaceMember — `@Transactional`
    - `removeMember()`: only OWNER can remove; don't allow removing the last OWNER
- Write a private helper: `getMemberOrThrow(userId, workspaceId)` — reuse this everywhere
- Create `WorkspaceController` with all workspace endpoints
- Add `@PreAuthorize` where needed, or do manual role checks in the service

**Key learning:** Service-layer authorization patterns. How @Transactional rolls back when you throw an exception mid-method.

---

### ✅ Day 4 — Project Module + Task Module
**Goal:** Create projects inside workspaces, create and manage tasks.

**What to do:**
- Create `Project` entity/repo/service/controller
    - Before creating a project, check the user is a MANAGER or OWNER in that workspace
    - `archiveProject()`: set project status to ARCHIVED, set all IN_PROGRESS tasks to a paused state — wrap in `@Transactional`
- Create `Task` entity/repo/service/controller
    - Create task: set `createdBy` to current user, validate assignee is a workspace member
    - `changeTaskStatus()`: implement a simple state machine — TODO can go to IN_PROGRESS, IN_PROGRESS can go to IN_REVIEW or back to TODO, IN_REVIEW can go to DONE or back to IN_PROGRESS
    - Write an audit log entry every time a task status changes — same transaction
- Add filtering on `GET /tasks` by status, priority, and assignee using JPA `@Query` or Spring Data query methods

**Key learning:** JPA `@Query` with JPQL. State machine pattern in services. Multi-step @Transactional operations.

---

### ✅ Day 5 — Comments + Audit Log + Exception Handling
**Goal:** Add commenting, build audit trail, handle errors gracefully.

**What to do:**
- Create `Comment` entity/repo/service/controller (simpler — no special rules)
- Create `AuditLog` entity/repo
    - Create an `AuditService` with a `log(action, entityType, entityId, userId)` method
    - Call this from inside task status changes, member invitations, project creation etc.
- Create `GlobalExceptionHandler` with `@RestControllerAdvice`:
    - Handle `ResourceNotFoundException` → return 404 with message
    - Handle `UnauthorizedException` → return 403
    - Handle `MethodArgumentNotValidException` → return 400 with field errors from `@Valid`
    - Handle generic `Exception` → return 500 with safe message
- Add `@Valid` to all your request DTOs and add validation annotations (`@NotBlank`, `@Email`, `@Size`, etc.) on DTO fields

**Key learning:** How @RestControllerAdvice intercepts exceptions app-wide. How @Valid triggers bean validation. Why you should never let raw exceptions escape controllers.

---

### ✅ Day 6 — Admin Endpoints + Security Hardening
**Goal:** Build admin features, tighten up security.

**What to do:**
- Add `GET /api/admin/users` — list all users (ADMIN only)
- Add `PATCH /api/admin/users/{id}/role` — promote/demote users (ADMIN only)
- Add `GET /api/admin/workspaces` — list all workspaces (ADMIN only)
- Use `@EnableMethodSecurity` in your config and use `@PreAuthorize("hasRole('ADMIN')")` on admin service methods
- Make sure no user can access another user's data by crafting requests (test this manually)
- Review all your service methods: are you checking "does the current user have permission" before every mutation?
- Add a seed script or `CommandLineRunner` bean that creates a default ADMIN user on first startup if no users exist
- Review your DTOs: make sure you never return the password field, never return internal IDs you don't need to expose

**Key learning:** Method-level security with @PreAuthorize. The principle of least privilege. Why input validation and output sanitization are both important.

---

### ✅ Day 7 — Testing + Polish + README
**Goal:** Write tests, clean up, document.

**What to do:**
- Write at least 3 unit tests for your service layer using JUnit 5 + Mockito:
    - Test that creating a workspace also creates a WorkspaceMember for the owner
    - Test that inviting an already-existing member throws an exception
    - Test that a MEMBER cannot archive a project (throws UnauthorizedException)
- Write 2 integration tests using `@SpringBootTest` + `@AutoConfigureMockMvc`:
    - Test the full register → login → create workspace flow
    - Test that an unauthenticated request to `/api/workspaces` returns 403
- Clean up: remove any `System.out.println`, remove unused imports, standardize response formats
- Use a consistent API response wrapper: `{ "success": true, "data": {...}, "message": "..." }`
- Final test in Postman: go through an entire flow end to end as a new user

**Key learning:** How to mock dependencies in unit tests. How @SpringBootTest wires the full context. What makes a test actually useful.

---

## ⚙️ application.yml Configuration Reference

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/projectmanager
    username: postgres
    password: yourpassword
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update       # use 'validate' once schema is stable
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  jackson:
    default-property-inclusion: non_null   # don't return null fields in JSON

jwt:
  secret: your-256-bit-secret-key-here-make-it-long-and-random
  expiration: 86400000   # 24 hours in milliseconds

server:
  port: 8080
```

---

## 🔗 JPA Relationship Quick Reference

```
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "workspace_id", nullable = false)
private Workspace workspace;

// For nullable relationships (task assignee can be null):
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "assignee_id", nullable = true)
private User assignee;

// For enums — always STRING, never ORDINAL:
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private TaskStatus status;

// For timestamps:
@CreationTimestamp
@Column(updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
private LocalDateTime updatedAt;
```

---

## 🚨 Common Mistakes to Avoid

**1. LazyInitializationException**
You load an entity in a transaction, the transaction ends, then you try to access a lazy-loaded relationship outside the transaction. Fix: use `@Transactional` on the calling service method, or use DTOs and map inside the transaction.

**2. N+1 Query Problem**
Fetching a list of 50 tasks where each task lazily loads its project = 51 queries. Fix: use `JOIN FETCH` in your JPQL query when you know you need the related entity.

**3. Bidirectional relationship infinite JSON recursion**
`Task` has `Project`, `Project` has `List<Task>`, Jackson tries to serialize forever. Fix: use `@JsonIgnore` on the back-reference, or better — don't serialize entities at all, always use DTOs.

**4. Forgetting to check membership**
Anyone authenticated can hit `/api/projects/{id}/tasks` if you forget the membership check. Fix: always call your `getMemberOrThrow` helper at the top of every workspace-scoped service method.

**5. @Transactional on private methods**
Spring AOP can't intercept private methods — `@Transactional` is silently ignored. Always put it on `public` methods.

**6. Using the same DTO for request and response**
Your `CreateTaskRequest` should not be the same class as `TaskResponse`. They evolve independently. Keep them separate.

---

## 🏁 Definition of Done (What "Complete" Looks Like)

By the end of Day 7, you should be able to:

- [ ] Register two users (Alice and Bob)
- [ ] Log in as Alice, get a JWT
- [ ] Create a workspace as Alice
- [ ] Invite Bob to the workspace with MEMBER role
- [ ] Log in as Bob, create a project and a task
- [ ] Assign that task to Alice
- [ ] Alice changes the task status from IN_PROGRESS to IN_REVIEW
- [ ] Bob adds a comment on the task
- [ ] The entire history shows up in the audit log
- [ ] An unauthenticated user cannot access any of these endpoints
- [ ] A user not in the workspace cannot access that workspace's resources

If you can walk through all of the above in Postman, you've genuinely understood everything you studied.

---

## 🚀 Stretch Goals (If You Finish Early)

- Add **refresh tokens** — store in DB, allow token rotation on expiry
- Add **pagination** using `Pageable` on all list endpoints
- Add **Flyway** for proper DB migrations instead of `ddl-auto=update`
- Add **Spring Cache** (`@Cacheable`) on workspace member lookups
- Add a `/api/projects/{id}/tasks/summary` endpoint that returns task count by status — practice aggregate JPQL queries
- Dockerize with a `docker-compose.yml` that spins up your app + Postgres together

---

*Good luck. Build it, break it, fix it. That's how it sticks.*