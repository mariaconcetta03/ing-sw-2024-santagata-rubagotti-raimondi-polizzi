package CODEX.distributed.messages;

import java.io.Serializable;

public class PingMessage implements Serializable { //ClientHandlerThread asks, ClientSCK reply
    //we can't use an enum if we want to use getObjectInputFilter() to select a PingMessage between SCKMessages
}
