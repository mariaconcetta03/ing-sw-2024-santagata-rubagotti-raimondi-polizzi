package CODEX.view.GUI;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class GUINicknameController {

    public Button sendNicknameButton;
    @FXML
    private TextField nickname;
    @FXML
    private Label nicknameUsed;


    private int network = 0; // it means that user hasn't chosen (1 = rmi  2 = sck)
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private Stage stage;
    boolean correctNickname = false;



    public GUINicknameController() throws RemoteException {
    }



    public synchronized void sendNickname() throws IOException {
        String input = nickname.getText();
        if(!input.isBlank()) {
            System.out.println(nickname.getCharacters());
            System.out.println("prec NET" + network);

            if (network == 1) { //RMI
                correctNickname = rmiClient.setNickname(nickname.getCharacters().toString());
            } else if (network == 2) { //SCK
                clientSCK.setErrorState(false);
                try {
                    clientSCK.chooseNickname(nickname.getCharacters().toString());
                } catch (RemoteException | NotBoundException ignored) {
                }
                correctNickname = clientSCK.setNickname(nickname.getCharacters().toString());
            }

            if (!correctNickname) {
                nicknameUsed.setText("WARNING! The nickname you have selected is already in use, please retry ");
                nicknameUsed.setOpacity(1); // shows the message error
                nickname.clear(); // se il nick è sbagliato, allora cancello il field in modo che l'utente inserisca daccapo
            } else {
                nicknameUsed.setOpacity(0); // not necessary because we will change our window!
                nickname.disabledProperty();
                sendNicknameButton.disabledProperty();
                // let's show the new window!
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lobby.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                GUILobbyController ctr = fxmlLoader.getController();
                if(network==1){
                    synchronized (rmiClient.getGuiGamestateLock()) {
                        rmiClient.setGuiLobbyController(ctr);
                        rmiClient.getGuiGamestateLock().notify();
                    }
                }if(network==2){
                    synchronized (clientSCK.getGuiGamestateLock()) {
                        clientSCK.setGuiLobbyController(ctr);
                        clientSCK.getGuiGamestateLock().notify();
                    }
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
                ctr.setStage(stage);

                // old dimensions and position
                double width = stage.getWidth();
                double height = stage.getHeight();
                double x = stage.getX();
                double y = stage.getY();

                // new scene
                stage.setScene(scene);

                // setting the od values of position and dimension
                stage.setWidth(width);
                stage.setHeight(height);
                stage.setX(x);
                stage.setY(y);

                // setting the dynamic parameters of the new window
                System.out.println("NET" + network);

                ctr.setOnEnterPressed();

                if (network == 1) {
                    ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", now join a lobby");
                    ctr.setAvailableLobbies(rmiClient.getAvailableLobbies());
                    ctr.setRmiClient(rmiClient);
                    ctr.setNetwork(1);
                } else if (network == 2) {
                    clientSCK.checkAvailableLobby();
                    ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", now join a lobby");
                    ctr.setAvailableLobbies(clientSCK.getAvailableLobbies().stream().toList());
                    ctr.setClientSCK(clientSCK);
                    ctr.setNetwork(2);
                }
            }
        }else {
            nicknameUsed.setText("INVALID NICKNAME");
            nicknameUsed.setOpacity(1);
            nickname.clear();
        }
    }



    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }

    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK = clientSCK;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public void setNicknameOnKeyPressed() {
        nickname.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Chiamata alla tua funzione qui
                try {
                    sendNickname();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                nicknameUsed.setOpacity(0);
            }
        });
    }
}


