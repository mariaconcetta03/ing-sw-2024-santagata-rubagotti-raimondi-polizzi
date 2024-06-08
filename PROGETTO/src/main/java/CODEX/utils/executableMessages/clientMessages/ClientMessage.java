package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.distributed.Socket.ClientHandlerThread;
import CODEX.org.model.Game;
import CODEX.utils.executableMessages.events.Event;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface ClientMessage extends Serializable {
    void execute(ClientHandlerThread clientHandlerThread);

}
