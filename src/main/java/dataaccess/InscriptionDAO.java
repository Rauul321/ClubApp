package dataaccess;

import Server.ClubMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for managing inscriptions in the database.
 */
public class InscriptionDAO {

    // SQL Statements
    private static final String INSERT_INSCRIPTION_SQL =
            "INSERT INTO INSCRIPCIONES (miembro_id, actividad_id) VALUES (?, ?);";

    private static final String DELETE_INSCRIPTION_SQL =
            "DELETE FROM INSCRIPCIONES WHERE actividad_id = ? AND miembro_id = ?;";

    private static final String GET_ID_BY_EMAIL_SQL =
            "SELECT id FROM MEMBERS WHERE email = ?;";

    private static final String GET_PARTICIPANTS_BY_ACTIVITY_SQL =
            "SELECT cm.* FROM MIEMBROS cm " +
            "JOIN INSCRIPTIONS ins ON cm.id = ins.miembro_id " +
            "WHERE ins.actividad_id = ?;";

    private static final String CHECK_ENROLLMENT_SQL =
            "SELECT 1 FROM INSCRIPCIONES WHERE actividad_id = ? AND miembro_id = ?;";


    /**
     * Saves a new inscription.
     * @param activityId The id of the activity
     * @param memberId The id of the member to be enrolled
     * @return True if the inscription was saved successfully, false otherwise
     */
    public boolean save(int activityId, int memberId) {
        try(Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_INSCRIPTION_SQL)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, activityId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating inscription failed, no rows affected.");
            }
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Intento de inscripción duplicada omitido.");
                return false;
            }

            // Si es otro error distinto, sí lo imprimimos para debuggear
            System.err.println("Error real en DB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes an inscription.
     * @param activityId The id of the activity
     * @param email The email of the member to be unenrolled
     * @return True if the inscription was deleted successfully, false otherwise
     */
    public boolean delete(int activityId, String email) {
        int memberId = getPartnerId(email);
        // Luego, eliminar la inscripción usando el activityId y partnerId
        try(Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_INSCRIPTION_SQL)) {
            stmt.setInt(1, activityId);
            stmt.setInt(2, memberId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a member is enrolled in an activity.
     * @param activityId The id of the activity
     * @param email The email of the member
     * @return True if the member is enrolled, false otherwise
     */
    public boolean isEnrolled(int activityId, String email) {
        try {
            int partnerId = getPartnerId(email);
            try (Connection conn = DBManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(CHECK_ENROLLMENT_SQL)) {
                stmt.setInt(1, activityId);
                stmt.setInt(2, partnerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in the DAO while checking the enrollment: " + e.getMessage());
            return false;
        }
    }


    /**
     * Gets the partner ID by email.
     * @param email The email of the member
     * @return The partner ID, or -1 if not found
     */
    private static int getPartnerId(String email) {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ID_BY_EMAIL_SQL)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in getPartnerId: " + e.getMessage());
        }
        return -1;
    }

}
