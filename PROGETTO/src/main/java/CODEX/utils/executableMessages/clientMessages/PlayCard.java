package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.Coordinates;
import CODEX.org.model.PlayableCard;

/**
 * This class is used to represent a message which is sent when a client wants to play a card
 */
public class PlayCard implements ClientMessage {
    private final String nickname;
    private final PlayableCard selectedCard;
    private final Coordinates position;
    private final boolean orientation;


    /**
     * This method is used to play a card
     *
     * @param nickname     player which is playing
     * @param selectedCard card to play
     * @param position     where to play
     * @param orientation  front or back
     */
    public PlayCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) {
        this.nickname = nickname;
        this.selectedCard = selectedCard;
        this.position = position;
        this.orientation = orientation;
    }


    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.playCard(this.nickname, this.selectedCard, this.position, this.orientation);

    }
}
