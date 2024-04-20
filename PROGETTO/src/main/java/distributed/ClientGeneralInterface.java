package distributed;

import utils.Event;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface ClientGeneralInterface {
    public void startConnectionWithServer() throws RemoteException, NotBoundException;
    public void receveEvent (Event event) throws RemoteException;
    public void startClient() throws RemoteException;

}
