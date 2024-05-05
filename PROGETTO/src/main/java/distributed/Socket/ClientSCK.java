package distributed.Socket;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import distributed.messages.*;
import org.model.Board;
import org.model.Player;
import utils.Event;




//to clear the ideas: this class isn't a socket but has a socket inside.
// l'update andrà a modificare il flusso (BUFFER) tra server e client che può essere letto dalla socket di questa classe
public class ClientSCK {//here we can have the view attributes that would be modified after we read the buffer

    private Board board;
    private ArrayList<Player> listOfPlayers;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ClientSCK(String address, int port) throws IOException, ClassNotFoundException { //we call this constructor after we ask the IP address and the port of the server

        Socket socket = new Socket();
        //when we connect the class ServerSCK can accept our connection and then create a thread (ClientHandlerThread)
        socket.connect(new InetSocketAddress(address, port), 1000); //the address and the port of the server

        //in this way we are not using the real streams but copies of them: we can not write on the real input stream if we use its copy (the communication doesn't work)
        //this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        //this.inputStream = new ObjectInputStream(socket.getInputStream()); //that would be the output stream of the server

        //these aren't copies:
        this.outputStream = (ObjectOutputStream) socket.getOutputStream();
        this.inputStream = (ObjectInputStream) socket.getInputStream(); //in this way the stream is converted into objects


        //IMPORTANTE: il client vero (questo) deve continuare a leggere l'input stream della sua socket per vedere se sono arrivati nuovi messaggi


        //questa soluzione però non utilizza un buffer quindi se arrivano più messaggi tra una chiamata di receiveMessage e l'altra DEI MESSAGGI ANDRANNO PERSI
        // a Thread waiting for new messages can be useful because it's blocking
        /*
        Thread clientThread = new Thread(()-> {
            ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();;
            executorService.execute(()->{
                while(!executorService.isShutdown()){  //come si fa lo shutdown??
                    try{
                        SCKMessage message = receivedMessage();
                        //update(message);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        },"ClientThread");
        clientThread.start();

         */

        // we can memorize the messages and then update the view reading all of them
        //Scanner in = new Scanner(inputStream);
        //PrintWriter out = new PrintWriter(outputStream);
        //while (in.hasNext()){ //in this way there are no lost messages and the updates are sequential
        //    SCKMessage message = receivedMessage();
        //    //update(message);
        //}


        //oppure (forse meglio):   ->no funziona solo se gli oggetti che dobbiamo estrarre sono stringhe
        /*Scanner in = new Scanner(inputStream);
        PrintWriter out = new PrintWriter(outputStream);
        while (in.hasNext()){ //in this way there are no lost messages and the updates are sequential
            SCKMessage sckMessage = in.nextLine();
            update(sckMessage);

        }

         */


        //questo è un buffer per byte -> non va bene
        //ReadableByteChannel channel = Channels.newChannel(socket.getInputStream());

        //meglio utilizzare ObjectOutputStream perchè converte lo strem in oggetti
        while (true) {
            try {
                SCKMessage sckMessage = (SCKMessage) this.inputStream.readObject(); //così non abbiamo più bisogno della funzione receiveMessage
                modifyClientSide(sckMessage); //questo è bloccante-> meglio utilizzare un thread...a meno che non vogliamo fare una modifica alla volta
            } catch (Exception e) {
                break; //se per esempio il flusso viene interrotto (dovrebbe venire lanciata un eccezione di Input/Output)
            }

        }
        //view attributes: ...

        //we have to read this stream every time there is a server update to the client (->in the Thread local to server we modify this stream)
        //public SCKMessage receivedMessage () throws IOException, ClassNotFoundException
        //{ //the socket stream has been changed in the Thread (locally to the server)
        //    return (SCKMessage) inputStream.readObject(); //we are reading the object written in the inputStream
        //}

    }

    // qualunque sia update da fare dovrebbe venire chiamata senMessage per dire al server tutto okay.
    // sendMessage la utilizziamo anche per comunicare le scelte del client al server
    public void sendMessage(SCKMessage sckMessage) throws IOException { //we write the socket so that if the Thread reads this stream, it can receive the message
        try {
            outputStream.writeObject(sckMessage); //here we are writing sckMessage in a buffer
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            System.err.println("\n\nServer crashed!");
            System.exit(-1);
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
        this.listOfPlayers = (ArrayList<Player>) this.inputStream.getObjectInputFilter(); //da riguardare bene filter (è da usare anche per l'estrazione di SCKMessage?)
        // e altro.... come i goal comuni
    }

}




