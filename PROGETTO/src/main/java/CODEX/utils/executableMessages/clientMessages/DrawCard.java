package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class DrawCard implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
        case DRAW_CARD->{
                try {
                    drawCard((String) sckMessage.getObj().get(0), (PlayableCard) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
         */
    }
}
