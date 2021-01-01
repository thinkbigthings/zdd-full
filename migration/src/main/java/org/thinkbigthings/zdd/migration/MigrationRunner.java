package org.thinkbigthings.zdd.migration;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigrationRunner implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(MigrationRunner.class);

    public static void main(String[] args) {
        SpringApplication.run(MigrationRunner.class, args);
    }

    @Override
    public void run(String... args) {
        LOG.info("Migration ran and is exiting");
    }

}