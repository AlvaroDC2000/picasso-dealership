package dealership.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class responsible for providing database connections.
 * <p>
 * This class centralizes the configuration and creation of JDBC connections
 * to the application database. It is used by DAO classes to obtain a
 * {@link Connection} when executing SQL queries.
 * </p>
 */
public class DbConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/concesionario?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is intended to be used only through its static methods.
     * </p>
     */
    private DbConnection() {
    }

    /**
     * Creates and returns a new database connection.
     * <p>
     * Each call returns a fresh {@link Connection} instance using the
     * configured JDBC URL, username and password.
     * </p>
     *
     * @return a new {@link Connection} to the database
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
