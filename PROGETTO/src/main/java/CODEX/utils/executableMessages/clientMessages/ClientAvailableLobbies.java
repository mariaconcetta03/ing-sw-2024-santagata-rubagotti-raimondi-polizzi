package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.distributed.messages.SCKMessage;
import CODEX.utils.executableMessages.serverMessages.ServerAvailableLobbies;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ClientAvailableLobbies implements ClientMessage{
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {
        List<Object> list = new ArrayList<>();
        try {
            list.addAll(clientHandlerThread.getServerController().getAllGameControllers().keySet());
        } catch (RemoteException ignored) {
        }
        ServerMessage serverMessage=new ServerAvailableLobbies(list);
        clientHandlerThread.writeTheStream(new SCKMessage(serverMessage));
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
