package com.example.clubapp.controller;

import Server.ClubMember;
import com.example.clubapp.model.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for a participant item in the activity participants list.
 */
public class ParticipantItemController {
    // FXML UI component
    @FXML private Label partnerEmail;

    // Dependency of the domain
    private final MemberService memberService;

    // Internal ID of the activity
    private final int activityId;

    // Participant
    private ClubMember participant;

    // Listener to notify the parent controller to refresh the participants list
    private Runnable onRefreshListener;

    /**
     * Constructor with dependency injection.
     * @param memberService
     * @param activityId
     * @param participant
     */
    public ParticipantItemController(MemberService memberService, int activityId, ClubMember participant) {
        this.memberService = memberService;
        this.activityId = activityId;
        this.participant = participant;
    }

    /**
     * Sets the listener to be called when a refresh is needed.
     * @param listener
     */
    public void setOnRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }

    /**
     * Sets the participant details in the UI.
     * @param name
     */
    public void setParticipantDetails(String name) {
        partnerEmail.setText(name);
    }

    /**
     * Handle unroll participant button action.
     */
    @FXML
    private void handleUnrollParticipant() {
        String email = partnerEmail.getText();

        if (memberService.unrollParticipantFromActivity(participant.getEmail(), activityId)) {
            System.out.println("Participant unrolled: " + email + " from activity with ID: " + activityId);

            // if there is a listener, notify to refresh the participants list
            if (onRefreshListener != null) {
                onRefreshListener.run();
            }
        }
    }
}