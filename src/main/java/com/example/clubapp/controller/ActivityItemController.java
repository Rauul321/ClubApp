package com.example.clubapp.controller;

import com.example.clubapp.model.ActivityService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

/**
 * ActivityItemController manages the UI and interactions for a single activity item.
 */
public class ActivityItemController {
    // FXML UI components
    @FXML private Label activityName;
    @FXML private Label date;
    @FXML private Label typeLabel;
    @FXML private Button joinButton;
    @FXML private Button unjoinButton;

    // Internal ID (The one stored on database)
    private int internalId;

    // Dependency of the domain
    private final ActivityService actService;

    // Listeners
    private Runnable onRefreshListener; // For refreshing the list on deletion or modification
    private Runnable onModifyListener; // For notifying the parent to modify this activity
    private Consumer<Integer> onShowParticipantsListener; // For showing the participants list of this activity

    /**
     * Constructor with dependency injection.
     * @param actService
     */
    public ActivityItemController(ActivityService actService) {
        this.actService = actService;
    }

    /**
     * Sets the listener to be called when a refresh is needed.
     * @param listener
     */
    public void setOnRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }

    /**
     * Sets the listener to be called when modification is requested.
     * @param listener
     */
    public void setOnModifyListener(Runnable listener) {
        this.onModifyListener = listener;
    }

    /**
     * Sets the listener to be called when showing participants is requested.
     * @param listener
     */
    public void setOnShowParticipantsListener(Consumer<Integer> listener) {
        this.onShowParticipantsListener = listener;
    }

    /**
     * Sets the activity details in the UI.
     * @param id
     * @param name
     * @param d
     * @param type
     * @param numeroFila
     */
    @FXML public void setActivityDetails(int id, String name, String d, String type, int numeroFila) {
        this.internalId = id;
        activityName.setText(numeroFila + ". " + name);
        date.setText(d);
        typeLabel.setText(type);
        updateButtonStates();
    }

    /**
     * Updates the states of the join and unjoin buttons based on enrollment status.
     */
    private void updateButtonStates() {
        // Lógica de Dominio: el software como sistema discreto
        boolean isEnrolled = actService.isEnrolledInActivity(internalId);
        joinButton.setDisable(isEnrolled);
        unjoinButton.setDisable(!isEnrolled);
    }

    /**
     * Handles the deletion of the activity.
     */
    @FXML private void handleDeleteActivity() {
        if (actService.deleteActivity(internalId)) {
            if (onRefreshListener != null) onRefreshListener.run();
        }
    }

    /**
     * Handles the modification request of the activity.
     */
    @FXML private void handleModifyActivity() {
        //Avisamos al padre de que queremos modificar esta actividad
        if (onModifyListener != null) {
            onModifyListener.run();
        }
    }

    /**
     * Handles the joining of the activity.
     */
    @FXML private void handleJoinActivity() {
        if (actService.enrollInActivity(internalId)) {
            updateButtonStates();
        }
    }

    /**
     * Handles the unjoining of the activity.
     */
    @FXML private void handleUnjoinActivity() {
        if (actService.unrollActivity(internalId)) {
            updateButtonStates();
        }
    }

    /**
     * Handles the request to show participants of the activity.
     */
    @FXML private void handleParticipants() {
        if (onShowParticipantsListener != null) {
            onShowParticipantsListener.accept(internalId);
        }
    }
}
