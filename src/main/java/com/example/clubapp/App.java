package com.example.clubapp;

import com.example.clubapp.controller.WelcomeController;
import com.example.clubapp.model.ActivityService;
import javafx.application.Application;
import javafx.stage.Stage;

import com.example.clubapp.communication.ClubServiceClient;
import com.example.clubapp.model.AuthenticationService;
import com.example.clubapp.model.MemberService;
import utilities.NavigationUtils;


public class App extends Application {

    private ClubServiceClient clubClient;

    private AuthenticationService authService;
    private ActivityService actService;
    private MemberService memberService;

    private final NavigationUtils navUtils = new NavigationUtils();

    /**
     * Initializes the application's core components and launches the first view.
     * @param primaryStage The first stage of the application initialized by JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Domain Initialization
            //Establishing connection with the central server via TCP/IP Sockets.
            clubClient = new ClubServiceClient("localhost", 5000);

            // 2. Dependency Injection
            // Providing the communication client to the business services.
            authService = new AuthenticationService(clubClient);
            actService = new ActivityService(clubClient);
            memberService = new MemberService(clubClient);

            WelcomeController welcomeController = new WelcomeController(
                    authService, actService, memberService, navUtils
            );
            welcomeController.setStage(primaryStage);

            // 3. Launching the First View
            navUtils.changeScene(
                    primaryStage,
                    "/view/FirstView.fxml",
                    "Welcome to the Club App",
                    welcomeController
            );

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación: " + e.getMessage());
        }
    }

    /**
     * Cleans up resources when the application is closed.
     */
    @Override
    public void stop() {
        if (clubClient != null) {
            System.out.println("Conexión con el servidor cerrada correctamente.");
        }
    }

    /**
     * Main entry point for launching the JavaFX application.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}