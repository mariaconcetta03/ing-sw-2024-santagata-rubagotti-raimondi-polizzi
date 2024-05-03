package distributed.messages;
import utils.Event;

import java.io.Serializable;

public abstract class Message implements Serializable {
        protected final Event event;
        protected final Object obj; //that is what is being sent


    /**
     * @param obj
     * @param event
     */
    public Message(Object obj, Event event) {
            this.obj = obj;
            this.event = event;
        }


        public Object getObj() {
            return this.obj;
        }


        public Event getMessageEvent() {
            return this.event;
        }
}
