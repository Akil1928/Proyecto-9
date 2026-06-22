package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.entities.User.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio singleton que gestiona los usuarios del sistema.
 *
 * Credenciales de prueba:
 *   admin       / admin123   → ADMINISTRADOR
 *   estudiante  / est123     → ESTUDIANTE  (Juan Pérez,     carné B10001)
 *   maria       / maria456   → ESTUDIANTE  (María González, carné B10002)
 *   carlos      / carlos789  → ESTUDIANTE  (Carlos Gutierrez, carné B10003)
 *
 * IMPORTANTE: el username de cada estudiante debe coincidir exactamente
 * con el id registrado en StudentDirectoryService, para que el sistema
 * pueda encontrar su perfil al solicitar matrícula.
 */
public class UserService {

    private static UserService instance;

    private final List<User> users = new ArrayList<>();
    private User currentUser;

    private UserService() {
        users.add(new User("admin",      "admin123",  "Administrador UCR",  Role.ADMINISTRADOR));
        users.add(new User("profesor",   "prof123",   "Prof. Guzmán",    Role.PROFESOR));
        users.add(new User("estudiante", "est123",    "Héctor Sandoval",    Role.ESTUDIANTE));
        users.add(new User("maria",      "maria456",  "María González",     Role.ESTUDIANTE));
        users.add(new User("carlos",     "carlos789", "Carlos Gutierrez",   Role.ESTUDIANTE));
    }

    public static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username)
                        && u.checkPassword(password))
                .findFirst();
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser()          { return currentUser; }
    public void logout()                  { this.currentUser = null; }
}