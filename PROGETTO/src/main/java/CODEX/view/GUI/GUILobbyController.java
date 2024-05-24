package CODEX.view.GUI;

import CODEX.Exceptions.FullLobbyException;
import CODEX.Exceptions.GameAlreadyStartedException;
import CODEX.Exceptions.GameNotExistsException;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class GUILobbyController {

    @FXML
    Label labelWithPlayerName;

    RMIClient rmiClient;
    ClientSCK clientSCK;
    int network = 0; //1 = rmi  2 = sck



    @FXML
    ComboBox<Integer> availableLobbies;

    @FXML
    Label lobbyError1;
    @FXML
    Label lobbyError2;
    @FXML
    Label lobbyError3;
    @FXML
    Button joinButton;
    @FXML
    Label waitingPlayers;
    @FXML
    Button createButton;
    @FXML
    TextField createText;
    @FXML
    Label createLabel;
    @FXML
    Label question1;
    @FXML
    Label question2;
    @FXML
    Label wrongNumber;
    @FXML
    Label joinLabel;
    @FXML
    Label fullLobby;
    @FXML
    Button refreshButton;

    Integer chosenLobby;


    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }


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
                System.out.println("STO AGGIUNTANDO");
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
                 clientSCK.checkAvailableLobby();
                 System.out.println("STO AGGIUNTANDO");
                 availableLobbies.getItems().clear();
                 setAvailableLobbies(clientSCK.getAvailableLobbies());
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
        boolean lobbyHasStarted = false;
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
        new Thread(() -> {
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
            }
        }).start();
    }


    public void joinLobby() {
        fullLobby.setOpacity(0);
        if (availableLobbies.getValue() != null) {
            if (network == 1) {
                try {
                    rmiClient.addPlayerToLobby(rmiClient.getPersonalPlayer().getNickname(), availableLobbies.getValue());
                    setWaitingPlayers();
                } catch (RemoteException | GameNotExistsException | NotBoundException e) {
                    throw new RuntimeException(e);
                } catch (GameAlreadyStartedException | FullLobbyException e) {
                    fullLobby.setOpacity(1); // shows the message error "This lobby is full"
                    updateAvailableLobbies(); // updates the available lobbies
                }
            } else if (network == 2) {
                try {
                    clientSCK.addPlayerToLobby(rmiClient.getPersonalPlayer().getNickname(), availableLobbies.getValue());
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



    void setRmiClient(RMIClient client) {
             this.rmiClient = client;
    }


    void setClientSCK (ClientSCK client) {
        this.clientSCK = client;
    }

    void setNetwork (int network) {
        this.network = network;
    }




}
