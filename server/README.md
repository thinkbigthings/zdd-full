# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Prerequisites

Software that needs to be installed:

* Java 15
* Docker


### Install Java

Download the latest Java from [AdoptOpenJDK](https://adoptopenjdk.net)


### Install Docker

On Linux: `sudo apt install docker.io`
Note: On Linux, needed to run docker as sudo.
docker daemon must run as root, but you can specify that a group other than docker should own the Unix socket with the -G option.

On Mac: can install Docker Desktop from docker hub, or use brew



# Database Migrations


## Docker Postgres

Running the full build (with the integration tests) will create and populate
a docker container with a postgres database. The database is normally built and
destroyed for every build, but to leave it up and running after the tests
(say, for inspection, or to run the app standalone), find the PostgreSQLContainer
in the test code, and call its `.withReuse(true)` method with `true` instead of `false` to leave it
up and running even after the build finishes.

Handy Commands:

See running images with `docker ps`
Stop a container with `docker container stop container_name`



## Migrations

We use [Flyway](https://flywaydb.org) and run the migration standalone (not on default startup of the server)
so that we have more control over the migration process.

The server is run in a "migration only" mode that does the migrations and then shuts down.

e.g.

use `migrate` script that runs the migration profile with gradle,

or 
    cd server
    java --enable-preview -Dspring.profiles.active=migration -jar build/libs/server-1.0-SNAPSHOT.jar


## Heroku database

Can get a postgres command prompt with

    heroku pg:psql --app stage-zdd-full


## Environment variables

Heroku automatically creates environment variables for you. To see all of them, run

    heroku run env --app zdd-full

e.g.

    JAVA_OPTS=-XX:+UseContainerSupport -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8
    PORT=38476


## Fitting in with Heroku

There are a number of database connection environment variables generated automatically by Heroku.
They overlap, so you can use them with different technologies (i.e. straight Java vs Spring)

SPRING_DATASOURCE_URL to Spring is the same as spring.datasource.url
given the [properties rules](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config-relaxed-binding-from-environment-variables).

command line properties take top priority, so even though SPRING_DATASOURCE_URL is defined as an environment variable
automatically by Heroku, we can still override it in the command in the Procfile
to add custom database properties to the URL

    DATABASE_URL=postgres://pafei...:23782e...@ec2-34-236-215-156.compute-1.amazonaws.com:5432/d5oqne55s6np1v

    JDBC_DATABASE_URL=jdbc:postgresql://ec2...compute-1.amazonaws.com:5432/d5oqne55s6np1v?password=23782e...&sslmode=require&user=pafei...
    JDBC_DATABASE_USERNAME=pafei...
    JDBC_DATABASE_PASSWORD=23782e2da93a7a8f987949613942f9ff30a530afc640e6e05294a4cd6658c3b4

    SPRING_DATASOURCE_URL=jdbc:postgresql://ec2...compute-1.amazonaws.com:5432/d5oqne55s6np1v?password=23782e...&sslmode=require&user=pafei...
    SPRING_DATASOURCE_USERNAME=pafei...
    SPRING_DATASOURCE_PASSWORD=23782e...



## Heroku Database Migrations

See [Heroku Migrations](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps)

Heroku's [release phase](https://devcenter.heroku.com/articles/release-phase)
is one intended mechanism for migrations.

Heroku requires apps to bind a port in 60s or it's considered crashed.
Migrations can eat into that time, so do that separately from deployment.
The release phase has a 1h timeout and a release can be
monitored and [stopped](https://help.heroku.com/Z44Q4WW4/how-do-i-stop-a-release-phase).

Running from a [flyway caller](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps#using-flyway)
is the best way to do a migration without doing the source code deployment.

Besides the release phase, database migrations can also be run in a
[one-off dyno](https://devcenter.heroku.com/articles/one-off-dynos)


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

We don't include the p12 file when deploying to heroku, 
but get https by virtue of being a subdomain of herokuapps.com which has a CA cert.
Http automatically redirects to https on heroku. Locally it always requires https.


## Running

Doing a full build and leaving the docker container for postgres running
will allow us to run standalone.

## Debugging

Right click the main class and "Debug Application (main)"

## Testing



### Unit test
 
    gradlew test

### Integration Test

    gradlew integrationTest
    
### Both tests

    gradlew check
    
### Code Coverage

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
