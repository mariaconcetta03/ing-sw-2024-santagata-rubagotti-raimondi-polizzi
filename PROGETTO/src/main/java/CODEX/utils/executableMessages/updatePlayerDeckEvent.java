package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;

public class updatePlayerDeckEvent implements Event{
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
}
