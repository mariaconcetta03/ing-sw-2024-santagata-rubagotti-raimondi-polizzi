package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class LeaveGame implements ClientMessage{ //poi questo andra tolto perchè se il client vuole lasciare il gioco la tui fa exit e si interrompe la connessione
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
         case LEAVE_GAME->{
                try {
                    leaveGame((String) sckMessage.getObj().get(0));
                    //running=false;
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
         */
    }
}
