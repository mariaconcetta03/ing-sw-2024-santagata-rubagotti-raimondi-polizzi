package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Chat;
import CODEX.org.model.ChatMessage;

import java.rmi.RemoteException;

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
