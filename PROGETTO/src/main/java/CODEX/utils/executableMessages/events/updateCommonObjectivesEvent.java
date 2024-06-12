package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.ObjectiveCard;
import java.rmi.RemoteException;


/**
 * This event is useful to communicate that common objectives have been set
 */
public class updateCommonObjectivesEvent implements Event {
    private ObjectiveCard objCard1;
    private ObjectiveCard objCard2;

    public updateCommonObjectivesEvent(ObjectiveCard objCard1, ObjectiveCard objCard2) {
        this.objCard1 = objCard1;
        this.objCard2 = objCard2;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateCommonObjectives(objCard1, objCard2);
        return false;
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateCommonObjectives(objCard1, objCard2);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    public boolean executeSCKServerSide() {
        return false;

    }
}
