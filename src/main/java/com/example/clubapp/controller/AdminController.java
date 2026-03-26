package com.example.clubapp.controller;

import Server.Activity;
import Server.ClubMember;
import com.example.clubapp.model.ActivityService;
import com.example.clubapp.model.MemberService;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utilities.NavigationUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.util.List;

/**
 * Admin menu controller.
 */
public class AdminController {

    // Dependencies of the domain
    private final ActivityService actService;
    private final MemberService memberService;
    private final NavigationUtils navUtils;
    private List<ClubMember> allMembersMaster = new java.util.ArrayList<>();
    private List<Activity> allActivitiesMaster = new java.util.ArrayList<>();

    // FXML UI components
    @FXML private VBox activityContainer;
    @FXML private VBox memberContainer;
    @FXML private VBox participantsContainer;
    @FXML private AnchorPane contentPane;
    @FXML private Button exitButton;
    @FXML private Button addActivityButton;
    @FXML private Button addPartnerButton;
    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> filterChoiceBox;
    @FXML private Label lblWelcome;

    // Constructor with dependency injection
    public AdminController(ActivityService actService, MemberService memberService, NavigationUtils navUtils) {
        this.actService = actService;
        this.memberService = memberService;
        this.navUtils = navUtils;
    }

    /**
     * Initializes the controller after the FXML is loaded.
     */
    @FXML
    public void initialize() {

        //Load the dashboard view by default
        navUtils.loadInternalView(contentPane, "/view/DashboardView.fxml", null);

        //Configuration of special fileds and choice boxes from the view
        configureSearchField();
        configureChoiceBox();
    }

    /**
     * Configures the filter choice box for activity types (All/Course/Competition).
     */
    private void configureChoiceBox() {
        if (filterChoiceBox != null) {
            filterChoiceBox.setValue("All"); // Valor por defecto

            filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals("All")) {
                    updateActivityInView();
                } else {
                    filterActivitiesByType(newValue);
                }
            });
        }
    }

    /**
     * Configures the search field to filter members or activities based on the current view.
     */
    private void configureSearchField() {
        if (searchField != null) {
            //Add listener to the text property
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {

                // We use the search field for both members and activities, depending on the view, so:

                // If memberContainer is not null, we are in the members list
                if (memberContainer != null && memberContainer.getScene() != null) {
                    filterMembers(newValue);
                }

                // Else, if activityContainer is not null, we are in the activities list
                else if (activityContainer != null && activityContainer.getScene() != null) {
                    filterActivitiesByName(newValue);
                }
            });
        }
    }

    // --- GESTIÓN DE VISTAS PRINCIPALES ---

    /**
     * Handles the click on the "List Activities" button.
     */
    @FXML private void handleListActivities() {
        navUtils.loadInternalView(contentPane, "/view/ActivitiesList.fxml", this);
        updateActivityInView();
    }

    /**
     * Handles the click on the "List Members" button.
     */
    @FXML private void handleListMembers() {
        //Load the members list view
        navUtils.loadInternalView(contentPane, "/view/PartnersList.fxml", this);

        // Update the members in the view
        updateMemberInView();
    }

    /**
     * Handles the click on the "Profile" button.
     */
    @FXML private void handleProfile() {
        ProfileController profileController = new ProfileController(memberService, navUtils);
        navUtils.loadInternalView(contentPane, "/view/ProfileView.fxml", profileController);
    }

    /**
     * Handles the click on the "Add Activity" button.
     */
    @FXML private void handleAddActivity() {
        // Create the controller with needed dependencies
        ActivityCreatorController creatorController = new ActivityCreatorController(actService);

        // Set the listener to refresh the activity list after creation
        creatorController.setOnActivityCreatedListener(() -> updateActivityInView());

        // Open the creation window
        navUtils.openNewWindow("/view/createActivity.fxml", "Create New Activity", creatorController, false);
    }

    /**
     * Handles the click on the "Add Partner" button.
     */
    @FXML private void handleAddPartner() {
        // Create the controller with needed dependencies
        MemberCreatorController creatorController = new MemberCreatorController(memberService);

        // Set the listener to refresh the member list after creation
        creatorController.setOnPartnerCreatedListener(() -> updateMemberInView());

        // Open the creation window
        navUtils.openNewWindow("/view/createPartner.fxml", "Create New Partner", creatorController, false);
    }




    // --- METHODS USED TO UPDATE THE VIEWS WITH DOMAIN DATA ---


    /**
     * Updates the activity list in the view when a change occurs.
     */
    public void updateActivityInView() {
        // Cleanup selective to protect the "Add Activity" button
        activityContainer.getChildren().removeIf(node -> node != addActivityButton);

        // Ensure the "Add Activity" button is present after cleanup
        if (!activityContainer.getChildren().contains(addActivityButton)) {
            activityContainer.getChildren().add(0, addActivityButton); // Lo ponemos al principio
        }

        // Load all activities from the service and save to master list
        allActivitiesMaster = actService.getAllActivities();
        if (allActivitiesMaster != null) {
            for (int i = 0; i < allActivitiesMaster.size(); i++) {
                renderActivityItem(allActivitiesMaster.get(i), i + 1);
            }
        }
    }

    /**
     * Renders a single activity item in the activity container.
     *
     * @param activity   The activity to render.
     * @param rowNumber The row number to display in the view of the item (not id).
     */
    private void renderActivityItem(Activity activity, int rowNumber) {
        // Create the controller for the activity item
        ActivityItemController itemController = new ActivityItemController(actService);

        // Set up listeners for refresh, show participants, and modify actions
        itemController.setOnRefreshListener(() -> updateActivityInView());
        itemController.setOnShowParticipantsListener((id -> showParticipantsFromActivity(id)));
        itemController.setOnModifyListener(() -> {
            // Open the edit window when modify is requested
            ActivityEditorController editorController = new ActivityEditorController(actService, activity);
            editorController.setOnActivityUpdatedListener(() -> updateActivityInView());
            navUtils.openNewWindow("/view/ModifyActivity.fxml", "Edit Activity", editorController, false);
        });
        // Load the activity item component into the container
        navUtils.loadInternalComponent(activityContainer, "/view/activityItem.fxml", itemController);
        itemController.setActivityDetails(activity.getId(), activity.getName(), activity.getDate().toString(), activity.getType(), rowNumber);
    }

    /**
     * Updates the member list in the view when a change occurs.
     */
    public void updateMemberInView() {
        // Cleanup selective to protect the "Add Partner" button
        memberContainer.getChildren().removeIf(node -> node != addPartnerButton);

        // Ensure the "Add Partner" button is present after cleanup
        if (!memberContainer.getChildren().contains(addPartnerButton)) {
            memberContainer.getChildren().add(0, addPartnerButton); // Lo ponemos al principio
        }

        // Load all members from the service and save to master list
        allMembersMaster = memberService.getAllMembers();

        // Render each member item in the container
        for(ClubMember member : allMembersMaster) {
            renderMemberItem(member);
        }
    }

    /**
     * Renders a single member item in the member container.
     *
     * @param member The instance of the club member to render.
     */
    private void renderMemberItem(ClubMember member) {
        // Create the controller for the partner item
        PartnerItemController itemController = new PartnerItemController(memberService);

        // Set up listeners for delete, modify, and update actions
        itemController.setOnDeletedListener(() -> updateMemberInView());
        itemController.setOnModifyListener(email -> {
            openEditWindow(email); // Método en el MainController que abre el FXML de edición
        });
        itemController.setOnUpdatedListener(() -> updateMemberInView());

        // Load the partner item component into the container
        navUtils.loadInternalComponent(memberContainer, "/view/partnerItem.fxml", itemController);
        itemController.setPartnerDetails(member.getName(), member.getSurname(), member.getEmail(), member.getRole());
    }

    /**
     * Filters the member list based on the search query.
     *
     * @param query The search query string.
     */
    private void filterMembers(String query) {
        // Cleanup selective to protect the "Add Partner" button
        memberContainer.getChildren().removeIf(node -> node != addPartnerButton);

        // Convert the query to lowercase for case-insensitive comparison
        String lowerQuery = query.toLowerCase();

        // Iterate through the master member list and render matching members
        for (ClubMember m : allMembersMaster) {
            if (m.getName().toLowerCase().contains(lowerQuery) ||
                    m.getSurname().toLowerCase().contains(lowerQuery) ||
                    m.getEmail().toLowerCase().contains(lowerQuery)) {
                renderMemberItem(m);
            }
        }
    }

    /**
     * Filters the activity list based on the search query.
     *
     * @param query The search query string.
     */
    private void filterActivitiesByName(String query) {
        // Cleanup selective to protect the "Add Activity" button
        activityContainer.getChildren().removeIf(node -> node != addActivityButton);

        // Convert the query to lowercase for case-insensitive comparison
        String lowerQuery = query.toLowerCase();

        // Iterate through the master activity list and render matching activities
        for (int i = 0; i < allActivitiesMaster.size(); i++) {
            Activity a = allActivitiesMaster.get(i);
            if (a.getName().toLowerCase().contains(lowerQuery) ||
                    a.getType().toLowerCase().contains(lowerQuery)) {
                renderActivityItem(a, i + 1);
            }
        }
    }

    /**
     * Filters the activity list based on the activity type.
     *
     * @param query The activity type to filter by.
     */
    private void filterActivitiesByType(String query) {
        // Cleanup selective to protect the "Add Activity" button
        activityContainer.getChildren().removeIf(node -> node != addActivityButton);

        // Convert the query to lowercase for case-insensitive comparison
        String lowerQuery = query.toLowerCase();

        // Iterate through the master activity list and render matching activities
        for (int i = 0; i < allActivitiesMaster.size(); i++) {
            Activity a = allActivitiesMaster.get(i);
            if (a.getType().toLowerCase().contains(lowerQuery)) {
                renderActivityItem(a, i + 1);
            }
        }
    }

    // --- PARTICIPANTES ---

    /**
     * Shows the participants of a specific activity.
     *
     * @param id The ID of the activity.
     */
    public void showParticipantsFromActivity(int id) {
        // Load the participants list view
        navUtils.loadInternalView(contentPane, "/view/ParticipantsList.fxml", this);
        updateParticipantsInActivity(id);
    }

    /**
     * Updates the participants list in the view for a specific activity.
     *
     * @param activityId The ID of the activity.
     */
    public void updateParticipantsInActivity(int activityId) {
        // Clear existing participants
        participantsContainer.getChildren().clear();
        // Fetch participants from the service
        List<ClubMember> participants = actService.getParticipantsOfActivity(activityId);

        // Render each participant item in the container
        if (participants != null) {
            for (ClubMember p : participants) {
                // Create the controller for the participant item
                ParticipantItemController itemController = new ParticipantItemController(memberService, activityId, p);

                // Set up listener for refresh action
                itemController.setOnRefreshListener(() -> updateParticipantsInActivity(activityId));

                // Load the participant item component into the container
                navUtils.loadInternalComponent(participantsContainer, "/view/ParticipantsItem.fxml", itemController);
                itemController.setParticipantDetails(p.getName() + " " + p.getSurname());
            }
        }
    }

    /**
     * Opens the edit window for a specific member.
     *
     * @param email The email of the member to edit.
     */
    private void openEditWindow(String email) {
        // Create the controller for member editing
        MemberEditorController editorController = new MemberEditorController(memberService, email);

        // Set up listener to update the member list after editing
        editorController.setOnMemberUpdatedListener(() -> updateMemberInView());

        // Open the edit window
        navUtils.openNewWindow("/view/ModifyMember.fxml", "Edit Partner", editorController, false);
    }

    /**
     * Handles the exit button action to close the application window.
     */
    @FXML private void handleExit() {
        ((Stage) exitButton.getScene().getWindow()).close();
    }
}
