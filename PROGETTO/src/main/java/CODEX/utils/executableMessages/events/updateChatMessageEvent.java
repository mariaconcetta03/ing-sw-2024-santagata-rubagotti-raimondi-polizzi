package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.ChatMessage;

import java.rmi.RemoteException;

public class updateChatMessageEvent implements Event{
    private ChatMessage messageToBeAdded;
    private String chatIdentifier;

    public updateChatMessageEvent(String chatIdentifier, ChatMessage messageToBeAdded){
        this.chatIdentifier=chatIdentifier;
        this.messageToBeAdded=messageToBeAdded;
    }
    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {

    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
