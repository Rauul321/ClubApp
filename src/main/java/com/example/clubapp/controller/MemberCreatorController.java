package com.example.clubapp.controller;


import com.example.clubapp.model.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static utilities.FormatValidationUtils.isValidEmail;

/**
 * Member Creator Controller
 */
public class MemberCreatorController {
    // FXML UI components
    @FXML private TextField partnerNameField;
    @FXML private TextField partnerSurnameField;
    @FXML private TextField partnerEmailField;
    @FXML private PasswordField partnerPasswordField;
    @FXML private ChoiceBox <String> roleChoiceBox;
    @FXML private Label statusLabel;

    // Dependency of the domain
    private final MemberService memberService;

    // "Cable" to notify the parent controller upon success
    private Runnable onPartnerCreatedListener;

    /**
     * Constructor with dependency injection.
     * @param memberService
     */
    public MemberCreatorController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Sets the listener to be called when a partner is created.
     * @param listener
     */
    public void setOnPartnerCreatedListener(Runnable listener) {
        this.onPartnerCreatedListener = listener;
    }

    /**
     * Handle create partner button action.
     */
    @FXML
    private void handleCreateMember() {
        String name = partnerNameField.getText();
        String surname = partnerSurnameField.getText();
        String email = partnerEmailField.getText();
        String password = partnerPasswordField.getText();
        String role = roleChoiceBox.getValue();

        // Email validation
        if(!isValidEmail(email)) {
            statusLabel.setVisible(true);
            statusLabel.setText("Invalid email format");
            return;
        }
        if (memberService.createMember(name, surname, email, password, role)) {
            System.out.println("✅ Partner created: " + name + " " + surname);

            // Notify listener if set
            if (onPartnerCreatedListener != null) {
                onPartnerCreatedListener.run();
            }

            closeWindow();
        } else {
            System.err.println("❌ Failed to create partner.");
        }
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) partnerNameField.getScene().getWindow();
        stage.close();
    }
}
