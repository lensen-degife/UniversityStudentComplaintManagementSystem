package app.db;

import java.io.InputStream;
import java.util.Properties;

public class DBConnection {
    private static Properties props = new Properties();

    static {
        try (InputStream is = DBConnection.class.getResourceAsStream("/dbconfig.properties")) {
            if (is == null) {
                throw new RuntimeException("dbconfig.properties file not found!");
            }
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.sql.Connection getConnection() throws Exception {
        Class.forName(props.getProperty("db.driver"));
        return java.sql.DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
        );
    }
}