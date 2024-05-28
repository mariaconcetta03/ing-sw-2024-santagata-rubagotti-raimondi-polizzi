package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Coordinates;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.image.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;
import javafx.scene.control.Label;
public class GUIBaseCardController {

    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;

    @FXML
    private Label labelWithPlayerName;

    @FXML
    private ImageView baseCard1;

    @FXML
    private ImageView baseCard2;

    @FXML
    Label stateLabel;

    private boolean baseCardPlayed = false;

    public void setBaseCard1(int cardID) {
        String path;
        path = "/images/cards/front/ (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard1.setImage(image);
    }

    public void setBaseCard2(int cardID) {
        String path;
        path = "/images/cards/back/ (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard2.setImage(image);
    }

    public void selectedFront() {
        // first thread of JAVA FX ==> modifies the label on the screen
        Platform.runLater(() -> {
            stateLabel.setText("Front side selected! Now wait for everyone to choose.");
        });

        // second general thread (executed after the first one)
        new Thread(() -> {
        if (network == 1 && !baseCardPlayed) {
            try {
                rmiClient.playBaseCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[0], true);

            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2 && !baseCardPlayed) {
            try {
                clientSCK.playBaseCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[0], true);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }

        // third thread to change the scene always in JAVA FX thread
        Platform.runLater(() -> {
            System.out.println("selezionato fronte");
            baseCardPlayed = true;
            changeScene();
        });
        }).start();
    }

    public void selectedBack() {
        // first thread of JAVA FX ==> modifies the label on the screen
        Platform.runLater(() -> {
            stateLabel.setText("Back side selected! Now wait for everyone to choose.");
        });

        // second general thread (executed after the first one)
        new Thread(() -> {
            if (network == 1) {
                try {
                    if (!baseCardPlayed) {
                        rmiClient.playBaseCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[0], false);
                    }
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (network == 2) {
                try {
                    if (!baseCardPlayed) {
                        clientSCK.playBaseCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[0], false);
                    }
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }

            // third thread to change the scene always in JAVA FX thread
            Platform.runLater(() -> {
                baseCardPlayed = true;
                changeScene();
                System.out.println("Selezionato retro");
            });
        }).start();
    }

    GUIObjectiveController ctr;

    int network = 0; //1 = rmi  2 = sck

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    void setRmiClient(RMIClient client) {
        this.rmiClient = client;
    }


    void setClientSCK(ClientSCK client) {
        this.clientSCK = client;
    }

    void setNetwork(int network) {
        this.network = network;
    }

    @FXML
    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }


    public void changeScene() {
        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objective.fxml"));
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
            ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", now choose your");
            while (rmiClient.getPersonalPlayer().getPlayerDeck()[0] == null || rmiClient.getPersonalPlayer().getPlayerDeck()[1] == null ||rmiClient.getPersonalPlayer().getPlayerDeck()[2] == null || rmiClient.getPersonalPlayer().getPersonalObjectives().size() < 2){
                System.out.println("giving initial cards..."); // ATTENZIONE!! LE CARTE NON VENGONO DATE IN MODO CORRETTO
            }

            ctr.setCard1(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setCard2(rmiClient.getPersonalPlayer().getPlayerDeck()[1].getId());
            ctr.setCard3(rmiClient.getPersonalPlayer().getPlayerDeck()[2].getId());
            ctr.setObjCard1(rmiClient.getPersonalPlayer().getPersonalObjectives().get(0).getId());
            ctr.setObjCard2(rmiClient.getPersonalPlayer().getPersonalObjectives().get(1).getId());
            ctr.setBaseCard(rmiClient.getPersonalPlayer().getBoard().getTable()[rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2][rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2].getId(), rmiClient.getPersonalPlayer().getBoard().getTable()[rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2][rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2].getOrientation());
            // (0,0) because our base card is always in the center of the table!
        } else if (network == 2) {
            while (clientSCK.getPersonalPlayer().getPlayerDeck()[0] == null || clientSCK.getPersonalPlayer().getPlayerDeck()[1] == null ||clientSCK.getPersonalPlayer().getPlayerDeck()[2] == null || clientSCK.getPersonalPlayer().getPersonalObjectives().size() < 2){
                System.out.println("giving initial cards..."); // ATTENZIONE!! LE CARTE NON VENGONO DATE IN MODO CORRETTO
            }
            ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", now choose your");
            ctr.setCard1(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setCard2(clientSCK.getPersonalPlayer().getPlayerDeck()[1].getId());
            ctr.setCard3(clientSCK.getPersonalPlayer().getPlayerDeck()[2].getId());
            ctr.setObjCard1(clientSCK.getPersonalPlayer().getPersonalObjectives().get(0).getId());
            ctr.setObjCard2(clientSCK.getPersonalPlayer().getPersonalObjectives().get(1).getId());
            ctr.setBaseCard(clientSCK.getPersonalPlayer().getBoard().getTable()[clientSCK.getPersonalPlayer().getBoard().getBoardDimensions()/2][clientSCK.getPersonalPlayer().getBoard().getBoardDimensions()/2].getId(), clientSCK.getPersonalPlayer().getBoard().getTable()[clientSCK.getPersonalPlayer().getBoard().getBoardDimensions()/2][clientSCK.getPersonalPlayer().getBoard().getBoardDimensions()/2].getOrientation());
        }

        // old dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // new scene
        Scene scene;
        scene = new Scene(root);

        stage.setScene(scene);

        // setting the od values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);


        stage.show();
    }
}
