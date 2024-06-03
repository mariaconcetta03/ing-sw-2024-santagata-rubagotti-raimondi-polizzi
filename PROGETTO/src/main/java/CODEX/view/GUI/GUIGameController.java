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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
import java.util.concurrent.ScheduledExecutorService;

public class GUIGameController {


    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    private int network;
    private List<Player> playersInOrder;
    private ScheduledExecutorService scheduler;
    private Object disconnectionLock;
    private boolean orientationCard1;
    private boolean orientationCard2;
    private boolean orientationCard3;
    private int selectedCard = 0; // 1 = card1  2 = card 2  3 = card3
    private Integer dimension;
    private PlayableCard[][] tableP1;
    private PlayableCard[][] tableP2;
    private PlayableCard[][] tableP3;
    private PlayableCard[][] tableP4;
    private int nPlayers;
    private boolean cardDrawn = false;




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
    @FXML
    private ImageView goldDeck;
    @FXML
    private ImageView resourceDeck;
    @FXML
    private ImageView resourceCard1;
    @FXML
    private ImageView resourceCard2;
    @FXML
    private ImageView goldCard1;
    @FXML
    private ImageView goldCard2;
    @FXML
    private Button buttonP1Board;
    @FXML
    private Button buttonP2Board;
    @FXML
    private Button buttonP3Board;
    @FXML
    private Button buttonP4Board;
    @FXML
    private GridPane gridPaneTest;
    @FXML
    private ScrollPane boardPane;
    @FXML
    private GridPane grid;
    @FXML
    private Label selectedCardLabel;




    public void setTurnLabel() {
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
        synchronized (disconnectionLock) { //o sono in leaveGame() o in startPeriodicDisconnectionCheck()
            if (!clientSCK.getADisconnectionHappened()) { //se è avvenuta una disconnessione leaveGame non esegue niente
                scheduler.shutdown(); //così nel caso avvenga una disconnessione mentre siamo in questa syn poi lo scheduler non va a toccare il client che ha già fatto la exit
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

                PauseTransition pause = new PauseTransition(Duration.seconds(4)); // 4 secondi di ritardo
                pause.setOnFinished(event -> stage.close());
                pause.play();
                if (network == 1) {
                    try {
                        this.rmiClient.leaveGame(this.rmiClient.getPersonalPlayer().getNickname()); //lato server verrà mandato agli altri player evento disconnessione
                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                } else if (network == 2) {
                    try { //in clientSCK viene fatta la exit
                        this.clientSCK.leaveGame(this.clientSCK.getPersonalPlayer().getNickname()); //lato server verrà mandato agli altri player evento disconnessione
                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }




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
            this.dimension=rmiClient.getPersonalPlayer().getBoard().getBoardDimensions();

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

            // SETTING THE TABLE AND ITS DIMENSIONS
            int i=1;
            for (Player p: playersInOrder) {
                System.out.println("nickname: " + p.getNickname());
                if(i==1){
                   tableP1=p.getBoard().getTable();
                }
                if(i==2){
                    tableP2=p.getBoard().getTable();
                }
                if(i==3){
                    tableP3=p.getBoard().getTable();
                }
                if(i==4){
                    tableP4=p.getBoard().getTable();
                }
                i++;
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


            // SETTING THE 2 DECKS AND THE 4 MARKET'S CARDS
            path = "/images/cards/back/ (" + rmiClient.getGoldDeck().checkFirstCard().getId() + ").png";
            Image gd = new Image(getClass().getResourceAsStream(path));
            goldDeck.setImage(gd);

            path = "/images/cards/back/ (" + rmiClient.getResourceDeck().checkFirstCard().getId() + ").png";
            Image rd = new Image(getClass().getResourceAsStream(path));
            resourceDeck.setImage(rd);

            path = "/images/cards/front/ (" + rmiClient.getResourceCard1().getId() + ").png";
            Image rc1 = new Image(getClass().getResourceAsStream(path));
            resourceCard1.setImage(rc1);

            path = "/images/cards/front/ (" + rmiClient.getResourceCard2().getId() + ").png";
            Image rc2 = new Image(getClass().getResourceAsStream(path));
            resourceCard2.setImage(rc2);

            path = "/images/cards/front/ (" + rmiClient.getGoldCard1().getId() + ").png";
            Image gc1 = new Image(getClass().getResourceAsStream(path));
            goldCard1.setImage(gc1);

            path = "/images/cards/front/ (" + rmiClient.getGoldCard2().getId() + ").png";
            Image gc2 = new Image(getClass().getResourceAsStream(path));
            goldCard2.setImage(gc2);


            // SETTING THE ORIENTATION OF MY 3 CARDS
            orientationCard1 = true;
            orientationCard2 = true;
            orientationCard3 = true;


        } else if (this.network == 2) {
            this.dimension=clientSCK.getPersonalPlayer().getBoard().getBoardDimensions(); //le dimensioni sono uguali per tutti i giocatori

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

            // SETTING THE BOARD AND ITS DIMENSIONS
            int i=1;
            for (Player p: playersInOrder) {
                System.out.println("nickname: " + p.getNickname());
                if(i==1){
                    tableP1=p.getBoard().getTable();
                }
                if(i==2){
                    tableP2=p.getBoard().getTable();
                }
                if(i==3){
                    tableP3=p.getBoard().getTable();
                }
                if(i==4){
                    tableP4=p.getBoard().getTable();
                }
                i++;
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


            // SETTING THE 2 DECKS AND THE 4 MARKET'S CARDS
            path = "/images/cards/back/ (" + clientSCK.getGoldDeck().checkFirstCard().getId() + ").png";
            Image gd = new Image(getClass().getResourceAsStream(path));
            goldDeck.setImage(gd);

            path = "/images/cards/back/ (" + clientSCK.getResourceDeck().checkFirstCard().getId() + ").png";
            Image rd = new Image(getClass().getResourceAsStream(path));
            resourceDeck.setImage(rd);

            path = "/images/cards/front/ (" + clientSCK.getResourceCard1().getId() + ").png";
            Image rc1 = new Image(getClass().getResourceAsStream(path));
            resourceCard1.setImage(rc1);

            path = "/images/cards/front/ (" + clientSCK.getResourceCard2().getId() + ").png";
            Image rc2 = new Image(getClass().getResourceAsStream(path));
            resourceCard2.setImage(rc2);

            path = "/images/cards/front/ (" + clientSCK.getGoldCard1().getId() + ").png";
            Image gc1 = new Image(getClass().getResourceAsStream(path));
            goldCard1.setImage(gc1);

            path = "/images/cards/front/ (" + clientSCK.getGoldCard2().getId() + ").png";
            Image gc2 = new Image(getClass().getResourceAsStream(path));
            goldCard2.setImage(gc2);


            // SETTING THE ORIENTATION OF MY 3 CARDS
            orientationCard1 = true;
            orientationCard2 = true;
            orientationCard3 = true;
        }


        // SETTING THE POINTS, THE NICKNAMES, THE CARDS IN HAND AND THE BUTTONS
        // 2 players
        if (((this.network == 1)&&(rmiClient.getPlayersInTheGame().size() == 2))||((this.network == 2)&&(clientSCK.getPlayersInTheGame().size() == 2)) ){
            this.nPlayers=2;
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
            buttonP1Board.setOpacity(1);
            buttonP1Board.setText(playersInOrder.get(0).getNickname());
            buttonP2Board.setOpacity(1);
            buttonP2Board.setText(playersInOrder.get(1).getNickname());
            buttonP3Board.setOpacity(0);
            buttonP4Board.setOpacity(0);
            // SETTING THE FIRST POSITION OF THE SCROLL IN THE SCROLLPANE
            // THANKS TO THIS THE PLAYER CAN IMMEDIATELY SEE HIS BASE CARD IN THE CENTER OF HIS PANE
        }

        // 3 players
        else if (((this.network == 1)&&(rmiClient.getPlayersInTheGame().size() == 3))||((this.network == 2)&&(clientSCK.getPlayersInTheGame().size() == 3))) {
            this.nPlayers=3;
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
            buttonP1Board.setOpacity(1);
            buttonP1Board.setText(playersInOrder.get(0).getNickname());
            buttonP2Board.setOpacity(1);
            buttonP2Board.setText(playersInOrder.get(1).getNickname());
            buttonP3Board.setOpacity(1);
            buttonP3Board.setText(playersInOrder.get(2).getNickname());
            buttonP4Board.setOpacity(0);
            // SETTING THE FIRST POSITION OF THE SCROLL IN THE SCROLLPANE
            // THANKS TO THIS THE PLAYER CAN IMMEDIATELY SEE HIS BASE CARD IN THE CENTER OF HIS PANE
        }

        // 4 players
        else if (((this.network == 1)&&(rmiClient.getPlayersInTheGame().size() == 4))||((this.network == 2)&&(clientSCK.getPlayersInTheGame().size() == 4))) {
            this.nPlayers=4;
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
            buttonP1Board.setOpacity(1);
            buttonP1Board.setText(playersInOrder.get(0).getNickname());
            buttonP2Board.setOpacity(1);
            buttonP2Board.setText(playersInOrder.get(1).getNickname());
            buttonP3Board.setOpacity(1);
            buttonP3Board.setText(playersInOrder.get(2).getNickname());
            buttonP4Board.setOpacity(1);
            buttonP4Board.setText(playersInOrder.get(3).getNickname());
            // SETTING THE FIRST POSITION OF THE SCROLL IN THE SCROLLPANE
            // THANKS TO THIS THE PLAYER CAN IMMEDIATELY SEE HIS BASE CARD IN THE CENTER OF HI
        }


        // SETTING THE CORRECT NUMBER OF POINTS (UPDATE)
        setPoints();

        // INITIALIZING THE CELLS IN THE SCROLLPANE
        initializeGridPaneCells();



        // dimensioni scrollpane: prefHeight="377.0" prefWidth="1028.0
        // SETTING THE CURRENT PLAYER (is the first in the list)
        // SETTING THE PAWNS
    }




    public void turnCard1() {
        selectedCard = 1;
        selectedCardLabel.setText("Selected Card: LEFT");
        if (orientationCard1) {
            orientationCard1 = false;
            String path;
            path = "/images/cards/back/ (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player1Card1.setImage(card1);
        } else {
            orientationCard1 = true;
            String path;
            path = "/images/cards/front/ (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player1Card1.setImage(card1);
        }
    }



    public void turnCard2() {
        selectedCard = 2;
        selectedCardLabel.setText("Selected Card: CENTER");
        if (orientationCard2) {
            orientationCard2 = false;
            String path;
            path = "/images/cards/back/ (" + playersInOrder.get(0).getPlayerDeck()[1].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player1Card2.setImage(card1);
        } else {
            orientationCard2 = true;
            String path;
            path = "/images/cards/front/ (" + playersInOrder.get(0).getPlayerDeck()[1].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player1Card2.setImage(card1);
        }
    }



    public void turnCard3() {
        selectedCard = 3;
        selectedCardLabel.setText("Selected Card: RIGHT");
        if (orientationCard3) {
            orientationCard3 = false;
            String path;
            path = "/images/cards/back/ (" + playersInOrder.get(0).getPlayerDeck()[2].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player1Card3.setImage(card1);
        } else {
            orientationCard3 = true;
            String path;
            path = "/images/cards/front/ (" + playersInOrder.get(0).getPlayerDeck()[2].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player1Card3.setImage(card1);
        }
    }



    public void showP1Board() { //togliamo la board precedente e stampiamo in ordine le carte della board del player 1
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        // SETTING THE CORRECT POSITION: IT DEPENDS FROM THE NUMBER OF THE PLAYERS IN THE GAME
        if (nPlayers == 2) {
            boardPane.setHvalue(0.48780488);
            boardPane.setVvalue(0.5);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.28447504302925986);
            boardPane.setVvalue(0.30693257359924053);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }


        // SETTING ALL THE CARDS IN THE TABLE
        boolean placed = false;
        for (int count = -1; count < dimension; count++) {
            placed = false;
             for (int i = 0; i < dimension && !placed; i++) {
                 for (int j = 0; j < dimension && !placed; j++) {
                     if (tableP1[i][j] != null && count == tableP1[i][j].getPlayOrder()) {
                         String path = "/images/cards/front/ (" + (tableP1[i][j]).getId() + ").png";
                         Image card1 = new Image(getClass().getResourceAsStream(path));
                         ImageView imageView = new ImageView(card1);
                         imageView.setFitWidth((card1.getWidth() / 5.8));  // larghezza desiderata
                         imageView.setFitHeight((card1.getHeight() / 5.8)); // altezza desiderata
                         imageView.setPreserveRatio(true);
                         imageView.setSmooth(true);
                         imageView.toFront();
                         this.gridPaneTest.add(imageView, i, j);
                         placed = true;
                     }
                 }
             }
        }

    }



    public void showP2Board(){ //togliamo la board precedente e stampiamo in ordine le carte della board del player 2
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        // SETTING THE CORRECT POSITION: IT DEPENDS FROM THE NUMBER OF THE PLAYERS IN THE GAME
        if (nPlayers == 2) {
            boardPane.setHvalue(0.48780488);
            boardPane.setVvalue(0.5);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.28447504302925986);
            boardPane.setVvalue(0.30693257359924053);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }


        // SETTING ALL THE CARDS IN THE TABLE
        boolean placed = false;
        for (int count = -1; count < dimension; count++) {
            placed = false;
            for (int i = 0; i < dimension && !placed; i++) {
                for (int j = 0; j < dimension && !placed; j++) {
                    if (tableP2[i][j] != null && count == tableP2[i][j].getPlayOrder()) {
                        String path = "/images/cards/front/ (" + (tableP2[i][j]).getId() + ").png";
                        Image card1 = new Image(getClass().getResourceAsStream(path));
                        ImageView imageView = new ImageView(card1);
                        imageView.setFitWidth((card1.getWidth() / 5.8));  // larghezza desiderata
                        imageView.setFitHeight((card1.getHeight() / 5.8)); // altezza desiderata
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.toFront();
                        this.gridPaneTest.add(imageView, i, j);
                        placed = true;
                    }
                }
            }
        }

    }



    public void showP3Board(){ //togliamo la board precedente e stampiamo in ordine le carte della board del player 3
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        // SETTING THE CORRECT POSITION: IT DEPENDS FROM THE NUMBER OF THE PLAYERS IN THE GAME
        if (nPlayers == 2) {
            boardPane.setHvalue(0.48780488);
            boardPane.setVvalue(0.5);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.28447504302925986);
            boardPane.setVvalue(0.30693257359924053);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }


        // SETTING ALL THE CARDS IN THE TABLE
        boolean placed = false;
        for (int count = -1; count < dimension; count++) {
            placed = false;
            for (int i = 0; i < dimension && !placed; i++) {
                for (int j = 0; j < dimension && !placed; j++) {
                    if (tableP3[i][j] != null && count == tableP3[i][j].getPlayOrder()) {
                        String path = "/images/cards/front/ (" + (tableP3[i][j]).getId() + ").png";
                        Image card1 = new Image(getClass().getResourceAsStream(path));
                        ImageView imageView = new ImageView(card1);
                        imageView.setFitWidth((card1.getWidth() / 5.8));  // larghezza desiderata
                        imageView.setFitHeight((card1.getHeight() / 5.8)); // altezza desiderata
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.toFront();
                        this.gridPaneTest.add(imageView, i, j);
                        placed = true;
                    }
                }
            }
        }
    }




    public void showP4Board(){ //togliamo la board precedente e stampiamo in ordine le carte della board del player 4
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        // SETTING THE CORRECT POSITION: IT DEPENDS FROM THE NUMBER OF THE PLAYERS IN THE GAME
        if (nPlayers == 2) {
            boardPane.setHvalue(0.48780488);
            boardPane.setVvalue(0.5);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.28447504302925986);
            boardPane.setVvalue(0.30693257359924053);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }


        // SETTING ALL THE CARDS IN THE TABLE
        boolean placed = false;
        for (int count = -1; count < dimension; count++) {
            placed = false;
            for (int i = 0; i < dimension && !placed; i++) {
                for (int j = 0; j < dimension && !placed; j++) {
                    if (tableP4[i][j] != null && count == tableP4[i][j].getPlayOrder()) {
                        String path = "/images/cards/front/ (" + (tableP4[i][j]).getId() + ").png";
                        Image card1 = new Image(getClass().getResourceAsStream(path));
                        ImageView imageView = new ImageView(card1);
                        imageView.setFitWidth((card1.getWidth() / 5.8));  // larghezza desiderata
                        imageView.setFitHeight((card1.getHeight() / 5.8)); // altezza desiderata
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.toFront();
                        this.gridPaneTest.add(imageView, i, j);
                        placed = true;
                    }
                }
            }
        }
    }


    public void drawCardFromRD() {

    }

    public void drawCardFromGD() {

    }

    public void drawCardFromRC1(){

    }

    public void drawCardFromRC2(){

    }

    public void drawCardFromGC1(){

    }

    public void drawCardFromGC2(){

    }


    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public void setDisconnectionLock(Object disconnectionLock) {
        this.disconnectionLock = disconnectionLock;
    }


    public void playCard(MouseEvent mouseEvent) {

    }


    public void initializeGridPaneCells() {
        int count = 0;
        for (int row = 0; row < 82; row++) { //81
            for (int col = 0; col < 84; col++) { //83
                count++;
                Pane cell = new Pane();
                cell.setPrefSize(68, 36);
                cell.setId("cell" + count);
                cell.setOnMouseClicked(this::playCard);
                grid.add(cell, col, row);
            }
        }
    }


}
