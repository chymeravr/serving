package com.chymeravr.serving.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by rubbal on 10/2/17.
 * Class to create PSQL connections.
 */
public class PsqlUnpooledConnectionFactory implements ConnectionFactory {
    private final String jdbcResourceUrl;
    private final String userName;
    private final String password;

    public PsqlUnpooledConnectionFactory(String hostname,
                                         int port,
                                         String databaseName,
                                         String userName,
                                         String password) throws ClassNotFoundException, SQLException {
        this.jdbcResourceUrl = String.format("jdbc:postgresql://%s:%d/%s", hostname, port, databaseName);
        this.userName = userName;
        this.password = password;
        // Verify the class is present and DB connection can be established
        Class.forName("org.postgresql.Driver");
        DriverManager.getConnection(this.jdbcResourceUrl, userName, password);
    }

    /**
     * @return A sql connection. The connection must be closed by the client once the processing is finished
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.jdbcResourceUrl, this.userName, this.password);
    }
}
