package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Chat;
import java.rmi.RemoteException;

/**
 * This event is useful to communicate that one chat has been changed, because of
 * new messages, and so it needs to be updated
 */
public class updateChatEvent implements Event{
    Chat chatToBeAdded;
    public updateChatEvent(Chat chatToBeAdded){
        this.chatToBeAdded=chatToBeAdded;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {

        client.updateChat(chatToBeAdded.getId(),chatToBeAdded);
        return false;
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
