package com.example.clubapp.controller;

import Server.Activity;
import com.example.clubapp.model.ActivityService;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Activity editor controller for modifying activity details.
 */
public class ActivityEditorController {
    // FXML UI components
    @FXML private TextField txtName;
    @FXML private DatePicker dpDate;
    @FXML private ChoiceBox<String> cbType;

    // Dependencies of the domain
    ActivityService actService;
    Activity activity;

    // Listener for activity updates
    Runnable onActivityUpdatedListener;

    /**
     * Constructor with dependency injection.
     */
    public ActivityEditorController(ActivityService actService, Activity a) {
        this.activity = a;
        this.actService = actService;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     */
    @FXML
    public void initialize() {
        txtName.setText(activity.getName());

        int d = activity.getDate().getDay();
        int m = activity.getDate().getMonth();
        int y = activity.getDate().getYear();


        try {
            LocalDate dateForPicker = LocalDate.of(y, m, d);
            dpDate.setValue(dateForPicker);
        } catch (Exception e) {
            System.err.println("Fecha inválida en la base de datos: " + d + "/" + m + "/" + y);
        }

        cbType.setValue(activity.getType());
    }

    /**
     * Sets the listener to be called when the activity is updated.
     */
    public void setOnActivityUpdatedListener(Runnable listener) {
        this.onActivityUpdatedListener = listener;
    }

    /**
     * Handle saving the modified activity details.
     */
    @FXML
    private void handleSave() {
        String dateFormated = dpDate.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        actService.modifyActivity(
            activity.getId(),
            txtName.getText(),
            dateFormated,
            cbType.getValue());

        if (onActivityUpdatedListener != null) {
            onActivityUpdatedListener.run();
        }
        closeWindow();
    }


    /**
     * Handle cancelling the edit operation.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }
}
