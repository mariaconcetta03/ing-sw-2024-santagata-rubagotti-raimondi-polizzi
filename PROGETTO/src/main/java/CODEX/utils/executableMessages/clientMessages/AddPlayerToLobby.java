package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class AddPlayerToLobby implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
        case ADD_PLAYER_TO_LOBBY->{
                System.out.println("sono in ADD_PLAYER_TO_LOBBY");
                try {
                    addPlayerToLobby((String) sckMessage.getObj().get(0), (Integer) sckMessage.getObj().get(1));
                }catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
         */
    }
}
