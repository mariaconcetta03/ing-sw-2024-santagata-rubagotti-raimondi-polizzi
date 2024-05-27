package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Event extends Serializable {

    void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException;


     void executeSCK(ClientGeneralInterface client); //TCP doesn't need to throw RemoteException + in updatePlayerDeckEvent we have an execute method different from RMI


    //it checks Boolean startCheckConnection (returns true if this Boolean is true). It's true when we are in updateGameState and the new state is 'STARTED'
    //+ in updatePlayerDeck writes the attribute convertedArray (an array can't be transmitted using a TCP stream)
    boolean executeSCKServerSide(); //needed to convert an array into a list and to decide server side when to start checking the connection


}
