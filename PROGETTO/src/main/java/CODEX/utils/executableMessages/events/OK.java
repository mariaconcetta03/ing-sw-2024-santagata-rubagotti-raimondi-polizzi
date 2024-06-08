package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.rmi.RemoteException;

public class OK implements Event{
    private final String nickname;

    public OK(String nickname){
        this.nickname=new String(nickname);
    }
    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        //guardare ServerOk in serverMessages
        client.okEventExecute(this.nickname);
        return true;
    }

    @Override
    public void executeSCK(ClientGeneralInterface client) {
        //niente

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;
    }
}
