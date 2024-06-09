package CODEX.view.GUI;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Game;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

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

    private ScheduledExecutorService scheduler;
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private int network = 0; //1 = rmi  2 = sck
    private GUIPawnsController ctr;
    private Stage stage;
    private Thread pointsThread=null;
    boolean lobbyHasStarted=false;
    private StackPane root;
    private Rectangle overlay;


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


    public void showNoLobbyError() {
        availableLobbies.setOpacity(0);
        lobbyError1.setOpacity(1);
        lobbyError2.setOpacity(1);
        lobbyError3.setOpacity(1);
        joinButton.setOpacity(0);
    }


    public void hideNoLobbyError() {
        availableLobbies.setOpacity(1);
        lobbyError1.setOpacity(0);
        lobbyError2.setOpacity(0);
        lobbyError3.setOpacity(0);
        joinButton.setOpacity(1);
    }


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
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
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
             } catch (RemoteException e) {
                  throw new RuntimeException(e);
             }
        }
    }


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
                    //throw new RuntimeException(e);
                }
                /*
                System.out.println("sto per controllare");
                if (network == 1) {
                    System.out.println("sono nel caso RMI");
                    if (rmiClient.getInGame()) {
                        System.out.println("ho cambiato, lobby partita!!");
                        lobbyHasStarted = true;
                    }
                } else if (network == 2) {
                    if (clientSCK.getInGame()) {
                        lobbyHasStarted = true;
                    }
                }

                 */
            }

           System.out.println("sono prima del runlater, changescene");
           Platform.runLater(this::changeScene);
            // platform.runLater grants that this method is called in the JAVAFX Application thread
           // "this::changeScene" used for a reference to a NON static method (becomes a runnable)
       });
        System.out.println("sto per far partire i puntini");
       pointsThread.start();
    }



    public void changeScene(){

        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pawns.fxml"));
        StackPane nextRoot = null;
        try {
            nextRoot = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }
        stage.setOnCloseRequest(event -> {
            if (network == 1) {
                try {
                    rmiClient.handleDisconnectionFunction();
                } catch (RemoteException ignored) {

                }
            } else if (network == 2) {
                try {
                    clientSCK.handleDisconnectionFunction();
                } catch (RemoteException ignored) {

                }
            }
        });
        // setting the parameters in the new controller, also the BASE CARD (front and back)
        stage.setOnCloseRequest(event -> {
            if (network == 1) {
                try {
                    rmiClient.handleDisconnectionFunction();
                } catch (RemoteException ignored) {

                }
            } else if (network == 2) {
                try {
                    clientSCK.handleDisconnectionFunction();
                } catch (RemoteException ignored) {

                }
            }
        });
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);
       if (network == 1) {
           //while(rmiClient.getPersonalPlayer().getPlayerDeck()[0] == null) {}
               ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", now click the");
               ctr.setColoredPanes();
           } else if (network == 2) {
           //while(clientSCK.getPersonalPlayer().getPlayerDeck()[0] == null) {}
               ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", now click the");
               ctr.setColoredPanes();
        }

        // old dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // new scene
        Scene scene;
        scene = new Scene(nextRoot);

        // Create a transparent overlay
        Rectangle nextOverlay = new Rectangle();
        nextOverlay.setFill(Color.TRANSPARENT);
        nextOverlay.setOnMouseClicked(MouseEvent::consume); // Consume all mouse clicks

        // Bind the overlay's size to the root's size
        nextOverlay.widthProperty().bind(nextRoot.widthProperty());
        nextOverlay.heightProperty().bind(nextRoot.heightProperty());

        ctr.setRoot (nextRoot);
        ctr.setOverlay(nextOverlay);

        if(network==1){
            synchronized (rmiClient.getGuiPawnsControllerLock()) {
                rmiClient.setGuiPawnsController(ctr);
                rmiClient.getGuiPawnsControllerLock().notify();
                rmiClient.setDone(true);
            }
        }if(network==2){ //ancora da implementare
            synchronized (clientSCK.getGuiPawnsControllerLock()) {
                clientSCK.setGuiPawnsController(ctr);
                clientSCK.getGuiPawnsControllerLock().notify();
                clientSCK.setDone(true);
            }
        }
        root.getChildren().remove(overlay); // Remove overlay
        stage.setScene(scene); //questo è il momento in cui la nuova scena viene mostrata

        // setting the od values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);


        //stage.show(); //si fa solo se cambia lo stage
    }


    public void joinLobby() {
        root.getChildren().add(overlay); // Add overlay
        fullLobby.setOpacity(0);
        if (availableLobbies.getValue() != null) {
            if (network == 1) {
                try {
                    System.out.println("sto per chiamare addplayertolobby");
                    rmiClient.addPlayerToLobby(rmiClient.getPersonalPlayer().getNickname(), availableLobbies.getValue());
                    System.out.println("ho chiamato addplayer to lobby e sto per chiamare checknplayers");
                    rmiClient.getGameController().checkNPlayers(); // starts the game if the number of players is correct
                    System.out.println("ho chiamato il checknplayers e chiamo SETWAITINGPLAYERS");
                    setWaitingPlayers();
                } catch (RemoteException | GameNotExistsException | NotBoundException e) {
                    throw new RuntimeException(e);
                } catch (GameAlreadyStartedException | FullLobbyException e) {
                    root.getChildren().remove(overlay); // Remove overlay
                    fullLobby.setOpacity(1); // shows the message error "This lobby is full"
                    updateAvailableLobbies(); // updates the available lobbies
                }
            } else if (network == 2) {
                try {
                    clientSCK.addPlayerToLobby(clientSCK.getPersonalPlayer().getNickname(), availableLobbies.getValue());
                } catch (Exception ignored) {
                }
                if(clientSCK.getErrorState()) {
                    clientSCK.setErrorState(false);
                    root.getChildren().remove(overlay); // Remove overlay
                    fullLobby.setOpacity(1); // shows the message error "This lobby is full"
                    updateAvailableLobbies(); // updates the available lobbies
                }else{
                    clientSCK.checkNPlayers(); // starts the game if the number of players is correct
                    setWaitingPlayers();
                }
            }
        }else{
            root.getChildren().remove(overlay); // Remove overlay
        }
    }


    public void setOnEnterPressed() {
        // JOINING A LOBBY
        availableLobbies.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        // Chiamata alla tua funzione qui
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



    public void createNewLobby(){
            root.getChildren().add(overlay); // Add overlay
            wrongNumber.setOpacity(0);
            int number;
            String input = createText.getText();
            if (!input.isBlank() && (input.equals("2") || input.equals("3") || input.equals("4"))) {
                if (network == 1) {
                    try {
                        rmiClient.createLobby(rmiClient.getPersonalPlayer().getNickname(), Integer.parseInt(input));
                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                } else if (network==2) {
                    try {
                        clientSCK.createLobby(clientSCK.getPersonalPlayer().getNickname(), Integer.parseInt(input));
                    } catch (Exception ignored) {
                    }
                }
                updateAvailableLobbies();
                setWaitingPlayers();
            } else {
                root.getChildren().remove(overlay); // Remove overlay
                wrongNumber.setOpacity(1);
            }
    }

    public void setRmiClient(RMIClient client) {
             this.rmiClient = client;
    }

    public void setClientSCK (ClientSCK client) {
        this.clientSCK = client;
    }

    public void setNetwork (int network) {
        this.network = network;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public void updateGameState() {


            lobbyHasStarted=true;

            System.out.println("lobbyHasStarted: "+ lobbyHasStarted);
    }

    public void setRoot(StackPane root) {
        this.root=root;
    }

    public void setOverlay(Rectangle overlay) {
        this.overlay=overlay;
    }
}
