package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class manages the database connection and configuration using SQLite.
 */
public class DBManager {

    // URL of the SQLite database file
    private static String DB_URL = "jdbc:sqlite:club_database.db";

    // Credentials (not used for SQLite but kept for compatibility)
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    // Connection for test mode
    private static Connection testConnection = null;


    public enum DBMode {
        PROD,
        SIMULATION,
        TEST
    }

    public static void setDBMode(DBMode mode) {
        switch (mode) {
            case TEST:
                DB_URL = "jdbc:sqlite:target/test.db";
                break;
            case SIMULATION:
                DB_URL = "jdbc:sqlite:simulation_database.db";
                break;
            case PROD:
            default:
                DB_URL = "jdbc:sqlite:club_database.db";
                break;
        }
    }

    /**
     * Gets a connection to the SQLite database.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {

        if (DB_URL.equals("jdbc:sqlite:target/test.db")) {
            if (testConnection == null || testConnection.isClosed()) {
                testConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                // Activamos foreign keys una sola vez
                try (Statement stmt = testConnection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }
            return testConnection;
        }

        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;"); // Activa el soporte de cascada
        }

        return conn;

    }

    /**
     * Closes the test database connection if in test mode.
     * @throws SQLException
     */
    public static void closeTestConnection() throws SQLException {
        if (DB_URL.equals("jdbc:sqlite:target/test.db")) {
            if( testConnection != null && !testConnection.isClosed()) {
                testConnection.close();
            }
        }
    }

    /**
     * Initializes the database by creating necessary tables if they do not exist.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createMiembrosTable =
                    "CREATE TABLE IF NOT EXISTS MEMBERS (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "email TEXT UNIQUE NOT NULL," +
                            "nombre TEXT NOT NULL," +
                            "apellido TEXT NOT NULL," +
                            "pass_hash TEXT NOT NULL," +
                            "rol TEXT NOT NULL" +
                            ");";

            String createActividadesTable =
                    "CREATE TABLE IF NOT EXISTS ACTIVITIES (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "date TEXT NOT NULL," +
                            "type TEXT NOT NULL" +
                            ");";

            String createInscriptionsTable =
                    "CREATE TABLE IF NOT EXISTS INSCRIPCIONES (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "miembro_id INTEGER NOT NULL," +
                            "actividad_id INTEGER NOT NULL," +
                            "UNIQUE(miembro_id, actividad_id)," +

                            "FOREIGN KEY(miembro_id) REFERENCES MEMBERS(id) ON DELETE CASCADE," +
                            "FOREIGN KEY(actividad_id) REFERENCES ACTIVITIES(id) ON DELETE CASCADE" +
                            ");";

            stmt.execute(createMiembrosTable);
            stmt.execute(createActividadesTable);
            stmt.execute(createInscriptionsTable);
            System.out.println("Table MEMBERS verified/created.");
            System.out.println("Table INSCRIPCIONES verified/created");
            System.out.println("Table ACTIVITIES verified/created");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Resets the database by deleting all records from all tables.
     * Only used for simulation
     */
    public static void resetDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM INSCRIPCIONES");
            stmt.execute("DELETE FROM ACTIVITIES");
            stmt.execute("DELETE FROM MEMBERS");

            stmt.execute("DELETE FROM sqlite_sequence");

        } catch (SQLException e) {
            System.err.println("Error resetting database: " + e.getMessage());
        }
    }



}