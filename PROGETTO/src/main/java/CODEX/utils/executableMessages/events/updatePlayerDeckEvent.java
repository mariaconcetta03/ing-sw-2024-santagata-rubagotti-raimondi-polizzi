package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class updatePlayerDeckEvent implements Event {
    private String playerNickname;
    private PlayableCard[] playerDeck;

    private List<PlayableCard> convertedArray; //an array can't be transmitted using a TCP stream

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
        //the name of the method will be changed to become more comprehensible
        PlayableCard[] RewrittenplayerDeck=new PlayableCard[3];
        playerDeck[0]= convertedArray.get(0);
        playerDeck[1]=convertedArray.get(1);
        playerDeck[2]=convertedArray.get(2);
        try {
            client.updatePlayerDeck(playerNickname, RewrittenplayerDeck);
        }catch (RemoteException ignored){ //è il modo migliore di gestire la cosa?

        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        List<PlayableCard> list=new ArrayList<>();
        PlayableCard[] playableCards=this.playerDeck;
        for (PlayableCard c:playableCards){
            list.add(c);
        }
        this.convertedArray=new ArrayList<PlayableCard>(list);
        return false;

    }
}
