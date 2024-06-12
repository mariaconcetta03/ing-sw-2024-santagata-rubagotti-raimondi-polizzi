package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.PlayableCard;

/**
 * This class is used to represent a message which is sent when a client wants to play a base card
 */
public class PlayBaseCard implements ClientMessage{
    private final String nickname;
    private final PlayableCard baseCard;
    private final boolean orientation;



    /**
     * This method is used to play a base card
     * @param nickname player
     * @param baseCard card to play
     * @param orientation true or false = front or back
     */
    public PlayBaseCard(String nickname, PlayableCard baseCard, boolean orientation){
        this.nickname=nickname;
        this.baseCard=baseCard;
        this.orientation=orientation;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.playBaseCard(this.nickname,this.baseCard,this.orientation);

    }
}
