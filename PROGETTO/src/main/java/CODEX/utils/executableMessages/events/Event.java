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
    /**
     * This method is used by rmi and makes update about game elements
     * @param client is a player
     * @param wrappedObserver is one of the other client
     * @return a boolean to check rmi connection (true when it starts the check)
     * @throws RemoteException if there's a problem in the connection
     */
     boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException;
    // the return value is needed to decide server side when to start checking rmi connection



    /**
     * This method makes update about game elements (only tcp)
     * @param client is a player
     */
     void executeSCK(ClientSCK client);
    // TCP doesn't need to throw RemoteException


    /**
     * This method is used by server side (sck - clientHandlerThread)
     * @return boolean to check sck connection (true when it starts the check)
     */
    boolean executeSCKServerSide();
    // the return value is needed to decide server side when to start checking tcp connection

}
