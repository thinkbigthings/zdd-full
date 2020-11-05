# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Prerequisites

Software that needs to be installed:

* Java 15
* PostgreSQL 11 (via docker, see below)
* Gradle (via gradle wrapper, see below)

## Database Setup



To make it easy for development, we can use a docker container to run Postgres.
Some postgres-on-docker steps are here: https://hackernoon.com/dont-install-postgres-docker-pull-postgres-bee20e200198


### Install Docker


On Linux: `sudo apt install docker.io`
Note: On Linux, needed to run docker as sudo.
docker daemon must run as root, but you can specify that a group other than docker should own the Unix socket with the -G option.

On Mac: can install Docker Desktop from docker hub, or use brew


### Set up Docker PG

Then pull the docker image: `docker pull postgres`

use different host port in case there are other Postgres instances running on the host
POSTGRES_PASSWORD is the password for the default admin "postgres" user
`docker run --rm --name pg-docker -d -e POSTGRES_PASSWORD=postgres -d -p 5555:5432 postgres`
`docker container ls`

### Prepare DB

After starting the container run create-db.sql 
Flyway connects to an existing database in a transaction,
and creating a database is outside a transaction, so db creation should be part of setup.

If you have PG on the host, you can just call from the host:
`psql -h localhost -p 5555 -U postgres -f db/create-db.sql`

If you do NOT have PG on the host, you should be able to access the database from within docker:
`docker exec -it pg-docker psql -U postgres --command="CREATE DATABASE app OWNER postgres ENCODING 'UTF8';"`

### Blow away and rebuild DB


To make this easier, see the commands file with the aliases.
Use `clean` and `migrate`
Just put ./commands on your PATH


Blow away the whole database and start from scratch

    docker container stop pg-docker
    docker run --rm   --name pg-docker -e POSTGRES_PASSWORD=postgres -d -p 5555:5432 postgres
    docker exec -it pg-docker psql -U postgres --command="CREATE DATABASE app OWNER postgres ENCODING 'UTF8';"


## Migrations

We use Flyway: https://flywaydb.org/getstarted/firststeps/gradle

Flyway as run from gradle doesn't by default use the database connection info in the properties file
It uses the database connection info in the "flyway" block in build.gradle
But we can load the properties from application.properties so we only have to define them in one place.

Drop all tables on managed schemas: `gradlew flywayClean -i`
Run all the migrations: `gradlew flywayMigrate -i`
Drop and run all migrations: `gradlew flywayClean; gradlew flywayMigrate -i`

We run the migration standalone (not on startup of the application)
So that we have more control over the migration process.


## Threads

The Logging filter's ScheduledThreadPoolExecutor has a core pool size

The server defines a number of standard spring boot threads:
server.tomcat.max-threads
server.tomcat.min-spare-threads 
server.tomcat.accept-count


## Security

## HTTPS

To make self-signed keys for dev:
`keytool -genkeypair -alias app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore app.dev.p12 -validity 3650`

To update HTTPS related files and properties, see the `server.ssl.*` properties used by Spring Boot

## Running

If starting with a new run of docker, need to ensure the migrations have been run
since they don't run automatically on app startup. See migration steps.

Run `gradlew bootRun`, or run `gradlew cleanRun` to clear the database and run the server in one step

## Debugging

Right click the main class and "Debug Application (main)"

## Testing

### Unit test
 
run `gradlew test`

Code coverage metrics with Jacoco
`gradlew test jacocoTestReport`
Then see output in build/reports/jacoco/html/index.html

### Manual test

curl quick guide: https://gist.github.com/subfuzion/08c5d85437d5d4f00e58

WITH SECURITY

(this one should fail)
curl -k --user user:password "https://localhost:9000/user/admin"

(this one should pass)
curl -k --user admin:admin "https://localhost:9000/user/admin"



rm cookies.txt
curl -k -v -b cookies.txt -c cookies.txt --user admin:admin "https://localhost:9000/login"
cat cookies.txt
curl -k -v -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
cat cookies.txt
curl -k -v -b cookies.txt -c cookies.txt "https://localhost:9000/logout"
cat cookies.txt
curl -k -v -b cookies.txt -c cookies.txt "https://localhost:9000/user/admin"
cat cookies.txt
rm cookies.txt

Run the server, then from another command line run `curl -k https://localhost:9000/user`

See most recent users:
`curl -k "https://localhost:9000/user?page=0&size=2&sort=registrationTime,desc"`

post:
`curl -k -X POST -H "Content-Type: application/json" -d '{"username":"user1", "displayName":"user1", "email":"us@r.com"}' https://localhost:9000/user`
or if the json is in a file:
`curl -k -X POST -H "Content-Type: application/json" -d @data-file.json https://localhost:9000/user`

Actuator (admin/management endpoints) enpoints are listed at
`https://localhost:9000/actuator`

For example, try /actuator/health

### Web

Base URL is at https://localhost:9000/index.html

Static content (built JS, etc) should go into src/main/resources/static


# Managing dependencies

From this project, use `../gradlew dependencies`

To upgrade versions of Java in general:

- Set the project base build.gradle's sourceCompatibility
- Update the README that references Java version

To upgrade versions of Java in IntelliJ:

- I think you need to add the SDK in Module Settings -> Platform Settings -> SDK
  But see if updating Build Tools below works first
- Click "IntelliJ IDEA" -> Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle
  and set Gradle JVM to the new version
- Might need to right click the project and go to module settings to set it there too?
- You'll also need to edit the version in any Run Configurations


## Cloud (Heroku)

### Initial setup notes

Getting started docs
https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#cloud-deployment-heroku
https://devcenter.heroku.com/articles/preparing-a-spring-boot-app-for-production-on-heroku
https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku
https://devcenter.heroku.com/articles/deploying-gradle-apps-on-heroku

Preparation:

Procfile needs to be in the root of the git repo
Need system.properties file with java.runtime.version=15
Put properties that don't differ between environments in src/main/resources/application.properties

Very First Time commands

heroku create
heroku apps:rename zdd-fullstack

heroku addons:create papertrail

heroku addons:create heroku-postgresql
heroku config
heroku pg

// note: deleted app online and was constantly getting "no such app" on command line
// resolved with git remote rm heroku

TODO Pushed to branch other than [main, master], skipping build.
https://stackoverflow.com/questions/14593538/make-heroku-run-non-master-git-branch
git push heroku heroku-branch:master
and set back with this (pushes master to heroku)
git push -f heroku master:master 



TODO not sure this is the right command, does it get the procfile?
git subtree push --prefix server heroku heroku
to push a non-master branch
git subtree push --prefix path/to/app-subdir heroku heroku-branch:master
or make a buildpack, see https://jtway.co/deploying-subdirectory-projects-to-heroku-f31ed65f3f2

or
git push heroku heroku-branch

heroku run gradlew flywayMigrate -i
heroku restart
heroku open


``

Heroku local can use environment variables in a local `.env` file 
It could read something like this:

```
DATABASE_URL=postgres://localhost:5432/gradle_database_name
```


### Heroku Database Migrations

Heroku requires apps to bind a port in 60s or it's considered crashed
Migrations can eat into that time, so do that separately from deployment

We can run a migration with a one-off dyno. Would rather not use the release phase
because it's better to monitor the migration and app.
A one-off dyno needs to finish in 1h unless run:detached which is 24h

Flyway is not on the classpath of the server, so it should not run automatically on startup.
