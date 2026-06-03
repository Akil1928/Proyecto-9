package cr.ac.ucr.sga.model.entities;

/**
 * Entidad que representa un usuario del sistema.
 * Tiene un rol que determina a qué vistas puede acceder.
 */
public class User {

    public enum Role {
        ESTUDIANTE,
        ADMINISTRADOR
    }

    private final String username;
    private final String password;
    private final String displayName;
    private final Role role;

    public User(String username, String password, String displayName, Role role) {
        this.username    = username;
        this.password    = password;
        this.displayName = displayName;
        this.role        = role;
    }

    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getDisplayName() { return displayName; }
    public Role   getRole()        { return role; }

    /** Verifica si la contraseña ingresada coincide (comparación simple). */
    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }
}