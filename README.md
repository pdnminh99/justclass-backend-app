# Just Class back-end application

## Info

- Semester: 19.2A - 1933
- Goals: Develop a classroom management application using Flutter & Spring Boot.

## Techs used

- GCP App Engine Standard.
- GCP Firestore Native.
- Spring Boot.

## How to run project locally

### Requirements

- Must include google credentials file in project's root directory, since this project is using GCP Firestore service. Rename as `key.json`.
- JDK11 and above, since this project use some features not included in JDK8 (for example, type inference `var` or functional method `ifPresentOrElseGet`, etc.).
- Maven 3.6.3 and above.

### How-to-run-locally

- Clone the project.
- Create GCP Firestore (Native) service, download google credentials key, rename it as `key.json` and place it at the root of the project.
- Run from terminal <code>mvn spring-boot:run</code>

### Deploy on GCP App Engine

- Create a project on App Engine.
- Create App Engine standard service and Firestore (Native) service, download google credentials key, rename it as `key.json` and place it at the root of the project.
- Modify `app.yaml` located in `src/main/appengine` directory. Replace environment variable `GOOGLE_CLOUD_PROJECT` as current `projectId`.
- Run from terminal <code>mvn package appengine:deploy -Dapp.deploy.projectId=[YOUR_PROJECT_ID]</code>.