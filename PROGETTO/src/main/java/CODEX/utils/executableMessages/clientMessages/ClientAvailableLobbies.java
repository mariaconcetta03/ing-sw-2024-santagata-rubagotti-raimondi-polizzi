package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;

public class ClientAvailableLobbies implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        /*
        case AVAILABLE_LOBBY -> {
                try {
                    System.out.println("sono in available lobby");
                    List<Object> list = new ArrayList<>();
                    list.addAll(serverController.getAllGameControllers().keySet());
                    writeTheStream(new SCKMessage(list, Event.AVAILABLE_LOBBY));
                }catch (RemoteException ignored){}
            }
         */

    }
}
