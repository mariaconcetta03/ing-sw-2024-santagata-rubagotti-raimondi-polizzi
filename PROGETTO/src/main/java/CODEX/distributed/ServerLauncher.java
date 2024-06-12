package CODEX.distributed;

import CODEX.controller.ServerController;
import CODEX.distributed.RMI.RMIServer;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.distributed.Socket.ServerSCK;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.Scanner;


/**
 * This is the server. It launches both RMI and TCP Servers.
 */
public class ServerLauncher extends UnicastRemoteObject {



    /**
     * Class constructor
     * @throws RemoteException
     */
    public ServerLauncher() throws RemoteException {}



    /**
     * Main method
     * @param args unused
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ServerController serverController = new ServerController();

        String serverAddress = "";
        System.out.println("Insert Server's IP address (leave blank for localHost): ");
        Scanner sc = new Scanner(System.in);
        serverAddress = sc.nextLine();
        if (serverAddress.equals("")) { // if string is empty then it uses localhost
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            System.out.println("Server address: 127.0.0.1");
        } else {
            System.setProperty("java.rmi.server.hostname", serverAddress); // choosing right address
            System.out.println("Server address: " + serverAddress);
        }

        RMIServer rmiServer= new RMIServer(serverController);
        rmiServer.startServer();

        ServerSCK socketServer= new ServerSCK(serverController);
        ServerSCK.Settings.setServerName(serverAddress);
        socketServer.startServer();
    }
}
