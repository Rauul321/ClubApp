package com.example.clubapp.controller;

import com.example.clubapp.model.ActivityService;
import com.example.clubapp.model.MemberService;
import com.example.clubapp.model.AuthenticationService;
import utilities.NavigationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * LoginController handles user login interactions.
 */
public class LoginController {
    // FXML UI components
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    // Reference to the primary stage
    private Stage stage;

    // Dependencies of the domain
    private final AuthenticationService authService;
    private final ActivityService actService;
    private final MemberService memberService;
    private final NavigationUtils navUtils; // Inyectamos nuestra infraestructura

    // Constructor with dependency injection
    public LoginController(AuthenticationService authService, ActivityService actService,
                           MemberService memberService, NavigationUtils navUtils) {
        this.authService = authService;
        this.actService = actService;
        this.memberService = memberService;
        this.navUtils = navUtils;
    }

    // Sets the primary stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handle user pulling the login button.
     */
    @FXML
    private void handleLogin() {
        // Get user input
        String email = emailField.getText();
        String password = passwordField.getText();


        try {
            // The authentication service processes the login logic
            String response = authService.login(email, password);

            //Process the response
            processLoginResponse(response);

        } catch (Exception e) {
            // Handle unexpected errors
            statusLabel.setText("❌ Login error: " + e.getMessage());
        } finally {
            // Clear input fields
            emailField.clear();
            passwordField.clear();
        }
    }

    /**
     * Process the login response and navigate accordingly.
     */
    private void processLoginResponse(String response) {

        switch (response) {
            // Successful login for partner
            case "SUCCESS PARTNER":
                statusLabel.setText("✅ Login successful!");
                navigateToPartnerMenu();
                break;

            // Successful login for admin
            case "SUCCESS ADMIN":
                statusLabel.setText("✅ Admin login successful!");
                navigateToAdminMenu();
                break;

            // Failed login due to invalid credentials
            case "FAIL_CREDENTIALS":
                statusLabel.setText("❌ Login failed: Invalid credentials.");
                break;

            // Login error due to server communication issues
            case "LOGIN_ERROR":
                statusLabel.setText("❌ Login error: Server communication issue.");
                break;

            // Unknown response from server
            default:
                statusLabel.setText("❌ Login error: Unknown response.");
                break;
        }
    }

    /**
     * Navigate to the Partner Menu.
     */
    private void navigateToPartnerMenu() {
        // Create the PartnerMenuController with dependencies
        PartnerMenuController partnerMenuController = new PartnerMenuController(
                authService, actService, memberService, navUtils
        );
        // Set the current stage
        partnerMenuController.setStage(this.stage);

        //Change the scene to Partner Menu
        navUtils.changeScene(stage, "/view/partnerMenu.fxml", "Club App - Partner Menu", partnerMenuController);
    }

    /**
     * Navigate to the Admin Menu.
     */
    private void navigateToAdminMenu() {
        //Create the AdminController with dependencies
        AdminController adminController = new AdminController(actService, memberService, navUtils);

        navUtils.changeScene(this.stage, "/view/AdminMenu.fxml", "Club App - Admin Menu", adminController);

    }

    /**
     * Handle user clicking the sign-up link.
     */
    @FXML
    private void handleLinkSignup() {
        SignupController signUpController = new SignupController(authService, actService, memberService, navUtils);
        signUpController.setLoginStage(this.stage);

        navUtils.changeScene(stage, "/view/SignUpWindow.fxml", "Club App - Crear Cuenta", signUpController);
    }

}

