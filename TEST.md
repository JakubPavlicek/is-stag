# Spring Boot 4.0.1 Testing Guide: Unit & Integration Tests

Based on current best practices, here's a comprehensive approach to testing Spring Boot 4.0.1 microservices with modern tools and patterns.

### Testing Strategy: The Test Pyramid

For microservices, follow the Test Pyramid principle:

- 70-80% Unit Tests: Fast, isolated, test business logic
- 15-20% Integration Tests: Test component interactions and database layers
- 5-10% E2E Tests: Critical user journeys across services

**Technologies to Use**

| Concern                | Unit (inside JVM)  | Integration (full Spring context)  | Remarks                                                                  |
|------------------------|--------------------|------------------------------------|--------------------------------------------------------------------------|
| Test runner            | JUnit 6            | JUnit 6                            | de-facto standard                                                        |
| Assertions + fluent    | AssertJ            | AssertJ                            | produces human-readable failure messages                                 |
| Mocking collaborators  | Mockito            | – (use `@SpringBootTest`)          | Mockito 5.21 is already on the classpath with `spring-boot-starter-test` |
| Repository slice tests | -                  | `@DataJpaTest`                     | Testing database layer                                                   |
| Testing MVC layer only | -                  | `@WebMvcTest`                      | Testing MVC layer only (with MockMvc)                                    |
| Test data builder      | Instancio          | –                                  | keeps tests DRY                                                          |
| Test containers        | –                  | Testcontainers (JUnit 6 extension) | spin up real Postgres, Redis…                                            |
| DB schema creation     | –                  | Hibernate DDL                      | guarantees schema parity with prod                                       |
| Build tool             | Maven              | same                               | -                                                                        |
| CI                     | GitHub Actions     | same                               | matrix build with caches                                                 |
| Quality gate           | SonarQube / Jacoco | same                               | 80 % branch coverage gate                                                |

## 1. Unit Testing

**Best Practices**

- Don't use `@SpringBootTest` – it loads full context; unit tests should be lightweight
- Mock all external dependencies – repositories, clients, services
- Follow Given-When-Then Pattern
- Descriptive test names: methodName_StateUnderTest_ExpectedBehavior()
- Test one behavior per test
- Use `@ExtendWith(MockitoExtension.class)`
- Test edge cases: null inputs, exceptions, boundaries

## 2. Integration Testing

**Best Practices**

- Use test slices (`@WebMvcTest`, `@DataJpaTest`) instead of full context when possible
- Use real databases with TestContainers (avoid H2 for production-like behavior)
- Rollback transactions after each test
- Mock external services with WireMock
- Run on random ports for parallel test execution
- Reuse application context across test classes
- Use `@ActiveProfiles("test")` for test-specific configs

## Other Strategies

- Always use `@DisplayName` annotation for test names
- Use Context Caching
- Use `@TestConfiguration` to expose beans only for tests
- Use Instancio – never reuse builders from the main code.
- Make tests reproducible with a fixed random seed (`Instancio.gen().seed(42);`)
- Test security configuration (Spring Security has support for it)
- Avoid `@DirtiesContext` unless necessary

## Strategies for "Structured Concurrency" Testing

- Structured concurrency introduces three new failure modes:

| Mode                          | Symptom                                | How to verify                                                 |
|-------------------------------|----------------------------------------|---------------------------------------------------------------|
| 1. Scope leak                 | Task keeps running after test ends     | JUnit extension that `join()`s or `shutdown()`s the scope     |
| 2. Cancellation race          | Child task ignores `Thread::interrupt` | Assert cancellation was *immediate* (< 50 ms)                 |
| 3. Uncaught exception in fork | Escaped unchecked exception            | Assert scope throws `ExecutionException` with suppressed list |

- Make the executor controllable in tests so futures complete immediately and deterministically
- Use a fixed `ScheduledExecutorService` to test time-outs
- Test time-out logic with Awaitility + Retry
- Use `@RepeatedTest` + same-thread executor to find races
- Document the contract with `@Timeout` on the test method
- Finally – always await uninterruptibly in tests
- Awaitility or AssertJ succeedsWithin() – never `join()` or `get()` in tests

## Generic Rules

- Don't write any given/when/then comments
- Don't write any Javadoc comments
- For JSON strings, use two spaces indentation
- Also test negative cases

## @RestController Tests Rules

- Test 401 and all exceptions from the `GlobalExceptionHandler` class
- Test invalid input (just thoroughly – not every possible edge case)
- Always put the JSON body into the `content()` method directly

## JpaRepository Tests Rules

- Use @DataJpaTest instead of @SpringBootTest
- Don't use @TestEntityManager or @EntityManager – use repository directly
- Use Testcontainers to spin up a real Oracle 19c Database