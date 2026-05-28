package app.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = DBConnection.class.getResourceAsStream("/dbconfig.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("dbconfig.properties was not found on classpath");
            }
            PROPERTIES.load(inputStream);
            Class.forName(PROPERTIES.getProperty("db.driver"));
        } catch (Exception exception) {
            throw new ExceptionInInitializerError("Failed to initialize database connection: " + exception.getMessage());
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.username"),
                PROPERTIES.getProperty("db.password")
        );
    }
}
