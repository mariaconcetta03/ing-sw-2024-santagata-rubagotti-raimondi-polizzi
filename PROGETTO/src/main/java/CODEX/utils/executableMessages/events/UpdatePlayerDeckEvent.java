package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;


/**
 * This event is useful to communicate that the player deck has changed
 */
public class UpdatePlayerDeckEvent implements Event {
    private String playerNickname;
    private PlayableCard[] playerDeck;


    public UpdatePlayerDeckEvent(String playerNickname, PlayableCard[] playerDeck) {
        this.playerNickname = playerNickname;
        this.playerDeck = playerDeck;
    }


    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updatePlayerDeck(playerNickname, playerDeck);
        return false;
    }
    @Override
    public void executeSCK(ClientSCK client) {

            client.updatePlayerDeck(playerNickname, playerDeck);

    }

    @Override
    public boolean executeSCKServerSide() {
        return false;

    }
}
