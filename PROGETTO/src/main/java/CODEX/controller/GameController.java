package CODEX.controller;

import CODEX.Exceptions.CardNotDrawableException;
import CODEX.Exceptions.CardNotOwnedException;
import CODEX.Exceptions.ColorAlreadyTakenException;
import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.GameControllerInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.*;
import CODEX.utils.Observer;
import CODEX.utils.ErrorsAssociatedWithExceptions;
import CODEX.utils.executableMessages.events.OK;
import CODEX.utils.executableMessages.events.setUpPhaseFinishedEvent;
import CODEX.utils.executableMessages.events.updatePlayersOrderEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * This class is the controller of a game. Here are present all the methods which can be invoked during a match.
 */
public class GameController extends UnicastRemoteObject implements GameControllerInterface {
    private static final int TIMEOUT = 4; // seconds
    private boolean disconnection = false;
    private boolean firstDisconnection = true;
    private Map <String, Long> lastHeartbeatTimesOfEachPlayer;
    private ServerController serverController;
    private Game game;
    private int lastRounds;
    private int lastDrawingRounds;
    private List<Player> winners;
    private List<Player> gamePlayers;
    private int numberOfPlayers;
    private int id;
    private Map<String, Observer> clientsConnected;
    private final Object disconnectionLock=new Object();

    private boolean alreadyCheckedNPlayers;
    private boolean alreadyCheckedBaseCards;
    private boolean alreadyCheckedColors;
    private boolean alreadyCheckedObjectiveCards;

    /**
     * Class constructor, initialises lastRounds and lastDrawingRounds to 10 (improbable value)
     */
    public GameController() throws RemoteException {
        super();
        game = null;
        serverController = null;
        lastRounds = 10;
        lastDrawingRounds = 10;
        winners = new ArrayList<>();
        gamePlayers = new ArrayList<>();
        clientsConnected = new HashMap<>();
        numberOfPlayers = 0;
        id = 0;
        lastHeartbeatTimesOfEachPlayer = new HashMap<>();
        alreadyCheckedNPlayers=false;
        alreadyCheckedColors=false;
        alreadyCheckedBaseCards=false;
        alreadyCheckedObjectiveCards=false;
    }



    /**
     * This method creates the Game that will be managed by GameController
     * @param gamePlayers is the List of players that will be in the Game
     * @throws RemoteException
     */
    public void createGame (List<Player> gamePlayers) throws RemoteException  {
        if(this.game==null) {
            game = new Game(gamePlayers, id);
            // adding Observers to Game and Player classes
            for (Observer obs : clientsConnected.values()) {
                game.addObserver(obs);
            }
            for (Player p : gamePlayers) {
                for (Observer obs : clientsConnected.values()) {
                    p.addObserver(obs);
                }
            }
        }
    }



    /**
     * This method adds a player to the waiting ones in the lobby (gamePlayers)
     * @param player is the one player added to the lobby
     * @throws ArrayIndexOutOfBoundsException if the of players is exceeded
     * @throws RemoteException
     */
    public void addPlayer(Player player) throws ArrayIndexOutOfBoundsException, RemoteException {
        if (gamePlayers.size() < numberOfPlayers) {
            gamePlayers.add(player);
        } else {
            throw new ArrayIndexOutOfBoundsException("This lobby is already full!");
        }
    }



    /**
     * This method is invoked when we need to check how many players there are in the game.
     * If the number of players inside is correct, then it starts the game
     * @throws RemoteException
     */
    public synchronized void checkNPlayers() throws RemoteException{
        if ((!alreadyCheckedNPlayers)&&(gamePlayers.size() == numberOfPlayers)) {
            alreadyCheckedNPlayers=true;
            createGame(gamePlayers);
            startGame(); //removed illegalstate
        }
    }



    /**
     * This method starts the game. The game state is set to STARTED. 2 objective cards are given to each
     * player, and he will need to choose one of these. Then the market is completed and each player receives
     * 3 cards (2 resource cards and 1 gold card).
     * INTERNAL USE METHOD
     * @throws RemoteException
     */
    public void startGame() throws RemoteException {
        if(game.getState() == Game.GameState.WAITING_FOR_START) {
            game.startGame();
        }
    }



    /**
     * This method let the Player place the baseCard (in the middle of the table) and, if all the players
     * have placed their baseCard, it let the Game finish the set-up phase giving the last necessary cards
     * @param nickname the player who plays the card
     * @param baseCard the base card played
     * @param orientation of the played card
     */
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) { //TODO no remote?
        Player player= getPlayerByNickname(nickname);
        player.playBaseCard(orientation, baseCard);
        player.getPlayerDeck()[0]=null; // baseCard deleted from player deck
    }



    /**
     * This method checks if all the players have played their base card.
     * If they all did, then it gives the initial cards for the starting of the match.
     * @throws RemoteException
     */
    public synchronized void checkBaseCardPlayed() throws RemoteException {
        PlayableCard[][] tmp;
        //for all players in the game
        for (Player p1 : game.getPlayers()) {
            tmp = p1.getBoard().getTable();
            //if all players haven't played their baseCard
            if (tmp[p1.getBoard().getBoardDimensions() / 2][p1.getBoard().getBoardDimensions() / 2] == null) {
                return;
            }
        }
        if (!alreadyCheckedBaseCards){
            alreadyCheckedBaseCards=true;
            game.giveInitialCards();
        }
    }



    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param nickname is the nickname of the Player that wants to play a card
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     * @throws RemoteException
     * @throws IllegalArgumentException if there is an error in playing the card
     */
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException, IllegalArgumentException {
        Player currentPlayer = game.getPlayers().get(0);
        if (!getPlayerByNickname(nickname).equals(currentPlayer)) {
            game.setLastEvent(ErrorsAssociatedWithExceptions.NOT_YOUR_TURN);
        }else{
            currentPlayer.playCard(selectedCard, position, orientation);
            if ((game.getState() != Game.GameState.ENDING) && (currentPlayer.getPoints() >= 20) && (lastRounds==10)) {
                game.setState(Game.GameState.ENDING);
                calculateLastMoves();
            }
            if(lastDrawingRounds<=0){
                nextPhase();
            }
        }
        game.setLastEvent(new OK(nickname));
    }



    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname of the player who is going to draw the card
     * @param selectedCard is the Card the Players wants to draw
     * @throws RemoteException
     */
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException {
        // we can draw a card from one of the decks or from the uncovered cards
        Player currentPlayer = game.getPlayers().get(0);
        if (!getPlayerByNickname(nickname).equals(currentPlayer)) {
            game.setLastEvent(ErrorsAssociatedWithExceptions.NOT_YOUR_TURN);
        } else {
            boolean draw = false;
            for (int i = 0; i < 3 && !draw; i++) {
                if (currentPlayer.getPlayerDeck()[i] == null) {
                    draw = true;
                }
            }
            if (draw) {
                try {
                    currentPlayer.drawCard(selectedCard);
                } catch (CardNotDrawableException e) {
                    System.out.println("This card can't be drawn!");
                    game.setLastEvent(ErrorsAssociatedWithExceptions.CARD_NOT_DRAWN);
                    return;
                }
                if ((game.getState() != Game.GameState.ENDING) && ((game.getResourceDeck().isFinished()) && (game.getGoldDeck().isFinished()))) {
                    game.setState(Game.GameState.ENDING);
                    calculateLastMoves();
                } // if the score becomes higher than 20 there would be only one another turn to be played
                nextPhase();
            }else{
                // you can't draw if you still haven't played a card
                game.setLastEvent(ErrorsAssociatedWithExceptions.CARD_NOT_DRAWN);
            }
        }
        game.setLastEvent(new OK(nickname));
    }



    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooserNickname is the nickname of the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     * @throws RemoteException
     */
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException  {
        Player chooser= getPlayerByNickname(chooserNickname);
        try {
            chooser.setPersonalObjective(selectedCard);
        }catch(CardNotOwnedException e) {
            // only if the player is able to partially hack the game
            game.setLastEvent (ErrorsAssociatedWithExceptions.OBJECTIVE_CARD_NOT_OWNED);
        }
    }



    /**
     * This method is invoked to check if all the players have chosen their objective card
     * @throws RemoteException
     */
    public synchronized void checkObjectiveCardChosen() throws RemoteException{
        for(Player p: game.getPlayers()){
            if(p.getPersonalObjectives().size()==2){
                return;
            }
        }
        // finished choosing the objective card: need to start the real Game
        if(!alreadyCheckedObjectiveCards){
            alreadyCheckedObjectiveCards=true;
            game.setLastEvent(new setUpPhaseFinishedEvent());
        }
    }



    /**
     * This method allows a player to choose a Pawn color
     * @param chooserNickname is the nickname of the player who is going to select the Pawn
     * @param selectedColor the chosen colour
     * @throws RemoteException
     * @throws ColorAlreadyTakenException if the pawn has been chosen by another user before
     */
    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, ColorAlreadyTakenException {
        Player chooser= getPlayerByNickname(chooserNickname);
        synchronized (game.getAlreadySelectedColors()) {
            if (!game.getAlreadySelectedColors().contains(selectedColor)) {
                chooser.setColor(selectedColor);
                game.getAlreadySelectedColors().add(selectedColor);
            } else {
                throw new ColorAlreadyTakenException();
            }
        }
        game.setLastEvent(new OK(chooserNickname));
    }



    /**
     * This method is invoked to check if all the players have chosen their pawn color
     * @throws RemoteException
     */
    public synchronized void checkChosenPawnColor() throws RemoteException {
        for(Player p1: game.getPlayers()){
            if(p1.getChosenColor()==null){
                return;
            }
        }
        if(!alreadyCheckedColors) {
            alreadyCheckedColors=true;
            game.chosenPawns();
        }
    }



    /**
     * This method allows the player to send a text message in the chat
     * @param senderNickname is the nickname of the player who sends the message
     * @param receiversNicknames are the nickname of the players who are going to receive the message
     * @param message the string (message) sent
     * @throws RemoteException
     */
    public void sendMessage(String senderNickname, List<String> receiversNicknames, String message) throws RemoteException {
       receiversNicknames.add(senderNickname);
       Chat tmp;
       if(!game.getChats().isEmpty()) {
           tmp=game.getChatByUsers(receiversNicknames);
               if(tmp!=null){
                   receiversNicknames.remove(senderNickname);
                   tmp.sendMessage(new ChatMessage(message, senderNickname, receiversNicknames, new Timestamp(System.currentTimeMillis())));
                   return;
               }
           }
           if (receiversNicknames.size() == game.getnPlayers()) {
               receiversNicknames.remove(senderNickname);
               tmp=game.startGeneralChat();
               for(Observer obs: clientsConnected.values()){
                   tmp.addObserver(obs);
               }
               tmp.sendMessage(new ChatMessage(message, senderNickname, receiversNicknames, new Timestamp(System.currentTimeMillis())));
           }else {
               receiversNicknames.remove(senderNickname);
               tmp = game.startChat(senderNickname, receiversNicknames.get(0));
               for(String s: receiversNicknames){
                   for(String nickname: clientsConnected.keySet()){
                       if(s.equals(nickname)){
                           tmp.addObserver(clientsConnected.get(nickname));
                       }
                   }
               }
               tmp.sendMessage(new ChatMessage(message, senderNickname, receiversNicknames, new Timestamp(System.currentTimeMillis())));
           }
        game.setLastEvent(new OK(senderNickname));
    }



    /**
     * This method is the one setting the right player in turn. If drawCard (decks finished) or playCard (20 points)
     * have triggered the ENDING condition it decreases our indexes to determine when the Game has to end.
     * INTERNAL USE METHOD
     * @throws RemoteException
     */
    private void nextPhase() throws RemoteException {
        if (game.getState() == Game.GameState.ENDING && lastRounds > 0) {
            lastRounds --;
            game.setLastMoves(lastRounds);
            if(lastDrawingRounds>0) {
                lastDrawingRounds--;
            }
            game.nextRound();
        if (game.getState() == Game.GameState.ENDING && lastRounds == 0) {
            // 20 points reached --> the players will have only another round
            endGame();
        }
        } else if (game.getState() == Game.GameState.STARTED) {
            // the order of the players in the list will be changed
            game.nextRound();
        }
    }



    /**
     * This method is called when the ENDING Game condition is triggered to calculate how many moves are left and
     * how many times the players will draw in the next plays.
     * INTERNAL USE METHOD
     * @throws RemoteException
     */
    private void calculateLastMoves() throws RemoteException {
            int firstPlayer = 0;
            lastRounds = game.getnPlayers();

            for (int i = 0; i<game.getnPlayers(); i++) {
                if (game.getPlayers().get(i).isFirst()) {
                    firstPlayer = i;
                }
            }
            if (firstPlayer > 0) {
                lastRounds = lastRounds + firstPlayer;
                lastDrawingRounds=firstPlayer;
            } else if (firstPlayer == 0) {
                lastRounds = game.getnPlayers()*2;
                lastDrawingRounds=game.getnPlayers();
            }
            game.setLastMoves(lastRounds);
        }



    /**
     * This method ends the game. It sets the game state to ENDED, checks all the objectives (2 common objs
     * and 1 personalObj) and adds the points to the correct player.
     * Finally, it checks the winner (or winners) of the game, and puts them in a list called "winners".
     * INTERNAL USE METHOD
     * @throws RemoteException
     */
    private void endGame() throws RemoteException {
        // setting the game state to ENDED
        game.endGame();

        // checking the objectives and adding the points to the correct player
        ObjectiveCard commonObj1 = game.getObjectiveCard1();
        ObjectiveCard commonObj2 = game.getObjectiveCard2();
        ObjectiveCard personalObjective;
        for (int i = 0; i<game.getnPlayers(); i++) {
            personalObjective = game.getPlayers().get(i).getPersonalObjective();
            commonObj1.addPointsToPlayer(game.getPlayers().get(i));
            commonObj2.addPointsToPlayer(game.getPlayers().get(i));
            personalObjective.addPointsToPlayer(game.getPlayers().get(i));
        }
        game.setLastEvent(new updatePlayersOrderEvent(game.getPlayers())); //lista aggiornata con i punti
        game.winner();

        //removing all the observers since the game will not continue
        for(Player p: game.getPlayers()){
            p.removeObservers();
        }
        for(Chat c: game.getChats().values()){
            c.removeObservers();
        }
        game.removeObservers();

        //removing the ID
        serverController.getAllGameControllers().remove(id);

        for(Player p: gamePlayers) {
            serverController.getAllNicknames().remove(p.getNickname());
        }
    }



    /**
     * This method allows the ServerController to add a Client connected to the specific GameController
     * @param nickname is the nickname of the Client
     * @param client is the new added Client
     */
    public void addClient(String nickname, Observer client){
        if(!clientsConnected.containsValue(client)){
            clientsConnected.put(nickname, client);
        }
    }



    /**
     * This method is called remotely by the RMIClient when he asks to create a Lobby or join a Lobby
     * @param nickname is the nickname of the Client
     * @param ro is the client who is joining the game
     */
    public void addRMIClient(String nickname, ClientGeneralInterface ro){
        WrappedObserver wrapObs= new WrappedObserver(ro);
        if(!clientsConnected.containsValue(wrapObs)){
            clientsConnected.put(nickname, wrapObs);
        }
    }



    /**
     * This method is used for receiving the heartbeats to check disconnections
     * @param nickname of the player
     * @throws RemoteException
     */
    public void heartbeat(String nickname) throws RemoteException {
        lastHeartbeatTimesOfEachPlayer.put(nickname,System.currentTimeMillis());
        System.out.println("Received heartbeat at " + lastHeartbeatTimesOfEachPlayer.get(nickname)+ " from "+ nickname);
    }



    /**
     * This method is used for starting the heartbeats to check disconnections
     * @param nickname of the player
     * @throws RemoteException
     */
    public void startHeartbeat(String nickname) throws RemoteException {
        lastHeartbeatTimesOfEachPlayer.put(nickname,System.currentTimeMillis());
        startHeartbeatMonitor(nickname);
    }



    /**
     * This method is used for monitoring the heartbeats of the different clients
     * @param nickname of the player
     */
    private void startHeartbeatMonitor(String nickname) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        var lambdaContext = new Object() {
            ScheduledFuture<?> heartbeatTask;
        };
        lambdaContext.heartbeatTask =scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if (((currentTime - lastHeartbeatTimesOfEachPlayer.get(nickname)) / 1000 > TIMEOUT)||disconnection) {
                disconnection=true;
                if (lambdaContext.heartbeatTask != null && !lambdaContext.heartbeatTask.isCancelled()) {
                    lambdaContext.heartbeatTask.cancel(true); // closing the scheduler
                }
                disconnection();
            }
        }, 0, TIMEOUT, TimeUnit.SECONDS);
    }



    /**
     * This method is used when the server detects a disconnection, so it removes all the observers
     * and removes the gameController of the match where the disconnection happened
     */
    public void disconnection(){ // notify with disconnectionEvent
        synchronized (disconnectionLock) {
            if(firstDisconnection) {
                System.out.println("the server has detected a disconnection");
                game.notifyDisconnectionEvent();
                for (Player p : gamePlayers) {
                    serverController.getAllNicknames().remove(p.getNickname());
                }
                for(Chat c: game.getChats().values()){
                    c.removeObservers();
                }
                for (Player p : game.getPlayers()) {
                    p.removeObservers();
                }
                game.removeObservers();

                try {
                    serverController.getAllGameControllers().remove(id);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                firstDisconnection=false;
            }
        }
    }



    /**
     * Getter method
     * @return the ID of the gameController
     */
    public int getId() {
        return id;
    }



    /**
     * Getter method
     * @return the game of the related gameController
     */
    public Game getGame() {
        return game;
    }



    /**
     * Getter method
     * @return how many rounds there are before closing the game
     */
    public int getLastRounds() {
        return lastRounds;
    }



    /**
     * Getter method
     * @return how many rounds there are before the players can't draw any cards
     */
    public int getLastDrawingRounds() {
        return lastDrawingRounds;
    }



    /**
     * Getter method
     * This method returns the player given his nickname
     * @param Nickname is the nickname we are using to search for a player
     * @return the player if found, null otherwise
     */
    public Player getPlayerByNickname(String Nickname) {
        for(Player player : gamePlayers){
            if(player.getNickname().equals(Nickname)){
                return player;
            }
        }
        return null;
    }



    /**
     * Getter method
     * @return the list of the players in the game
     */
    public List<Player> getGamePlayers() {
        return gamePlayers;
    }



    /**
     * Setter method
     * @param numberOfPlayers who are playing
     * @throws IllegalArgumentException if the number of players is wrong
     * @throws  RemoteException
     * INTERNAL USE METHOD
     */
    public void setNumberOfPlayers(int numberOfPlayers) throws IllegalArgumentException, RemoteException {
        if ((numberOfPlayers >= 2) && (numberOfPlayers <= 4)) {
            this.numberOfPlayers = numberOfPlayers;
        }else throw new IllegalArgumentException("Wrong number of players!");
    }



    /**
     * Setter method
     * @param id of the Game controller
     */
    public void setId(int id) {
        this.id = id;
    }



    /**
     * Setter method
     * @param serverController which manages the different game controllers
     * @throws RemoteException
     */
    public void setServerController(ServerController serverController) throws RemoteException {
        this.serverController = serverController;
    }

}
