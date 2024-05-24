package CODEX.distributed.messages;
import CODEX.utils.Event;

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable { //questa classe verrà cancellata e si usarà solo SCKMessage per le SCK
        protected final Event event;
        protected List<Object> obj; //to be deleted

    /**
     * Constructor method
     * @param obj single generic object
     * @param event the action we have to perform
     */
    public Message(Object obj, Event event){
            this.obj=new ArrayList<>();
            this.obj.add(obj);
            this.event=event;
        }

    /**
     * Constructor method
     * @param obj List of generic objects
     * @param event the action we have to perform
     */
    public Message(List<Object> obj, Event event) {
            this.obj = obj;
            this.event = event;
        }

    public Message() {
        this.event=null;
    }


    public List<Object> getObj() {
            return this.obj;
        }

        public Event getMessageEvent() {
            return this.event;
        }
}
