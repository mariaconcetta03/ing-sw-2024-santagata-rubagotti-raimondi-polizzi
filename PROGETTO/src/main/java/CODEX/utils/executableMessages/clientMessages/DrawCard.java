package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.PlayableCard;

/**
 * This class is used to represent a message which is sent when a client wants to draw a card
 */
public class DrawCard implements ClientMessage {
    private final String nickname;
    private final PlayableCard selectedCard;


    /**
     * This method is used to draw a card
     *
     * @param nickname     player
     * @param selectedCard to draw
     */
    public DrawCard(String nickname, PlayableCard selectedCard) {
        this.nickname = nickname;
        this.selectedCard = selectedCard;
    }


    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.drawCard(this.nickname, this.selectedCard);

    }
}
