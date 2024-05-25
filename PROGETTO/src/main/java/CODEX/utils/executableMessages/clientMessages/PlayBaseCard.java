package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.PlayableCard;

public class PlayBaseCard implements ClientMessage{
    private final String nickname;
    private final PlayableCard baseCard;
    private final boolean orientation;
    public PlayBaseCard(String nickname, PlayableCard baseCard, boolean orientation){
        this.nickname=nickname;
        this.baseCard=baseCard;
        this.orientation=orientation;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        System.out.println("sono in execute di PlayBaseCard");
        clientHandlerThread.playBaseCard(this.nickname,this.baseCard,this.orientation);

    }
}
