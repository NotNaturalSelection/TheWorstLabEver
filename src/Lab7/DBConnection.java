package Lab7;

import java.sql.*;

public class DBConnection {

    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5433/postgres";

    private static final String USER = "postgres";

    private static final String PASS = "ScAk9aFpI1";

    private Connection connection;

    public DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {}
        connection = null;
        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);
        } catch (SQLException ignored) {}
    }

    public Connection getConnection() {
        return connection;
    }
}