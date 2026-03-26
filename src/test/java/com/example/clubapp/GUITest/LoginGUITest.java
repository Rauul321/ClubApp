package com.example.clubapp.GUITest;

import com.example.clubapp.controller.LoginController;
import com.example.clubapp.model.ActivityService;
import com.example.clubapp.model.AuthenticationService;
import com.example.clubapp.model.MemberService;
import utilities.NavigationUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class LoginGUITest extends ApplicationTest {

    // Mocks de los servicios que usa el controlador
    private AuthenticationService authMock = mock(AuthenticationService.class);
    private ActivityService actMock = mock(ActivityService.class);
    private MemberService memMock = mock(MemberService.class);
    private NavigationUtils navMock = mock(NavigationUtils.class);

    private LoginController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Instanciamos el controlador pasando los Mocks
        controller = new LoginController(authMock, actMock, memMock, navMock);
        controller.setStage(stage);

        // 2. Cargamos el FXML asociando el controlador manualmente
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginWindow.fxml"));
        loader.setController(controller);

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testLoginInvalidCredentials() throws IOException, ClassNotFoundException {

        when(authMock.login("user@test.com", "wrong")).thenReturn("FAIL_CREDENTIALS");


        clickOn("#emailField").write("user@test.com");
        clickOn("#passwordField").write("wrong");
        clickOn("Enter");

        FxAssert.verifyThat("#statusLabel", LabeledMatchers.hasText("❌ Login failed: Invalid credentials."));
    }

    @Test
    void testLoginSuccessPartner() throws IOException, ClassNotFoundException {
        when(authMock.login("raul@club.com", "1234")).thenReturn("SUCCESS PARTNER");

        clickOn("#emailField").write("raul@club.com");
        clickOn("#passwordField").write("1234");
        clickOn("Enter");

        FxAssert.verifyThat("#statusLabel", LabeledMatchers.hasText("✅ Login successful!"));

        verify(navMock).changeScene(any(), eq("/view/partnerMenu.fxml"), anyString(), any());
    }

    @Test
    void testLoginSuccessAdmin() throws IOException, ClassNotFoundException {
        when(authMock.login("admin@club.com", "adminpass")).thenReturn("SUCCESS ADMIN");

        clickOn("#emailField").write("admin@club.com");
        clickOn("#passwordField").write("adminpass");
        clickOn("Enter");

        FxAssert.verifyThat("#statusLabel", LabeledMatchers.hasText("✅ Admin login successful!"));

        verify(navMock).changeScene(any(), eq("/view/AdminMenu.fxml"), anyString(), any());
    }
}
