package distributed.RMI;

import distributed.ClientGeneralInterface;
import org.model.Player;
import utils.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class RMIClient extends UnicastRemoteObject implements ClientGeneralInterface {
    private ServerRMIInterface SRMIInterface; //following the slides' instructions


    /**
     * Class constructor
     * @throws RemoteException
     */
    protected RMIClient() throws RemoteException {
    }



    /**
     * Main method
     * This method calls the method "startConnectionWithServer()". After the calling of this method the server
     * is able to receive the requests of the clients
     * @param args none
     */
    public static void main( String[] args )
    {
        try {
            new RMIClient().createLobby();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * This method starts the connection with the server
     * @throws RemoteException
     * @throws NotBoundException
     */
    public Event createLobby() throws RemoteException, NotBoundException { //exceptions added automatically
        Registry registryServer = null;
        Event event;
        registryServer = LocateRegistry.getRegistry(Settings.SERVER_NAME,
                Settings.PORT);
        // Looking up the registry for the remote object
        this.SRMIInterface = (ServerRMIInterface) registryServer.lookup("Server");
        event = this.SRMIInterface.createLobby(new Player(), 4);
        event.printEvent();
        return event;
    }



    /**
     * Settings class
     * It is about port and ip address of the client with which the server communicates
     */
    public static class Settings { //this is an attribute
        static int PORT = 50001; // free ports: from 49152 to 65535
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }

}
