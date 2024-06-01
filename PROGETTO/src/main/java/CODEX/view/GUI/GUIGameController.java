package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.ObjectiveCard;
import CODEX.org.model.PlayableCard;
import CODEX.org.model.PlayableDeck;
import CODEX.org.model.Player;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class GUIGameController {

    private PlayableDeck goldDeck;
    private PlayableDeck resourceDeck;
    private ObjectiveCard commonObj1;
    private ObjectiveCard commonObj2;
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    private int network;
    private List<Player> playersInOrder;


    @FXML
    private Label turnLabel;
    @FXML
    private Label points1;
    @FXML
    private Label points2;
    @FXML
    private Label points3;
    @FXML
    private Label points4;



    public void setTurnLabel() { //ok
        if (network == 1) {
            if (rmiClient.getPlayersInTheGame().get(0).getNickname().equals(rmiClient.getPersonalPlayer().getNickname())) {
                this.turnLabel.setText("It's your turn!");
            } else {
                this.turnLabel.setText("It's " + rmiClient.getPlayersInTheGame().get(0).getNickname() + "'s turn!");
            }
        } else if (network == 2) {
            if (clientSCK.getPlayersInTheGame().get(0).getNickname().equals(clientSCK.getPersonalPlayer().getNickname())) {
                this.turnLabel.setText("It's your turn!");
            } else {
                this.turnLabel.setText("It's " + clientSCK.getPlayersInTheGame().get(0).getNickname() + "'s turn!");
            }
        }
    }


    public void leaveGame() {
        if (network == 1) {
            try {
                this.rmiClient.leaveGame(this.rmiClient.getPersonalPlayer().getNickname());
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2) {
            try {
                this.clientSCK.leaveGame(this.clientSCK.getPersonalPlayer().getNickname());
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        //changeScene():
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gameLeft.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

        PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
        pause.setOnFinished(event -> stage.close());
        pause.play();
    }


    /**
     *
     */
    public void setPoints(){
        if(network == 1){
            for (Player p: playersInOrder) {
                int counter = 0;
                for (Player p2 : rmiClient.getPlayersInTheGame()) {
                    int counter2 = 0;
                    if (p.getNickname().equals(p2.getNickname())) {
                        // devo aggiornare i punti di quel player
                        if (counter == 0) {
                            this.points1.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        } else if (counter == 1) {
                            this.points2.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        } else if (counter == 2) {
                            this.points3.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        } else if (counter == 3) {
                            this.points4.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        }
                    }
                    counter2++;
                }
                counter++;
            }

        }else if (network == 2){
            for (Player p: playersInOrder) {
                int counter = 0;
                for (Player p2 : clientSCK.getPlayersInTheGame()) {
                    int counter2 = 0;
                    if (p.getNickname().equals(p2.getNickname())) {
                        // devo aggiornare i punti di quel player
                        if (counter == 0) {
                            this.points1.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        } else if (counter == 1) {
                            this.points2.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        } else if (counter == 2) {
                            this.points3.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        }else if (counter == 3) {
                            this.points4.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(counter2).getPoints()) + " pt");
                        }
                    }
                    counter2++;

                }
                counter++;
            }
        }
    }

    public void setPlayersInOrder(List<Player> playersInOrder) {
        this.playersInOrder = playersInOrder;
    }

    public void setStage(Stage stage) {
        this.stage=stage;
    }

    public void setNetwork(int network) {
        this.network=network;
    }

    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK=clientSCK;
    }

    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }


    /**
     * Sets all the parameters when the game begins.
     * This method is called by the previous GUI Objective Controller
     */
    public void setAllFeatures() {
        setTurnLabel();
        if (this.network == 1) {
            setPlayersInOrder(rmiClient.getPlayersInTheGame());
        } else if (this.network == 2) {
            setPlayersInOrder(clientSCK.getPlayersInTheGame());
        }
        setPoints();


        // SETTING THE PLAYER DECKS
        // SETTING THE CURRENT PLAYER (is the first in the list)
        // SETTING THE PAWNS
    }

}
