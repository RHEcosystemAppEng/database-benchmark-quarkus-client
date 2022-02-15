package com.redhat.database.benchmark.client.postgres;

import com.redhat.database.benchmark.client.IBenchmarkService;
import com.redhat.database.benchmark.client.Fruit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@ApplicationScoped
public class RdbmsFruitService implements IBenchmarkService {
    private Logger logger = LoggerFactory.getLogger(RdbmsFruitService.class);
    @Inject
    Instance<DataSource> rdbmsDataSourceInstance;

    @PostConstruct
    void config() {
        initdb();
    }

    private void initdb() {
        logger.info("Going to initiate database");

        try (Connection connection = rdbmsDataSourceInstance.get().getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(" CREATE TABLE IF NOT EXISTS fruits ( " +
                        "  id serial PRIMARY KEY, " +
                        "  uuid varchar(100) , " +
                        "  name varchar(100) , " +
                        "  description varchar(450) " +
                        ")");
                logger.info("RDBMS Database initiated");
            } catch (SQLException e) {
                logger.error("Error processing statement", e);
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            logger.error("Error processing connection", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Fruit add(Fruit fruit) {
        logger.debug("Adding Fruits");
        try (Connection connection = rdbmsDataSourceInstance.get().getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                int insertedRecordsCount = stmt.executeUpdate("INSERT INTO fruits(uuid, name, description) VALUES ('" + fruit.getUuid() + "', '" + fruit.getName() + "', '" + fruit.getDescription() + "')");
                logger.debug("Fruit Record Inserted - {}", insertedRecordsCount);
            }
        } catch (SQLException e) {
            logger.error("Error processing connection", e);
            throw new RuntimeException(e);
        }
        return fruit;
    }

    @Override
    public Fruit get(String id) {
        logger.debug("Getting Fruit");
        Fruit fruit;

        try (Connection connection = rdbmsDataSourceInstance.get().getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(" SELECT * FROM FRUITS WHERE UUID='" + id + "'")) {
                    fruit = new Fruit(rs.getString("uuid"), rs.getString("name"), rs.getString("description"));
                }
            }
            return fruit;
        }catch (SQLException e) {
            logger.error("Error processing connection", e);
            throw new RuntimeException(e);
        }
    }
}