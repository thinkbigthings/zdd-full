# Database Migrations

## Prerequisites

Software that needs to be installed:

* PostgreSQL 12 (via docker, see below)


## Database Setup

To make it easy for development, we can use a docker container to run Postgres.
Some postgres-on-docker steps are here: https://hackernoon.com/dont-install-postgres-docker-pull-postgres-bee20e200198


### Install Docker

On Linux: `sudo apt install docker.io`
Note: On Linux, needed to run docker as sudo.
docker daemon must run as root, but you can specify that a group other than docker should own the Unix socket with the -G option.

On Mac: can install Docker Desktop from docker hub, or use brew


### Set up Docker PG

Then pull the docker image: `docker pull postgres:12`

### Prepare DB

After starting the container run create-db.sql 
Flyway connects to an existing database in a transaction,
and creating a database is outside a transaction, so db creation should be part of setup.

use different host port in case there are other Postgres instances running on the host
POSTGRES_PASSWORD is the password for the default admin "postgres" user

If you have PG on the host, you can just call from the host:
`psql -h localhost -p 5555 -U postgres -f db/create-db.sql`

If you do NOT have PG on the host, you should be able to access the database from within docker:
`docker exec -it pg-12-docker psql -U postgres --command="CREATE DATABASE app OWNER postgres ENCODING 'UTF8';"`

Blow away the whole database and start from scratch

    docker container stop pg-12-docker
    docker run --rm --name pg-12-docker -e POSTGRES_PASSWORD=postgres -d -p 5555:5432 postgres:12
    docker exec -it pg-12-docker psql -U postgres --command="CREATE DATABASE app OWNER postgres ENCODING 'UTF8';"


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








## Heroku database

Can get a postgres command prompt with

    heroku pg:psql --app stage-zdd-full


## Fitting in with Heroku

Heroku's [release phase](https://devcenter.heroku.com/articles/release-phase) seems to be the intended mechanism for migrations.
Heroku requires apps to bind a port in 60s or it's considered crashed.
Migrations can eat into that time, so do that separately from deployment.
The release phse has a 1h timeout and a release can be 
monitored and [stopped](https://help.heroku.com/Z44Q4WW4/how-do-i-stop-a-release-phase). 

Running from a [flyway caller](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps#using-flyway) 
is the best way to do a migration without doing the source code deployment.


## Environment variables

Heroku automatically creates environment variables for you. To see all of them, run

    heroku run env --app zdd-full

e.g.

    JAVA_OPTS=-XX:+UseContainerSupport -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8
    PORT=38476



## Heroku Database Migrations

See [Heroku Migrations](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps)

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
    
