package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Event extends Serializable {

    void execute(ClientGeneralInterface client) throws RemoteException;


     void executeSCK(ClientGeneralInterface client); //TCP doesn't need to throw RemoteException + in updatePlayerDeckEvent we have an execute method different from RMI


    //it checks Boolean startCheckConnection (returns true if this Boolean is true). It's true when we are in updateGameState and the new state is 'STARTED'
    //+ in updatePlayerDeck writes the attribute convertedArray (an array can't be transmitted using a TCP stream)
    boolean executeSCKServerSide(); //needed to convert an array into a list and to decide server side when to start checking the connection


    //ClientActionsInterface client: we can use it to do actions server-side (after receiving a msg from ClientSCK)
    //when we receive an update from the model in ClientHandlerThread, we write null in the place of ClientActionsInterface parameter

    //ATTENZIONE: ClientActionsInterface client sarà usato veramente in nuove sottoclassi di eventi ancora da aggiungere

    //CASI SCK ANCORA DA DECIDERE: messaggi di errore e ping (potremmo fare che nella classe SCKmessage abbiamo sia un attributo che implementa queste due rimanenti cose che un attributo Event)
}
