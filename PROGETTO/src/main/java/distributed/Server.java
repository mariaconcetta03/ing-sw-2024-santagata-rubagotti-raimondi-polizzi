package distributed;

import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.List;

import org.model.*;


/**
 * SINGLETON CLASS
 * This is the only one server
 */
public class Server extends UnicastRemoteObject {

    private static Server instance;

    public static Server getInstance() throws RemoteException {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    Server() throws RemoteException {
    }
}
