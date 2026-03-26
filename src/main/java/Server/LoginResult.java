package Server;
 // O el paquete donde la necesites

import java.io.Serializable;

/**
 * Class to represent the result of a login attempt.
 */
public class LoginResult implements Serializable {

    /**
     * Enum to represent the status of the login attempt.
     */
    public enum Status {
        SUCCESS,
        FAIL_CREDENTIALS,
    }

    private final Status status;
    private final ClubMember member;

    /**
     * Constructor for LoginResult.
     * @param status The status of the login attempt.
     * @param member The ClubMember object if login was successful, null otherwise.
     */
    public LoginResult(Status status, ClubMember member) {
        this.status = status;
        this.member = member;
    }

    /**
     * Get the status of the login attempt.
     * @return Status of the login attempt.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Get the ClubMember object if login was successful.
     * @return ClubMember object or null if login failed.
     */
    public ClubMember getMember() {
        return member;
    }


    /**
     * Check if the login was successful.
     * @return true if login was successful, false otherwise.
     */
    public boolean isSuccessful() {
        return this.status == Status.SUCCESS;
    }


}
