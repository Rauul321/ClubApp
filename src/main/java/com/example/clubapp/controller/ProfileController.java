package com.example.clubapp.controller;

import Server.ClubMember;
import com.example.clubapp.model.MemberService;
import com.example.clubapp.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import utilities.NavigationUtils;

/**
 * ProfileController manages the user profile view and interactions.
 */
public class ProfileController {

    // FXML UI components
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label emailLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label fullNameLabel;
    @FXML private Button changePasswordButton;


    private ClubMember member;
    private MemberService memberService;
    private NavigationUtils navUtils;

    /**
     * Constructor with dependency injection.
     * @param memberService
     * @param navUtils
     */
    public ProfileController(MemberService memberService, NavigationUtils navUtils) {
        // Constructor con inyección de dependencias si es necesario
        this.member = SessionManager.getInstance().getUser();
        this.memberService = memberService;
        this.navUtils = navUtils;
    }

    /**
     * Initializes the controller after the FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        // Rellenar los campos con los datos del miembro
        fullNameLabel.setText(member.getName() + " " + member.getSurname());
        nameLabel.setText(member.getName());
        surnameLabel.setText(member.getSurname());
        emailLabel.setText(member.getEmail());
        userRoleLabel.setText(member.getRole());
    }

    /**
     * Handle change password button action.
     */
    @FXML
    private void handleChangePassword() {
        ChangePasswordController chgPwdCtrl = new ChangePasswordController(memberService, member.getPassword());
        navUtils.openNewWindow("/view/ChangePassword.fxml", "Change Password", chgPwdCtrl, false);
    }


}
