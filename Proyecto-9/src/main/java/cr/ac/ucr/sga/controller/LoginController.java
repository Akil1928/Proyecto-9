package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.services.UserService;
import cr.ac.ucr.sga.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Controlador de la pantalla de login.
 *
 * Flujo:
 *   1. El usuario ingresa credenciales y presiona "Ingresar".
 *   2. Se valida contra UserService.
 *   3. Si es correcto, se guarda el usuario en sesión y se carga main-view.fxml.
 *   4. Si es incorrecto, se muestra un mensaje de error.
 *
 * La ventana principal recibe el rol a través de ViewFactory.showMainView(stage, user).
 */
public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblError;
    @FXML private Label         lblStatus;

    @FXML
    public void initialize() {
        lblError.setVisible(false);
        lblStatus.setText("");
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        //validación básica de campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor ingrese usuario y contraseña.");
            return;
        }

        //autenticación
        Optional<User> result = UserService.getInstance().authenticate(username, password);

        if (result.isPresent()) {
            User user = result.get();
            UserService.getInstance().setCurrentUser(user);
            lblError.setVisible(false);

            //obtener el Stage actual y cargar la vista principal
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            ViewFactory.showMainView(stage, user);

        } else {
            showError("Usuario o contraseña incorrectos. Intente nuevamente.");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}