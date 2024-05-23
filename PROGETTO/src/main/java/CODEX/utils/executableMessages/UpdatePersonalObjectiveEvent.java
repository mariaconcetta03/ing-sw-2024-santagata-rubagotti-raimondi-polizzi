package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.ObjectiveCard;

import java.rmi.RemoteException;

public class UpdatePersonalObjectiveEvent implements Event{
    ObjectiveCard personalObjCard;
    String nickname;

    public UpdatePersonalObjectiveEvent(ObjectiveCard personalObjCard, String nickname) {
        this.personalObjCard = personalObjCard;
        this.nickname = nickname;
    }

    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updatePersonalObjective(personalObjCard, nickname);
    }
}
