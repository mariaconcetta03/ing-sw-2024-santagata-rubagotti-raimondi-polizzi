package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Event extends Serializable {
    void execute(ClientGeneralInterface client) throws RemoteException;
}
