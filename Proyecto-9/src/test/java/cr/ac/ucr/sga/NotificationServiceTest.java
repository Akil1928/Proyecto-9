package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.entities.Notification;
import cr.ac.ucr.sga.model.services.NotificationService;
import cr.ac.ucr.sga.view.observers.NotificationObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class NotificationServiceTest {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instance = NotificationService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    // ─── Observer ───────────────────────────────────────────────────────

    @Test
    void testObserverRecibeNotificacion() {
        System.out.println("=== NotificationService: Observer recibe notificación ===");
        NotificationService service = NotificationService.getInstance();

        System.out.println("Registrando observer...");
        service.addObserver(new NotificationObserver() {
            @Override
            public void onNotification(String message, String level) {
                System.out.println("Observer recibió - Mensaje: \"" + message + "\" | Nivel: " + level);
            }
            @Override
            public void onNotification(String message) {
                System.out.println("Observer recibió - Mensaje: \"" + message + "\"");
            }
        });

        System.out.println("Enviando notificación INFO...");
        service.notify("Sistema iniciado correctamente", "INFO");
    }

    @Test
    void testMultiplesObservers() {
        System.out.println("\n=== NotificationService: Múltiples observers ===");
        NotificationService service = NotificationService.getInstance();

        service.addObserver(new NotificationObserver() {
            @Override public void onNotification(String m, String l) { System.out.println("Observer A recibió: " + m); }
            @Override public void onNotification(String m) { System.out.println("Observer A recibió: " + m); }
        });
        service.addObserver(new NotificationObserver() {
            @Override public void onNotification(String m, String l) { System.out.println("Observer B recibió: " + m); }
            @Override public void onNotification(String m) { System.out.println("Observer B recibió: " + m); }
        });
        service.addObserver(new NotificationObserver() {
            @Override public void onNotification(String m, String l) { System.out.println("Observer C recibió: " + m); }
            @Override public void onNotification(String m) { System.out.println("Observer C recibió: " + m); }
        });

        System.out.println("Enviando una sola notificación a 3 observers...");
        service.notify("Broadcast a todos", "INFO");
    }

    @Test
    void testObserverNoSeDuplica() {
        System.out.println("\n=== NotificationService: Observer no se registra duplicado ===");
        NotificationService service = NotificationService.getInstance();

        int[] contador = {0};
        NotificationObserver obs = new NotificationObserver() {
            @Override public void onNotification(String m, String l) { contador[0]++; }
            @Override public void onNotification(String m) { contador[0]++; }
        };

        service.addObserver(obs);
        service.addObserver(obs); // intento duplicado

        service.notify("Test duplicado", "INFO");
        System.out.println("Veces que el observer fue invocado (esperado: 1): " + contador[0]);
    }

    @Test
    void testRemoverObserver() {
        System.out.println("\n=== NotificationService: Remover observer ===");
        NotificationService service = NotificationService.getInstance();

        int[] contador = {0};
        NotificationObserver obs = new NotificationObserver() {
            @Override public void onNotification(String m, String l) { contador[0]++; }
            @Override public void onNotification(String m) { contador[0]++; }
        };

        service.addObserver(obs);
        service.notify("Primera notificación", "INFO");
        System.out.println("Invocaciones tras primera notificación (esperado: 1): " + contador[0]);

        service.removeObserver(obs);
        service.notify("Segunda notificación (observer ya removido)", "INFO");
        System.out.println("Invocaciones tras segunda notificación (esperado: 1, sin cambio): " + contador[0]);
    }

    // ─── Prioridad ──────────────────────────────────────────────────────

    @Test
    void testPrioridadHighPrimero() {
        System.out.println("\n=== NotificationService: Prioridad HIGH se atiende primero ===");
        NotificationService service = NotificationService.getInstance();

        service.notify("Información general", "INFO");
        service.notify("¡Error crítico en el sistema!", "HIGH");
        service.notify("Advertencia de uso", "WARN");

        Notification top = service.peekLastNotification();
        System.out.println("Notificación en el tope de la cola (esperado nivel HIGH o ERROR):");
        System.out.println("  Mensaje: " + (top != null ? top.getMessage() : "null"));
        System.out.println("  Nivel:   " + (top != null ? top.getLevel() : "null"));
    }

    @Test
    void testSinObserversNoLanzaExcepcion() {
        System.out.println("\n=== NotificationService: Sin observers no lanza excepción ===");
        NotificationService service = NotificationService.getInstance();

        try {
            System.out.println("Enviando notificación sin observers registrados...");
            service.notify("Nadie escucha", "INFO");
            System.out.println("Notificación enviada sin errores (correcto).");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testColaVaciaRetornaNull() {
        System.out.println("\n=== NotificationService: Cola vacía retorna null en peek ===");
        NotificationService service = NotificationService.getInstance();

        Notification result = service.peekLastNotification();
        System.out.println("peek() en cola vacía (esperado: null): " + result);
    }
}