package cr.ac.ucr.sga.model.states;

import cr.ac.ucr.sga.model.entities.Tramit;

public interface TramitState {
    void handle(Tramit tramit);
    String getName();
}

