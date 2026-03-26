package com.example.clubapp.session;
import Server.ClubMember;

/**
 * Singleton class to manage user session.
 */
public class SessionManager {
    // Singleton instance
    private static SessionManager instance;

    // Logged-in user
    private ClubMember loggedUser;

    /**
     * Private constructor to prevent instantiation.
     */
    private SessionManager() {}

    /**
     * Get the singleton instance of SessionManager.
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Logs in a user by setting the loggedUser.
     * @param user
     */
    public void login(ClubMember user) {
        this.loggedUser = user;
    }

    /**
     * Logs out the current user by clearing the loggedUser.
     */
    public void logout() {
        this.loggedUser = null;
    }

    /**
     * Get the currently logged-in user.
     * @return ClubMember representing the logged-in user
     */
    public ClubMember getUser() {
        return loggedUser;
    }
}
