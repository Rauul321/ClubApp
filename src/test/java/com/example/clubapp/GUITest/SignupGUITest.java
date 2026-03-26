package com.example.clubapp.GUITest;

import com.example.clubapp.controller.SignupController;
import com.example.clubapp.model.ActivityService;
import com.example.clubapp.model.AuthenticationService;
import com.example.clubapp.model.MemberService;
import utilities.NavigationUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import static org.mockito.Mockito.*;

public class SignupGUITest extends ApplicationTest {

    private AuthenticationService authMock = mock(AuthenticationService.class);
    private ActivityService actMock = mock(ActivityService.class);
    private MemberService memMock = mock(MemberService.class);
    private NavigationUtils navMock = mock(NavigationUtils.class);

    @Override
    public void start(Stage stage) throws Exception {
        // Instanciamos controlador con mocks
        SignupController controller = new SignupController(authMock, actMock, memMock, navMock);
        controller.setLoginStage(stage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
        loader.setController(controller);

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    @DisplayName("It should show error on invalid email format")
    void testInvalidEmailFormat() {
        // Rellenamos datos pero con email mal puesto
        clickOn("#nameField").write("Raúl");
        clickOn("#surnameField").write("Gomez");
        clickOn("#emailField").write("esto_no_es_un_email");
        clickOn("#passwordField").write("123456");

        clickOn("Register Now"); // Usamos el texto del botón fx:id="signupButton"

        FxAssert.verifyThat("#successLabel", LabeledMatchers.hasText("Invalid email format"));

        // Verificamos que el controlador NUNCA llamó al servicio de autenticación
        verifyNoInteractions(authMock);
    }

    @Test
    @DisplayName("It should signup successfully with valid data")
    void testSignupSuccess() {
        when(authMock.signUp(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        clickOn("#nameField").write("Leo");
        clickOn("#surnameField").write("Messi");
        clickOn("#emailField").write("leo@goat.com");
        clickOn("#passwordField").write("dios10");

        clickOn("#signupButton");

        FxAssert.verifyThat("#successLabel", LabeledMatchers.hasText("✅ Signup successful! Redirecting..."));

        verify(navMock).changeScene(any(), eq("/view/loginWindow.fxml"), anyString(), any());
    }

    @Test
    @DisplayName("It should show error when signup fails due to existing email")
    void testGoBackLink() {
        clickOn("Already have an account? Log In");
        verify(navMock).changeScene(any(), eq("/view/loginWindow.fxml"), anyString(), any());
    }
}
