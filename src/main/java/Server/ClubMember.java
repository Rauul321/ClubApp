package Server;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract class representing a member of the club.
 */
public abstract class ClubMember implements Serializable {
    // Serial version UID for serialization
    private static final long serialVersionUID = 1L;

    // Member attributes
    private int id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String role;

    /**
     * Constructor for ClubMember.
     * @param id Unique identifier for the member.
     * @param name First name of the member.
     * @param surname Last name of the member.
     * @param email Email address of the member.
     * @param passwordHash Hashed password for authentication.
     * @param role Role of the member (e.g., "ADMIN", "PARTNER").
     */
    public ClubMember(int id, String name, String surname, String email, String passwordHash, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = passwordHash;
        this.role = role;
    }

    /**
     * Get the member's first name.
     * @return First name of the member.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the member's last name.
     * @return Last name of the member.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Get the member's full name.
     * @return Full name of the member.
     */
    public String getFullName() { return name + " " + surname; }

    /**
     * Get the member's email address.
     * @return Email address of the member.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the member's role.
     * @return Role of the member.
     */
    public String getRole() {
        return role;
    }

    /**
     * Get the member's password hash.
     * @return Password hash of the member.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the member's password hash.
     * @param password New password hash for the member.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the member's unique identifier.
     * @return Unique identifier of the member.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the member's unique identifier.
     * @param id New unique identifier for the member.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Enroll the member in an activity.
     * @param activityId ID of the activity to enroll in.
     * @param club The club instance where the activity exists.
     * @return true if enrollment was successful, false otherwise.
     */
    public boolean enrollInActivity(int activityId, Club club) {
        return club.enroll(activityId, this.getId());
    }

    /**
     * Unroll the member from an activity.
     * @param activityId ID of the activity to unroll from.
     * @param club The club instance where the activity exists.
     * @return true if unrollment was successful, false otherwise.
     */
    public boolean unrollFromActivity(int activityId, Club club) {
        return club.unroll(activityId, this.getEmail());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMember that = (ClubMember) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
