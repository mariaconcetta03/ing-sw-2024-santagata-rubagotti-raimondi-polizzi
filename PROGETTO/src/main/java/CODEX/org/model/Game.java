package CODEX.org.model;

import CODEX.Exceptions.CardNotDrawableException;
import CODEX.utils.ErrorsAssociatedWithExceptions;
import CODEX.utils.Observable;
import CODEX.utils.executableMessages.events.*;
import java.io.Serializable;
import java.util.*;


/**
 * This class represents the instance of a single game started by the Server
 */
public class Game extends Observable implements Serializable {
    private int id; // each Game has a different id
    private int nPlayers; // number of players in this game. It's decided by the lobby-creator
    private List<Player> players; // all the players in the game
    /**
     * This enum represents the different states of the game
     */
    public enum GameState {
        /**
         * The game has started
         */
        STARTED,

        /**
         * The game is ending (last turns)
         */
        ENDING,

        /**
         * The game has ended
         */
        ENDED,

        /**
         * The game is waiting for start
         */
        WAITING_FOR_START
    }
    private GameState state=null;

    private Player currentPlayer; // player who needs to play at this moment (now, it's his turn)
    private PlayableDeck resourceDeck; // contains all the resource cards
    private PlayableDeck goldDeck; // contains all the gold cards
    private PlayableDeck baseDeck; // contains all the base cards, which are the cards that players use to start the game
    private ObjectiveDeck objectiveDeck; // contains all the objective cards

    /* these ones are the 4 cards which are at the table center: players can see them and can decide to draw from the
       deck, or to draw one of these cards */
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;

    /* this 2 cards represent the 2 common goals (objectives) */
    private ObjectiveCard objectiveCard1;
    private ObjectiveCard objectiveCard2;

    private Map<Integer, Chat> chats; // contains all the chats started during the game. String is the crasi of the nickname
    private int chatId=0;
    private final List<Pawn> alreadySelectedColors;
    private ErrorsAssociatedWithExceptions lastEvent; // this flag gives some essential information about the last event which occurred in this Game
    private int lastMoves;
    private final Object gameStateIsChangingLock= new Object();



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
        this.chats = new HashMap<>();
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
        this.lastMoves=-1;
    }



    /**
     * Class constructor (used for testing purposes) @TODO rimuovere
     * @param player creator
     * @param id game ID
     */
    public Game (Player player, int id) {
        this.id = id;
        this.players = new ArrayList<>();
        players.add(player);
        this.chats = new HashMap<>();
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
        this.lastMoves=-1;
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
    public void startGame () throws IllegalArgumentException{
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
        for (int i = randomFirstPlayer+1; i<this.nPlayers && not_assigned > 0; i++) {
            // assigning the game-order position to the players that follow the first player in the list
            newOrder.add(this.players.get(i)); // the game-order positions start from 1. Example: 1,2,3,4
            not_assigned-- ;
        }
        for (int i = 0; not_assigned > 0 && i<this.nPlayers; i++) { //assigning the game-order position to the
            // players that comes before the first player in the list
            newOrder.add(this.players.get(i)); // the game-order positions start from 1. Example: 1,2,3,4
            not_assigned--;
        }
        this.players = newOrder;
        players.get(0).setState(Player.PlayerState.IS_PLAYING);


        notifyObservers(new updateGoldDeckEvent(goldDeck));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
    }



    /**
     * This method is called by the GameController when all the players have chosen their Pawn's color
     */
    public void chosenPawns(){
        notifyObservers(new updatePlayersOrderEvent(players));
    }



    /**
     * After the player has played on the board the baseCard, this function is invoked.
     * It gives to each Player (2 resourceCards + 1 goldCard). It also reveals the common ObjectiveCards
     * and gives 2 ObjectiveCards per Player to be selected later
     */
    public void giveInitialCards (){
        for (int i=0; i<nPlayers; i++) {
            try {
                players.get(i).drawCard(resourceDeck.checkFirstCard()); // resource card #1
                players.get(i).drawCard(resourceDeck.checkFirstCard()); // resource card #2
                players.get(i).drawCard(goldDeck.checkFirstCard()); // gold card #1
            }catch (CardNotDrawableException | EmptyStackException ignored){}
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
    public void endGame (){
        setState(GameState.ENDED);
    }



    /**
     * This method returns a List with the player who won the match.
     * If there are 2 or more winners, it returns a List with the players
     * with the same "Points and Objectives completed" situation.
     * @return winner is the List containing the winner or the players who tied
     */
    public Map<Integer, List<String>> winner (){

        Map<String, Player> playersNicknames=new HashMap<>();
        for(Player p: players){
            playersNicknames.put(p.getNickname(), p);
        }
        List<String> playersToBeRemoved=new ArrayList<>();
        for(Player p: players){
            playersToBeRemoved.add(p.getNickname());
        }
        int position=1;
        Map<Integer, List<String>> finalScoreBoard=new HashMap<>();
        List<String> tmp;
        while(!playersToBeRemoved.isEmpty()) {
            tmp= new ArrayList<>();
            int maxPoints = -1;
            int maxObjReached = -1;

            for (String s : playersToBeRemoved) {
                if (playersNicknames.get(s).getPoints() > maxPoints) {
                    maxPoints = playersNicknames.get(s).getPoints();
                }
            }

            for (String s : playersToBeRemoved) {
                if ((playersNicknames.get(s).getPoints() == maxPoints)&&(playersNicknames.get(s).getNumObjectivesReached()>maxObjReached)) {
                    maxObjReached=playersNicknames.get(s).getNumObjectivesReached();
                }
            }

            for(String s: playersToBeRemoved){
                if((playersNicknames.get(s).getNumObjectivesReached()==maxObjReached)&&(playersNicknames.get(s).getPoints()==maxPoints)){
                    tmp.add(s);
                }
            }
            for(String s: tmp){
                playersToBeRemoved.remove(s);
            }

            finalScoreBoard.put(position, tmp);

            position+=tmp.size();

        }

        notifyObservers(new winnerEvent(finalScoreBoard));
        return finalScoreBoard;
    }



    /**
     * This method adds a new chat to the List chat in this game.
     * P1 and P2 are the 2 players the chat is composed by
     * @param p1Nickname first player in the chat
     * @param p2Nickname second player in the chat
     * @return the created new chat
     */
    public Chat startChat (String p1Nickname, String p2Nickname) {
        List<String> playersInChat = new ArrayList<>();
        playersInChat.add(p1Nickname);
        playersInChat.add(p2Nickname);
        // creating a list of the players in the chat

        Chat newChat = new Chat(playersInChat, chatId);
        chats.put(chatId,newChat);
        chatId++;
        return newChat;
    }



    /**
     * This method adds a new chat to the List chat in this game. The new chat is composed by all the players in this game
     * @return the new chat created
     */
    public Chat startGeneralChat() {
        // the General Chat includes all the players in the game
        String generalChatString="";
        List<String> playerNicknames=new ArrayList<>();
        for(Player p: this.players){
            playerNicknames.add(p.getNickname());
        }
        Chat newChat = new Chat(playerNicknames, chatId);
        chats.put(chatId,newChat);
        chatId++;
        return newChat;
    }



    /**
     * This method returns the chat given the users
     * @param usersNicknames are the users for which I'm looking for the Chat
     * @return the Chat if found, null otherwise
     */
    public Chat getChatByUsers(List<String> usersNicknames) {
        Chat tmp=null;
        for (Chat chat : chats.values()) {
            if (!(chat.getUsers().size() == usersNicknames.size())) {
                continue;
            }
            tmp = chat;
            for (String playerNickname : usersNicknames) {
                if (!chat.getUsers().contains(playerNickname)) {
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
     */
    public void nextRound(){
        this.players.get(0).setState(Player.PlayerState.IS_WAITING);
        this.players.add(this.players.get(0));
        this.players.remove(0);
        this.currentPlayer = this.players.get(0);
        this.players.get(0).setState(Player.PlayerState.IS_PLAYING);
        notifyObservers(new updatePlayersOrderEvent(players));
    }



    /**
     * This method is useful to replace the gold card 1 in the market.
     */
    public void resetGoldCard1 () {
        if(!this.goldDeck.isFinished()){
            this.goldCard1=this.goldDeck.getFirstCard();
        }else if(!this.resourceDeck.isFinished()){
            this.goldCard1=this.resourceDeck.getFirstCard();
        }else{
            this.goldCard1=null;
        }
        notifyObservers(new updateGoldCard1Event(goldCard1));
        notifyObservers(new updateGoldDeckEvent(goldDeck));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
    }



    /**
     * This method is useful to replace the gold card 2 in the market.
     */
    public void resetGoldCard2 () {
        if(!this.goldDeck.isFinished()){
            this.goldCard2=this.goldDeck.getFirstCard();
        }else if(!this.resourceDeck.isFinished()){
            this.goldCard2=this.resourceDeck.getFirstCard();
        }else{
            this.goldCard2=null;
        }
        notifyObservers(new updateGoldCard2Event(goldCard2));
        notifyObservers(new updateGoldDeckEvent(goldDeck));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
    }



    /**
     * This method is useful to replace the resource card 1 in the market.
     */
    public void resetResourceCard1 () {
        if(!this.resourceDeck.isFinished()){
            this.resourceCard1=this.resourceDeck.getFirstCard();
        }else if(!this.goldDeck.isFinished()){
            this.resourceCard1=this.goldDeck.getFirstCard();
        }else{
            this.resourceCard1=null;
        }
        notifyObservers(new updateResourceCard1Event(resourceCard1));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
        notifyObservers(new updateGoldDeckEvent(goldDeck));
    }



    /**
     * This method is useful to replace the resource card 2 in the market.
     */
    public void resetResourceCard2 () {
        if(!this.resourceDeck.isFinished()){
            this.resourceCard2=this.resourceDeck.getFirstCard();
        }else if(!this.goldDeck.isFinished()){
            this.resourceCard2=this.goldDeck.getFirstCard();
        }else{
            this.resourceCard2=null;
        }
        notifyObservers(new updateResourceCard2Event(resourceCard2));
        notifyObservers(new updateResourceDeckEvent(resourceDeck));
        notifyObservers(new updateGoldDeckEvent(goldDeck));
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
    public Map<Integer, Chat> getChats() {
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
     * @return players are the ones who are playing
     */
    public List<Player> getPlayers () {
        return this.players;
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
     * @return a List containing the already selected pawn colors
     */
    public List<Pawn> getAlreadySelectedColors() {
        return alreadySelectedColors;
    }



    /**
     * Getter method
     * This method is used to check which are the possible colors left to choose between
     * @return a List containing the currently available pawn colors
     */
    public List<Pawn> getAvailableColors(){
        List<Pawn> tmp=new ArrayList<>();
        tmp.add(Pawn.GREEN);
        tmp.add(Pawn.RED);
        tmp.add(Pawn.YELLOW);
        tmp.add(Pawn.BLUE);
        for(Pawn p: alreadySelectedColors){
            tmp.remove(p);
        }
        return tmp;
    }


    /**
     * Getter method
     * @return a lock used in class GameController (disconnection())
     */
    public Object getGameStateIsChangingLock() {
        return gameStateIsChangingLock;
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
    public void setState (GameState state) {
        synchronized (gameStateIsChangingLock) {
            this.state = state;
            boolean theGameHasJustStarted;
            if (this.state.equals(GameState.STARTED)) {
                theGameHasJustStarted = true;
            } else {
                theGameHasJustStarted = false;
            }
            notifyObservers(new updateGameStateEvent(this.state, theGameHasJustStarted));
        }
    }



    /**
     * Setter method
     * @param lastEvent which occurred in this game
     */
    public void setLastEvent(ErrorsAssociatedWithExceptions lastEvent) {
        this.lastEvent = lastEvent;
    }



    /**
     * Setter method
     * @param event which occurred in this game
     */
    public void setLastEvent(Event event){
        notifyObservers(event);
    }



    /**
     * Setter method
     * @param lastMoves moves left in this game
     */
    public void setLastMoves(int lastMoves) {
        this.lastMoves = lastMoves;
        notifyObservers(new updateLastMovesEvent(this.lastMoves));
    }
}
