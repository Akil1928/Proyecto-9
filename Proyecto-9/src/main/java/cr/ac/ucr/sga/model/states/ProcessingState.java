package cr.ac.ucr.sga.model.states;

import cr.ac.ucr.sga.model.entities.Tramit;
import cr.ac.ucr.sga.model.services.NotificationService;

import java.time.LocalDateTime;

public class ProcessingState implements TramitState {
    @Override
    public void handle(Tramit tramit) {
        tramit.setState(new ResolvedState());
        tramit.setStateName("Resuelto");
        tramit.setUpdatedAt(LocalDateTime.now());
        NotificationService.getInstance().notify("Trámite " + tramit.getId() + " fue Resuelto exitosamente", "INFO");
    }

    @Override
    public String getName() {
        return "Procesando";
    }
}

