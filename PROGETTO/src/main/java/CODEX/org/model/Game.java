package CODEX.org.model;
import CODEX.Exceptions.CardNotDrawableException;

import CODEX.utils.ErrorsAssociatedWithExceptions;
import CODEX.utils.Observable;
import CODEX.utils.executableMessages.events.*;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

/**
 * This class represents the instance of a single game started by the Server
 */


public class Game extends Observable implements Serializable {
    private int id; // each Game has a different id
    private int nPlayers; // number of players in this game. It's decided by the lobby-creator
    private List<Player> players; // all the players in the game

    public enum GameState {
        STARTED,
        ENDING,
        ENDED,
        WAITING_FOR_START
    }
    private GameState state;
    private Player currentPlayer; // player who needs to play at this moment (now, it's his turn)
    private PlayableDeck resourceDeck; // contains all the resource cards
    private PlayableDeck goldDeck; // contains all the gold cards
    private PlayableDeck baseDeck; // contains all the base cards, which are the cards that players use to start the game
    private ObjectiveDeck objectiveDeck; // contains all the objective cards

    /** these ones are the 4 cards which are at the table center: players can see them and can decide to draw from the
       deck, or to draw one of these cards */
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;

    /**
     *  this 2 cards represent the 2 common goals (objectives)
     */
    private ObjectiveCard objectiveCard1;
    private ObjectiveCard objectiveCard2;

    private List<Chat> chats; // contains all the chats started during the game
    private List<Pawn> alreadySelectedColors;
    private ErrorsAssociatedWithExceptions lastEvent; // this flag gives some essential information about the last event which occurred in this Game


//maybe we can make the GameController check if the numPlayer is okay, and pass to Game a List with all the players
    /**
     * Class constructor
     * @param gamePlayers is the List of players that will play the game
     * @param id each game has a different id
     */
    public Game (List<Player> gamePlayers, int id) {
        super();
        this.id = id;
        this.players = gamePlayers;
        this.nPlayers=gamePlayers.size();
        for(Player player: players){
            player.setGame(this);
        }
        this.chats = new ArrayList<>();
        this.state = GameState.WAITING_FOR_START;
        this.currentPlayer = null;
        this.resourceDeck = PlayableDeck.resourceDeck();
        this.goldDeck = PlayableDeck.goldDeck();
        this.baseDeck = PlayableDeck.baseDeck();
        this.objectiveDeck = ObjectiveDeck.objectiveDeck();
        this.resourceCard1 = null;
        this.resourceCard2 = null;
        this.goldCard1 = null;
        this.goldCard2 = null;
        this.objectiveCard1 = null;
        this.objectiveCard2 = null;
        this.alreadySelectedColors= new ArrayList<>();
    }

    /**
     * 2nd constructor, we will have to delete that + delete addPlayer()
     * @param player
     * @param id
     */
    public Game (Player player, int id) { //TODO rimuovere questo costruttore, non serve più
        this.id = id;
        this.players = new ArrayList<>();
        players.add(player);
        this.chats = new ArrayList<>();
        this.state = GameState.WAITING_FOR_START;
        this.currentPlayer = null;
        this.resourceDeck = PlayableDeck.resourceDeck();
        this.goldDeck = PlayableDeck.goldDeck();
        this.baseDeck = PlayableDeck.baseDeck();
        this.objectiveDeck = ObjectiveDeck.objectiveDeck();
        this.resourceCard1 = null;
        this.resourceCard2 = null;
        this.goldCard1 = null;
        this.goldCard2 = null;
        this.objectiveCard1 = null;
        this.objectiveCard2 = null;
    }



    /**
     * Only the first player is added to the list by the constructor, the others will be added thanks to this function
     * @param p a player that has to be added
     * @throws ArrayIndexOutOfBoundsException if max number of player is reached
     */
    public void addPlayer (Player p) throws ArrayIndexOutOfBoundsException {
        if (players.size() < nPlayers) {
            this.players.add(p);
        } else throw new ArrayIndexOutOfBoundsException("This lobby is full!");
    }


    /**
     * This method sets the state of the game to STARTED,
     * it shuffles the resource deck and the gold one, giving for each type of deck 2 cards to the market,
     * then it shuffles the base deck and each player draws a starter card (base card)
     * the method lets the players decide the color of their pawn, the order of choosing is the order in which the player connected to the server
     * it gives 2 cards to the market as common objective
     * it gives each player 2 objective cards, he will decide which one to choose
     * it sets the game-order of the players
     * @throws IllegalArgumentException if players are less than 2 or more than 4
     */
    public void startGame () throws IllegalArgumentException, RemoteException {
        if((players.size()<2)||(players.size()>4)){
            throw new IllegalArgumentException("Incorrect number of players");} //will never be called

        // setting the state of the game to STARTED
        setState(GameState.STARTED);
        notifyObservers(new updatePlayersOrderEvent(players));//NEW

        // shuffling the resource deck and giving 2 cards to the market
        this.resourceDeck.shuffleDeck();
        this.resourceCard1 = this.resourceDeck.getFirstCard();
        notifyObservers(new updateResourceCard1Event(resourceCard1));//NEW

        this.resourceCard2 = this.resourceDeck.getFirstCard();
        notifyObservers(new updateResourceCard2Event(resourceCard2));//NEW



        // shuffling the gold deck and giving 2 cards to the market
        this.goldDeck.shuffleDeck();
        this.goldCard1 = this.goldDeck.getFirstCard();
        notifyObservers(new updateGoldCard1Event(goldCard1));
        this.goldCard2 = this.goldDeck.getFirstCard();
        notifyObservers(new updateGoldCard2Event(goldCard2));



        // shuffling the base deck and each player draws a starter card (base card)
        this.baseDeck.shuffleDeck();
        for (int i=0; i<this.players.size(); i++) {
            try {
                this.players.get(i).drawCard(this.baseDeck.getFirstCard());
            }catch(CardNotDrawableException | EmptyStackException ignored) {}//it will never happen here
            }

        // shuffling the objective deck
        this.objectiveDeck.shuffleDeck();

        for(Player player : players){
            player.setBoard(new Board(player));
            player.getBoard().setBoard(players.size());
        }


        // setting the game-order of the players
        Random random = new Random();
        List<Player> newOrder = new ArrayList<>();
        int randomFirstPlayer = random.nextInt(nPlayers); // sorting a random number between 0 and nPlayers -1
        this.players.get(randomFirstPlayer).setIsFirst(true); // he is the first player
        this.currentPlayer = this.players.get(randomFirstPlayer); // he is the first player and the current player
        newOrder.add(this.players.get(randomFirstPlayer)); // the game-order positions start from 1. Example: 1,2,3,4

        int not_assigned = nPlayers-1;
        for (int i = randomFirstPlayer+1; i<this.nPlayers && not_assigned > 0; i++) { //assigning the game-order position to the players
            // that follow the first player in the list
            newOrder.add(this.players.get(i)); // the game-order positions start from 1. Example: 1,2,3,4
            not_assigned-- ;
        }
        for (int i = 0; not_assigned > 0 && i<this.nPlayers; i++) { //assigning the game-order position to the
            // players that comes before the first player in the list
            newOrder.add(this.players.get(i)); // the game-order positions start from 1. Example: 1,2,3,4
            not_assigned--;
        }
        this.players = newOrder;
        players.get(0).setState(Player.PlayerState.IS_PLAYING); //no notify al momomento


        notifyObservers(new updateGoldDeckEvent(goldDeck));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));


        notifyObservers(new updatePlayersOrderEvent(players));//ora inviamo ai player l'ordine di gioco
        //notifyObservers(new Message(null, Event.SETUP_PHASE_1)); @TODO serve?
    }







    /**
     * After the player has played on the board the baseCard, this function is invoked.
     * It gives to each Player (2 resourceCards + 1 goldCard). It also reveals the common ObjectiveCards
     * and gives 2 ObjectiveCards per Player to be selected later
     */
    public void giveInitialCards () throws RemoteException {
        for (int i=0; i<nPlayers; i++) {
            try {
                players.get(i).drawCard(resourceDeck.checkFirstCard()); // resource card #1
                players.get(i).drawCard(resourceDeck.checkFirstCard()); // resource card #2
                players.get(i).drawCard(goldDeck.checkFirstCard()); // gold card #1
            }catch (CardNotDrawableException | EmptyStackException ignored){}
            //it will never happen here
        }

        // giving 2 cards to the market as common objective
        this.objectiveCard1 = objectiveDeck.getFirstCard();
        this.objectiveCard2 = objectiveDeck.getFirstCard();

        List<Object> tmp=new ArrayList<>();
        tmp.add(this.objectiveCard1);
        tmp.add(this.objectiveCard2);
        notifyObservers(new updateCommonObjectivesEvent(objectiveCard1, objectiveCard2));

        // giving each player 2 objective cards, next he will decide which one to choose
        for (int i = 0; i<nPlayers; i++) {
            this.players.get(i).addPersonalObjective(objectiveDeck.getFirstCard());
            this.players.get(i).addPersonalObjective(objectiveDeck.getFirstCard());
        }


        notifyObservers(new updateGoldDeckEvent(goldDeck));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
    }







    /**
     * This method ends the game
     */
    public void endGame () {
        state = GameState.ENDED;
    }






    /**
     * This method returns a List with the player who won the match.
     * If there are 2 or more winners, it returns a List with the players
     * with the same "Points & Objectives completed" situation.
     * @return winner is the List containing the winner or the players who tied
     */
    public List<Player> winner () {
        List<Player> winners = new ArrayList<>();
        int maxPoints = 0;
        for (int i = 0; i < this.players.size(); i++) { // looking for the highest points
            if (maxPoints < this.players.get(i).getPoints()) {
                maxPoints = this.players.get(i).getPoints();
            }
        }

        for (int i = 0; i < this.players.size(); i++) { // putting in the list the player(s) with highest points
            if (this.players.get(i).getPoints() == maxPoints) {
                winners.add(this.players.get(i));
            }
        }

        if (winners.size() > 1) { // I need to check how many objectives every player has respected
            int maxObjectives = 0;

            for (int i = 0; i < winners.size(); i++) { // checking the higher number of objectives achieved
                if (winners.get(i).getNumObjectivesReached() > maxObjectives) {
                    maxObjectives = winners.get(i).getNumObjectivesReached();
                }
            }

            int tmp= winners.size();
            List<Player> toBeRemoved=new ArrayList<>();
            for (int i = 0; i < winners.size(); i++) { // I need to remove the ones who have achieved less objectives
                if (winners.get(i).getNumObjectivesReached() < maxObjectives) {
                    toBeRemoved.add(winners.get(i));
                }
            }
            for(Player p: toBeRemoved){
                winners.remove(p);
            }
        }
        return winners;
    }







    /**
     * This method adds a new chat to the List chat in this game.
     * P1 and P2 are the 2 players the chat is composed by
     * @param p1 first player in the chat
     * @param p2 second player in the chat
     */
    public Chat startChat (Player p1, Player p2) {
        int index = this.chats.size();
        // choosing the id of my new chat: starting from 0, then 1,2,3,...
        List<Player> playersInChat = new ArrayList<>();
        playersInChat.add(p1);
        playersInChat.add(p2);
        // creating a list of the players in the chat

        Chat newChat = new Chat(playersInChat, index);
        chats.add(newChat);
        return newChat;
    }






    /**
     * This method adds a new chat to the List chat in this game. The new chat is composed by all the players in this game
     */
    public Chat startGeneralChat() {
        int index = this.chats.size();
        // choosing the id of my new chat: starting from 0, then 1,2,3,...

        // the General Chat includes all the players in the game
        Chat newChat = new Chat(this.players, index);
        chats.add(newChat);
        return newChat;
    }

    /**
     * This method returns the chat given the users
     * @param users are the users for which I'm looking for the Chat
     * @return the Chat if found, null otherwise
     */
    public Chat getChatByUsers(List<Player> users) {
        Chat tmp=null;
        for (Chat chat : chats) {
            if (!(chat.getUsers().size() == users.size())) {
                continue;
            }
            tmp = chat;
            for (Player player : users) {
                if (!chat.getUsers().contains(player)) {
                    tmp = null;
                    break;
                }
            }
        }
        return tmp;
    }





    /**
     *  The first player of the list, after this method is invoked, is the one who will need to play soon, at the next round.
     *  The order of the whole list is modified
     *  @return the next player is who will play soon
     */
    public Player nextRound() throws RemoteException{
        this.players.get(0).setState(Player.PlayerState.IS_WAITING);
        this.players.add(this.players.get(0));
        this.players.remove(0);
        this.currentPlayer = this.players.get(0);
        this.players.get(0).setState(Player.PlayerState.IS_PLAYING);

            notifyObservers(new updatePlayersOrderEvent(players));
        return this.currentPlayer;
    }



    /**
     * These 4 methods are useful to replace a card in the market.
     * The market is formed by 2 gold cards and 2 resource cards, which the player can pick up during the game
     */
    public void resetGoldCard1 ()  throws RemoteException {
        if(!this.goldDeck.isFinished()){
            this.goldCard1=this.goldDeck.getFirstCard();
        }else if(!this.resourceDeck.isFinished()){
            this.goldCard1=this.resourceDeck.getFirstCard();
        }else{
            this.goldCard1=null;
        }
        notifyObservers(new updateGoldCard1Event(goldCard1));
        notifyObservers(new updateGoldDeckEvent(goldDeck));
    }

    public void resetGoldCard2 () throws RemoteException {
        if(!this.goldDeck.isFinished()){
            this.goldCard2=this.goldDeck.getFirstCard();
        }else if(!this.resourceDeck.isFinished()){
            this.goldCard2=this.resourceDeck.getFirstCard();
        }else{
            this.goldCard2=null;
        }
        notifyObservers(new updateGoldCard2Event(goldCard2));
        notifyObservers(new updateGoldDeckEvent(goldDeck));
    }

    public void resetResourceCard1 () throws RemoteException {
        if(!this.resourceDeck.isFinished()){
            this.resourceCard1=this.resourceDeck.getFirstCard();
        }else if(!this.goldDeck.isFinished()){
            this.resourceCard1=this.goldDeck.getFirstCard();
        }else{
            this.resourceCard1=null;
        }
        notifyObservers(new updateResourceCard1Event(resourceCard1));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
    }

    public void resetResourceCard2 () throws RemoteException {
        if(!this.resourceDeck.isFinished()){
            this.resourceCard2=this.resourceDeck.getFirstCard();
        }else if(!this.goldDeck.isFinished()){
            this.resourceCard2=this.goldDeck.getFirstCard();
        }else{
            this.resourceCard2=null;
        }
        notifyObservers(new updateResourceCard2Event(resourceCard2));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
    }




    /**
     * Getter method
     * @return goldCard1 in the market
     */
    public PlayableCard getGoldCard1() {
        return this.goldCard1;
    }



    /**
     * Getter method
     * @return goldCard2 in the market
     */
    public PlayableCard getGoldCard2() {
        return this.goldCard2;
    }



    /**
     * Getter method
     * @return objectiveCard1 is one of the common objective
     */
    public ObjectiveCard getObjectiveCard1() {
        return this.objectiveCard1;
    }


    /**
     * Getter method
     * @return chats is the list of all opened chats
     */
    public List<Chat> getChats() {
        return this.chats;
    }



    /**
     * Getter method
     * @return objectiveCard2 is one of the common objective
     */
    public ObjectiveCard getObjectiveCard2() {
        return this.objectiveCard2;
    }



    /**
     * Getter method
     * @return resourceCard1 in the market
     */
    public PlayableCard getResourceCard1() {
        return this.resourceCard1;
    }




    /**
     * Getter method
     * @return CurrentPlayer in the match
     */
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }




    /**
     * Getter method
     * @return resourceCard2 in the market
     */
    public PlayableCard getResourceCard2() {
        return this.resourceCard2;
    }



    /**
     * Getter method
     * @return resourceDeck of the game
     */
    public PlayableDeck getResourceDeck() {
        return this.resourceDeck;
    }



    /**
     * Getter method
     * @return goldDeck of the game
     */
    public PlayableDeck getGoldDeck() {
        return this.goldDeck;
    }



    /**
     * Getter method
     * @return state of the game (for example if it's ended)
     */
    public GameState getState() {
        return this.state;
    }



    /**
     * Getter method
     * @return baseDeck of the game
     */
    public PlayableDeck getBaseDeck() {
        return baseDeck;
    }



    /**
     * Getter method
     * @return objectiveDeck of the game
     */
    public ObjectiveDeck getObjectiveDeck() {
        return objectiveDeck;
    }



    /**
     * Getter method
     * @return players are the ones who are playing
     */
    public List<Player> getPlayers () {
        return this.players;
    }


    public void setResourceDeck (PlayableDeck resourceDeck) {
        this.resourceDeck = resourceDeck;
    }

    public void setGoldDeck (PlayableDeck goldDeck) {
        this.goldDeck = goldDeck;
    }





    /**
     * Getter method
     * @return id of the game
     */
    public int getId() {
        return id;
    }



    /**
     * Getter method
     * @return nPlayers is the number of the players
     */
    public int getnPlayers() {
        return nPlayers;
    }



    /**
     * Getter method
     * @return lastEvent which occurred in this game
     */
    public ErrorsAssociatedWithExceptions getLastEvent() {
        return lastEvent;
    }



    /**
     * Setter method
     * @param id of the game
     */
    public void setId(int id) {
        this.id = id;
    }



    /**
     * Setter method
     * @param nPlayers is the number of the players
     */
    public void setnPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }



    /**
     * Setter method
     * @param state of the game
     */
    public void setState (GameState state) throws RemoteException {
        this.state = state;
        boolean theGameHasJustStarted;
        if(this.state.equals(GameState.STARTED)){
            theGameHasJustStarted=true;
        }else {
            theGameHasJustStarted=false;
        }
        notifyObservers(new updateGameStateEvent(this.state,theGameHasJustStarted));
    }



    /**
     * Setter method
     * @param player which is playing now
     */
    public void setCurrentPlayer (Player player) {
        this.currentPlayer = player;
    }

    public List<Pawn> getAlreadySelectedColors() {
        return alreadySelectedColors;
    }



    /**
     * Setter method
     * @param lastEvent which occurred in this game
     */
    public void setLastEvent(ErrorsAssociatedWithExceptions lastEvent) { //per far funzionare tcp NON devono venire notificati i messaggi di essore qui
        this.lastEvent = lastEvent;
        if(lastEvent.equals(ErrorsAssociatedWithExceptions.SETUP_PHASE_2)||lastEvent.equals(ErrorsAssociatedWithExceptions.GAME_LEFT)) {
            try {
                notifyObservers(new setUpPhaseFinishedEvent()); //GAME_LEFT non è incluso
            } catch (RemoteException e) {
            }
        }

    }
    public void setLastEvent(Event event){
        try {
            notifyObservers(event); //disconnectionEvent
        } catch (RemoteException ignored) { //in caso di connection lost
            //se mentre notifico di connection lost mi vengono lanciate RemoteException i casi sono due:
            //1. sto facendo la notify proprio al player per cui è stata chiamata disconnection()
            //2. altri player nel frattempo si sono disconnessi
            //in entrambi i casi ignoro l'eccezione perchè tanto dopo queste notify devo chiudere tutto
        }
    }

}
