package distributed.RMI;

import distributed.ClientGeneralInterface;
import utils.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements ClientGeneralInterface {
    private ServerRMIInterface SRMIInterface; //following the slides' instructions

    public static void main( String[] args )
    {
        try {
            new RMIClient().startConnectionWithServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new RMIClient().startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected RMIClient() throws RemoteException {
    }
    public void startConnectionWithServer() throws RemoteException, NotBoundException { //exceptions added automatically
        Registry registryServer = null;
        registryServer = LocateRegistry.getRegistry(SettingsClientToServer.SERVER_NAME,
                SettingsClientToServer.PORT);
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("ServerChat");
        this.SRMIInterface.sendMessage("Ciao! Sono il client e mi sono connesso con te");
        System.out.println ("Iniziamo la conversazione");
        this.SRMIinterface.sendEvent(new Event(Event.EventType.EVENT_1));
    }


    public void startClient() throws RemoteException {
            Registry registry = LocateRegistry.createRegistry(SettingsServerToClient.PORT);
            try {
                registry.bind("ClientChat", this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Client ready");
        }


    public void receiveEvent (Event event) throws RemoteException {
            System.out.println("Sono il client e sto ricevendo questo evento:");
            event.printEvent();
    }




    //aggiungere un metodo che permette di dire al Server 'ho finito il mio turno' così che il Server possa mettersi in contatto con un altro Client (un altro player)

    public static class SettingsServerToClient { //this is an attribute
        static int PORT = 50000; // free ports: from 49152 to 65535
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)

    }

    public static class SettingsClientToServer { //this is an attribute
        static int PORT = 50001; // free ports: from 49152 to 65535
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)

    }


}
