package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.ChatMessage;

import java.rmi.RemoteException;

public class updateChatEvent implements Event{
    ChatMessage message;
    public updateChatEvent(ChatMessage message){
        this.message=message;
    }

    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateChat(message);
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
