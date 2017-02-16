package com.chymeravr.serving.dbconnector;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by rubbal on 10/2/17.
 * Class to create PSQL connections.
 */
@Slf4j
public class PsqlUnpooledConnectionFactory implements ConnectionFactory {
    private final String jdbcResourceUrl;
    private final Properties jdbcProperties;

    public PsqlUnpooledConnectionFactory(String jdbcFile) throws ClassNotFoundException, SQLException {
        PropertiesConfiguration configuration;
        try {
            configuration = new PropertiesConfiguration(jdbcFile);
            configuration.setThrowExceptionOnMissing(true);
        } catch (ConfigurationException e) {
            log.error("Unable to initialise PsqlUnpooledConnectionFactory", e);
            throw new SQLException(jdbcFile);
        }

        this.jdbcResourceUrl = String.format("jdbc:postgresql://%s:%d/%s",
                configuration.getString("hostname"),
                configuration.getInt("port"),
                configuration.getString("databaseName")
        );

        this.jdbcProperties = ConfigurationConverter.getProperties(configuration);
        // Verify the class is present and DB connection can be established
        Class.forName("org.postgresql.Driver");
        DriverManager.getConnection(this.jdbcResourceUrl, this.jdbcProperties);
    }

    @Override
    public Connection getConnection() throws SQLException {
        log.error("Getting connection for URL: {}, with Properties: {}", this.jdbcResourceUrl, this.jdbcProperties);
        return DriverManager.getConnection(this.jdbcResourceUrl, this.jdbcProperties);
    }
}
