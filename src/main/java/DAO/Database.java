package DAO;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
 * Singleton database connection manager for payrollsystem_db.
 * Wraps JDBC connection lifecycle; reconnects if the link is dropped.
 */
public class Database {
 
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "payrollsystem_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "test123";
 
    private static final String DB_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
 
    private static Database INSTANCE;
    private Connection connection;
 
    private Database() {}
 
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
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
