package distributed.Socket;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocket extends UnicastRemoteObject{
    private final int port;

    public ServerSocket (int port) throws RemoteException {
        super();
        this.port = port;
    }
    
    public void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            return;
        }
        System.out.println("Server ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(new ClientHandlerThread(socket));
            } catch(IOException e) {
                break; // Entrerei qui se serverSocket venisse chiuso
            }
        }
        executor.shutdown();
    }

    private Socket accept() { //to be implemented
        return null;
    }


    public static void main(String[] args) throws RemoteException {
        ServerSocket serverSocket = new ServerSocket(1234);
        serverSocket.startServer();
        }

}
