package distributed.Socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//to clear the ideas: this class isn't a socket but inside we initialize a ServerSocket and a Socket (the representation of the client's one)

//the update method called in the model after every modification should reach the game controller which has saved all the players of that game
//this server cannot call the update because it doesn't decide to which game each player goes
//in the game controller we can save the sockets of the players and the update should modify the socket
//then the class ClientSCK (the real client because it's not on the same virtual machine) reads the modified socket and change its view
public class ServerSCK extends UnicastRemoteObject { //we can't use the name ServerSocket because it already exists in java.net library
    private final int port;

    public ServerSCK (int port) throws RemoteException {
        super();
        this.port = port; //we can ask the virtual machine which port is available
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
