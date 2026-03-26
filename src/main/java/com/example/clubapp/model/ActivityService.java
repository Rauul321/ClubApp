package com.example.clubapp.model;

import Server.Activity;
import Server.ClubMember;
import com.example.clubapp.communication.ClubServiceClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ActivityService provides methods to manage activities in the club.
 */
public class ActivityService {
    // Dependency on the communication client
    private final ClubServiceClient clubService;

    /**
     * Constructor with dependency injection.
     * @param clubService
     */
    public ActivityService(ClubServiceClient clubService) {
        this.clubService = clubService;
    }

    /**
     * Creates a new activity.
     * @param activityName
     * @param activityDate
     * @param activityType
     * @return
     */
    public boolean createActivity(String activityName, String activityDate, String activityType) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("ADD_ACTIVITY|" + activityName + "|" + activityDate + "|" + activityType);
            if (response.equals("ACTIVITY_ADDED")) {
                System.out.println("Activity created: " + activityName);
                status = true;
            } else {
                System.out.println("Failed to create activity: " + activityName);
            }
        } catch (Exception e) {
            System.err.println("Error creating activity: " + e.getMessage());
        }
        return status;
    }

    /**
     * Retrieves all activities.
     * @return
     */
    public List<Activity> getAllActivities() {
        try {
            Object response = clubService.getObjects("GET_ACTIVITIES");

            if (response == null) {
                System.err.println("Error: Server returned a null response for activities.");
                return Collections.emptyList();
            }
            if (response instanceof List<?>) {
                List<?> activitiesList = (List<?>) response;
                if (!activitiesList.isEmpty() && !(activitiesList.get(0) instanceof Activity)) {
                    System.err.println("Error: Received list contains unexpected data types.");
                    return Collections.emptyList();
                }
                return (List<Activity>) activitiesList;
            }
            System.err.println("Error: Unexpected object type received: " + response.getClass().getName());
        } catch (Exception e) {
            System.err.println("Exception while fetching activities: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Modifies an existing activity.
     * @param activityId
     * @param newName
     * @param newDate
     * @param newType
     * @return
     */
    public boolean modifyActivity(int activityId, String newName, String newDate, String newType) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("MODIFY_ACTIVITY|" + activityId + "|" + newName + "|" + newDate + "|" + newType);
            if (response.equals("ACTIVITY_MODIFIED")) {
                System.out.println("Activity modified: " + activityId);
                status = true;
            } else {

                System.out.println("Failed to modify activity: " + activityId + " Response: " + response);
            }
        } catch (Exception e) {
            System.err.println("Error modifying activity: " + e.getMessage());
        }
        return status;
    }

    /**
     * Deletes an activity.
     * @param activityId
     * @return
     */
    public boolean deleteActivity(int activityId) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("DELETE_ACTIVITY|" + activityId);
            if (response.equals("ACTIVITY_DELETED")) {
                System.out.println("Activity deleted: " + activityId);
                status = true;
            } else {
                System.out.println("Failed to delete activity: " + activityId);
            }
        } catch (Exception e) {
            System.err.println("Error deleting activity: " + e.getMessage());
        }
        return status;
    }

    /**
     * Enrolls the current user in an activity.
     * @param activityId
     * @return
     */
    public boolean enrollInActivity(int activityId) {
        try {
            Object response = clubService.sendCommand("JOIN_ACTIVITY|" + activityId);

            if (response == null) {
                System.err.println("Server returned null");
                return false;
            }

            if ("JOINED_ACTIVITY".equals(response)) {
                return true;
            } else {
                System.out.println("The server denied the enrollment" + response);
            }

        } catch (Exception e) {
            System.err.println("Communication error");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Unrolls the current user from an activity.
     * @param activityId
     * @return
     */
    public boolean unrollActivity(int activityId) {
        boolean status = false;
        try {
            String response = (String)clubService.sendCommand("UNROLL_ME|" + activityId);
            if (response.equals("UNROLLED_FROM_ACTIVITY")) {
                System.out.println("You were unrolled from activity: " + activityId);
                status = true;
            } else {
                System.out.println("Failed to unroll you from activity: " + activityId);
            }
        } catch (Exception e) {
            System.err.println("Error unrolling you from activity: " + e.getMessage());
        }
        return status;
    }

    /**
     * Sends the command to check if the current user is enrolled in an activity.
     * @param activityId
     * @return
     */
    public boolean isEnrolledInActivity(int activityId) {
        try {
            Object response = clubService.sendCommand("IS_ENROLLED_IN_ACTIVITY|" + activityId);

            if (response instanceof Boolean) {
                return (Boolean) response;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error checking enrolment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sends the command if the current user is the organizer of an activity.
     * @param activityId
     * @return
     */
    public List<ClubMember> getParticipantsOfActivity(int activityId) {
        List<ClubMember> participants = new ArrayList<>();
        try {
            participants = (List<ClubMember>) clubService.getObjects("GET_PARTICIPANTS|" + activityId);
        } catch (Exception e) {
            System.err.println("Error fetching participants: " + e.getMessage());
        }
        return participants;
    }
}
