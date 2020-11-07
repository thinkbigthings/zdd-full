
# Deploy to Cloud (Heroku)

## Preparation:

- Procfile needs to be there
Procfile needs to specify the port, port is not picked up automatically as a property override by spring boot
- Need system.properties file with java.runtime.version=15 to use something other than the default Java 8
- Put properties that don't differ between environments in src/main/resources/application.properties

## Deploy JAR

https://devcenter.heroku.com/articles/deploying-executable-jar-files

cd heroku/build
heroku plugins:install java
heroku create --no-remote
heroku apps:rename --app safe-spire-21060 zdd-full
heroku addons:create papertrail --app zdd-full
heroku addons:create heroku-postgresql --app zdd-full
heroku config --app zdd-full
heroku pg --app zdd-full
heroku deploy:jar server-1.0-SNAPSHOT.jar --app zdd-full --include Procfile system.properties
heroku run ls --app zdd-full
heroku logs --app zdd-full
heroku open --app zdd-full

## Heroku Database Migrations

See [Heroku Migrations](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps)

Heroku requires apps to bind a port in 60s or it's considered crashed
Migrations can eat into that time (and the free dyno is not super fast), so do that separately from deployment

We can run a migration with a one-off dyno. Would rather not use the release phase
because it's better to monitor the migration and app.
A one-off dyno needs to finish in 1h unless run:detached which is 24h

