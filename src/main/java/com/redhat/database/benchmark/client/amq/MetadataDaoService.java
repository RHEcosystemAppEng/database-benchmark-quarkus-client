package com.redhat.database.benchmark.client.amq;

import com.redhat.database.benchmark.client.Message;
import com.redhat.database.benchmark.client.Metadata;
import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;

@Singleton
public class MetadataDaoService {

    private Logger logger = LoggerFactory.getLogger(MetadataDaoService.class);

    @Inject
    AgroalDataSource h2DataSource;

    @PostConstruct
    void config() {
        initdb();
    }

    private void initdb() {
        logger.info("Going to initiate Messages table if it is not existing");

        try (Connection connection = h2DataSource.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(" CREATE TABLE IF NOT EXISTS METADATA (" +
                        "  id serial PRIMARY KEY, " +
                        "  benchmarkseqid varchar(100) , " +
                        "  starttime datetime, " +
                        "  endtime datetime " +
                        ")");
                logger.info("METADATA table setup is done.");
            } catch (SQLException e) {
                logger.error("Statement - Error while processing METADATA table setup", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Connection - Error while processing METADATA table setup", e);
            throw new RuntimeException(e);
        }
    }


    public int deleteMetada() {
        int deletedNoOfRecords = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "DELETE FROM METADATA";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                deletedNoOfRecords = stmt.executeUpdate();
                logger.info("All metadata deleted in h2 database ={}",deletedNoOfRecords);
            } catch (SQLException e) {
                logger.error("Error processing deleteMetada statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing deleteMetada connection", e);
            throw new RuntimeException(e);
        }
        return deletedNoOfRecords;
    }


    public void insertMetadata(Metadata metadata) {
        this.deleteMetada();
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = " INSERT INTO METADATA(benchmarkseqid, starttime) VALUES(?, ?) ";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                stmt.setString(1,metadata.getBenchmarkSeqId());
                stmt.setTimestamp(2, metadata.getStartTime());
                int result = stmt.executeUpdate();
                logger.info("metadata is inserted to h2={}",result);
            } catch (SQLException e) {
                logger.error("Error processing insertMessage statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing insertMessage connection", e);
            throw new RuntimeException(e);
        }
    }



    public int updateMetadata(Metadata metadata) {
        int result = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "UPDATE METADATA SET endtime=? WHERE benchmarkseqid=? ";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                stmt.setTimestamp(1,metadata.getEndTime());
                stmt.setString(2, metadata.getBenchmarkSeqId());
                result = stmt.executeUpdate();
                logger.info("metadata is updated in h2 database ={}",result);
            } catch (SQLException e) {
                logger.error("Error processing updateMetadata statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing updateMetadata connection", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public Metadata getMetadata() {
        Metadata metadata = null;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT * FROM METADATA";
            try (Statement stmt = connection.createStatement()) {

                ResultSet resultSet = stmt.executeQuery(SQL);
                if (resultSet.next()) {
                    metadata = new Metadata(resultSet.getString("benchmarkseqid"),resultSet.getTimestamp("starttime"), resultSet.getTimestamp("endtime"));
                }

            } catch (SQLException e) {
                logger.error("Error processing getMetadata statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing getMetadata connection", e);
            throw new RuntimeException(e);
        }
        return metadata;
    }


    public void printMetadata() {
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT * FROM METADATA";
            try (Statement stmt = connection.createStatement()) {

                ResultSet resultSet = stmt.executeQuery(SQL);
                while (resultSet.next()) {
                    // Now we can fetch the data by column name, save and use them!
                    String id = resultSet.getString("id");
                    Timestamp starttime = resultSet.getTimestamp("starttime");
                    Timestamp endtime = resultSet.getTimestamp("endtime");
                    String benchmarkseqid = resultSet.getString("benchmarkseqid");


                    System.out.println("\tid: " + id +
                            ", starttime: " + starttime +
                            ", endtime: " + endtime +
                            ", benchmarkseqid: " + benchmarkseqid);
                }

            } catch (SQLException e) {
                logger.error("Error processing printMetadata statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing printMetadata connection", e);
            throw new RuntimeException(e);
        }
    }



    public int getMetadataCount() {
        int rowCount = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT count(*) as row_count FROM METADATA ";
            try (Statement stmt = connection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(SQL);
                while (resultSet.next()) {
                    // Now we can fetch the data by column name, save and use them!
                    rowCount = resultSet.getInt("row_count");
            }
            } catch (SQLException e) {
                logger.error("Error processing getMetadataCount statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing getMetadataCount connection", e);
            throw new RuntimeException(e);
        }

        return rowCount;
    }

}
