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
import CODEX.utils.executableMessages.events.updatePlayersOrderEvent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameController extends UnicastRemoteObject implements GameControllerInterface {
    private static final int TIMEOUT = 7; // seconds
    private boolean disconnection=false; //per controllare quando fermare gli heartbeat agli altri player che non si sono disconnessi ma che giocavano con uno che si è disconnesso
    private boolean firstDisconnection=true; //per chiamre disconnection() solo per il primo player che si disconnette
    Map <String, Long> lastHeartbeatTimesOfEachPlayer;

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

    /**
     * Class constructor, initialises lastRounds and lastDrawingRounds to 10
     */
    public GameController() throws RemoteException {
        super();
        game=null;
        serverController=null;
        lastRounds=10;
        lastDrawingRounds=10;
        winners=new ArrayList<>();
        gamePlayers= new ArrayList<>();
        clientsConnected= new HashMap<>();
        numberOfPlayers=0;
        id=0;
        lastHeartbeatTimesOfEachPlayer=new HashMap<>();

    }

    /**
     * This method creates the Game that will be managed by GameController
     * @param gamePlayers is the List of players that will be in the Game
     */
    public void createGame (List<Player> gamePlayers) throws RemoteException  {
        game = new Game(gamePlayers, id);
        //adding Observers to Game and Player classes
        for(Observer obs: clientsConnected.values()){
            game.addObserver(obs);
        }
        for(Player p: gamePlayers){
            for(Observer obs: clientsConnected.values()){
                p.addObserver(obs);
            }
        }
        game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
    }

    /**
     * This method adds a player to the waiting ones in the lobby (gamePlayers)
     * INTERNAL USE METHOD
     * @param player is the one player added to the lobby
     * @throws ArrayIndexOutOfBoundsException if the of players is exceeded
     */
    public void addPlayer(Player player) throws ArrayIndexOutOfBoundsException, RemoteException  {
        if (gamePlayers.size() < numberOfPlayers) {
            gamePlayers.add(player);
        } else {
            throw new ArrayIndexOutOfBoundsException("This lobby is already full!"); //@TODO will never actually be thrown
        }
        /*
        if (gamePlayers.size() == numberOfPlayers) {
            createGame(gamePlayers);
            try {
                startGame();
            } catch (IllegalStateException e) {
                System.out.println("The game is already started!");
            }
        }
         */
    }

    public void checkNPlayers() throws RemoteException{
        if (gamePlayers.size() == numberOfPlayers) {
            createGame(gamePlayers);
            try {
                startGame();
            } catch (IllegalStateException e) {
                System.out.println("The game is already started!");
            }
        }
    }


    /**
     * This method starts the game. The game state is set to STARTED. 2 objective cards are given to each
     * player, and he will need to choose one of these. Then the market is completed and each player receives
     * 3 cards (2 resource cards and 1 gold card).
     * INTERNAL USE METHOD
     * @throws IllegalArgumentException if there's an invalid game status
     */
    public void startGame() throws IllegalStateException, RemoteException {
        if(game.getState() == Game.GameState.WAITING_FOR_START) {
            game.startGame();
            game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
        } else {
            game.setLastEvent(ErrorsAssociatedWithExceptions.INVALID_GAME_STATUS); //@TODO will never be thrown
            throw new IllegalStateException();
        }
    }


    /**
     * This method let the Player place the baseCard (in the middle of the table) and, if all the players
     * have placed their baseCard, it let the Game finish the set-up phase giving the last necessary cards
     * @param nickname the player who plays the card
     * @param baseCard the base card played
     * @param orientation of the played card
     */
    public void playBaseCard(String nickname, PlayableCard baseCard, boolean orientation) {
        Player player= getPlayerByNickname(nickname);
        player.playBaseCard(orientation, baseCard);
        player.getPlayerDeck()[0]=null; //the player played the baseCard
    }
    public void checkBaseCardPlayed() throws RemoteException {
        PlayableCard[][] tmp;
        //for all players in the game
        for(Player p1: game.getPlayers()){
            tmp=p1.getBoard().getTable();
            //if the players haven't all played their baseCard
            if(tmp[p1.getBoard().getBoardDimensions()/2][p1.getBoard().getBoardDimensions()/2]==null){
                game.setLastEvent(ErrorsAssociatedWithExceptions.OK); //necessario?
                return;
            }
        }
        game.giveInitialCards();
        game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
    }

    /**
     * This method "plays" the card selected by the Player in his own Board
     * @param nickname is the nickname of the Player that wants to play a card
     * @param selectedCard the Card the Player wants to play
     * @param position the position where the Player wants to play the Card
     * @param orientation the side on which the Player wants to play the Card
     */
    public void playCard(String nickname, PlayableCard selectedCard, Coordinates position, boolean orientation) throws RemoteException,IllegalArgumentException{
        Player currentPlayer = game.getPlayers().get(0);
        if (!getPlayerByNickname(nickname).equals(currentPlayer)) {
            System.out.println("NON E IL TUO TURNO, NON PUOI GIOCARE LA CARTA");
            game.setLastEvent(ErrorsAssociatedWithExceptions.NOT_YOUR_TURN);
        }else{
            try {
                currentPlayer.playCard(selectedCard, position, orientation);
                if ((game.getState() != Game.GameState.ENDING) && (currentPlayer.getPoints() >= 20) && (lastRounds==10)) {
                    game.setState(Game.GameState.ENDING);
                    calculateLastMoves();
                }
                if(lastDrawingRounds<=0){
                    nextPhase();
                }
                game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
            } catch (IllegalArgumentException e) {
                game.setLastEvent(ErrorsAssociatedWithExceptions.UNABLE_TO_PLAY_CARD); //così però non me lo scrive...@TODO RIMUOVERE
                throw e; //@TODO da rimuovere try/catch
            }
        }
        game.setLastEvent(new OK(nickname));
    }

    /**
     * This method allows the currentPlayer to draw a card from the decks or from the unveiled ones
     * @param nickname of the player who is going to draw the card
     * @param selectedCard is the Card the Players wants to draw
     */
    public void drawCard(String nickname, PlayableCard selectedCard) throws RemoteException { //we can draw a card from one of the decks or from the uncovered cards
        Player currentPlayer = game.getPlayers().get(0);
        if (!getPlayerByNickname(nickname).equals(currentPlayer)) {
            System.out.println("Not your turn, you can't draw!");
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
                    game.setLastEvent(ErrorsAssociatedWithExceptions.CARD_NOT_DRAWN); //@TODO probabilmente da passare
                    return;
                }catch(EmptyStackException e){
                    System.out.println(e.getMessage());
                    game.setLastEvent(ErrorsAssociatedWithExceptions.CARD_NOT_DRAWN);
                    return;
                }
                if ((game.getState() != Game.GameState.ENDING) && ((game.getResourceDeck().isFinished()) && (game.getGoldDeck().isFinished()))) {
                    game.setState(Game.GameState.ENDING);
                    calculateLastMoves();
                } //if the score become higher than 20 there would be only one another turn to be played
                nextPhase();
                game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
            }else{
                //non hai ancora giocato la carta, non puoi pescare!!!
                game.setLastEvent(ErrorsAssociatedWithExceptions.CARD_NOT_DRAWN);
            }
        }
        game.setLastEvent(new OK(nickname));
    }

    /**
     * This method allows the chooser Player to select his personal ObjectiveCard
     * @param chooserNickname is the nickname of the player selecting the ObjectiveCard
     * @param selectedCard is the ObjectiveCard the player selected
     */
    public void chooseObjectiveCard(String chooserNickname, ObjectiveCard selectedCard) throws RemoteException  {
        Player chooser= getPlayerByNickname(chooserNickname);
        try {
            chooser.setPersonalObjective(selectedCard);
            game.setLastEvent (ErrorsAssociatedWithExceptions.OK);
        }catch(CardNotOwnedException e){
            game.setLastEvent (ErrorsAssociatedWithExceptions.OBJECTIVE_CARD_NOT_OWNED); //@TODO will never be thrown
        }
    }
    public void checkObjectiveCardChosen() throws RemoteException{
        for(Player p: game.getPlayers()){
            if(p.getPersonalObjectives().size()==2){
                return;
            }
        }
        game.setLastEvent(ErrorsAssociatedWithExceptions.SETUP_PHASE_2); //finished choosing the objective card: need to start the real Game
        //@TODO removing lastEvent, add an update for having chosen the obj cards
    }


    /**
     * This method allows a player to choose a Pawn color
     * @param chooserNickname is the nickname of the player who is going to select the Pawn
     * @param selectedColor the chosen colour
     */

    public void choosePawnColor(String chooserNickname, Pawn selectedColor) throws RemoteException, ColorAlreadyTakenException {
        Player chooser= getPlayerByNickname(chooserNickname);
        synchronized (game.getAlreadySelectedColors()) {
            if (!game.getAlreadySelectedColors().contains(selectedColor)) {
                chooser.setColor(selectedColor);
                game.getAlreadySelectedColors().add(selectedColor);
            } else {
                throw new ColorAlreadyTakenException();
                //game.setLastEvent(ErrorsAssociatedWithExceptions.NOT_AVAILABLE_PAWN); //lancio eccezione?
            }
        }
        game.setLastEvent(new OK(chooserNickname));
    }
    public void checkChosenPawnColor() throws RemoteException {
        for(Player p1: game.getPlayers()){
            if(p1.getChosenColor()==null){
                return;
            }
        }
        game.chosenPawns();
    }

    /**
     * This method allows the player to send a text message in the chat
     * @param senderNickname is the nickname of the player who sends the message
     * @param receiversNicknames are the nickname of the players who are going to receive the message
     * @param message the string (message) sent
     */

    public void sendMessage(String senderNickname, List<String> receiversNicknames, String message)throws RemoteException {
        /*
        Player sender = getPlayerByNickname(senderNickname);
        List<Player> receivers= new ArrayList<>();
        for (String nick : receiversNicknames){
            receivers.add(getPlayerByNickname(nick));
        }
         */
       receiversNicknames.add(senderNickname);
       Chat tmp;
       if(!game.getChats().isEmpty()) {
           tmp=game.getChatByUsers(receiversNicknames);
               if(tmp!=null){
                   receiversNicknames.remove(senderNickname);
                   tmp.sendMessage(new ChatMessage(message, senderNickname, receiversNicknames, new Timestamp(System.currentTimeMillis())));
                   game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
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
        game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
        game.setLastEvent(new OK(senderNickname));
    }


    /**
     * This method is the one setting the right player in turn. If drawCard (decks finished) or playCard (20 points)
     * have triggered the ENDING condition it decreases our indexes to determine when the Game has to end.
     * INTERNAL USE METHOD
     */
    private void nextPhase()throws RemoteException {
        if (game.getState() == Game.GameState.ENDING && lastRounds > 0) {
            lastRounds --;
            game.setLastMoves(lastRounds);
            if(lastDrawingRounds>0) {
                lastDrawingRounds--;
            }
            game.nextRound();
        if (game.getState() == Game.GameState.ENDING && lastRounds == 0) { //20 points reached the players will have only another round
            endGame();
        }
        } else if (game.getState() == Game.GameState.STARTED) {
            game.nextRound(); //the order of the players in the list will be changed
        }
        game.setLastEvent(ErrorsAssociatedWithExceptions.OK);
    }

    /**
     * This method is called when the ENDING Game condition is triggered to calculate how many moves are left and
     * how many times the players will draw in the next plays.
     * INTERNAL USE METHOD
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
                lastRounds = lastRounds + firstPlayer; //ok; tolto -1
                lastDrawingRounds=firstPlayer;
            } else if (firstPlayer == 0) {
                lastRounds = game.getnPlayers()*2; //se chi innesca ENDING è il firstPlayer; tolto -1
                lastDrawingRounds=game.getnPlayers();
            }
            game.setLastMoves(lastRounds);
        }



        //metodo da togliere (gestiamo l'uscita dal gioco con le disconnessioni)
    /**
     * This method let the player leave the game anytime during the match and also closes the Game itself
     * @param nickname of the player who is going leave the game
     * @throws IllegalArgumentException if the specific Player is not part of the Game
     */
    public void leaveGame(String nickname) throws IllegalArgumentException, RemoteException {
        Player tmp=getPlayerByNickname(nickname);

        if(tmp==null){
            throw new IllegalArgumentException("This player is not playing the match");
        }else {
            if (!(game.getState().equals(Game.GameState.ENDED))) { //se è durante la partita ATTIVA
                //this will alert the listeners to notify all the players that the game has ENDED
                game.setLastEvent(ErrorsAssociatedWithExceptions.GAME_LEFT);

                //removing all the observers since the game will not continue
                for (Player p : game.getPlayers()) {
                    p.removeObservers();
                }
                game.removeObservers();
                game.setState(Game.GameState.ENDED);
                serverController.getAllGameControllers().remove(id); //il gamecontroller si "auto"rimuove dal server controller

            }
        }
    }


    /**
     * This method ends the game. It sets the game state to ENDED, checks all the objectives (2 common objs
     * and 1 personalObj) and adds the points to the correct player.
     * Finally, it checks the winner (or winners) of the game, and puts them in a list called "winners".
     * INTERNAL USE METHOD
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
        // checking the winner(s)
        //winners = game.winner(); //@TODO dovremmo comunicare noi dal Server il vincitore ai client
        game.winner();
        /*
        if(winners.size()==1) { //just for testing
            System.out.println(winners.get(0).getNickname() + " WON!!!");
        }else if(winners.size()>1){
            for (Player p: winners){
                System.out.print(p.getNickname()+", ");
            }
            System.out.println("tied!");
        }

         */
        game.setLastEvent (ErrorsAssociatedWithExceptions.OK);
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
        //
        for(Player p: gamePlayers) {
            serverController.getAllNicknames().remove(p.getNickname());
        }
        System.out.println("Ho fatto tutte le rimozioni");
    }



    /**
     * Setter method
     * @param numberOfPlayers who are playing
     * @throws IllegalArgumentException if the number of players is wrong
     * INTERNAL USE METHOD
     */
    public void setNumberOfPlayers(int numberOfPlayers) throws IllegalArgumentException, RemoteException {
        if ((numberOfPlayers >= 2)&&(numberOfPlayers <= 4)) {
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

    public int getLastRounds() {
        return lastRounds;
    }

    public int getLastDrawingRounds() {
        return lastDrawingRounds;
    }

    /**
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

    public List<Player> getGamePlayers() {
        return gamePlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setServerController(ServerController serverController) throws RemoteException {
        this.serverController = serverController;
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
    public void heartbeat(String nickname) throws RemoteException{ //qui va passato l'identificativo del player per settare il lastHeartbeatTime giusto
        lastHeartbeatTimesOfEachPlayer.put(nickname,System.currentTimeMillis());
        System.out.println("Received heartbeat at " + lastHeartbeatTimesOfEachPlayer.get(nickname)+ " from "+ nickname);
    }
    public void startHeartbeat(String nickname) throws RemoteException { //viene chiamato una sola volta, prima del primo heartbeat
        lastHeartbeatTimesOfEachPlayer.put(nickname,System.currentTimeMillis());
        startHeartbeatMonitor(nickname);
    }


    private void startHeartbeatMonitor(String nickname) { //scheduler.shutdownNow(); in caso di connection lost o Game ENDED
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        var lambdaContext = new Object() {
            ScheduledFuture<?> heartbeatTask;
        };
        lambdaContext.heartbeatTask =scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if (((currentTime - lastHeartbeatTimesOfEachPlayer.get(nickname)) / 1000 > TIMEOUT)||disconnection) { //se un solo player si disconnette si finisce di mandare heartbeat a tutti gli altri player
                disconnection=true;
                if (lambdaContext.heartbeatTask != null && !lambdaContext.heartbeatTask.isCancelled()) {
                    lambdaContext.heartbeatTask.cancel(true); //chiude lo scheduler
                }
                //caso in cui il client risulta irragiungibile->handleDisconnection: vanno avvisati i player e chiuso tutto
                disconnection();
            }
        }, 0, TIMEOUT, TimeUnit.SECONDS); //usciti da qua se il il server ha rilevato la prima disconnessione sono già stati mandati gli updates disconnectionEvent a tutti i players
    }
    public void disconnection(){ //notify con disconnectionEvent
        //ATTENZIONE: togliere dagli observers il client che ha effettuato la disconnessione prima di mandare notify all
        //nel caso di più disconnessioni contemporanee potrebbero non essere tolti tutti coloro che hannno fatto la disconnessione
        //dagli observers e quindi potrebbero venir lanciate eccezioni.
        //ATTENZIONE: la disconnessione potrebbe venir rilevata da una RemoteException al posto che da un heartbeat: anche in quel caso va chiamato disconnection()


        //scopiazzo quello che era leaveGame senza mandare gli update che mostrano i vincitori con i relativi punti

        synchronized (disconnectionLock) {
            if(firstDisconnection) {
                System.out.println("the server has detected a disconnection");
                game.notifYDisconnectionEvent(); //@TODO si puo fare con attributo disconnected, e poi new disconnectionEvent()
                for (Player p : gamePlayers) {
                    serverController.getAllNicknames().remove(p.getNickname());
                }
//mancano le chat
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

    // lato client quando i vari metodi del GameController lanciano un'eccezione:
    // disconnection=true; //questo solo per RMI forse
    // if(firstDisconnection){
    //   firstDisconnection=false; //chiamo solo una volta disconnection() anche se sono più client a disconnettersi
    //   disconnection(); //bisogna settare qualche parametro in caso di più disconnection() in contemporanea per non mandare troppi disconnectionEvent
    // }
    //devo usare questi getter per farlo:
    public boolean getDisconnection(){
        return this.disconnection;
    }
    public boolean getFirstDisconnection(){
        return this.firstDisconnection;
    }

    public void setFirstDisconnection(boolean firstDisconnection) {
        this.firstDisconnection=firstDisconnection;
    }
}
