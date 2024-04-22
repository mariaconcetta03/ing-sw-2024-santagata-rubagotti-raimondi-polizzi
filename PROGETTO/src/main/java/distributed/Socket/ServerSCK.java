package distributed.Socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//to clear the ideas: this class isn't a socket but inside we initialize a ServerSocket and a Socket (the client's one)
public class ServerSCK extends UnicastRemoteObject { //we can't use the name ServerSocket because it already exists in java.net library
    private final int port;

    public ServerSCK (int port) throws RemoteException {
        super();
        this.port = port;
    }

    public void startServer() throws IOException { //when the input/output stream is terminated we have this IOException
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
            Socket client = serverSocket.accept(); //accept() returns the client just accepted
            //if we create a constructor of ClientHandlerThread by which we pass the serverSocket then the thread can send messages to the server
            executor.submit(new ClientHandlerThread(client));
        }
    }
    //we can add a method receiveMessage by which the ClientHandlerThread can send messages to the server (if we give the thread the server address as a parameter of the constructor)

    public static void main(String[] args) throws IOException {
        ServerSCK serverSCK = new ServerSCK(1234); //that is the port found in the slides
        serverSCK.startServer();
    }

}
