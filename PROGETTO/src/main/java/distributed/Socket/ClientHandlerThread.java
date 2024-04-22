package distributed.Socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandlerThread implements Runnable { //this is a Thread (it isn't blocking)
    private Socket socket;
    //what is the difference with Scanner and Printer? (can we use the print function with these below?)
    //private final ObjectOutputStream output; //used to send
    //private final ObjectInputStream input; //used to receive
    ClientSCK clientSCK= null; //that's not a real socket but contains all the implemented method of the ClientGeneralInterface

    public ClientHandlerThread(Socket client) {
        this.socket = client; //the client can communicate with the server by this thread using this socket
        //here we would have to initialize
        //this.output = new ObjectOutputStream(client.getOutputStream());
        //this.input = new ObjectInputStream(client.getInputStream());
    }
    //to decide what to do we can ask the client controller (which is the one who have to initialize ClientSCK)
    public void run() { //that's not the only method we can have in this thread
        try { //here we can match the user requests with the ClientSCK methods
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            // Leggo e scrivo nella connessione finche' non ricevo "quit"
            while (true) {
                String line = in.nextLine();
                if (line.equals("quit")) {
                    break;
                } else {
                    out.println("Received: " + line);
                    out.flush();
                }
            }
            // Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    //we can add method in which we can control the status of the connection (asking it to the controller)
}

