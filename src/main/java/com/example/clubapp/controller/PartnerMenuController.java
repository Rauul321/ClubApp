package com.example.clubapp.controller;

import Server.Activity;
import Server.ClubMember;
import com.example.clubapp.model.ActivityService;
import com.example.clubapp.model.AuthenticationService;
import com.example.clubapp.model.MemberService;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utilities.NavigationUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import com.example.clubapp.session.SessionManager;

/**
 * PartnerMenuController manages the partner menu view and its interactions.
 */
public class PartnerMenuController {

    // FXML UI components
    @FXML private AnchorPane contentPane;
    @FXML private VBox activityContainer;
    @FXML private VBox participantsContainer;
    @FXML private javafx.scene.control.Button exitButton;
    @FXML private Label lblWelcomeName;
    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> filterChoiceBox;

    // Dependencies of the domain
    private final AuthenticationService authService;
    private final ActivityService actService;
    private final MemberService memberService;

    // Dependencies of the navigation utilities
    private final NavigationUtils navUtils;

    // Reference to the primary stage
    private Stage stage;

    // Master list of all activities for filtering
    private List<Activity> allActivitiesMaster = new java.util.ArrayList<>();

    /**
     * Initializes the controller after its root element has been completely processed.
     */
    @FXML
    public void initialize() {
        navUtils.loadInternalView(contentPane, "/view/DashboardView.fxml", null);
        ClubMember memberLogged = SessionManager.getInstance().getUser();
        lblWelcomeName.setText("Welcome, " + memberLogged.getName() + "!");
        configureSearchField();
        configureFilterChoiceBox();
    }

    /**
     * Constructor with dependency injection.
     */
    public PartnerMenuController(AuthenticationService authService,
                                 ActivityService actService,
                                 MemberService memberService,
                                 NavigationUtils navUtils) {
        this.authService = authService;
        this.actService = actService;
        this.memberService = memberService;
        this.navUtils = navUtils;
    }

    /**
     * Sets the primary stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Configures the search field to filter activities by name.
     */
    private void configureSearchField() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newValue) -> applyFilters());
        }
    }

    /**
     * Configures the filter choice box to filter activities by type (All/Course/Competition).
     */
    private void configureFilterChoiceBox() {
        if (filterChoiceBox != null) {
            // Ensuring the filter options are only added once
            if (filterChoiceBox.getItems().isEmpty()) {
                filterChoiceBox.getItems().addAll("All", "Course", "Competition");
            }

            // Set default value
            filterChoiceBox.setValue("All");

            // Add listener for selection changes
            filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
                applyFilters();
            });
        }
    }

    /**
     * Applies both name and type filters to the activity list.
     */
    private void applyFilters() {
        // Clear current activities
        if (activityContainer == null || activityContainer.getScene() == null) {
            return;
        }
        activityContainer.getChildren().clear();

        // Get filter values
        String nameQuery = (searchField != null) ? searchField.getText().toLowerCase() : "";
        String typeQuery = (filterChoiceBox != null) ? filterChoiceBox.getValue() : "All";

        // Apply combined filters
        for (int i = 0; i < allActivitiesMaster.size(); i++) {
            Activity a = allActivitiesMaster.get(i);

            boolean matchesName = a.getName().toLowerCase().contains(nameQuery);
            boolean matchesType = typeQuery.equals("All") || a.getType().equalsIgnoreCase(typeQuery);

            if (matchesName && matchesType) {
                renderActivityItem(a, i + 1);
            }
        }
    }

    // --- NAVEGACIÓN DE VISTAS ---

    /**
     * Handle click on "List Activities" button.
     */
    @FXML
    private void handleListActivities() {
        // Load the activities list view
        navUtils.loadInternalView(contentPane, "/view/ActivitiesPartnersList.fxml", this);
        refreshActivities();
    }

    /**
     * Handle click on "Profile" button.
     */
    @FXML
    private void handleProfile() {
        // Load the profile view
        ProfileController profileController = new ProfileController(memberService, navUtils);
        navUtils.loadInternalView(contentPane, "/view/ProfileView.fxml", profileController);
    }

    // --- METHODS TO UPDATE ACTIVITY AND PARTICIPANT LISTS ---

    /**
     * Refreshes the list of activities displayed.
     */
    public void refreshActivities() {
        activityContainer.getChildren().clear();
        allActivitiesMaster = actService.getAllActivities();

        for (int i = 0; i < allActivitiesMaster.size(); i++) {
            renderActivityItem(allActivitiesMaster.get(i), i + 1);
        }
    }

    /**
     * Renders a single activity item in the activity container.
     * @param activity
     * @param rowNumber
     */
    private void renderActivityItem(Activity activity, int rowNumber) {
        // Create and configure the activity item controller
        ActivityItemController itemController = new ActivityItemController(actService);

        // Set up listeners for item actions
        itemController.setOnRefreshListener(() -> refreshActivities());
        itemController.setOnShowParticipantsListener(id -> showParticipantsFromActivity(activity.getId()));

        // Load the activity item FXML into the container
        navUtils.loadInternalComponent(activityContainer, "/view/activityPartnerItem.fxml", itemController);
        itemController.setActivityDetails(activity.getId(), activity.getName(),
                activity.getDate().toString(), activity.getType(), rowNumber);
    }

    /**
     * Shows the participants of a specific activity.
     * @param activityId
     */
    public void showParticipantsFromActivity(int activityId) {
        // Load the participants list view
        navUtils.loadInternalView(contentPane, "/view/ParticipantsList.fxml", this);
        updateParticipants(activityId);
    }

    /**
     * Updates the participants list for a given activity.
     * @param activityId
     */
    private void updateParticipants(int activityId) {
        // Clear existing participants and fetch new ones
        try {
            participantsContainer.getChildren().clear();
            List<ClubMember> participants = actService.getParticipantsOfActivity(activityId);

            for (ClubMember p : participants) {
                renderParticipantItem(activityId, p);
            }
        } catch (Exception e) {
            // Error handler
            System.err.println("Error actualizando participantes: " + e.getMessage());
        }
    }

    /**
     * Renders a single participant item in the participants container.
     * @param activityId
     * @param participant
     */
    private void renderParticipantItem(int activityId, ClubMember participant) {
        ParticipantItemController itemController = new ParticipantItemController(memberService, activityId, participant);
        navUtils.loadInternalComponent(participantsContainer, "/view/ParticipantsPartnerItem.fxml", itemController);
        itemController.setParticipantDetails(participant.getName() + " " + participant.getSurname());
    }

    /**
     * Handle click on "Exit" button.
     */
    @FXML
    private void handleExit() {
        // Ensure the button and scene are not null before closing
        if (exitButton != null && exitButton.getScene() != null) {
            // Close the current stage
            ((Stage) exitButton.getScene().getWindow()).close();
        }
    }
}
