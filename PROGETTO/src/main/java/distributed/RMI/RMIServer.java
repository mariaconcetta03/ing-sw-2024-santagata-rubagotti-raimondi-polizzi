package distributed.RMI;

import controller.GameController;
import distributed.ClientGeneralInterface;
import org.model.Coordinates;
import org.model.ObjectiveCard;
import org.model.PlayableCard;
import org.model.Player;
import utils.*;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements ServerRMIInterface {
    // il server è unico e al suo interno ha un attributo Game Controller che mi
    // permette di dare il turno ai giocatori di un Game (senza unire giocatori di
    // più Game)


    /**
     * Class constructor
     * @throws RemoteException
     */
    RMIServer() throws RemoteException {
    }


    /**
     * Main method
     * This method calls the method "startServer". After the calling of this method the server
     * is able to receive the requests of the clients
     * @param args none
     * @throws RemoteException
     */
    public static void main (String[] args) throws RemoteException {
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


    /**
     * This method starts the server, so it can listen to the clients and receive
     * their requests (the clients will invoke functions on the server)
     * @throws RemoteException
     */
    @Override
    public void startServer() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(Settings.PORT);
        try {
            registry.bind("Server", this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server ready");
    }


    /**
     * This method calls the function into the ServerController
     * @param creator is the player who wants to create a new lobby
     * @param numOfPlayers is the number of player the creator decided can play in the lobby
     * @return the event "LOBBY_CREATED" when the lobby has been created on the server
     * @throws RemoteException
     */
    public Event createLobby (Player creator, int numOfPlayers) throws RemoteException {
        System.out.println("Sono il server, e ho ricevuto l'ordine di creare una nuova Lobby");
        // il giocatore invoca la funzione del server controller, ma comunicando prima col server generico e poi con quello RMI
        // se l'operazione va a buon fine...
        return new Event(Event.EventType.LOBBY_CREATED);
    }





// ---------------------------------- DA FARE COME SOPRA -------------------------------------
/*
    public Event addPlayerToLobby (Player p, int gameId){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event chooseNickname (Player chooser, String nickname){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
   public Event createGame (List<Player> gamePlayers){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event addPlayerToGame (Player player){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event startGame(){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event playCard(PlayableCard selectedCard, Coordinates position, boolean orientation){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event playBaseCard(PlayableCard selectedCard, Coordinates position, boolean orientation) {
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event drawCard(PlayableCard selectedCard){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event chooseObjectiveCard(Player chooser, ObjectiveCard selectedCard){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event sendMessage(Player sender, List<Player> receivers, String message){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event nextRound(){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }
    public Event endGame(){
        // the server is creating a lobby
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
    }




    public void sendEvent (Event Event) throws RemoteException {
        System.out.println("Sono il server, e sto stampando quello che mi hai mandato:");
        Event.printEvent();
        System.out.println("Sono il server, e ora ti mando l'Evento successivo");
        flag = true;

        Registry registryClient;
        registryClient = LocateRegistry.getRegistry(SettingsServerToClient.SERVER_NAME,
                SettingsServerToClient.PORT);

        System.out.println("Started connection with client! (now the server can send messages to the client)");

        try {
            Remote rmt = null;
            registryClient.rebind("ClientChat", rmt);
            cInterface = (ClientGeneralInterface) registryClient.lookup("ClientChat");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }


        // Looking up the registry for the remote object
        if (cInterface == null) {
            System.out.println("NULLO");
        }
        this.cInterface.receiveEvent(new Event(utils.Event.EventType.EVENT_2));
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
