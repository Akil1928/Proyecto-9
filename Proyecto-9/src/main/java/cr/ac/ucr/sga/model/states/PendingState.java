package cr.ac.ucr.sga.model.states;

import cr.ac.ucr.sga.model.entities.Tramit;
import cr.ac.ucr.sga.model.services.NotificationService;

import java.time.LocalDateTime;

public class PendingState implements TramitState {
    @Override
    public void handle(Tramit tramit) {
        tramit.setState(new ProcessingState());
        tramit.setStateName("Procesando");
        tramit.setUpdatedAt(LocalDateTime.now());
        NotificationService.getInstance().notify("Trámite " + tramit.getId() + " ahora está en Procesando", "ADVERTENCIA");
    }

    @Override
    public String getName() {
        return "Pendiente";
    }
}

