package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

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
