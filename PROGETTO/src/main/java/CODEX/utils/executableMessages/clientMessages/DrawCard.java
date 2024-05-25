package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.PlayableCard;

public class DrawCard implements ClientMessage{
    private final String nickname;
    private final PlayableCard selectedCard;
    public DrawCard(String nickname, PlayableCard selectedCard){
        this.nickname=nickname;
        this.selectedCard=selectedCard;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.drawCard(this.nickname,this.selectedCard);

    }
}
