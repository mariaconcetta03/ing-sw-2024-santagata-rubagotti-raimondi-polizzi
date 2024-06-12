package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * This interface is useful for all the updates notified by the model
 */
public interface Event extends Serializable {
    boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException;
    // the return value is needed to decide server side when to start checking rmi connection

    void executeSCK(ClientSCK client);
    // TCP doesn't need to throw RemoteException

    boolean executeSCKServerSide();
    // the return value is needed to decide server side when to start checking tcp connection

}
