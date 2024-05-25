package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.ObjectiveCard;

public class ChooseObjectiveCard implements ClientMessage{
    private final String chooserNickname;
    private final ObjectiveCard selectedCard;
    public ChooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard){
        this.chooserNickname=chooserNickname;
        this.selectedCard=selectedCard;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.chooseObjectiveCard(this.chooserNickname,this.selectedCard);


    }
}
