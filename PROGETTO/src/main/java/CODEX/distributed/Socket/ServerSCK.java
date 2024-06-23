package CODEX.distributed.Socket;

import CODEX.controller.ServerController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * This class represents the Socket server, which is unique.
 */

public class ServerSCK extends UnicastRemoteObject {
    private final ServerController serverController;
    private String SERVER_NAME = "127.0.0.1"; // LOCALHOST


    /**
     * Class constructor
     *
     * @param serverController serverController
     * @throws RemoteException if an exception happens while communicating with the remote
     */
    public ServerSCK(ServerController serverController) throws RemoteException {
        this.serverController = serverController;
    }


    /**
     * This method starts the server socket and never returns (there is a non-ending while loop).
     * The maximum queue length for incoming connection is set to 50.
     * If a connection request arrives when the queue is full, the connection is refused
     *
     * @throws IOException when the input/output stream is terminated
     */
    public void startServer() throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            InetAddress serverAddress = InetAddress.getByName(this.SERVER_NAME);
            serverSocket = new ServerSocket(1085, 10, serverAddress);
        } catch (IOException e) {
            System.err.println(e.getMessage()); //port not available
            return;
        }
        System.out.println("TCP Server ready");
        while (true) {
            Socket client = serverSocket.accept();
            executor.submit(new ClientHandlerThread(client, serverController));
        }
    }


    /**
     * Setter method
     *
     * @param serverName server address
     */
    public void setServerName(String serverName) {
        this.SERVER_NAME = serverName;
    }
}

