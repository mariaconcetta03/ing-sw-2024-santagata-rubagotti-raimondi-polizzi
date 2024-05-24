package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class LeaveGame implements ClientMessage{ //poi questo andra tolto perchè se il client vuole lasciare il gioco la tui fa exit e si interrompe la connessione
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {

    }
}
