package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Chat;
import java.rmi.RemoteException;

/**
 * This event is useful to communicate that one chat has been changed, because of
 * new messages, and so it needs to be updated
 */
public class UpdateChatEvent implements Event{
    Chat chatToBeAdded;
    public UpdateChatEvent(Chat chatToBeAdded){
        this.chatToBeAdded=chatToBeAdded;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {

        client.updateChat(chatToBeAdded.getId(),chatToBeAdded);
        return false;
    }

    @Override
    public void executeSCK(ClientSCK client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
