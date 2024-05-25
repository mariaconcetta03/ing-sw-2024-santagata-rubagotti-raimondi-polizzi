package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class updatePlayerDeckEvent implements Event {
    private String playerNickname;
    private PlayableCard[] playerDeck;


    public updatePlayerDeckEvent(String playerNickname, PlayableCard[] playerDeck) {
        this.playerNickname = playerNickname;
        this.playerDeck = playerDeck;
    }


    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updatePlayerDeck(playerNickname, playerDeck);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updatePlayerDeck(playerNickname, playerDeck);
        }catch (RemoteException ignored){ //è il modo migliore di gestire la cosa?

        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
