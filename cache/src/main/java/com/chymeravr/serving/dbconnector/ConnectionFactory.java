package com.chymeravr.serving.dbconnector;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by rubbal on 10/2/17.
 */
public interface ConnectionFactory {
    Connection getConnection() throws SQLException;
}
