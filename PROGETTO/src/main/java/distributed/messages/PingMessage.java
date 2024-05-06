package distributed.messages;

import java.io.Serializable;

public enum PingMessage implements Serializable { //ClientHandlerThread asks, ClientSCK reply
    ARE_YOU_STILL_CONNECTED,
    YES_STILL_CONNECTED; //we set a timeout and ClientSCK has to reply by the end of the timeout, if it doesn't the connection will be declared dead

}
