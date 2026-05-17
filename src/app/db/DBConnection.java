package app.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static Properties props = new Properties();

    static {
        try (InputStream is = DBConnection.class.getResourceAsStream("/resources/dbconfig.properties")) {
            if (is == null) {
                throw new RuntimeException("❌ dbconfig.properties file not found in resources folder!");
            }
            props.load(is);
            System.out.println("✅ Database config loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String pass = props.getProperty("db.password");

        if (url == null || user == null || pass == null) {
            throw new RuntimeException("❌ Database configuration is missing!");
        }

        Class.forName(props.getProperty("db.driver"));
        return DriverManager.getConnection(url, user, pass);
    }
}