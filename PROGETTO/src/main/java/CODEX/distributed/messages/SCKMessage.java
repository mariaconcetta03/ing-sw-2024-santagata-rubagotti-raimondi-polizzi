package CODEX.distributed.messages;

import CODEX.utils.executableMessages.clientMessages.ClientMessage;
import CODEX.utils.executableMessages.events.Event;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;
import java.io.Serializable;

/**
 * This class defines a message that is used to pass information through the socket in a bidirectional way
 */
public class SCKMessage implements Serializable {
    private final ClientMessage clientMessage;
    private final ServerMessage serverMessage;
    private final Event event;


    /**
     * Class constructor
     * @param msg is the message for passing information to the server
     */
    public SCKMessage(ClientMessage msg) {
        this.event = null;
        this.serverMessage = null;
        this.clientMessage = msg;
    }



    /**
     * Class constructor
     * @param msg is the message for passing information to the client
     */
    public SCKMessage(ServerMessage msg) {
        this.event = null;
        this.clientMessage = null;
        this.serverMessage = msg;
    }



    /**
     * Class constructor
     * @param event is a message sent by the model (update)
     */
    public SCKMessage(Event event) {
        this.clientMessage = null;
        this.serverMessage = null;
        this.event = event;

    }



    /**
     * Getter method
     * @return event of the update sent by the model
     */
    public Event getEvent() {
        return this.event;
    }



    /**
     * Getter method
     * @return serverMessage which is a message sent by the server (return values of the controller)
     */
    public ServerMessage getServerMessage() {
        return this.serverMessage;
    }



    /**
     * Getter method
     * @return clientMessage which is a message sent by the client
     */
    public ClientMessage getClientMessage() {
        return this.clientMessage;
    }
}
