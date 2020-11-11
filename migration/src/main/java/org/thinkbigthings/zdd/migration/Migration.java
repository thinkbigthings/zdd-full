package org.thinkbigthings.zdd.migration;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Migration implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(Migration.class);

    public static void main(String[] args) {
        SpringApplication.run(Migration.class, args);
    }

    // need "autosave conservative" config, otherwise pg driver has caching issues with blue-green deployment
    // (org.postgresql.util.PSQLException: ERROR: cached plan must not change result type)
    // this isn't specified in the local properties because the URL could be automatically generated
    // when deployed to the cloud
    private final String pgOptions = "autosave=conservative";

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public Migration(Environment env) {
        dbUrl = env.getProperty("JDBC_DATABASE_URL") + "?" + pgOptions;
        dbUser = env.getProperty("JDBC_DATABASE_USERNAME");
        dbPassword = env.getProperty("JDBC_DATABASE_PASSWORD");
    }

    @Override
    public void run(String... args) {

        LOG.info("Preparing migrations on db url " + dbUrl);

        Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPassword)
                .load();

        LOG.info("Running migrations...");
        int numMigrationsApplied = flyway.migrate();
        LOG.info("Applied " + numMigrationsApplied + " migrations");
    }
}