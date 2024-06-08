package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.ObjectiveCard;

import java.rmi.RemoteException;

public class UpdatePersonalObjectiveEvent implements Event {
    ObjectiveCard personalObjCard;
    String nickname;

    public UpdatePersonalObjectiveEvent(ObjectiveCard personalObjCard, String nickname) {
        this.personalObjCard = personalObjCard;
        this.nickname = nickname;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updatePersonalObjective(personalObjCard, nickname);
        return false;
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updatePersonalObjective(personalObjCard, nickname);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
