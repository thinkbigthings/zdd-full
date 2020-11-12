# Database Migrations

Update docs
update migration section from Server, Update cloud deployment section
Running from a [flyway caller](https://devcenter.heroku.com/articles/running-database-migrations-for-java-apps#using-flyway) seems appropriate
Heroku [release phase](https://devcenter.heroku.com/articles/release-phase) seems to be the intended mechanism for migrations, 
it has a 1h timeout and a release can be monitored and [stopped](https://help.heroku.com/Z44Q4WW4/how-do-i-stop-a-release-phase). 

Heroku requires apps to bind a port in 60s or it's considered crashed.
Migrations can eat into that time, so do that separately from deployment.

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
    
