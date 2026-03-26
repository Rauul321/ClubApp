package com.example.clubapp.controller;

import com.example.clubapp.model.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import utilities.NavigationUtils;

/**
 * Controller for changing a member's password.
 */
public class ChangePasswordController {

    // FXML UI components
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label failureLabel;




    // Dependencies of the domain
    private MemberService memberService;
    private NavigationUtils navUtils;

    // Current password for validation
    private String currPassword;

    /**
     * Constructor with dependency injection.
     * @param memberService
     * @param currPassword
     */
    public ChangePasswordController(MemberService memberService, String currPassword) {
        this.memberService = memberService;
        this.currPassword = currPassword;
    }


    /**
     * Handle change password button action.
     */
    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Empty field validation
        if(currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            failureLabel.setText("Error: All fields are required.");
            return;
        }

        // Current password validation
        if(!currentPassword.equals(currPassword)) {
            failureLabel.setText("Error: Current password is incorrect.");
            return;
        }

        // New password match validation
        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Error: New passwords do not match.");
            return;
        }

        // Change password via member service
        boolean success = memberService.changePassword(newPassword);
        if (success) {
            statusLabel.setText("Password changed successfully.");
            closeWindow();
        } else {
            failureLabel.setText("Error: Failed to change password.");
        }

    }

    /**
     * Handle cancel button action.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) currentPasswordField.getScene().getWindow();
        stage.close();
    }
}
