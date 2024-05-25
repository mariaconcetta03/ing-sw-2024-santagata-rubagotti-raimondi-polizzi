package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class ChooseNickname implements ClientMessage{
    private final String nickname;
    public ChooseNickname(String nickname){
        this.nickname=nickname;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.chooseNickname(this.nickname);


    }
}
