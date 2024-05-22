package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
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
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateGoldCard1(card);
    }
}
