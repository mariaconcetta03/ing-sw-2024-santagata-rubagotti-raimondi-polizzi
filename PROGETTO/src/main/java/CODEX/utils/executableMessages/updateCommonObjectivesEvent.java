package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.ObjectiveCard;

import java.rmi.RemoteException;

public class updateCommonObjectivesEvent implements Event{
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
}
