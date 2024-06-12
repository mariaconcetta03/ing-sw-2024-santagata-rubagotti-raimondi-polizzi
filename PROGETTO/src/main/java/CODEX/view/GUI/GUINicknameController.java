package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * This class is uses to manage the nickname scene
 */
public class GUINicknameController {

    @FXML
    private Button sendNicknameButton;
    @FXML
    private TextField nickname;
    @FXML
    private Label nicknameUsed;

    private int network = 0; //1 = rmi  2 = sck
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    boolean correctNickname = false;


    /**
     * This method makes possible choosing a nickname before evry action
     * @throws IOException
     */
    public synchronized void sendNickname() throws IOException {
        String input = nickname.getText();
        if(!input.isBlank()) {
            System.out.println(nickname.getCharacters());
            System.out.println("prec NET" + network);

            if (network == 1) { //RMI
                correctNickname = rmiClient.setNickname(nickname.getCharacters().toString());
            } else if (network == 2) { //SCK
                clientSCK.setErrorState(false);

                    clientSCK.chooseNickname(nickname.getCharacters().toString());

                correctNickname = clientSCK.setNickname(nickname.getCharacters().toString());
            }

            if (!correctNickname) {
                nicknameUsed.setText("WARNING! The nickname you have selected is already in use, please retry ");
                nicknameUsed.setOpacity(1); // shows the message error
                nickname.clear();
            } else {
                nicknameUsed.setOpacity(0);
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

                            rmiClient.handleDisconnectionFunction();

                    } else if (network == 2) {

                            clientSCK.handleDisconnectionFunction();

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
        } else {
            nicknameUsed.setText("INVALID NICKNAME");
            nicknameUsed.setOpacity(1);
            nickname.clear();
        }
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
     * @param rmiClient which is the client of the player
     */
    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }



    /**
     * Setter method
     * @param clientSCK which is the client of the player
     */
    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK = clientSCK;
    }



    /**
     * Setter method
     * @param network which is the type of client of the player
     */
    public void setNetwork(int network) {
        this.network = network;
    }



    /**
     * This method makes possible using enter when choosing nickname and setting it
     */
    public void setNicknameOnKeyPressed() {
        nickname.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    sendNickname();
                } catch (IOException ignored) {
                }
            }else {
                nicknameUsed.setOpacity(0);
            }
        });
    }
}


