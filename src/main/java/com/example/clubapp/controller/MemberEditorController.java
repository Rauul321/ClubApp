package com.example.clubapp.controller;


import com.example.clubapp.model.MemberService;
import Server.ClubMember; // O ClubMember según tu jerarquía
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Member Editor Controller
 */
public class MemberEditorController {

    // FXML UI coponents
    @FXML private TextField txtName;
    @FXML private TextField txtSurname;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    // Member to be edited
    private ClubMember member;

    // Dependency of the domain
    private final MemberService memberService;

    // Email of the member to edit
    private final String emailToEdit;

    // "Cable" to notify the parent controller upon success
    private Runnable onMemberUpdatedListener;

    /**
     * Constructor with dependency injection.
     * @param memberService
     * @param email
     */
    public MemberEditorController(MemberService memberService, String email) {
        this.memberService = memberService;
        this.emailToEdit = email;
    }

    /**
     * Sets the listener to be called when a member is updated.
     * @param listener
     */
    public void setOnMemberUpdatedListener(Runnable listener) {
        this.onMemberUpdatedListener = listener;
    }

    /**
     * Initialize the editor with current member data.
     */
    @FXML
    public void initialize() {
        // 1. Pedir los datos actuales al servicio
        // Suponiendo que tienes un método getMemberByEmail en tu servicio
        ClubMember member = memberService.getMemberByEmail(emailToEdit);

        if (member != null) {
            // 2. Rellenar los campos
            this.member = member;
            txtName.setText(member.getName());
            txtSurname.setText(member.getSurname());
            txtEmail.setText(member.getEmail());
            txtPassword.setText(member.getPassword()); // O algún otro campo si lo tienes
            txtPassword.setPromptText("Dejar en blanco para no cambiar");

            // 3. Importante: El email es el ID, no permitas que se cambie aquí
            txtEmail.setEditable(false);
            txtEmail.setDisable(true);
        }
    }

    /**
     * Handle saving the modified member details.
     */
    @FXML
    private void handleSave() {
        String newName = txtName.getText();
        String newSurname = txtSurname.getText();
        String newPassword;
        if(!txtPassword.getText().isEmpty()) {
            newPassword = txtPassword.getText();
        } else {
            newPassword = member.getPassword();
        }

        boolean success = memberService.modifyMember(emailToEdit, newName, newSurname, newPassword);

        if (success) {

            if (onMemberUpdatedListener != null) {
                onMemberUpdatedListener.run();
            }
            closeWindow();
        } else {
            System.err.println("Error al actualizar el socio");
        }
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

