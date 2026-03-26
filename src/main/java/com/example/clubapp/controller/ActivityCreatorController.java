package com.example.clubapp.controller;

import com.example.clubapp.model.ActivityService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Activity Creator Controller
 */
public class ActivityCreatorController {
    // FXML UI components
    @FXML private TextField activityNameField;
    @FXML private DatePicker activityDatePicker;
    @FXML private Button createActivityButton;
    @FXML private ChoiceBox<String> activityTypeChoiceBox;

    // Dependency of the domain
    private final ActivityService actService;

    // "Cable" to notify the parent controller upon success
    private Runnable onActivityCreatedListener;

    /**
     * Constructor with dependency injection.
     */
    public ActivityCreatorController(ActivityService actService) {
        this.actService = actService;
    }

    /**
     * Sets the listener to be called when an activity is created.
     */
    public void setOnActivityCreatedListener(Runnable listener) {
        this.onActivityCreatedListener = listener;
    }

    /**
     * Handle create activity button action.
     */
    @FXML
    private void handleCreateActivity() {
        if (activityDatePicker.getValue() == null || activityNameField.getText().isEmpty()) {
            return;
        }

        String activityName = activityNameField.getText();
        String activityDate = formatDate(activityDatePicker.getValue().toString());
        String type = activityTypeChoiceBox.getValue();

        if (actService.createActivity(activityName, activityDate, type)) {
            System.out.println("✅ Activity created: " + activityName);

            if (onActivityCreatedListener != null) {
                onActivityCreatedListener.run();
            }

            closeWindow();
        } else {
            System.err.println("❌ Failed to create activity.");
        }
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) createActivityButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Formats the date from DD-MM-YYYY to DD/MM/YYYY.
     */
    private String formatDate(String date) {
        String[] parts = date.split("-");
        return parts[2] + "/" + parts[1] + "/" + parts[0];
    }
}
