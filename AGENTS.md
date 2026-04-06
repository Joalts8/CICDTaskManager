# AGENTS.md

This file provides guidance to AI coding assistants (GitHub Copilot, Cursor, Warp, etc.) when working with code in this repository. It is the standard way to give an AI agent the context it needs to understand the project without having to explore every file from scratch.

## Stack and runtime constraints
- Java 21 (project is configured with `maven.compiler.release` set to 21 in `pom.xml`)
- Maven is the build/test tool
- JUnit Jupiter 6 + Mockito are used for tests

## Common commands
- Compile:
  - `mvn compile`
- Clean + compile (common CI check):
  - `mvn clean compile`
- Run all tests:
  - `mvn test`
  - `mvn clean test`
- Run a single test class:
  - `mvn -Dtest=TaskServiceTest test`
- Run a single test method:
  - `mvn -Dtest=TaskServiceTest#shouldCreateValidTask test`
- Run integration tests:
  - `mvn integration-test`
- Build JAR:
  - `mvn package`
- Build JAR with clean:
  - `mvn clean package`
- Generate coverage report (opt-in profile):
  - `mvn test -P coverage`
- Generate Javadoc:
  - `mvn javadoc:javadoc`

## High-level architecture
- The codebase is split across four packages under `src/main/java/org/iips/actions/`:
  - `model`: `Task` — immutable value object defined as a Java 21 `record`. Validates id and description in the compact constructor.
  - `repository`: `TaskRepository` — `sealed interface` (only permits `InMemoryTaskRepository`). `InMemoryTaskRepository` uses a `ConcurrentHashMap` for thread-safe in-memory storage.
  - `service`: `TaskService` — service layer, injected with a `TaskRepository` via constructor. Contains all business logic: create, complete, delete, and find tasks.
  - `exception`: `TaskNotFoundException` and `InvalidTaskException` — unchecked exceptions thrown by the service layer.

## Test architecture
- `src/test/java/org/iips/actions/model/TaskTest.java`: Unit tests for the `Task` record (validation, equality).
- `src/test/java/org/iips/actions/repository/InMemoryTaskRepositoryTest.java`: Unit tests for `InMemoryTaskRepository`.
- `src/test/java/org/iips/actions/service/TaskServiceTest.java`: Unit tests for `TaskService` using `InMemoryTaskRepository` directly.
- `src/test/java/org/iips/actions/exception/ExceptionTest.java`: Unit tests for custom exception messages.
- `src/test/java/org/iips/actions/integration/TaskServiceIT.java`: Top-down integration tests for `TaskService` using Mockito mocks (`@Mock`, `@InjectMocks`). File follows the `*IT.java` naming convention.

## CI/CD Workflows
- `build.yml`: Single job — checkout, compile, test, package, javadoc (mirrors the cicdCalculator reference pipeline).
- `ci-single-job.yml`: All stages (compile, test, package, integration-test) as steps within a single job.
- `ci-multi-job.yml`: Each stage as an independent job with `needs:` dependencies (compile → test → build → integration-test; coverage and javadoc run in parallel after their dependencies).
- Individual workflows (`compile.yml`, `test.yml`, `integration-test.yml`, `javadoc.yml`): one workflow per stage, used to illustrate fine-grained pipeline control.
