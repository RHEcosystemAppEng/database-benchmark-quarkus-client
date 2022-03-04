package com.redhat.database.benchmark;

import com.redhat.database.benchmark.client.Message;
import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.time.Instant;

@Singleton
public class ErrorDaoService {

    private Logger logger = LoggerFactory.getLogger(ErrorDaoService.class);

    @Inject
    AgroalDataSource h2DataSource;

    @PostConstruct
    void config() {
        initdb();
    }

    private void initdb() {
        logger.info("Going to initiate Errors table if it is not existing");
        try (Connection connection = h2DataSource.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(" CREATE TABLE IF NOT EXISTS Errors ( " +
                        "  id serial PRIMARY KEY, " +
                        "  error_message varchar(1000),  " +
                        " created datetime " +
                        ")");
                logger.info("Errors table setup is done.");
            } catch (SQLException e) {
                logger.error("Statement - Error while processing Errors table setup", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Connection - Error while processing Errors table setup", e);
            throw new RuntimeException(e);
        }
    }





    public void insertError(String errorMessage) {
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = " INSERT INTO ERRORS (error_message, created) VALUES(?, ?) ";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                stmt.setString(1,errorMessage);
                stmt.setTimestamp(2, Timestamp.from(Instant.now()));

                int result = stmt.executeUpdate();
                logger.info("Error is inserted to h2={}",result);
            } catch (SQLException e) {
                logger.error("Error processing insertError statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing insertError connection", e);
            throw new RuntimeException(e);
        }
    }



    public void printTopHunErrors() {
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT * from ERRORS limit 100";
            try (Statement stmt = connection.createStatement()) {

                ResultSet resultSet = stmt.executeQuery(SQL);
                while (resultSet.next()) {
                    System.out.println("\tid: " + resultSet.getString("id") +
                            ", error_message: " + resultSet.getString("error_message") +
                            ", created: " + resultSet.getString("created"));
                }

            } catch (SQLException e) {
                logger.error("Error processing printTopHunErrors statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing printTopHunErrors connection", e);
            throw new RuntimeException(e);
        }
    }

    public long getErrorsCount() {
        long rowCount = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT count(*) as row_count FROM ERRORS ";
            try (Statement stmt = connection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(SQL);
                while (resultSet.next()) {
                    rowCount = resultSet.getLong("row_count");
                }
            } catch (SQLException e) {
                logger.error("Error processing getErrorsCount statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing getErrorsCount connection", e);
            throw new RuntimeException(e);
        }

        return rowCount;
    }

    public long deleteAllErrors() {
        long deletedNoOfRecords = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "DELETE FROM ERRORS";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                deletedNoOfRecords = stmt.executeUpdate();
                logger.info("All errors deleted in h2 database ={}",deletedNoOfRecords);
            } catch (SQLException e) {
                logger.error("Error processing deleteAllErrors statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing deleteAllErrors connection", e);
            throw new RuntimeException(e);
        }
        return deletedNoOfRecords;
    }

}
