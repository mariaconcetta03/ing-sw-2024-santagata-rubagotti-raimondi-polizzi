package distributed.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRMIInterface extends Remote {
    void startServer() throws RemoteException;

}
