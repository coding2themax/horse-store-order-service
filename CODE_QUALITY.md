# Code Quality Setup

This project includes comprehensive code quality checks that run automatically in GitHub Actions:

## Quality Tools Configured

### 1. **JaCoCo** - Code Coverage

- Generates test coverage reports
- Reports available in `target/site/jacoco/`
- HTML reports uploaded as artifacts in CI

### 2. **SpotBugs** - Static Analysis

- Finds potential bugs in Java code
- Configuration: `spotbugs-exclude.xml`
- Set to Medium threshold to balance thoroughness with noise
- Reports uploaded as artifacts in CI

### 3. **Checkstyle** - Code Style

- Enforces coding standards and style consistency
- Configuration: `checkstyle.xml` (custom configuration based on Sun checks)
- Checks indentation, naming conventions, imports, etc.
- Reports uploaded as artifacts in CI

### 4. **PMD** - Static Analysis

- Additional static analysis focusing on:
  - Best practices
  - Error-prone patterns
  - Security issues
- Reports uploaded as artifacts in CI

### 5. **OWASP Dependency Check** - Security

- Scans dependencies for known vulnerabilities
- Fails build on CVSS score ≥ 7 (High/Critical vulnerabilities)
- Configuration: `dependency-check-suppressions.xml` for false positives
- HTML reports uploaded as artifacts in CI

## Running Quality Checks Locally

```bash
# Run all quality checks
mvn clean test jacoco:report spotbugs:check checkstyle:check pmd:check org.owasp:dependency-check-maven:check

# Individual checks
mvn jacoco:report          # Code coverage
mvn spotbugs:check         # Static analysis
mvn checkstyle:check       # Code style
mvn pmd:check             # PMD static analysis
mvn org.owasp:dependency-check-maven:check  # Security check
```

## CI/CD Integration

The GitHub Actions workflow (`.github/workflows/maven.yml`) runs all quality checks on every push/PR to main/master branches.

- Quality checks are set to `continue-on-error: true` initially to avoid breaking builds
- All reports are uploaded as artifacts for review
- You can gradually make checks stricter by setting `failOnError: true` in the Maven plugin configurations

## Customizing Quality Rules

### SpotBugs

Edit `spotbugs-exclude.xml` to exclude specific bug patterns or classes.

### Checkstyle

Edit `checkstyle.xml` to modify style rules. Currently uses a moderate rule set.

### PMD

Modify rulesets in `pom.xml` under the PMD plugin configuration.

### Dependency Check

Add suppressions to `dependency-check-suppressions.xml` for known false positives.

## Recommended Next Steps

1. Review the quality reports from the first CI run
2. Fix any critical issues found
3. Gradually tighten the rules by:
   - Setting `failOnError: true` for tools
   - Lowering SpotBugs threshold to "Low"
   - Adding more PMD rulesets
   - Removing `continue-on-error: true` from GitHub Actions
