# Java 21 Compatibility Notes

## Notes on Java 21 compatibility

This document summarizes practical compatibility notes and recommendations for using Java 21 in the practice repository. The project has been tested to work with Java 21 and the GitHub Actions workflows are configured accordingly.

### Common issues to watch for

- **Locale-specific formatting in tests**: ensure deterministic formatting by using `Locale.US` wherever string numeric formatting is asserted.
- **Coverage tooling**: JaCoCo is compatible with Java 21; if issues arise, verify JaCoCo and Maven plugin versions. JaCoCo is configured as an opt-in profile (`-P coverage`) to avoid impacting standard builds.
- **Mockito and sealed interfaces**: Mockito 5.x supports mocking sealed interfaces in most cases. `TaskRepository` is a `sealed interface` — keep Mockito and Byte Buddy versions up-to-date if mocking issues appear.

### Project adjustments applied

- Code, comments and documentation written in English throughout.
- `pom.xml` sets Java 21 via `maven.compiler.release` property.
- JaCoCo moved to an opt-in `coverage` profile to keep the default build lean.

### Recommended Maven properties
```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <junit.version>6.0.3</junit.version>
    <mockito.version>5.23.0</mockito.version>
    <jacoco.version>0.8.11</jacoco.version>
</properties>
```

### Surefire / JVM flags
Tests run without special JVM flags on Java 21. If dynamic agent loading warnings appear (Mockito/Byte Buddy), they typically do not block CI. Keep dependencies up-to-date to avoid them.

### Test results
- ✅ Tests pass on Java 21 in CI when dependencies are updated appropriately
- ✅ JAR generation and Javadoc generation work
- ⚠️ Dynamic agent loading warnings from Mockito can be reviewed but do not block CI

### Recommendations for students

- Target Java 21 in `pom.xml` and in GitHub Actions workflows (`actions/setup-java@v4` with `java-version: '21'`).
- Use `cache: maven` in `actions/setup-java@v4` to speed up dependency downloads in CI.
- Prefer stable LTS versions (Java 21) for student projects to avoid tooling incompatibilities.
- Document any tool-specific limitations in the repository `README`.

---
**Date**: April 2026
**Java Version**: 21
**Status**: Configured for classroom use
