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
import javafx.stage.Stage;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

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
    private GUIBaseCardController ctr;
    private Stage stage;



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
       Thread t= new Thread(() -> {
            boolean lobbyHasStarted = false;
            while (!lobbyHasStarted) {
                try {
                    // Update text on the JavaFX Application Thread
                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players"));
                    Thread.sleep(1000);

                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players."));
                    Thread.sleep(1000);

                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players.."));
                    Thread.sleep(1000);

                    Platform.runLater(() -> waitingPlayers.setText("Waiting for players..."));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
            }
           Platform.runLater(this::changeScene);
            // platform.runLater grants that this method is called in the JAVAFX Application thread
           // "this::changeScene" used for a reference to a NON static method (becomes a runnable)
       });
       t.start();
    }



    public void changeScene(){
        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/baseCard.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }

        // setting the parameters in the new controller, also the BASE CARD (front and back)
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);
       if (network == 1) {
           while(rmiClient.getPersonalPlayer().getPlayerDeck()[0] == null) {}
               ctr.setBaseCard1(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId()); // OK
               ctr.setBaseCard2(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
               ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", which side do you");
           } else if (network == 2) {
           while(clientSCK.getPersonalPlayer().getPlayerDeck()[0] == null) {}
           ctr.setBaseCard1(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId()); // OK
           ctr.setBaseCard2(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId());
           ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", which side do you");
        }

        // old dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // new scene
        Scene scene;
        scene = new Scene(root);

        stage.setScene(scene); //questo è il momento in cui la nuova scena viene mostrata

        // setting the od values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);

        if(network==2) { //implementato solo per tcp
            ctr.startPeriodicDisconnectionCheck();
        }

        //stage.show(); //si fa solo se cambia lo stage
    }


    public void joinLobby() {
        fullLobby.setOpacity(0);
        if (availableLobbies.getValue() != null) {
            if (network == 1) {
                try {
                    rmiClient.addPlayerToLobby(rmiClient.getPersonalPlayer().getNickname(), availableLobbies.getValue());
                    rmiClient.getGameController().checkNPlayers(); // starts the game if the number of players is correct
                    setWaitingPlayers();
                } catch (RemoteException | GameNotExistsException | NotBoundException e) {
                    throw new RuntimeException(e);
                } catch (GameAlreadyStartedException | FullLobbyException e) {
                    fullLobby.setOpacity(1); // shows the message error "This lobby is full"
                    updateAvailableLobbies(); // updates the available lobbies
                }
            } else if (network == 2) {
                try {
                    clientSCK.addPlayerToLobby(clientSCK.getPersonalPlayer().getNickname(), availableLobbies.getValue());
                    clientSCK.checkNPlayers(); // starts the game if the number of players is correct
                    setWaitingPlayers();
                } catch (GameNotExistsException | NotBoundException | RemoteException e) {
                    throw new RuntimeException(e);
                } catch (GameAlreadyStartedException | FullLobbyException e) {
                    fullLobby.setOpacity(1); // shows the message error "This lobby is full"
                    updateAvailableLobbies(); // updates the available lobbies
                }
            }
        }
    }


    public void createNewLobby(){
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

}
