package no.steria.kata.javaee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class DataSources {

    public static DataSource inMemoryDataSource(String databaseName) {
        JDBCDataSource jdbcDataSource = new JDBCDataSource();
        jdbcDataSource.setUrl("jdbc:hsqldb:mem:" + databaseName);
        jdbcDataSource.setUser("sa");
        jdbcDataSource.setPassword("");
        return jdbcDataSource;
    }

    public static void initDatabaseSchema(DataSource dataSource) throws SQLException, IOException {
        String ddlStrings = Resources.toString(DataSources.class.getResource("/person.ddl"), Charsets.UTF_8);
        try (Connection connection = dataSource.getConnection()) {
            for (String sql : ddlStrings.split("/")) {
                connection.createStatement().executeUpdate(sql);
            }
        }
    }

}
