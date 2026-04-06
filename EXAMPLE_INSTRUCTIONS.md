# CI/CD with GitHub Actions — In-class Exercise

This short exercise illustrates the CI/CD concepts covered in the lecture. The goal is to **observe** an existing pipeline in action, not to build one from scratch.

## Prerequisites
- GitHub account
- Basic Git knowledge

---

## Step 1 — Get your own copy of the repository

1. **Create an empty repository** in your GitHub account:
   - Go to [github.com/new](https://github.com/new)
   - Give it any name (e.g., `taskmanager-cicd`)
   - Leave *"Add a README"* and all other options **unchecked**
   - Click *"Create repository"*

2. **Clone the course repository** to your machine:
   ```bash
   git clone https://github.com/<professor>/<repo-name>.git
   cd <repo-name>
   ```

3. **Add your own repository as a second remote:**
   ```bash
   git remote add mygithub https://github.com/<your-username>/taskmanager-cicd.git
   ```
   At this point the local repository has two remotes:
   - `origin` — the course repository (read-only for you)
   - `mygithub` — your own copy on GitHub

4. **Push the code to your repository:**
   ```bash
   git push -u mygithub main
   ```

After the push, go to your repository on GitHub — you should already see the **Actions** tab showing a pipeline run in progress.

---

## Step 2 — Read the main workflow

Open `.github/workflows/build.yml`. This is the pipeline that runs automatically on every push:

```yaml
name: Build

on:
  push:
    branches: [ main ]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "21"
          cache: maven

      - name: Build with Maven
        run: mvn -B clean package --file pom.xml

      - name: Generate JavaDoc
        run: mvn javadoc:javadoc

      - name: Upload JAR artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/*.jar

      - name: Upload JavaDoc artifact
        uses: actions/upload-artifact@v4
        with:
          name: javadoc
          path: target/site/apidocs
```

---

## Step 3 — Single Job vs Multi Job

This project includes two additional workflows that illustrate different CI/CD approaches:

- **`ci-single-job.yml`** — All stages (compile, test, package, integration-test) as steps within a single job. Simple and sufficient for small projects, but offers limited visibility per stage.
- **`ci-multi-job.yml`** — Each stage as an independent job with `needs:` dependencies. Enables greater parallelism, per-job status badges, and finer control over failures.

Explore both files in `.github/workflows/` and compare the structure. Then go to the **Actions** tab in your repository and trigger a run by making a small change (e.g., editing the README):

```bash
echo "" >> README.md
git add README.md
git commit -m "Trigger CI"
git push mygithub main
```

---

## Step 4 — Observe the pipeline

In the **Actions** tab:
- Watch each job turn green (or red) as it completes.
- Click on a job to see its individual steps and logs.
- Download the uploaded artifacts (JAR, Javadoc) from the run summary page.

---

## Step 5 — Break the pipeline (optional)

Introduce a compile error and push:
```bash
# In src/main/java/org/iips/actions/service/TaskService.java
# Add a syntax error, save, commit and push
git add .
git commit -m "Introduce compile error"
git push mygithub main
```

Observe which job fails in `ci-multi-job.yml` and how the dependent jobs are skipped automatically.
