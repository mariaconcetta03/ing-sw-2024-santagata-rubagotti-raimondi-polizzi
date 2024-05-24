package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class SendMessage implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
        case SEND_MESSAGE->{
                try {
                    sendMessage((String) sckMessage.getObj().get(0), (List<String>) sckMessage.getObj().get(1), (String) sckMessage.getObj().get(2));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
         */

    }
}
