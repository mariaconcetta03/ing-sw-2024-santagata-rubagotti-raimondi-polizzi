package CODEX.view.GUI;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class GUINicknameController {

    @FXML
    private TextField nickname;
    @FXML
    private Label nicknameUsed;


    private int network = 0; // it means that user hasn't chosen (1 = rmi  2 = sck)
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private AnchorPane root;
    private Rectangle overlay;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private Stage stage;
    boolean correctNickname = false;



    public GUINicknameController() throws RemoteException {
    }



    public void sendNickname() throws IOException {
        Platform.runLater(() -> {
            root.getChildren().add(overlay); // Add overlay
        });
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
                Platform.runLater(() -> {
                    root.getChildren().remove(overlay); // Add overlay
                });
                nicknameUsed.setText("WARNING! The nickname you have selected is already in use, please retry ");
                nicknameUsed.setOpacity(1); // shows the message error
                nickname.clear(); // se il nick è sbagliato, allora cancello il field in modo che l'utente inserisca daccapo
            } else {
                nicknameUsed.setOpacity(0); // not necessary because we will change our window!

                // let's show the new window!
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lobby.fxml"));
                AnchorPane nextRoot  = fxmlLoader.load();
                Scene scene = new Scene(nextRoot);
                // Create a transparent overlay
                Rectangle nextOverlay = new Rectangle();
                nextOverlay.setFill(Color.TRANSPARENT);
                nextOverlay.setOnMouseClicked(MouseEvent::consume); // Consume all mouse clicks

                // Bind the overlay's size to the root's size
                nextOverlay.widthProperty().bind(nextRoot.widthProperty());
                nextOverlay.heightProperty().bind(nextRoot.heightProperty());

                GUILobbyController ctr = fxmlLoader.getController();
                ctr.setRoot (nextRoot );
                ctr.setOverlay(nextOverlay);
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
                Platform.runLater(() -> {
                    root.getChildren().remove(overlay); // Add overlay
                });
                // new scene
                stage.setScene(scene); //viene mostrata la nuova scena
            }
        }else {
            root.getChildren().remove(overlay); // Remove overlay
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

    public void setRoot(AnchorPane root) {
        this.root=root;
    }

    public void setOverlay(Rectangle overlay) {
        this.overlay=overlay;
    }
}


