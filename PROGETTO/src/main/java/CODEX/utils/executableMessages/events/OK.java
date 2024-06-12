package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import java.rmi.RemoteException;

/**
 * This event is useful to communicate that an operation was completed successfully
 * and the client when receives it finishes his waiting activity
 */
public class OK implements Event{
    private final String nickname; //this identifies the right receiver of this OK


    public OK(String nickname){
        this.nickname=new String(nickname);
    }


    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.okEventExecute(this.nickname);
        return true;
    }


    @Override
    public void executeSCK(ClientGeneralInterface client) {
    }


    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
