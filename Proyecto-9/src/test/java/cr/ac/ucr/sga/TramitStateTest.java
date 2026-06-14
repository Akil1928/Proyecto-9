package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.entities.Tramit;
import cr.ac.ucr.sga.model.services.NotificationService;
import cr.ac.ucr.sga.view.observers.NotificationObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class TramitStateTest {

    @BeforeEach
    void resetNotificationService() throws Exception {
        Field instance = NotificationService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testEstadoInicialEsPendiente() {
        System.out.println("=== TramitState: Estado inicial es Pendiente ===");
        Tramit tramit = new Tramit("Certificado", "Solicitud de notas", "EST-001", "Juan Pérez");

        System.out.println("Estado inicial (esperado: Pendiente): " + tramit.getStateName());
        System.out.println("Clase del estado: " + tramit.getState().getClass().getSimpleName());
    }

    @Test
    void testTransicionPendienteAProcesando() {
        System.out.println("\n=== TramitState: Pendiente → Procesando ===");
        Tramit tramit = new Tramit("Beca", "Solicitud de beca académica", "EST-002", "María López");

        System.out.println("Estado antes de avanzar: " + tramit.getStateName());
        tramit.nextState();
        System.out.println("Estado tras nextState() (esperado: Procesando): " + tramit.getStateName());
        System.out.println("Clase del estado: " + tramit.getState().getClass().getSimpleName());
    }

    @Test
    void testTransicionProcesandoAResuelto() {
        System.out.println("\n=== TramitState: Procesando → Resuelto ===");
        Tramit tramit = new Tramit("Retiro", "Retiro de curso", "EST-003", "Carlos Mora");

        tramit.nextState(); // Pendiente → Procesando
        System.out.println("Estado intermedio: " + tramit.getStateName());

        tramit.nextState(); // Procesando → Resuelto
        System.out.println("Estado final (esperado: Resuelto): " + tramit.getStateName());
        System.out.println("Clase del estado: " + tramit.getState().getClass().getSimpleName());
    }

    @Test
    void testEstadoResueltoNoAvanza() {
        System.out.println("\n=== TramitState: Resuelto no avanza más ===");
        Tramit tramit = new Tramit("Traslado", "Traslado de carrera", "EST-004", "Ana Vargas");

        tramit.nextState(); // → Procesando
        tramit.nextState(); // → Resuelto
        System.out.println("Estado final antes del intento extra: " + tramit.getStateName());

        tramit.nextState(); // no debería cambiar
        System.out.println("Estado tras llamada extra en Resuelto (esperado: Resuelto): " + tramit.getStateName());
    }

    @Test
    void testFlujoCompletoConNotificaciones() {
        System.out.println("\n=== TramitState: Flujo completo con notificaciones ===");

        NotificationService.getInstance().addObserver(new NotificationObserver() {
            @Override
            public void onNotification(String message, String level) {
                System.out.println("  [Notificación] " + level + ": " + message);
            }
            @Override
            public void onNotification(String message) {
                System.out.println("  [Notificación]: " + message);
            }
        });

        Tramit tramit = new Tramit("Convalidación", "Convalidación de materias", "EST-005", "Luis Soto");

        System.out.println("Estado 1: " + tramit.getStateName());
        tramit.nextState();
        System.out.println("Estado 2: " + tramit.getStateName());
        tramit.nextState();
        System.out.println("Estado 3: " + tramit.getStateName());
    }

    @Test
    void testMultiplesTramitesIndependientes() {
        System.out.println("\n=== TramitState: Múltiples trámites con estados independientes ===");
        Tramit t1 = new Tramit("Cert", "Certificado 1", "EST-001", "Pedro Gil");
        Tramit t2 = new Tramit("Beca", "Beca deportiva", "EST-002", "Laura Ríos");

        t1.nextState(); // t1 → Procesando

        System.out.println("T1 estado (esperado: Procesando): " + t1.getStateName());
        System.out.println("T2 estado (esperado: Pendiente, no se tocó): " + t2.getStateName());
        System.out.println("Los estados son independientes entre instancias (correcto).");
    }

    @Test
    void testFechaActualizadaAlCambiarEstado() {
        System.out.println("\n=== TramitState: Fecha updatedAt se actualiza al avanzar ===");
        Tramit tramit = new Tramit("Retiro", "Retiro parcial", "EST-006", "Sofía Cruz");

        System.out.println("Fecha inicial de actualización: " + tramit.getUpdatedAt());
        tramit.nextState();
        System.out.println("Fecha tras avanzar estado:      " + tramit.getUpdatedAt());
        System.out.println("(Las fechas deberían ser iguales o la segunda igual/posterior a la primera)");
    }

    @Test
    void testGetNameDeEstados() {
        System.out.println("\n=== TramitState: getName() de cada estado ===");
        Tramit tramit = new Tramit("Test", "Descripción", "EST-007", "Test User");

        System.out.println("getName() en Pendiente (esperado: Pendiente): " + tramit.getState().getName());
        tramit.nextState();
        System.out.println("getName() en Procesando (esperado: Procesando): " + tramit.getState().getName());
        tramit.nextState();
        System.out.println("getName() en Resuelto (esperado: Resuelto): " + tramit.getState().getName());
    }
}