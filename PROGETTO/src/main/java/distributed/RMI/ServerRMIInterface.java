package distributed.RMI;

import utils.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerRMIInterface extends Remote {
    void startServer() throws RemoteException;
    void sendMessage(String msg) throws RemoteException;
    void sendEvent(Event event) throws RemoteException;
}
