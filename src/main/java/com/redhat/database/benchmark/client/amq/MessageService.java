package com.redhat.database.benchmark.client.amq;

import com.redhat.database.benchmark.client.Message;
import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;

@Singleton
public class MessageService {

    private Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Inject
    AgroalDataSource h2DataSource;

    @PostConstruct
    void config() {
        initdb();
    }

    private void initdb() {
        logger.info("Going to initiate database");

        try (Connection connection = h2DataSource.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(" CREATE TABLE IF NOT EXISTS fruits ( " +
                        "  id serial PRIMARY KEY, " +
                        "  gen_uuid varchar(100) , " +
                        "  name varchar(100) , " +
                        "  description varchar(450),  " +
                        " sent datetime, " +
                        " received datetime " +
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


    public int deleteAllMessages() {
        int deletedNoOfRecords = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "DELETE FROM FRUITS";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                deletedNoOfRecords = stmt.executeUpdate();
                logger.info("All messages deleted in h2 database ={}",deletedNoOfRecords);
            } catch (SQLException e) {
                logger.error("Error processing deleteAllMessages statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing deleteAllMessages connection", e);
            throw new RuntimeException(e);
        }
        return deletedNoOfRecords;
    }


    public void insertMessage(Message message) {

        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = " INSERT INTO PUBLIC.FRUITS (GEN_UUID, NAME, DESCRIPTION, SENT, RECEIVED) VALUES(?, ?, ?, ?, ?) ";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                stmt.setString(1,message.getUuid());
                stmt.setString(2, message.getName());
                stmt.setString(3, message.getDescription());
                stmt.setTimestamp(4, message.getSent());
                stmt.setTimestamp(5, message.getReceived());
                int result = stmt.executeUpdate();
                logger.info("Message is inserted to h2={}",result);
            } catch (SQLException e) {
                logger.error("Error processing insertMessage statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing insertMessage connection", e);
            throw new RuntimeException(e);
        }
    }

    public int updateMessage(Message message) {
        int result = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "UPDATE FRUITS SET RECEIVED=? WHERE GEN_UUID=? AND RECEIVED IS NULL ";
            try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                stmt.setTimestamp(1,message.getReceived());
                stmt.setString(2, message.getUuid());
                result = stmt.executeUpdate();
                logger.info("Message is updated in h2 database ={}",result);
            } catch (SQLException e) {
                logger.error("Error processing updateMessage statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing updateMessage connection", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public void printTopHunMessages() {


        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT f.*, (f.received-f.sent) as diff FROM FRUITS f limit 100";
            try (Statement stmt = connection.createStatement()) {

                ResultSet resultSet = stmt.executeQuery(SQL);
                while (resultSet.next()) {
                    // Now we can fetch the data by column name, save and use them!
                    String id = resultSet.getString("id");
                    String uuid = resultSet.getString("gen_uuid");
                    String name = resultSet.getString("name");
                    Timestamp received = resultSet.getTimestamp("received");
                    Timestamp sent = resultSet.getTimestamp("sent");
                    long diff = received.getTime() - sent.getTime();

                    System.out.println("\tid: " + id +
                            ", uuid: " + uuid +
                            ", name: " + name +
                            ", received: " + received +
                            ", sent: " + sent+
                            ", diff: " + diff);
                }

            } catch (SQLException e) {
                logger.error("Error processing updateMessage statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing updateMessage connection", e);
            throw new RuntimeException(e);
        }
    }

    public long getNumberOfMessages(Timestamp received) {
        long rowCount = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT count(*) as row_count FROM FRUITS where received <= ?";
            try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {
                pstmt.setTimestamp(1, received);
                ResultSet resultSet = pstmt.executeQuery();
                while (resultSet.next()) {
                    // Now we can fetch the data by column name, save and use them!
                    rowCount = resultSet.getLong("row_count");
                }
            } catch (SQLException e) {
                logger.error("Error processing updateMessage statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing updateMessage connection", e);
            throw new RuntimeException(e);
        }
        return rowCount;
    }

    public int getMessagesCount() {
        int rowCount = -1;
        try (Connection connection = h2DataSource.getConnection()) {
            final String SQL = "SELECT count(*) as row_count FROM FRUITS ";
            try (Statement stmt = connection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(SQL);
                while (resultSet.next()) {
                    // Now we can fetch the data by column name, save and use them!
                    rowCount = resultSet.getInt("row_count");
            }
            } catch (SQLException e) {
                logger.error("Error processing updateMessage statement", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            logger.error("Error processing updateMessage connection", e);
            throw new RuntimeException(e);
        }

        return rowCount;
    }

}
