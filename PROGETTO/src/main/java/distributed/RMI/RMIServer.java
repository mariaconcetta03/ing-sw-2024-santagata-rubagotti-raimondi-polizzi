package distributed.RMI;

import distributed.ClientGeneralInterface;
import org.model.Chat;
import utils.Event;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements ServerRMIInterface{
    //il server è unico e al suo interno ha un attributo Game Controller che mi permette di dare il turno ai giocatori di un Game (senza unire giocatori di più Game)

    private ClientGeneralInterface cInterface;
    protected RMIServer() throws RemoteException {
    }
    public static void main (String[] args)
    {
        try {
            new RMIServer().startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new RMIServer().StartConnectionWithClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startServer() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(SettingsClientToServer.PORT);
        try {
            registry.bind("ServerChat", this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server ready");
    }


    public void StartConnectionWithClient () throws NotBoundException, RemoteException {
        Registry registry = null;
        while (registry == null) {
            registry = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                    SettingsServerToClient.PORT);
        }
        registry.lookup("ClientChat");

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        // Looking up the registry for the remote object
        this.cInterface = (ClientGeneralInterface) registry.lookup("ClientChat");
   }


    public void sendMessage (String msg) throws RemoteException {
        System.out.println(msg);
    }


    public void sendEvent (Event event) throws RemoteException {
        System.out.println("Sono il server, e sto stampando quello che mi hai mandato:");
        event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'evento successivo");
        this.cInterface.receveEvent(new Event(Event.EventType.EVENT_2));
    }

    public static class SettingsServerToClient { //this is an attribute
        static int PORT = 50000; // free ports: from 49152 to 65535
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)

    }

    public static class SettingsClientToServer { //this is an attribute
        static int PORT = 50001; // free ports: from 49152 to 65535
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)

    }
}
