package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.ObjectiveCard;

import java.rmi.RemoteException;

public class updateCommonObjectivesEvent implements Event {
    private ObjectiveCard objCard1;
    private ObjectiveCard objCard2;

    public updateCommonObjectivesEvent(ObjectiveCard objCard1, ObjectiveCard objCard2) {
        this.objCard1 = objCard1;
        this.objCard2 = objCard2;
    }

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateCommonObjectives(objCard1, objCard2);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateCommonObjectives(objCard1, objCard2);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
