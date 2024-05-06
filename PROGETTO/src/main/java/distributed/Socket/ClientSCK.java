package distributed.Socket;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import distributed.messages.*;
import org.model.Board;
import org.model.ObjectiveCard;
import org.model.Player;
import utils.Event;


/**
 * This class represents the Client who chose TCP as network protocol.
 * It listens to the SCKMessage sent by the ClientHandlerThread through the socket
 * and performs action to update the view. It also sends User input to the ClientHandlerThread
 * through the socket to be processed
 */
public class ClientSCK {

    private Board board;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final Thread threadCheckConnection;
    private Player player;
    private List<Player> playersInTheGame;
    private ObjectiveCard commonObjective1, commonObjective2;

    public ClientSCK(String address, int port) throws IOException, ClassNotFoundException { //we call this constructor after we ask the IP address and the port of the server
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(address, port), 1000); //the address and the port of the server

        //in this way the stream is converted into objects
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream()); //what ClientHandlerThread writes in its socket's output stream ends up here

        //to control the status of the connection (a player can leave the game without any advice)
        threadCheckConnection= new Thread(()-> { //ci serve qualcosa su cui fare la syn?
            try { //dobbiamo usare il filter?? per leggere il PingMessage
                PingMessage pingMessage = (PingMessage) this.inputStream.readObject(); //we receive 'ARE_YOU_STILL_CONNECTED'
                outputStream.writeObject(PingMessage.YES_STILL_CONNECTED);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        },"CheckConnection"); //to be started when ClientHandlerThread stars because we need n clients to create a game and we want to be sure the all the clients are still connected
        threadCheckConnection.start();


        while (true) {
            try {
                SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject(); //così non abbiamo più bisogno della funzione receiveMessage
                modifyClientSide(sckMessage); //questo è bloccante-> meglio utilizzare un thread...a meno che non vogliamo fare una modifica alla volta
            } catch (Exception e) {
                break; //se per esempio il flusso viene interrotto (dovrebbe venire lanciata un eccezione di Input/Output)
            }

        }

        //we have to read this stream every time there is a server update to the client (->in the Thread local to server we modify this stream)
        //public SCKMessage receivedMessage () throws IOException, ClassNotFoundException {
        //    //the socket stream has been changed in the Thread (locally to the server)
        //    return (SCKMessage) inputStream.readObject(); //we are reading the object written in the inputStream
        //}

    }

    /**
     * This method allows the Client to send, through the socket, a message to be read (using its input stream) by the ClientHandlerThread.
     * @param sckMessage is the message containing objects and Event relative to the action to perform
     * @throws IOException in case the Server is unreachable we shut down the Client.
     */
    public void sendMessage(SCKMessage sckMessage) throws IOException {
        try {
            outputStream.writeObject(sckMessage);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            System.err.println("Server not available!");
            System.exit(-1); //this is a shutdown of the VirtualMachine
        }
    }


    //leggiamo l'evento per capire di che update si tratta e poi aggiorniamo quello che ci dice di aggiornare l'evento e chiamiamo infine sendMessage
    //non è l'update dei listeners (quello è in ClientHandlerThread: scriverà sull'input della socket)
    //con questo update andiamo a modificare le cose locali al client
    public void modifyClientSide(SCKMessage sckMessage) throws IOException, ClassNotFoundException {
        switch (sckMessage.getMessageEvent()) {
            case ALL_CONNECTED -> {
                getModel();
                sendMessage(new SCKMessage(null, Event.START)); //null is referred to the objects sent. 'START' to tell the server that this client is ready
            }
        }
    }

    public void getModel() throws IOException, ClassNotFoundException {
        //sendMessage(new SCKMessage(Event.ASK_SERVER_MODEL, Event.GAME_BOARD)); il 1 parametro è una List di Object non evento!
        this.board = (Board) this.inputStream.getObjectInputFilter(); // we need a filter because we may obtain a SCKMessage instead of a Board
        //sendMessage(new SCKMessage(Event.ASK_SERVER_MODEL, Event.GAME_PLAYERS));
        this.playersInTheGame = (List<Player>) this.inputStream.getObjectInputFilter(); //da riguardare bene filter (è da usare anche per l'estrazione di SCKMessage?)
        // e altro.... come i goal comuni
    }

}




