package CODEX.distributed;

import CODEX.controller.ServerController;
import CODEX.distributed.RMI.RMIServer;
import CODEX.distributed.Socket.ServerSCK;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;


/**
 * This is the server. It launches both RMI and TCP Servers.
 */
public class ServerLauncher {


    /**
     * Class constructor
     */
    public ServerLauncher() {
    }


    /**
     * Main method
     *
     * @param args unused
     */
    public static void main(String[] args) {
        ServerController serverController = new ServerController();

        String serverAddress = "";
        System.out.println("Insert Server's IP address (leave blank for localHost): ");
        Scanner sc = new Scanner(System.in);
        serverAddress = sc.nextLine();
        if (serverAddress.equals("")) {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        } else {
            System.setProperty("java.rmi.server.hostname", serverAddress);
        }

        try {
            RMIServer rmiServer = new RMIServer(serverController);
            rmiServer.startServer();
        } catch (RemoteException e) {
            System.out.println("Error while starting the RMI Server. Try again. Closing...");
            System.exit(-1);
        }
        try {
            ServerSCK socketServer = new ServerSCK(serverController);
            socketServer.setServerName(serverAddress);
            socketServer.startServer();
        } catch (IOException e) {
            System.out.println("Error while starting the TCP Server. Try again. Closing...");
            System.exit(-1);
        }
    }
}
