package CODEX.view.GUI;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


/**
 * This class represents the controller of the scene when loading the game
 */
public class GUILobbyController {

    @FXML
    private Label labelWithPlayerName;
    @FXML
    private ComboBox<Integer> availableLobbies;
    @FXML
    private Label lobbyError1;
    @FXML
    private Label lobbyError2;
    @FXML
    private Label lobbyError3;
    @FXML
    private Button joinButton;
    @FXML
    private Label waitingPlayers;
    @FXML
    private Button createButton;
    @FXML
    private TextField createText;
    @FXML
    private Label createLabel;
    @FXML
    private Label question1;
    @FXML
    private Label question2;
    @FXML
    private Label wrongNumber;
    @FXML
    private Label joinLabel;
    @FXML
    private Label fullLobby;
    @FXML
    private Button refreshButton;

    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private int network = 0; //1 = rmi  2 = sck
    private GUIPawnsController ctr;
    private Stage stage;
    private Thread pointsThread=null;
    boolean lobbyHasStarted=false;



    /**
     * This method sets the opacity of
     * the available lobbies to 0 because there are no lobbies to which a player can enter
     */
    public void showNoLobbyError() {
        availableLobbies.setOpacity(0);
        lobbyError1.setOpacity(1);
        lobbyError2.setOpacity(1);
        lobbyError3.setOpacity(1);
        joinButton.setOpacity(0);
    }



    /**
     * This method sets the opacity of
     * the available lobbies to 1 because there are more lobbies disposable
     */
    public void hideNoLobbyError() {
        availableLobbies.setOpacity(1);
        lobbyError1.setOpacity(0);
        lobbyError2.setOpacity(0);
        lobbyError3.setOpacity(0);
        joinButton.setOpacity(1);
    }



    /**
     * This method updates the available lobbies, if there are new ones
     */
    public void updateAvailableLobbies() {
        if (network == 1) { // RMI
            try {
                availableLobbies.getItems().clear();
                setAvailableLobbies(rmiClient.getAvailableLobbies());
                if (rmiClient.getAvailableLobbies().isEmpty()) {
                    showNoLobbyError();
                } else {
                    hideNoLobbyError();
                }
            } catch (RemoteException alreadyCaught) {}
        } else if (network == 2) { // TCP
             try {
                 availableLobbies.getItems().clear();
                 clientSCK.checkAvailableLobby();
                 setAvailableLobbies(clientSCK.getAvailableLobbies().stream().toList());
                 System.out.println("le lobby disponibili sono:");
                 clientSCK.printLobby(clientSCK.getAvailableLobbies());
                 if (clientSCK.getAvailableLobbies().isEmpty()) {
                     showNoLobbyError();
                 } else {
                     hideNoLobbyError();
                 }
             } catch (RemoteException ignored) {}
        }
    }



    /**
     * This method will change the scene to the next one, where the player has to choose the pawn
     */
    public void changeScene(){

        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pawns.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException ignored) {
        }

        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }
        stage.setOnCloseRequest(event -> {
            if (network == 1) {
                try {
                    rmiClient.handleDisconnectionFunction();
                } catch (RemoteException alreadyCaught) {}
            } else if (network == 2) {
                try {
                    clientSCK.handleDisconnectionFunction();
                } catch (RemoteException ignored) {}
            }
        });

        // setting the parameters in the new controller
        stage.setOnCloseRequest(event -> {
            if (network == 1) {
                try {
                    rmiClient.handleDisconnectionFunction();
                } catch (RemoteException alreadyCaught) {}
            } else if (network == 2) {
                try {
                    clientSCK.handleDisconnectionFunction();
                } catch (RemoteException ignored) {}
            }
        });

        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);

       if (network == 1) {

               ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", now click the");
               ctr.setColoredPawns();
           } else if (network == 2) {

               ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", now click the");
               ctr.setColoredPawns();
        }

        // old dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // new scene
        Scene scene;
        scene = new Scene(root);

        if(network==1){
            synchronized (rmiClient.getGuiPawnsControllerLock()) {
                rmiClient.setGuiPawnsController(ctr);
                rmiClient.getGuiPawnsControllerLock().notify();
                rmiClient.setDone(true);
            }
        }if(network==2){
            synchronized (clientSCK.getGuiPawnsControllerLock()) {
                clientSCK.setGuiPawnsController(ctr);
                clientSCK.getGuiPawnsControllerLock().notify();
                clientSCK.setDone(true);
            }
        }

        stage.setScene(scene);

        // setting the old values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);
    }



    /**
     * This method adds the players to a specific lobby
     */
    public synchronized void joinLobby() {
        fullLobby.setOpacity(0);
        if (availableLobbies.getValue() != null) {
            if (network == 1) {
                try {
                    System.out.println("sto per chiamare addplayertolobby");
                    rmiClient.addPlayerToLobby(rmiClient.getPersonalPlayer().getNickname(), availableLobbies.getValue());
                    System.out.println("ho chiamato addplayer to lobby e sto per chiamare checknplayers");
                    rmiClient.getGameController().checkNPlayers(); // starts the game if the number of players is correct
                    System.out.println("ho chiamato il checknplayers e chiamo SETWAITINGPLAYERS");
                    createButton.disabledProperty();
                    joinButton.disabledProperty();
                    setWaitingPlayers();
                } catch (RemoteException | GameNotExistsException | NotBoundException alreadyCaught) {
                } catch (GameAlreadyStartedException | FullLobbyException e) {
                    System.out.println("RMI: QUESTA LOBBY A CUI STAI CERCANDO DI AGGIUNGERTI è PIENA!");
                    fullLobby.setOpacity(1); // shows the message error "This lobby is full"
                    updateAvailableLobbies(); // updates the available lobbies
                }
            } else if (network == 2) {
                try {
                    clientSCK.addPlayerToLobby(clientSCK.getPersonalPlayer().getNickname(), availableLobbies.getValue());
                } catch (Exception ignored) {
                }
                if (clientSCK.getErrorState()) {
                    clientSCK.setErrorState(false);
                    System.out.println("TCP: QUESTA LOBBY A CUI STAI CERCANDO DI AGGIUNGERTI è PIENA!");
                    fullLobby.setOpacity(1); // shows the message error "This lobby is full"
                    updateAvailableLobbies(); // updates the available lobbies
                } else {
                    clientSCK.checkNPlayers(); // starts the game if the number of players is correct
                    createButton.disabledProperty();
                    joinButton.disabledProperty();
                    setWaitingPlayers();
                }
            }
        }
        System.out.println("eccoci");
    }



    /**
     * This method is called when the player wants to create a new lobby
     */
    public synchronized void createNewLobby(){
            wrongNumber.setOpacity(0);
            String input = createText.getText();
            if (!input.isBlank() && (input.equals("2") || input.equals("3") || input.equals("4"))) {
                if (network == 1) {
                    try {
                        rmiClient.createLobby(rmiClient.getPersonalPlayer().getNickname(), Integer.parseInt(input));
                        createButton.disabledProperty();
                        joinButton.disabledProperty();
                    } catch (RemoteException | NotBoundException alreadyCaught) {}
                } else if (network==2) {
                    try {
                        clientSCK.createLobby(clientSCK.getPersonalPlayer().getNickname(), Integer.parseInt(input));
                    } catch (Exception ignored) {}
                    createButton.disabledProperty();
                    joinButton.disabledProperty();
                }
                updateAvailableLobbies();
                setWaitingPlayers();
            } else {
                wrongNumber.setOpacity(1);
            }
    }



    /**
     * This method updates the game state to STARTED
     */
    public void updateGameState() {
        lobbyHasStarted=true;
        System.out.println("lobbyHasStarted: "+ lobbyHasStarted);
    }



    /**
     * Setter method
     * This method makes the players waiting for the game to start
     */
    public void setWaitingPlayers() {
        availableLobbies.setOpacity(0);
        lobbyError1.setOpacity(0);
        lobbyError2.setOpacity(0);
        lobbyError3.setOpacity(0);
        joinButton.setOpacity(0);
        createButton.setOpacity(0);
        createText.setOpacity(0);
        question1.setOpacity(0);
        question2.setOpacity(0);
        wrongNumber.setOpacity(0);
        createLabel.setOpacity(0);
        joinLabel.setOpacity(0);
        refreshButton.setOpacity(0);
        waitingPlayers.setOpacity(1);
        fullLobby.setOpacity(0);

        availableLobbies.disabledProperty();
        createText.disabledProperty();
        refreshButton.disabledProperty();

        // Dynamic text update in a separate thread
        this.pointsThread= new Thread(() -> {
            while (!lobbyHasStarted) {
                try {
                    // Update text on the JavaFX Application Thread
                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players"));
                    Thread.sleep(500);

                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players."));
                    Thread.sleep(500);

                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players.."));
                    Thread.sleep(500);

                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players..."));
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    pointsThread.interrupt();
                }

            }

            System.out.println("sono prima del runlater, changescene");
            Platform.runLater(this::changeScene);
        });
        System.out.println("sto per far partire i puntini");
        pointsThread.start();
    }



    /**
     * This method makes possible to use enter when joining a lobby or creating ones and so creatinng one
     */
    public void setOnEnterPressed() {
        // JOINING A LOBBY
        availableLobbies.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                joinLobby();
            }
        });

        // CREATING A LOBBY
        createText.setOnKeyPressed(event-> {
            if(event.getCode() == KeyCode.ENTER){
                createNewLobby();
            }
        });
    }



    /**
     * Setter method
     * @param client which is the client of the player
     */
    public void setRmiClient(RMIClient client) {
             this.rmiClient = client;
    }



    /**
     * Setter method
     * @param client which is the client of the player
     */
    public void setClientSCK (ClientSCK client) {
        this.clientSCK = client;
    }



    /**
     * Setter method
     * @param network which is the type of client of the player
     */
    public void setNetwork (int network) {
        this.network = network;
    }



    /**
     * Setter method
     * @param stage of the scene which will be changed here
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }



    /**
     * Setter method
     * @param text which is the name of the player
     */
    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }



    /**
     * Setter method
     * @param lobby sets the avialable lobbies to which the player can partecipate
     */
    public void setAvailableLobbies(List<Integer> lobby){
        for (int i = 0; i < lobby.size(); i++){
            availableLobbies.getItems().add(lobby.get(i));
        }
        if (lobby.isEmpty()) {
            showNoLobbyError();
        } else {
            hideNoLobbyError();
        }
    }

}
