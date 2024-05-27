package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;

//Interfaccia EVENT con tante sottoclassi che re-implementano il metodo Event.execute(client).
//In RMI lo farà il WRAPPEDOBSERVER nel metodo Update, in TCP lo farà il ClientSCK con i messaggi inviati dal
//ClientHandlerThread.
public class updateGoldCard1Event implements Event {
    /**
     * Constructor method
     * @param card is the card to be updated.
     */
    public updateGoldCard1Event(PlayableCard card){
        this.card=card;
    }
    private PlayableCard card;

    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateGoldCard1(card);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateGoldCard1(card);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
