package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.entities.User.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio singleton que gestiona los usuarios del sistema.
 * Incluye usuarios de prueba predefinidos para cada rol.
 *
 * Usuarios de prueba:
 *   admin   / admin123  → ADMINISTRADOR
 *   estudiante / est123 → ESTUDIANTE
 *   maria   / maria456  → ESTUDIANTE
 */
public class UserService {

    private static UserService instance;

    private final List<User> users = new ArrayList<>();

    /** Usuario actualmente en sesión (null si nadie ha iniciado sesión). */
    private User currentUser;

    private UserService() {
        // Usuarios de prueba predefinidos
        users.add(new User("admin",      "admin123", "Administrador UCR", Role.ADMINISTRADOR));
        users.add(new User("estudiante", "est123",   "Juan Pérez",        Role.ESTUDIANTE));
        users.add(new User("maria",      "maria456", "María González",    Role.ESTUDIANTE));
    }

    public static UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    /**
     * Intenta autenticar con las credenciales dadas.
     *
     * @return el User si las credenciales son correctas, Optional.empty() si no.
     */
    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username)
                        && u.checkPassword(password))
                .findFirst();
    }

    /** Guarda el usuario que inició sesión. */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /** Devuelve el usuario en sesión actual. Puede ser null antes del login. */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Cierra la sesión actual. */
    public void logout() {
        this.currentUser = null;
    }
}