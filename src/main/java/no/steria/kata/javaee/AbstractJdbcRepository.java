package no.steria.kata.javaee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class AbstractJdbcRepository {

    private final DataSource dataSource;
    private Connection connection;

    public AbstractJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected ResultSet executeQuery(String query) throws SQLException {
        return connection.createStatement().executeQuery(query);
    }

    protected PreparedStatement prepareStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public Transaction startTransaction() {
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            return new Transaction() {
                private boolean commit = false;

                @Override
                public void close() {
                    try {
                        if (commit) {
                            connection.commit();
                        } else {
                            connection.rollback();
                        }
                        connection.setAutoCommit(true);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void setCommit() {
                    commit = true;
                }
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}