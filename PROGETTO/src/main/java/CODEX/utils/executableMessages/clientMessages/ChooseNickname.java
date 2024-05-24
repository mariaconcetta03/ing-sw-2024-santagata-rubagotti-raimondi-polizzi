package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class ChooseNickname implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
        case CHOOSE_NICKNAME->{
                try {
                    chooseNickname((String)sckMessage.getObj().get(0));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
         */

    }
}
