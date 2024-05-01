package distributed.Socket;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.ObjectInputStream;

//to clear the ideas: this class isn't a socket but has a socket inside.
// l'update andrà a modificare il flusso (BUFFER) tra server e client che può essere letto dalla socket di questa classe
public class ClientSCK {//here we can have the view attributes that would be modified after we read the buffer
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public ClientSCK(String address, int port) throws IOException { //we call this constructor after we ask the IP address and the port of the server
        Socket socket = new Socket();
        //when we connect the class ServerSCK can accept our connection and then create a thread (ClientHandlerThread)
        socket.connect(new InetSocketAddress(address, port), 1000); //the address and the port of the server
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream()); //that would be the output stream of the server
    }
    //view attributes: ...

    //public SocketMessage receivedMessageC() throws IOException, ClassNotFoundException {
    //    return (SocketMessage) inputStream.readObject();
    //}
}
