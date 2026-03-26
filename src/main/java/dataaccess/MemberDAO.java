package dataaccess;

import Server.ClubMember;
import Server.Admin;
import Server.LoginResult;
import Server.Partner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;


/**
 * Data Access Object to manage ClubMember entities in the database.
 */
public class MemberDAO {

    // SQL Statements
    private static final String INSERT_MIEMBRO_SQL =
            "INSERT INTO MEMBERS (email, nombre, apellido, pass_hash, rol) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String LOGIN_SQL =
            "SELECT * FROM MEMBERS WHERE email = ? AND pass_hash = ?";

    private static final String UPDATE_MIEMBRO_SQL =
            "UPDATE MEMBERS SET nombre = ?, apellido = ?, pass_hash = ?" +
            "WHERE email = ?";

    private static final String DELETE_MIEMBRO_SQL =
            "DELETE FROM MEMBERS WHERE email = ?";

    private static final String GET_MEMBER_BY_EMAIL_SQL =
            "SELECT * FROM MEMBERS WHERE email = ?";

    private static final String FIND_ALL_MEMBERS_SQL =
            "SELECT * FROM MEMBERS";

    private static final String GET_PARTICIPANTS_BY_ACTIVITY_SQL =
            "SELECT cm.* FROM MEMBERS cm " +
            "JOIN INSCRIPCIONES ins ON cm.id = ins.miembro_id " +
            "WHERE ins.actividad_id = ?;";

    /**
     * Empty constructor.
     */
    public MemberDAO() {}


    /**
     * Saves a ClubMember (Admin or Partner) to the database.
     * @param member the ClubMember to save
     * @return true if saved successfully, false otherwise
     */
    public boolean save(ClubMember member) {
        boolean success = false;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_MIEMBRO_SQL)) {

            stmt.setString(1, member.getEmail());
            stmt.setString(2, member.getName());

            if (member instanceof Admin) {
                Admin admin = (Admin) member;
                stmt.setString(3, admin.getSurname());
                stmt.setString(4, admin.getPassword());
                stmt.setString(5, "ADMIN");

            } else if (member instanceof Partner) {
                Partner partner = (Partner) member;
                stmt.setString(3, partner.getSurname());
                stmt.setString(4, partner.getPassword());
                stmt.setString(5, "PARTNER");

            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (Statement idStmt = conn.createStatement();
                     ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        member.setId(generatedId);
                    }
                    success = true;
                } catch (SQLException e) {
                    System.err.println("Error obtaining the generated ID: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving the member: " + e.getMessage());
        }
        return success;
    }

    /**
     * Handles the login process for a member.
     * @param email The email of the member trying to log in
     * @param password The password of the member trying to log in
     * @return A LoginResult indicating success or failure and the member object if successful
     */
    public LoginResult login(String email, String password) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    LOGIN_SQL
            );
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ClubMember member = mapResultSetToMember(rs);
                if(member != null){
                    if(member instanceof Admin) {
                        LoginResult loginResult = new LoginResult(LoginResult.Status.SUCCESS, (Admin)member);
                        return loginResult;
                    } else {
                        LoginResult loginResult = new LoginResult(LoginResult.Status.SUCCESS, (Partner)member);
                        return loginResult;
                    }
                }
            } else {
                System.err.println("No member was found with those credentials.");
                return new LoginResult(LoginResult.Status.FAIL_CREDENTIALS, null);
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return null;
    }

    /**
     * Modifies an existing member's details.
     * @param newName The new name of the member
     * @param newSurname The new surname of the member
     * @param newPassword The new password of the member
     * @param email The email of the member to be updated
     * @return true if updated successfully, false otherwise
     */
    public boolean updateMember(String newName, String newSurname, String newPassword, String email) {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_MIEMBRO_SQL)) {

            stmt.setString(1, newName);
            stmt.setString(2, newSurname);
            stmt.setString(3, newPassword);
            stmt.setString(4, email);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a member from the database.
     * @param email The email of the member to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteMember(String email) {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_MIEMBRO_SQL)) {

            stmt.setString(1, email);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting the member: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a member by their email.
     * @param email The email of the member to retrieve
     * @return The ClubMember object if found, null otherwise
     */
    public ClubMember getMemberByEmail(String email) {
        try(Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_MEMBER_BY_EMAIL_SQL)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                System.out.println("Miembro encontrado con email: " + email);
                return mapResultSetToMember(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el miembro por email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Maps a ResultSet row to a ClubMember object (Admin or Partner).
     * @param rs The ResultSet to map
     * @return The corresponding ClubMember object
     * @throws SQLException If an SQL error occurs
     */
    private ClubMember mapResultSetToMember(ResultSet rs) throws SQLException {
        String role = rs.getString("rol");
        if ("ADMIN".equals(role)) {
            return new Admin(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("pass_hash")
            );
        } else if ("PARTNER".equals(role)) {
            return new Partner(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("pass_hash")
            );
        }

        return null;
    }


    /**
     * Retrieves all members from the database.
     * @return A list of all ClubMember objects
     */
    public List<ClubMember> findAll() {
        try(Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_ALL_MEMBERS_SQL)) {
            List<ClubMember> members = new java.util.ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                ClubMember member = mapResultSetToMember(rs);
                members.add(member);
            }
            return members;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves participants of a specific activity.
     * @param activityId The ID of the activity
     * @return A list of ClubMember objects who are participants of the activity
     */
    public List<ClubMember> getParticipantsByActivity(int activityId) {
        try(Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_PARTICIPANTS_BY_ACTIVITY_SQL)) {
            List<ClubMember> members = new java.util.ArrayList<>();
            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                ClubMember member = mapResultSetToMember(rs);
                members.add(member);
            }
            return members;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
