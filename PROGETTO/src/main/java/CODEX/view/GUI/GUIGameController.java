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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class GUIGameController {

    private PlayableDeck goldDeck;
    private PlayableDeck resourceDeck;
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
    @FXML
    private ImageView player1Card1;
    @FXML
    private ImageView player1Card2;
    @FXML
    private ImageView player1Card3;
    @FXML
    private ImageView player2Card1;
    @FXML
    private ImageView player2Card2;
    @FXML
    private ImageView player2Card3;
    @FXML
    private ImageView player3Card1;
    @FXML
    private ImageView player3Card2;
    @FXML
    private ImageView player3Card3;
    @FXML
    private ImageView player4Card1;
    @FXML
    private ImageView player4Card2;
    @FXML
    private ImageView player4Card3;
    @FXML
    private ImageView commonObj1;
    @FXML
    private ImageView commonObj2;
    @FXML
    private ImageView personalObj;
    @FXML
    private Label player1Nickname;
    @FXML
    private Label player2Nickname;
    @FXML
    private Label player3Nickname;
    @FXML
    private Label player4Nickname;





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
        int rmiPlayer = 0;
        int order = 0;
        int sckPlayer = 0;

        if(network == 1){
            for (Player p: playersInOrder) {
                rmiPlayer = 0;
                for (Player p2 : rmiClient.getPlayersInTheGame()) {
                    if (p.getNickname().equals(p2.getNickname())) {
                        // devo aggiornare i punti di quel player
                        if (order == 0 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) {
                            this.points1.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
                        } else if (order == 1 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) {
                            this.points2.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
                        } else if (order == 2 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) {
                            this.points3.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
                        } else if (order == 3 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) {
                            this.points4.setText(p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
                        }
                    }
                    rmiPlayer++;
                }
                order++;
            }

        }else if (network == 2){
            for (Player p: playersInOrder) {
                sckPlayer = 0;
                for (Player p2 : clientSCK.getPlayersInTheGame()) {
                    if (p.getNickname().equals(p2.getNickname())) {
                        // devo aggiornare i punti di quel player
                        if (order == 0 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
                            this.points1.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
                        } else if (order == 1 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
                            this.points2.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
                        } else if (order == 2 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
                            this.points3.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
                        }else if (order == 3 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
                            this.points4.setText(p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
                        }
                    }
                    sckPlayer++;
                }
                order++;
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


    private void setPlayer1Cards() {
        String path;
        path = "/images/cards/front/ (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        player1Card1.setImage(card1);

        path = "/images/cards/front/ (" + playersInOrder.get(0).getPlayerDeck()[1].getId() + ").png";
        Image card2 = new Image(getClass().getResourceAsStream(path));
        player1Card2.setImage(card2);

        path = "/images/cards/front/ (" + playersInOrder.get(0).getPlayerDeck()[2].getId() + ").png";
        Image card3 = new Image(getClass().getResourceAsStream(path));
        player1Card3.setImage(card3);
    }


    private void setPlayer2Cards() {
            String path;

            path = "/images/cards/back/ (" + playersInOrder.get(1).getPlayerDeck()[0].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player2Card1.setImage(card1);

            path = "/images/cards/back/ (" + playersInOrder.get(1).getPlayerDeck()[1].getId() + ").png";
            Image card2 = new Image(getClass().getResourceAsStream(path));
            player2Card2.setImage(card2);

            path = "/images/cards/back/ (" + playersInOrder.get(1).getPlayerDeck()[2].getId() + ").png";
            Image card3 = new Image(getClass().getResourceAsStream(path));
            player2Card3.setImage(card3);
    }


    private void setPlayer3Cards() {
        String path;
        path = "/images/cards/back/ (" + playersInOrder.get(2).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        player3Card1.setImage(card1);

        path = "/images/cards/back/ (" + playersInOrder.get(2).getPlayerDeck()[1].getId() + ").png";
        Image card2 = new Image(getClass().getResourceAsStream(path));
        player3Card2.setImage(card2);

        path = "/images/cards/back/ (" + playersInOrder.get(2).getPlayerDeck()[2].getId() + ").png";
        Image card3 = new Image(getClass().getResourceAsStream(path));
        player3Card3.setImage(card3);
    }


    private void setPlayer4Cards() {
        String path;
        path = "/images/cards/back/ (" + playersInOrder.get(3).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        player4Card1.setImage(card1);

        path = "/images/cards/back/ (" + playersInOrder.get(3).getPlayerDeck()[1].getId() + ").png";
        Image card2 = new Image(getClass().getResourceAsStream(path));
        player4Card2.setImage(card2);

        path = "/images/cards/back/ (" + playersInOrder.get(3).getPlayerDeck()[2].getId() + ").png";
        Image card3 = new Image(getClass().getResourceAsStream(path));
        player4Card3.setImage(card3);
    }


    /**
     * Sets all the parameters when the game begins.
     * This method is called by the previous GUI Objective Controller
     */
    public void setAllFeatures() throws RemoteException {
        System.out.println("STO SETTANDO TUTTE LE FEATURES");

        // SETTING THE LABEL WITH THE CORRECT NAME OF THE PLAYER IN TURN
        setTurnLabel();


        if (this.network == 1) {

            // PRINTING THE PLAYERS IN THE GAME
            for (Player p : rmiClient.getPlayersInTheGame()) {
                System.out.println("giocatore: " + p.getNickname());
            }

            // THE FIRST PLAYER IT'S ALWAYS ME NOW! (IN PLAYERSINORDER)
            setPlayersInOrder(rmiClient.getPlayersInTheGame());
            List<Player> newList = new ArrayList<>();
            newList.add(rmiClient.getPersonalPlayer());
            for (Player p: playersInOrder) {
                if (!p.getNickname().equals(rmiClient.getPersonalPlayer().getNickname())){
                    newList.add(p);
                }
            }
            playersInOrder = newList; // aggiungo in prima posizione il PERSONAL PLAYER
            System.out.println("ECCO I NUOVI PLAYERS IN ORDER CON ME COME PRIMA POSIZIONE!");
            for (Player p: playersInOrder) {
                System.out.println("nickname: " + p.getNickname());
            }


            // SETTING THE PERSONAL OBJECTIVE
            String path;
            path = "/images/cards/front/ (" + rmiClient.getPersonalPlayer().getPersonalObjective().getId() + ").png";
            Image persObj = new Image(getClass().getResourceAsStream(path));
            personalObj.setImage(persObj);


            // SETTING THE 2 COMMON OBJECTIVES
            path = "/images/cards/front/ (" + rmiClient.getCommonObjective1().getId() + ").png";
            Image obj1 = new Image(getClass().getResourceAsStream(path));
            commonObj1.setImage(obj1);

            path = "/images/cards/front/ (" + rmiClient.getCommonObjective2().getId() + ").png";
            Image obj2 = new Image(getClass().getResourceAsStream(path));
            commonObj2.setImage(obj2);


            // SETTING THE POINTS, THE NICKNAMES AND THE CARDS IN HAND
            if (rmiClient.getPlayersInTheGame().size() == 2) {
                points1.setOpacity(1);
                points2.setOpacity(1);
                points3.setOpacity(0);
                points4.setOpacity(0);
                player1Nickname.setText(playersInOrder.get(0).getNickname());
                player2Nickname.setText(playersInOrder.get(1).getNickname());
                player1Nickname.setOpacity(1);
                player2Nickname.setOpacity(1);
                player3Nickname.setOpacity(0);
                player4Nickname.setOpacity(0);
                setPlayer1Cards();
                setPlayer2Cards();
            } else if (rmiClient.getPlayersInTheGame().size() == 3) {
                points1.setOpacity(1);
                points2.setOpacity(1);
                points3.setOpacity(1);
                points4.setOpacity(0);
                player1Nickname.setText(playersInOrder.get(0).getNickname());
                player2Nickname.setText(playersInOrder.get(1).getNickname());
                player3Nickname.setText(playersInOrder.get(2).getNickname());
                player1Nickname.setOpacity(1);
                player2Nickname.setOpacity(1);
                player3Nickname.setOpacity(1);
                player4Nickname.setOpacity(0);
                setPlayer1Cards();
                setPlayer2Cards();
                setPlayer3Cards();
            } else if (rmiClient.getPlayersInTheGame().size() == 4) {
                points1.setOpacity(1);
                points2.setOpacity(1);
                points3.setOpacity(1);
                points4.setOpacity(1);
                player1Nickname.setText(playersInOrder.get(0).getNickname());
                player2Nickname.setText(playersInOrder.get(1).getNickname());
                player3Nickname.setText(playersInOrder.get(2).getNickname());
                player4Nickname.setText(playersInOrder.get(3).getNickname());
                player1Nickname.setOpacity(1);
                player2Nickname.setOpacity(1);
                player3Nickname.setOpacity(1);
                player4Nickname.setOpacity(1);
                setPlayer1Cards();
                setPlayer2Cards();
                setPlayer3Cards();
                setPlayer4Cards();
            }

        } else if (this.network == 2) {

            // PRINTING THE PLAYERS IN THE GAME
            for (Player p: clientSCK.getPlayersInTheGame()) {
                System.out.println("giocatore: " + p.getNickname());
            }

            // THE FIRST PLAYER IT'S ALWAYS ME NOW! (IN PLAYERSINORDER)
            setPlayersInOrder(clientSCK.getPlayersInTheGame());
            List<Player> newList = new ArrayList<>();
            newList.add(clientSCK.getPersonalPlayer());
            for (Player p: playersInOrder) {
                if (!p.getNickname().equals(clientSCK.getPersonalPlayer().getNickname()) ){
                    newList.add(p);
                }
            }
            playersInOrder = newList; // aggiungo in prima posizione il PERSONAL PLAYER
            System.out.println("ECCO I NUOVI PLAYERS IN ORDER CON ME COME PRIMA POSIZIONE!");
            for (Player p: playersInOrder) {
                System.out.println("nickname: " + p.getNickname());
            }


            // SETTING THE PERSONAL OBJECTIVE
            String path;
            path = "/images/cards/front/ (" + clientSCK.getPersonalPlayer().getPersonalObjective().getId() + ").png";
            Image persObj = new Image(getClass().getResourceAsStream(path));
            personalObj.setImage(persObj);


            // SETTING THE 2 COMMON OBJECTIVES
            path = "/images/cards/front/ (" + clientSCK.getCommonObjective1().getId() + ").png";
            Image obj1 = new Image(getClass().getResourceAsStream(path));
            commonObj1.setImage(obj1);

            path = "/images/cards/front/ (" + clientSCK.getCommonObjective2().getId() + ").png";
            Image obj2 = new Image(getClass().getResourceAsStream(path));
            commonObj2.setImage(obj2);


            // SETTING THE POINTS, THE NICKNAMES AND THE CARDS IN HAND
            if (clientSCK.getPlayersInTheGame().size() == 2) {
                points1.setOpacity(1);
                points2.setOpacity(1);
                points3.setOpacity(0);
                points4.setOpacity(0);
                player1Nickname.setText(playersInOrder.get(0).getNickname());
                player2Nickname.setText(playersInOrder.get(1).getNickname());
                player1Nickname.setOpacity(1);
                player2Nickname.setOpacity(1);
                player3Nickname.setOpacity(0);
                player4Nickname.setOpacity(0);
                setPlayer1Cards();
                setPlayer2Cards();
            } else if (clientSCK.getPlayersInTheGame().size() == 3) {
                points1.setOpacity(1);
                points2.setOpacity(1);
                points3.setOpacity(1);
                points4.setOpacity(0);
                player1Nickname.setText(playersInOrder.get(0).getNickname());
                player2Nickname.setText(playersInOrder.get(1).getNickname());
                player3Nickname.setText(playersInOrder.get(2).getNickname());
                player1Nickname.setOpacity(1);
                player2Nickname.setOpacity(1);
                player3Nickname.setOpacity(1);
                player4Nickname.setOpacity(0);
                setPlayer1Cards();
                setPlayer2Cards();
                setPlayer3Cards();
            } else if (clientSCK.getPlayersInTheGame().size() == 4) {
                points1.setOpacity(1);
                points2.setOpacity(1);
                points3.setOpacity(1);
                points4.setOpacity(1);
                player1Nickname.setText(playersInOrder.get(0).getNickname());
                player2Nickname.setText(playersInOrder.get(1).getNickname());
                player3Nickname.setText(playersInOrder.get(2).getNickname());
                player4Nickname.setText(playersInOrder.get(3).getNickname());
                player1Nickname.setOpacity(1);
                player2Nickname.setOpacity(1);
                player3Nickname.setOpacity(1);
                player4Nickname.setOpacity(1);
                setPlayer1Cards();
                setPlayer2Cards();
                setPlayer3Cards();
                setPlayer4Cards();
            }
        }

        // SETTING THE CORRECT NUMBER OF POINTS (UPDATE)
        setPoints();


        // TODO:
        // SETTING THE CURRENT PLAYER (is the first in the list)
        // SETTING THE PAWNS
    }


    public void handleDisconnection(){
        //mostro una scena che dice che qualcuno si è disconnesso e poi chiudo lo stage
    }
}
