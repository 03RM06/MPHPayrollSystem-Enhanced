package DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager for payrollsystem_db.
 * Wraps JDBC connection lifecycle; reconnects if the link is dropped.
 * Credentials are loaded from config.properties on the classpath.
 */
public class Database {

    private static final Properties CONFIG = loadConfig();

    private static final String DB_URL = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
        CONFIG.getProperty("db.host"),
        CONFIG.getProperty("db.port"),
        CONFIG.getProperty("db.name")
    );

    private static Database INSTANCE;
    private Connection connection;

    private Database() {}

    private static Properties loadConfig() {
        Properties p = new Properties();
        try (InputStream in = Database.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                    "config.properties not found on classpath. " +
                    "Copy config.properties.example to config.properties and fill in your values.");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
        return p;
    }

    public static synchronized Database getInstance() {
        if (INSTANCE == null) INSTANCE = new Database();
        return INSTANCE;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "MySQL JDBC driver not found. " +
                    "Add mysql-connector-j to pom.xml or project libraries.", e);
            }
            connection = DriverManager.getConnection(
                DB_URL,
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.pass")
            );
        }
        return connection;
    }

    public synchronized void close() {
        if (connection != null) {
            try { connection.close(); }
            catch (SQLException e) { e.printStackTrace(); }
            finally { connection = null; }
        }
    }
}
