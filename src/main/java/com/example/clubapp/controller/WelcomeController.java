package com.example.clubapp.controller;

import javafx.fxml.FXML;

import com.example.clubapp.model.ActivityService;
import com.example.clubapp.model.AuthenticationService;
import com.example.clubapp.model.MemberService;
import utilities.NavigationUtils; // Nuestra clase de soporte

import javafx.stage.Stage;

/**
 * WelcomeController handles the welcome screen interactions.
 */
public class WelcomeController {

    // Reference to the primary stage
    private Stage stage;

    // Dependencies of the domain
    private final AuthenticationService authService;
    private final ActivityService actService;
    private final MemberService memberService;

    // Dependences of the navigation utilities
    private final NavigationUtils navUtils;

    /**
     * Constructor with dependency injection.
     */
    public WelcomeController(AuthenticationService authService,
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
     * Handle transition to the Login window.
     */
    @FXML
    private void handleLogin() {
        // Creamos el controlador inyectando sus dependencias
        LoginController loginController = new LoginController(authService, actService, memberService, navUtils);
        loginController.setStage(this.stage);

        // Usamos la utilidad para cambiar de escena de forma limpia
        navUtils.changeScene(
                stage,
                "/view/loginWindow.fxml",
                "Club App - Login",
                loginController
        );
    }

    /**
     * Handle transition to the Sign Up window.
     */
    @FXML
    private void handleSignUp() {
        SignupController signUpController = new SignupController(authService, actService, memberService, navUtils);
        signUpController.setLoginStage(this.stage);

        navUtils.changeScene(
                stage,
                "/view/SignUpWindow.fxml",
                "Club App - Crear Cuenta",
                signUpController
        );
    }
}