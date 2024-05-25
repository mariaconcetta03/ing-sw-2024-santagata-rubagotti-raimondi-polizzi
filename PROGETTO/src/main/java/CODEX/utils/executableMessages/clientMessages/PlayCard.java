package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.Coordinates;
import CODEX.org.model.PlayableCard;

public class PlayCard implements ClientMessage{
    private final String nickname;
    private final PlayableCard selectedCard;
    private final Coordinates position;
    private final boolean orientation;

    public PlayCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation){
        this.nickname=nickname;
        this.selectedCard=selectedCard;
        this.position=position;
        this.orientation=orientation;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.playCard(this.nickname,this.selectedCard,this.position,this.orientation);

    }
}
