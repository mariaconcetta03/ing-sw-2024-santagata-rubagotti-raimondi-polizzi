package CODEX.distributed.Socket;

import CODEX.controller.ServerController;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//to clear the ideas: this class isn't a socket but inside we initialize a ServerSocket and a Socket (the representation of the client's one)

//the update method called in the model after every modification should reach the game controller which has saved all the players of that game
//in the game controller we can save the clientHandlerThread (socket) of the players and the update should modify the socket
//then the class ClientSCK (the real client because it's not on the same virtual machine) reads the modified socket and change its view
public class ServerSCK extends UnicastRemoteObject {
    private final int port;
    private final ServerController serverController;

    public ServerSCK (int port,ServerController serverController) throws RemoteException {
        this.port = port; //we can ask the virtual machine which port is available
        this.serverController=serverController;
    }

    //costruttore per testare la prima versione TCP locale
    public ServerSCK (ServerController serverController) throws RemoteException {
        this.port=Settings.PORT;
        this.serverController=serverController;
    }

    public void startServer() throws IOException { //when the input/output stream is terminated we have this IOException
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            // serverSocket = new ServerSocket(port); //This port number can then be retrieved by calling getLocalPort
            InetAddress localAddress = InetAddress.getByName("127.0.0.1");
            serverSocket = new ServerSocket(1085, 10, localAddress);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            return;
        }
        System.out.println("TCP Server ready");
        System.out.println("Server IP address: "+Inet4Address.getLocalHost().getHostAddress());
        //The maximum queue length for incoming connection indications (a request to connect) is set to 50.
        //If a connection indication arrives when the queue is full, the connection is refused
        while (true) {
            Socket client = serverSocket.accept(); //accept() returns the client just accepted
            //serverController will be called only by ClientHandlerThread
            executor.submit(new ClientHandlerThread(client,serverController)); //we also pass a pointer to the server
            //when a Thread starts it's called its method run
        }
    }


    //il main è da cancellare perchè il serverSCK viene fatto partire dalla classe Server che gli passa anche il serverController
    //public static void main(String[] args) throws IOException {
    //    ServerSCK serverSCK = new ServerSCK(serverController);
    //    serverSCK.startServer();
    //}


    public static class Settings { //this is an attribute
        static int PORT = 50000; // free ports: from 49152 to 65535, 1099 default port for RMI registry
        static String SERVER_NAME = "127.0.0.1"; // LOCALHOST (every client has the same virtual server at this @address)
    }


}
