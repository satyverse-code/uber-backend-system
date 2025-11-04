# Contributing Guide

## Branching
- `main` is protected. Open PRs from `dev` or feature branches.
- Preferred branch names: `feature/<topic>`, `bugfix/<topic>`, or `week-00-task-<name>`.

## Commit Convention
- Use Conventional Commits:
  - `feat: ...`
  - `fix: ...`
  - `docs: ...`
  - `chore: ...`
  - `refactor: ...`
  - `test: ...`

## Pull Requests
- Ensure build passes locally: `mvn clean verify`.
- Update docs if behavior changes.
- PR template checklist must be completed.
- Keep PRs small and focused.

## Code Style
- Java 17, Spring Boot best practices.
- Prefer constructor injection.
- Use `@Validated` and bean validation for inputs.

## Running Locally
- Start Postgres: `docker compose up -d`
- Run app: `mvn spring-boot:run`
