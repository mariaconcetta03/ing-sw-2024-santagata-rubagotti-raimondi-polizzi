package utils;

import org.model.Game;
import org.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
    private List<Observer> RMIlisteners;
    private List<Observer> TCPlisteners;

    //mi serve un costruttore di default se no ho errori nelle classi che estendono Observable
    public Observable(){
        this.RMIlisteners= new ArrayList<>();
        this.TCPlisteners= new ArrayList<>();
    }
    public Observable(List<Observer> RMIlisteners,List<Observer> TCPlisteners){
        this.RMIlisteners=RMIlisteners;
        this.TCPlisteners=TCPlisteners;  //Non credo glieli daremo alla creazione degli observable, ma bensì li aggiungeremo
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
     * This method updates a board in the listeners of a specific game
     * @param p is the player who has the new board (which has been changed recently)
     */
    public void updateBoard(Player p){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: TCPlisteners) {
            l.updateBoard(p.getBoard());
        }
        for(Observer l: RMIlisteners) {
            l.updateBoard(p.getBoard());
        }
    }



    /**
     * This method updates the resource deck in the listeners of that game
     */
    public void updateResourceDeck(){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateResourceDeck();
        }
        for(Observer l: TCPlisteners) {
            l.updateResourceDeck();
        }
    }



    /**
     * This method updates the gold deck in the listeners of that game
     */
    public void updateGoldDeck(Game game){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateGoldDeck();
        }
        for(Observer l: TCPlisteners) {
            l.updateGoldDeck();
        }
    }



    /**
     * This method updates a player deck in the listeners of a specific game
     * @param p is the player who has the new deck (which has been changed recently,
     * for example by playing a card or by drawing a card)
     */
    public void updatePlayerDeck(Player p){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updatePlayerDeck(p);
        }
        for(Observer l: TCPlisteners) {
            l.updatePlayerDeck(p);
        }
    }



    // ----------------------------- A T T E N Z I O N E ------------------------------
    // DOMANDA !?
    // MEGLIO FARE UNA FUNZIONE UNICA PER TUTTE LE CARTE DEL MERCATO? OPPURE PIU FUNZIONI?
    // SE FACCIAMO UNA FUNZIONE UNICA OVVIAMENTE BISOGNA AVERE QUALCHE PARAMETRO IN PIU
    // MA FORSE è PIU ELEGANTE ?
    // --------------------------------------------------------------------------------

    /**
     * This method updates the first resource card in the market of the cards of that game
     */
    public void updateResourceCard1(){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateResourceCard1();
        }
        for(Observer l: TCPlisteners) {
            l.updateResourceCard1();
        }
    }



    /**
     * This method updates the second resource card in the market of the cards of that game
     */
    public void updateResourceCard2(){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateResourceCard2();
        }
        for(Observer l: TCPlisteners) {
            l.updateResourceCard2();
        }
    }



    /**
     * This method updates the first gold card in the market of the cards of that game
     */
    public void updateGoldCard1(){
        // getting the list of the observers of the correct game


        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateGoldCard1();
        }
        for(Observer l: TCPlisteners) {
            l.updateGoldCard1();
        }
    }



    /**
     * This method updates the second gold card in the market of the cards of that game
     */
    public void updateGoldCard2(){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateGoldCard2();
        }
        for(Observer l: TCPlisteners) {
            l.updateGoldCard2();
        }
    }



    /**
     * This method updates a specific chat (with his id) of that game
     * @param chatID the ID of the chat which needs to be updated
     */
    public void updateChat(int chatID){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateChat(chatID);
        }
        for(Observer l: TCPlisteners) {
            l.updateChat(chatID);
        }
    }



    /**
     * This method updates all the pawns in a specific game. This method is
     * called after all the players have deided their own pawn color.
     */
    public void updatePawns(){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updatePawns();
        }
        for(Observer l: TCPlisteners) {
            l.updatePawns();
        }
    }



    /**
     * This method updates all the nicknames in a specific game. This method is
     * called after all the players have joined the lobby, and when it is started.
     */
    public void updateNickname(Game game){
        // getting the list of the observers of the correct game


        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateNickname();
        }
        for(Observer l: TCPlisteners) {
            l.updateNickname();
        }
    }



    /**
     * This method updates the round in a specific game. Thanks to this method it will be possible
     * to display the player which needs to play now
     */
    public void updateRound(Game game){
        // getting the list of the observers of the correct game

        // updating all the boards of the listeners in the correct game
        for(Observer l: RMIlisteners) {
            l.updateRound();
        }
        for(Observer l: RMIlisteners) {
            l.updateRound();
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


    //non ci serve perchè se un giocatore si disconnette finisce la partita (dobbiamo usare messaggi di ping per capire se tutti sono ancora connessi)
    /**
     * This method removes a listener from the list
     * @param listener to remove from the list
     */
    //public void removeListener(Game game, Observer listener){
    //    this.listeners.get(game).remove(listener);
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
