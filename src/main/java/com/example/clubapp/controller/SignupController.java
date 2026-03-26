package com.example.clubapp.controller;

import com.example.clubapp.model.ActivityService;
import com.example.clubapp.model.AuthenticationService;
import com.example.clubapp.model.MemberService;
import javafx.scene.control.Hyperlink;
import utilities.NavigationUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static utilities.FormatValidationUtils.isValidEmail;

public class SignupController {
    // FXML UI components
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label successLabel;
    @FXML private Hyperlink backToLogin;

    // Dependencies of the domain
    private final AuthenticationService authService;
    private final ActivityService actService;
    private final MemberService memberService;

    // Dependencies of the navigation utilities
    private final NavigationUtils navUtils; // Inyectamos la infraestructura

    // Reference to the primary stage
    private Stage stage;

    /**
     * Constructor with dependency injection.
     */
    public SignupController(AuthenticationService authService,
                            ActivityService actService,
                            MemberService memberService,
                            NavigationUtils navUtils) {
        this.authService = authService;
        this.actService = actService; // Los guardamos aunque no los usemos aquí...
        this.memberService = memberService; // ...para poder devolverlos al Login.
        this.navUtils = navUtils;
    }

    /**
     * Sets the primary stage.
     */
    public void setLoginStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handle user pulling the signup button.
     */
    @FXML
    private void handleSignup() {
        //Get user input
        String name = nameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        //format control
        if(!isValidEmail(email)) {
            successLabel.setVisible(true);
            successLabel.setText("Invalid email format");
            return;
        }


        try {
            // The authentication service processes the signup logic
            if(authService.signUp(name, surname, email, password)) {
                showSuccessAndRedirect();
            } else {

                //Show error message in case of failure
                successLabel.setVisible(true);
                successLabel.setText("❌ Signup failed. Email already registered.");
            }
        } catch (Exception e) {
            // Handle unexpected errors
            successLabel.setVisible(true);
            successLabel.setText("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Show success message and redirect to login.
     */
    private void showSuccessAndRedirect() {
        successLabel.setText("✅ Signup successful! Redirecting...");
        Platform.runLater(() -> {
            clearFields();
            handleGoBack();
        });
    }

    /**
     * Handle user clicking the "backToLogin" hyperlink.
     */
    @FXML
    private void handleGoBack() {
        // Navigate back to the login window
        LoginController loginCtrl = new LoginController(
                this.authService,
                this.actService,
                this.memberService,
                this.navUtils
        );
        loginCtrl.setStage(this.stage);

        //Use navigation utility to change scene cleanly
        navUtils.changeScene(stage, "/view/loginWindow.fxml", "Club App - Login", loginCtrl);
    }

    /**
     * Clear input fields.
     */
    private void clearFields() {
        nameField.clear();
        surnameField.clear();
        emailField.clear();
        passwordField.clear();
    }
}
