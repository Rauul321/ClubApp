package com.example.clubapp.controller;

import com.example.clubapp.model.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.function.Consumer;

/**
 * PartnerItemController manages the UI and interactions for a single partner item.
 */
public class PartnerItemController {

    // FXML UI components
    @FXML private Label partnerName;
    @FXML private Label partnerSurname;
    @FXML private Label partnerEmail;
    @FXML private Label role;

    // Dependency of the domain
    private final MemberService memberService;

    // Listeners
    private Runnable onDeletedListener;
    private Consumer<String> onModifyListener; // Recibe el email del socio a modificar
    private Runnable onUpdatedListener;

    /**
     * Constructor with dependency injection.
     * @param memberService
     */
    public PartnerItemController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Sets the listener to be called when a partner is deleted.
     * @param listener
     */
    public void setOnDeletedListener(Runnable listener) {
        this.onDeletedListener = listener;
    }

    /**
     * Sets the listener to be called when a partner is updated.
     * @param listener
     */
    public void setOnUpdatedListener(Runnable listener) {
        this.onUpdatedListener = listener;
    }

    /**
     * Sets the partner details in the UI.
     * @param name
     * @param surname
     * @param email
     * @param rol
     */
    public void setPartnerDetails(String name, String surname, String email, String rol) {
        partnerName.setText(name);
        partnerSurname.setText(surname);
        partnerEmail.setText(email);
        role.setText(rol);
    }

    /**
     * Sets the listener to be called when modification is requested.
     * @param listener
     */
    public void setOnModifyListener(Consumer<String> listener) {
        this.onModifyListener = listener;
    }

    /**
     * Handle modify button action.
     */
    @FXML
    private void handleModify() {
        if (onModifyListener != null) {
            // Avisamos al padre que queremos modificar este email
            onModifyListener.accept(partnerEmail.getText());
        }
    }

    /**
     * Handle delete button action.
     */
    @FXML
    private void handleDelete() {
        // Lógica de Dominio de Aplicación
        String email = partnerEmail.getText();
        if (memberService.deletePartner(email)) {
            // Si hay alguien escuchando, le avisamos que debe refrescar la vista
            if (onDeletedListener != null) {
                onDeletedListener.run();
            }
        }
    }


}
