package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class ChoosePawnColor implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
        case CHOOSE_PAWN_COLOR->{
                try {
                    choosePawnColor((String) sckMessage.getObj().get(0), (Pawn) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
         */
    }
}
