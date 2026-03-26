package com.example.clubapp.model;

import java.io.IOException;

import Server.ClubMember;
import com.example.clubapp.communication.ClubServiceClient;
import com.example.clubapp.session.SessionManager;

public class AuthenticationService {

    private final ClubServiceClient clubClient;
    private ClubMember currentUser;
    private String currentUserName;
    private String currentUserRole;
    private String lastAttemptedEmail;
    private String lastAttemptedPassword;


    /**
     * Constructor with dependency injection.
     * @param clubClient
     */
    public AuthenticationService(ClubServiceClient clubClient) {
        this.clubClient = clubClient;
    }

    /**
     * Sends the command to log in a user.
     * @param email
     * @param password
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public String login(String email, String password) throws IOException, ClassNotFoundException {
        String command = "LOGIN|" + email + "|" + password;


        if(!clubClient.isConnected()){
            boolean connected = clubClient.connect();
            if(!connected){
                return "LOGIN_ERROR";
            }
        }
        Object response = clubClient.sendCommand(command);

        if (response instanceof ClubMember) {
            ClubMember loggedUser = (ClubMember) response;

            SessionManager.getInstance().login(loggedUser);

            this.currentUserName = loggedUser.getName();
            this.currentUserRole = loggedUser.getRole();

            System.out.println("Login successful for " + currentUserName + " with role " + currentUserRole);
            return "SUCCESS " + currentUserRole;

        } else if (response instanceof String) {
            String msg = (String) response;
            if (msg.equals("FAIL_CREDENTIALS")) {
                return msg;
            }
        }

        return "LOGIN_ERROR";
    }

    /**
     * Sends the command to sign up a new partner.
     * @param name
     * @param surname
     * @param email
     * @param passwd
     */
    public boolean signUp(String name, String surname, String email, String passwd) {
        boolean status = false;
        try {
            String response = (String)clubClient.sendCommand("ADD_MEMBER|" + name + "|" + surname + "|" + email + "|" + passwd + "|" + "PARTNER");

            if (response.equals("MEMBER_ADDED")) {
                System.out.println("Sign-up successful for " + name + " " + surname);
                status = true;
            } else if (response.equals("PARTNER_EXISTS")) {
                System.out.println("Sign-up failed: Partner already exists with email " + email);
            } else {
                System.out.println("Sign-up failed for " + email + ". Server response: " + response);
            }

        } catch(IOException e){
            System.err.println("Sign-up error: Communication error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Sign-up error: Class not found.", e);
        }
        return status;
    }

    // --- Métodos de Ayuda para la Sesión ---

    /**
     * Gets the name of the currently logged-in user.
     * @return
     */
    public String getCurrentUserName() {
        return currentUserName;
    }

    /**
     * Gets the role of the currently logged-in user.
     * @return
     */
    public String getCurrentUserRole() {
        return currentUserRole;
    }

    /**
     * Checks if the current user is an admin.
     * @return
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(currentUserRole);
    }
}
