package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.Pawn;

/**
 * This class is used to represent a message which is sent when a client chooses a pawn color.
 */
public class ChoosePawnColor implements ClientMessage {
    private final String chooserNickname;
    private final Pawn selectedColor;


    /**
     * Class constructor
     *
     * @param chooserNickname is a player who is choosing the pawn color
     * @param selectedColor   is the selected color of the pawn
     */
    public ChoosePawnColor(String chooserNickname, Pawn selectedColor) {
        this.chooserNickname = chooserNickname;
        this.selectedColor = selectedColor;
    }


    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.choosePawnColor(this.chooserNickname, this.selectedColor);

    }
}
