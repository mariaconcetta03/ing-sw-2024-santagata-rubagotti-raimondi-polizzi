package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.util.List;

public class SendMessage implements ClientMessage{
    private final String senderNickname;
    private final List<String> receiversNickname;
    private final String message;
    public SendMessage(String senderNickname, List<String> receiversNickname, String message){
        this.senderNickname=senderNickname;
        this.receiversNickname=receiversNickname;
        this.message=message;
    }
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.sendMessage(this.senderNickname,this.receiversNickname,this.message);


    }
}
