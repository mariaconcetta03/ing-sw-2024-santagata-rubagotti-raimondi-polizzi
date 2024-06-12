package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

import java.util.List;

/**
 * This class is used to represent a message which is sent when a client wants to send a text in a chat
 */
public class SendMessage implements ClientMessage {
    private final String senderNickname;
    private final List<String> receiversNickname;
    private final String message;



    /**
     * This method is used to send a chat message
     * @param senderNickname sender
     * @param receiversNickname receiver / receivers
     * @param message text message
     */
    public SendMessage(String senderNickname, List<String> receiversNickname, String message) {
        this.senderNickname = senderNickname;
        this.receiversNickname = receiversNickname;
        this.message = message;
    }



    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        clientHandlerThread.sendMessage(this.senderNickname, this.receiversNickname, this.message);


    }
}
