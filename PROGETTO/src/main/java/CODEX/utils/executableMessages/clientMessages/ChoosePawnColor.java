package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.Pawn;

public class ChoosePawnColor implements ClientMessage{
    private final String chooserNickname;
    private final Pawn selectedColor ;
    public ChoosePawnColor(String chooserNickname, Pawn selectedColor){
        this.chooserNickname=chooserNickname;
        this.selectedColor=selectedColor;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.choosePawnColor(this.chooserNickname,this.selectedColor);

    }
}
