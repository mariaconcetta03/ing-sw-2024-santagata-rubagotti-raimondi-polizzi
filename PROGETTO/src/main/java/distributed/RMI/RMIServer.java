package distributed.RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements ServerRMIInterface{
    protected RMIServer() throws RemoteException {

    }
    public static void main (String[] args)
    {
        try {
            new RMIServer().startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startServer() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(Settings.PORT);
        try {
            registry.bind("ChatService", this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server ready");
    }

    public static class Settings {
        static int PORT;
        static String SERVER_NAME; //this is an IP address (such as "127.0.0.1")

    }
}
