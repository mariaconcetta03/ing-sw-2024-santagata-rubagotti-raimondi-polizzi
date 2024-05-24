package CODEX.utils;


import CODEX.utils.executableMessages.events.Event;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


// we can have one class for each Game, we can have an attribute observable in Game
// when a game starts we set this attribute and use the constructor to add the relative RMIlisteners and TCPlisteners
public class Observable {

    // ----------------------------------- A L C U N I   E S E M P I ------------------------------------
    // this object is something that is shared between the players
    // example: a group chat
    // example: all the players' tables (in a single image...)
    // Definire l'Observable (Soggetto): Questa è la classe che tiene traccia degli Observer e notifica
    // loro i cambiamenti. Ad esempio, potrebbe essere il modello nei tuoi termini MVC.
    // --------------------------------------------------------------------------------------------------
    private List<Observer> observers;

    //mi serve un costruttore di default se no ho errori nelle classi che estendono Observable
    public Observable(){
        this.observers= new ArrayList<>();
    }


    //non serve se mettiamo un attributo di tipo Observable in ogni classe Game
    //private Map <Game, List<Observer>> listeners;
    // this is a map which associates a game with a list of his listeners



    // ------------------------------ A T T E N Z I O N E ---------------------------
    // LE FUNZIONI UPDATE SONO NECESSARIE PER NOTIFICARE I VARI LISTENERS SUL FATTO
    // CHE è STATO MODIFICATO QUALCOSA, E QUESTO CAMBIAMENTO VA TRAMANDATO FINO ALLA
    // VIEW. IL PATTERN DI CHIAMATE DELLE FUNZIONI è IL SEGUENTE:
    // OBSERVABLE -> OBSERVER -> SERVER -> RMISERVER -> RMICLIENT -> VIEW
    // ------------------------------------------------------------------------------

    //RMI: (attenzione: update va chiamato dal server non dal client)
    // se RMISERVER ha una funzione update (del model del client che ha chiamato e del
    // model degli altri client), RMICLIENT può chiamarla tramite invocazione remota.
    // questo update deve contattare la lista listeners presente nell'interfaccia osservable
    // (sono i giocatori in questo caso), quindi la listeners avrà l'indirizzo di rete di
    // questi giocatori.
    // quest'update dovrebbe fare uso di un observable (che non è un'interfaccia) che andrà a
    // modificare la view di ogni client (a diversi indirizzi IP segnati in listeners)->quindi da
    // questo oggetto observable dobbiamo instaurare la connessione RMI verso i client.


    //SOCKET: (attenzione: update va chiamato dal server non dal client)
    // abbiamo un thread per ogni client e questo thread si trova all'indirizzo IP del server
    // e contiene una copia della socket del vero client (che si trova in un altro indirizzo IP,
    // che sarà incluso nei listeners: quindi il client vero, ClientSCK, avrà degli attributi della
    // view che potranno subire l'update).
    // questo Thread ascolta l'input dell'utente (tramite la copia della socket) e da questo input
    // capisce quali funzioni invocare.
    // l'update lo possiamo invocare al termine di una funzione chiamata dal client (il thread). Quindi
    // come prima siamo ancora all'indizzo IP del server che utilizzerà un oggetto observable che aggiornerà
    // la view di tutti i listeners tramite rete. Per fare tutto questo dobbiamo andare a scrivere nello
    // stream output della socket (gli observer saranno sempre i client che andranno a leggere lo stream output
    // appena modicato nella propria socket)


    /**
     * This method will be called at the end of the model's methods that "modify" something.
     * It will inform the Observers to perform an update action contacting the Client related to them.
     * @param e has caused the modification
     */

    public void notifyObservers(Event e) throws RemoteException{
        for(Observer obs: observers){
            obs.update(this, e);
        }
    }

    /**
     * This method add an Observer to the Observable.
     * @param obs is the Observer we are adding.
     */
    public void addObserver(Observer obs) throws RemoteException { //synchronized?
        if (!this.observers.contains(obs)) {
            this.observers.add(obs);
        }
    }
    /**
     * This method removes an observer from the list
     */
    public void removeObservers(){
        for(int i=0; i< observers.size();i++){
            observers.remove(observers.get(i));
        }
    }




    //se creiamo la lista di osservatori solo quando la partita inizia non abbiamo bisogno di questo metodo
    /**
     * This method adds a listener to the list (in the correct game)
     * @param listener to add to the list
     */
    //public void addListener(Game game, Observer listener) {
    //    this.listeners.get(game).add(listener);
    //}


    //non ci serve se abbiamo un attributo Observable per ogni Game
    /**
     * This method adds a new game to the map. After this method, we will need to call
     * "addListener" to add all the listener in this game
     * @param game is the game we want to add to the map
     */
    //public void addGame (Game game) {
    //    this.listeners.put(game, new ArrayList<>());
    //}




    //non ci serve se abbiamo un attributo Observable per ogni Game
    /**
     * Getter method
     * @param game the game which contains the listeners
     * @return the list of the listeners of that game
     */
    //public List<Observer> getListeners(Game game) {
    //    return listeners.get(game);
    //}

}
