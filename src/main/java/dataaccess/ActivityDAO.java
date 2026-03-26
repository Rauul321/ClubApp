package dataaccess;

import Server.Competition;
import Server.Course;
import utilities.DateParserUtils;

import Server.Activity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object for Activity entities.
 */
public class ActivityDAO {

    // SQL statements for operations
    private static final String INSERT_ACTIVITY_SQL =
            "INSERT INTO ACTIVITIES (name, date, type) VALUES (?, ?, ?);";

    private static final String DELETE_ACTIVITY_SQL =
            "DELETE FROM ACTIVITIES WHERE id = ?;";

    private static final String UPDATE_ACTIVITY_SQL =
            "UPDATE ACTIVITIES SET name = ?, date = ?, type = ? WHERE id = ?;";

    private static final String FIND_ALL_ACTIVITIES_SQL =
            "SELECT * FROM ACTIVITIES;";


    /**
     * Creates a new activity in the database.
     * @param activity The activity to be saved.
     * @return True if the activity was saved successfully, false otherwise.
     */
    public boolean save(Activity activity) {
        boolean success = false;

        // try-with-resources for automatic resource management
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ACTIVITY_SQL)) {

            stmt.setString(1, activity.getName());

            // Format date as a String "day/month/year"
            String dateStr = activity.getDate().getDay() + "/" +
                    activity.getDate().getMonth() + "/" +
                    activity.getDate().getYear();

            stmt.setString(2, dateStr);

            stmt.setString(3, activity.getType());

            int rowsAffected = stmt.executeUpdate();

            success = (rowsAffected > 0);

        } catch (SQLException e) {
            System.err.println("❌ Error en ActivityDAO.save: " + e.getMessage());
        }

        return success;
    }

    /**
     * Deletes an activity from the database by its ID.
     * @param activityId The ID of the activity to be deleted.
     * @return True if the activity was deleted successfully, false otherwise.
     */
    public boolean delete(int activityId) {
        boolean success = false;
        try(Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_ACTIVITY_SQL)) {
            stmt.setInt(1, activityId);
            stmt.executeUpdate();
            success = true;
        } catch (SQLException e) {
            System.err.println("❌ Error en ActivityDAO.delete: " + e.getMessage());
        }
        return success;
    }

    /**
     * Updates an existing activity in the database.
     * @param activityId The ID of the activity to be updated.
     * @param name The name (or new name) of the activity to be updated
     * @param date The date (or new date) of the activity to be updated
     * @param type The type (or new type) of the activity to be updated
     * @return True if the activity was updated successfully, false otherwise.
     */
    public boolean update(int activityId, String name, String date, String type) {
        try(Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_ACTIVITY_SQL)) {

            stmt.setString(1, name);
            stmt.setString(2, date);
            stmt.setString(3, type);
            stmt.setInt(4, activityId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la actividad: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all activities from the database.
     * @return A list of all activities.
     */
    public List<Activity> findAll() {
        List<Activity> activities = new java.util.ArrayList<>();
        // Usamos try-with-resources para asegurar que todo se cierra
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_ACTIVITIES_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Activity activity = mapResultSetToActivity(rs);
                if (activity != null) {
                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in findAll activities: " + e.getMessage());
            // No lances RuntimeException aquí, mejor devuelve la lista vacía para no romper el servidor
        } catch (Exception e) {
            System.err.println("General error mapping activities: " + e.getMessage());
        }
        return activities;
    }

    /**
     * Maps a ResultSet row to an Activity object.
     * @param rs The ResultSet positioned at the current row.
     * @return The mapped Activity object.
     * @throws SQLException If a database access error occurs.
     */
    public Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);     // id
        String name = rs.getString(2); // name
        String dateStr = rs.getString(3); // date
        String type = rs.getString(4); // type
        if(type.equals("Course")) {
            return new Course(id, name, DateParserUtils.parseString(dateStr));
        }
        else{
            return new Competition(id, name, DateParserUtils.parseString(dateStr));
        }
    }

}
