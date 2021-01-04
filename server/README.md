# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Prerequisites

Software that needs to be installed:

* Java 15
* Docker


## Docker Postgres

Running the full build (with the integration tests) will create and populate
a docker container with a postgres database. The database is normally built and
destroyed for every build, but to leave it up and running after the tests
(say, for inspection, or to run the app standalone), find the PostgreSQLContainer
in the test code, and call its `.withReuse(true)` method with `true` to leave it 
up and running even after the build finishes.



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
