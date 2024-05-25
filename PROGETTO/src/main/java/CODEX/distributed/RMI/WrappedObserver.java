package CODEX.distributed.RMI;


import CODEX.utils.Observable;
import CODEX.utils.Observer;
import CODEX.utils.executableMessages.events.Event;

import java.rmi.RemoteException;


/**
 * This class represents a CLIENT as an OBSERVER
 */
public class WrappedObserver implements Observer {
    private ClientRMIInterface remoteClient;
    private String nickname;


    // ----------------- C O M E   A V V I E N E   L' U P D A T E ? --------------------
    // OBSERVABLE SI ACCORGE DEL CAMBIAMENTO (CHE PASSA DALLA VIEW, AL CONTROLLER, AL
    // MODEL, CHE è OBSERVABLE). A QUESTO PUNTO, INVOCA LE FUNZIONI DI UPDATE SUI VARI
    // OBSERVERS. QUESTI IN RMI, GRAZIE ALLA CLASSE WRAPPED OBSERVER, VANNO AD INVOCARE
    // DEI METODI DI CLIENTRMI CHE RICEVONO L'OGGETTO AGGIORNATO
    // ---------------------------------------------------------------------------------



    /**
     * Class constructor
     * @param ro the RMIClient
     */
    public WrappedObserver(ClientRMIInterface ro) {
        remoteClient = ro;
    }



    /**
     * This is an update method
     * @param obs is the observable who called the notify
     */
    public void update(Observable obs, Event e) throws RemoteException {
        e.execute(remoteClient);
    }



    /**
     * Setter method
     * @param nickname of the WrappedObserver
     */
    @Override
    public void setNickname(String nickname) throws RemoteException  {
       this.nickname = nickname;
    }



    /**
     * Getter method
     * @return nickname of the WrappedObserver
     */
    @Override
    public String getNickname() throws RemoteException {
        return this.nickname;
    }

}