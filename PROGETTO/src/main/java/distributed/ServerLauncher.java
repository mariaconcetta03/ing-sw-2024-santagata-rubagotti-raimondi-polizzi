package distributed;

import controller.ServerController;
import distributed.RMI.RMIServer;
import distributed.Socket.ServerSCK;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.*;


/**
 * This is the server. It launches the RMI and TCP Servers.
 */
public class ServerLauncher extends UnicastRemoteObject {

    public ServerLauncher() throws RemoteException {
    }
    public static void main(String[] args) throws IOException {
        ServerController serverController=new ServerController();

        RMIServer rmiServer= new RMIServer(serverController);
        rmiServer.startServer();

        ServerSCK socketServer= new ServerSCK(serverController);
        socketServer.startServer();
    }
}
