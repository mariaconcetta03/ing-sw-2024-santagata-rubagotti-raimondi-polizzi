package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

/**
 * This class is used to represent a message which is sent when a client chooses a nickname
 */
public class ChooseNickname implements ClientMessage{
    private final String nickname;


    /**
     * Class constructor
     * @param nickname chosen by a player
     */
    public ChooseNickname(String nickname){
        this.nickname = nickname;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.chooseNickname(this.nickname);


    }
}
