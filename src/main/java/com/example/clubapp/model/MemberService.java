package com.example.clubapp.model;

import Server.ClubMember;
import Server.Partner;
import com.example.clubapp.communication.ClubServiceClient;

import java.util.ArrayList;
import java.util.List;

/**
 * MemberService provides methods to manage club members.
 */
public class MemberService {
    // Dependency on the communication client
    private final ClubServiceClient clubService;

    /**
     * Constructor with dependency injection.
     * @param clubService
     */
    public MemberService(ClubServiceClient clubService) {
        this.clubService = clubService;
    }


    /**
     * Retrieves all club members.
     * @return the list of members
     */
    public List<ClubMember> getAllMembers() {
        List<ClubMember> members = new ArrayList<>(); // Inicializamos una lista vacía y mutable
        try {
            Object response = clubService.sendCommand("GET_MEMBERS");
            if (response instanceof List<?>) {
                members = (List<ClubMember>) response;
            } else {
                System.err.println("Error: El servidor no envió una lista de miembros.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching members: " + e.getMessage());
        }
        return members;
    }

    /**
     * Retrieves a club member by email.
     * @param email the email of the member
     * @return the member
     */
    public ClubMember getMemberByEmail(String email) {
        ClubMember member = null;
        try {
            Object response = clubService.sendCommand("GET_MEMBER_BY_EMAIL|" + email);
            if (response instanceof ClubMember) {
                member = (ClubMember) response;
            } else {
                System.err.println("Error: The server did not return a ClubMember object.");
            }
        } catch (Exception e) {
            System.err.println("Error fetching member by email: " + e.getMessage());
        }
        return member;
    }

    /**
     * Retrieves all partners.
     * @return the list of partners
     */
    public List<Partner> getAllPartners() {
        List<Partner> partners = new ArrayList<>(); // Inicializamos una lista vacía y mutable
        try {
            partners = clubService.getPartners();

        } catch (Exception e) {
            System.err.println("Error fetching partners: " + e.getMessage());
        }
        return partners;
    }

    /**
     * Sends a commando to the server to create a new member.
     * @param name the name of the member
     * @param surname the surname of the member
     * @param email the email of the member
     * @param password the password of the member
     * @param role the role of the member
     * @return
     */
    public boolean createMember(String name, String surname, String email, String password, String role) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("ADD_MEMBER|" + name + "|" + surname + "|" + email + "|" + password + "|" + role);
            if (response.equals("MEMBER_ADDED")) {
                System.out.println("Partner created: " + name + " " + surname);
                status = true;
            } else {
                System.out.println("Failed to create partner: " + name + " " + surname);
            }
        } catch (Exception e) {
            System.err.println("Error creating partner: " + e.getMessage());
        }
        return status;
    }

    /**
     * Sends a command to the server to delete a partner
     * @param email the email of the partner to be deleted
     * @return True if success else False
     */
    public boolean deletePartner(String email) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("DELETE_PARTNER|" + email);
            if (response.equals("PARTNER_DELETED")) {
                System.out.println("Partner deleted: " + email);
                status = true;
            } else {
                System.out.println("Failed to delete partner: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error deleting partner: " + e.getMessage());
        }
        return status;
    }

    /**
     * Sends a command to the server to modify a member
     * @param email the new email of the member
     * @param newName the new name of the member
     * @param newSurname the new surname of the member
     * @param newPassword the new password of the member
     * @return True if success else False
     */
    public boolean modifyMember(String email, String newName, String newSurname, String newPassword) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("MODIFY_MEMBER|" + newName + "|" + newSurname + "|" + newPassword + "|" + email);
            if (response.equals("MEMBER_MODIFIED")) {
                System.out.println("Member modified: " + email);
                status = true;
            } else {
                System.out.println("Failed to modify member: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error modifying member: " + e.getMessage());
        }
        return status;
    }

    /**
     * Unrolls a member from an activity.
     * @param email the email of the member
     * @param activityId the id of the activity
     * @return True if success else False
     */
    public boolean unrollFromActivity(String email, int activityId) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("UNROLL_ME|" + activityId + "|" + email);
            if (response.equals("UNROLLED_FROM_ACTIVITY")) {
                System.out.println("Member unrolled from activity: " + email + " from activity " + activityId);
                status = true;
            } else {
                System.out.println("Failed to unroll member from activity: " + email + " from activity " + activityId);
            }
        } catch (Exception e) {
            System.err.println("Error unrolling member from activity: " + e.getMessage());
        }
        return status;
    }

    /**
     * Unrolls a participant from an activity.
     * @param email the email of the participant
     * @param activityId the id of the activity
     * @return True if success else False
     */
    public boolean unrollParticipantFromActivity(String email, int activityId) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("UNROLL_PARTICIPANT|" + activityId + "|" + email);
            if (response.equals("PARTICIPANT_UNROLLED")) {
                status = true;
            } else {
                System.out.println("Failed to unroll participant from activity: " + email + " from activity " + activityId);
            }
        } catch (Exception e) {
            System.err.println("Error unrolling participant from activity: " + e.getMessage());
        }
        return status;
    }

    /**
     * Changes the password of the current user.
     * @param newPassword the new password
     * @return True if success else False
     */
    public boolean changePassword(String newPassword) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("CHANGE_PASSWORD|" + newPassword);
            if (response.equals("PASSWORD_CHANGED")) {
                System.out.println("Password changed for your account.");
                status = true;
            } else {
                System.out.println("Failed to change password for your account." + " Server response: " + response);
            }
        } catch (Exception e) {
            System.err.println("Error changing password for your account: " + e.getMessage());
        }
        return status;
    }

}
