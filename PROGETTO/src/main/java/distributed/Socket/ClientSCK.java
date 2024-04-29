package distributed.Socket;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.ObjectInputStream;

//o clear the ideas: this class isn't a socket. We use its methods through the client controller intermediation (which can be called by the ClientHandlerThread)
// l'update andrà a modificare il flusso (BUFFER) tra server e client che può essere letto dalle socket
public class ClientSCK {//this class has to be initialized by the client controller
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public ClientSCK(String address, int port) throws IOException { //with the construct we connect this client with the server
        Socket socket = new Socket();
        //when we connect the class ServerSCK can accept our connection and then create a thread (ClientHandlerThread)
        socket.connect(new InetSocketAddress(address, port), 1000); //the address and the port of the server
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream()); //that would be the output stream of the server
    }
    //this should be an implementation of ClientGeneralInterface but we have to make the user decide which method to pick (with another method maybe...)
}
