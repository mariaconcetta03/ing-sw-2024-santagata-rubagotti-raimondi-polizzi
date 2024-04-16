package distributed.RMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject {
    private ServerRMIInterface SRMIinterface; //following the slides' instructions

    public static void main( String[] args )
    {
        try {
            new RMIClient().startConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected RMIClient() throws RemoteException {
    }
    public void startConnection() throws RemoteException, NotBoundException { //exceptions added automatically
        Registry registry;
        registry = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        this.SRMIinterface = (ServerRMIInterface) registry.lookup("ChatService");

    }

    public static class Settings { //this is an attribute
        static int PORT;
        static String SERVER_NAME;

    }

}
