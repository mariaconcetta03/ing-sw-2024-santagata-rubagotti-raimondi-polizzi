package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.ObjectiveCard;


/**
 * This class is used to represent a message which is sent when a client chooses an objective card
 */
public class ChooseObjectiveCard implements ClientMessage {
    private final String chooserNickname;
    private final ObjectiveCard selectedCard;


    /**
     * Class constructor
     *
     * @param chooserNickname is a player who is choosing an objective card
     * @param selectedCard    is the objective card chosen
     */
    public ChooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) {
        this.chooserNickname = chooserNickname;
        this.selectedCard = selectedCard;
    }


    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.chooseObjectiveCard(this.chooserNickname, this.selectedCard);


    }
}
