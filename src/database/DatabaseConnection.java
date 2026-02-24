package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String url = "jdbc:postgresql://localhost:5432/mydatabase";
    private static final String user = "postgres";
    private static final String password = "testingpassword";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
