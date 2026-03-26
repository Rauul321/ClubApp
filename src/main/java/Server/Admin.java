package Server;

import java.io.Serial;
import java.io.Serializable;

/**
 * Admin class representing an administrator in the club system.
 */
public class Admin extends ClubMember implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    /**
     * Constructor for Admin with specified ID.
     * @param id Unique identifier for the admin.
     * @param name First name of the admin.
     * @param surname Last name of the admin.
     * @param email Email address of the admin.
     * @param passwordHash Hashed password for authentication.
     */
    public Admin(int id, String name, String surname, String email, String passwordHash) {
        super(id, name, surname, email, passwordHash, "ADMIN");
    }

    /**
     * Constructor for Admin without specified ID (ID will be assigned later).
     * @param name First name of the admin.
     * @param surname Last name of the admin.
     * @param email Email address of the admin.
     * @param passwordHash Hashed password for authentication.
     */
    public Admin(String name, String surname, String email, String passwordHash) {
        super(-1, name, surname, email, passwordHash, "ADMIN");
    }

    /**
     * Adds a new member to the club.
     * @param name First name of the member.
     * @param surname Last name of the member.
     * @param email Email address of the member.
     * @param passwordHash Hashed password for the member.
     * @param role Role of the member (e.g., "PARTNER").
     * @param club The club instance where the member will be added.
     * @return true if the member was added successfully, false otherwise.
     */
    public boolean addMember(String name, String surname, String email, String passwordHash, String role, Club club) {
        return club.registerMember(name, surname, email, passwordHash, role);
    }

    /**
     * Deletes a member from the club.
     * @param email Email address of the member to be deleted.
     * @param club The club instance from which the member will be removed.
     * @return true if the member was deleted successfully, false otherwise.
     */
    public boolean deleteMember(String email, Club club) {
        return club.removeMember(email);
    }

    /**
     * Modifies the details of an existing member.
     * @param email Email address of the member to be modified.
     * @param newName New first name for the member.
     * @param newSurname New last name for the member.
     * @param newPassword New hashed password for the member.
     * @param club The club instance where the member exists.
     * @return true if the member was modified successfully, false otherwise.
     */
    public boolean modifyMember(String newName, String newSurname, String newPassword, String email, Club club) {
        return club.modifyMember(newName, newSurname, newPassword, email);
    }

    /**
     * Adds a new activity to the club.
     * @param name Name of the activity.
     * @param type Type of the activity (e.g., "Course", "Competition").
     * @param date Date of the activity.
     * @param club The club instance where the activity will be added.
     * @return true if the activity was added successfully, false otherwise.
     */
    public boolean addActivity(String name, Date date, String type, Club club) {
        return club.createActivity(name, date, type);
    }

    /**
     * Deletes an activity from the club.
     * @param activityId Unique identifier of the activity to be deleted.
     * @param club The club instance from which the activity will be removed.
     * @return true if the activity was deleted successfully, false otherwise.
     */
    public boolean deleteActivity(int activityId, Club club) {
        return club.removeActivity(activityId);
    }

    /**
     * Modifies the details of an existing activity.
     * @param activityId Unique identifier of the activity to be modified.
     * @param newName New name for the activity.
     * @param newType New type for the activity.
     * @param newDate New date for the activity.
     * @param club The club instance where the activity exists.
     * @return true if the activity was modified successfully, false otherwise.
     */
    public boolean modifyActivity(int activityId, String newName, Date newDate, String newType, Club club) {
        return club.modifyActivity(activityId, newName, newDate, newType);
    }

    /**
     * Returns a string representation of the Admin.
     * @return String representation of the Admin.
     */
    @Override
    public String toString() {
        return "ADMIN: " + getFullName() + " (" + getEmail() + ")";
    }
}

