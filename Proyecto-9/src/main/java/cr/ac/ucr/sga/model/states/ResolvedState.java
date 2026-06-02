package cr.ac.ucr.sga.model.states;

import cr.ac.ucr.sga.model.entities.Tramit;

public class ResolvedState implements TramitState {
    @Override
    public void handle(Tramit tramit) {
        throw new IllegalStateException("El trámite ya está resuelto, no puede avanzar más");
    }

    @Override
    public String getName() {
        return "Resuelto";
    }
}

