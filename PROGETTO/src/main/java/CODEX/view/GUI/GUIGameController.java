package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class controls the window during the game scene
 */
public class GUIGameController {

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
    private ScrollPane boardPane;
    @FXML
    private GridPane grid;
    @FXML
    private Label selectedCardLabel;
    @FXML
    private Label boardLabel;

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
    private int selectedCard = 0; // 1 = card1  2 = card 2  3 = card
    private Integer dimension;
    private String currentBoard=null;
    private PlayableCard[][] tableP1;
    private PlayableCard[][] tableP2;
    private PlayableCard[][] tableP3;
    private PlayableCard[][] tableP4;
    private int nPlayers;
    private boolean cardPlaced = false;
    private Set<Coordinates> playablePositions=null;
    private Map<Integer,PlayableCard> cardsOnP1Board; // Map <turn number, card id>
    private Map<Integer,PlayableCard> cardsOnP2Board;
    private Map<Integer,PlayableCard> cardsOnP3Board;
    private Map<Integer,PlayableCard> cardsOnP4Board;

    // Counters for cards for each player
    private Integer p1Counter=1;
    private Integer p2Counter=1;
    private Integer p3Counter=1;
    private Integer p4Counter=1;

    private Integer emptySpace=0;
    private ObjectiveCard objectiveCardselected=null;
    private boolean lastRound=false;
    private MediaPlayer mediaPlayer;
    private boolean playingMusic = true;



    /**
     * This method sets the label of the turn
     * @param lastRound true if it's the last round, false otherwise
     */
    public void setTurnLabel(boolean lastRound) {
        cardPlaced=false;
        this.lastRound=lastRound;
        if (network == 1) {
            rmiClient.setGuiGameController(this);
            if (rmiClient.getPlayersInTheGame().get(0).getNickname().equals(rmiClient.getPersonalPlayer().getNickname())) {
                this.turnLabel.setText("It's your turn!");

                //mostriamo subito la board del giocatore in turno, se il giocatore in turno è p1 (quello a cui appartiene la view)
                showP1Board();
            } else {
                this.turnLabel.setText("It's " + rmiClient.getPlayersInTheGame().get(0).getNickname() + "'s turn!");
            }
        } else if (network == 2) {
            clientSCK.setGuiGameController(this);
            if (clientSCK.getPlayersInTheGame().get(0).getNickname().equals(clientSCK.getPersonalPlayer().getNickname())) {
                this.turnLabel.setText("It's your turn!");
                showP1Board();

            } else {
                this.turnLabel.setText("It's " + clientSCK.getPlayersInTheGame().get(0).getNickname() + "'s turn!");
            }
        }
    }



    /**
     * This method is invoked when a player clicks on the button "LEAVE GAME"
     */
    public void leaveGame() {
        synchronized (disconnectionLock) {
            if ( ((network==1)&&(!rmiClient.getADisconnectionHappened())) || ((network==2)&&(!clientSCK.getADisconnectionHappened())) ){
                scheduler.shutdown();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gameLeft.fxml"));
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                } catch (IOException ignored) {
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

                PauseTransition pause = new PauseTransition(Duration.seconds(4)); // 4 seconds of wait
                pause.setOnFinished(event -> stage.close());
                pause.play();
                if (network == 1) {

                        this.rmiClient.leaveGame(this.rmiClient.getPersonalPlayer().getNickname());


                } else if (network == 2) {

                        this.clientSCK.leaveGame(this.clientSCK.getPersonalPlayer().getNickname());

                }
            }
        }

    }



    /**
     * This method is used to update any label in JavaFX application thread
     * @param label to modify
     * @param text to insert
     */
    public void updateLabel(Label label, String text){
        Platform.runLater(() -> {
            label.setText(text);
        });
    }



    /**
     * This method is invoked to update every player's points in the main window
     */
    public void updatePoints() {
        Map<String,Integer> playersWithPoints=new HashMap<>();
        if(network==1){
            for(Player player:(rmiClient.getPlayersInTheGame())){
                playersWithPoints.put(player.getNickname(),player.getPoints());
            }
        }else if(network==2){
            for(Player player:(clientSCK.getPlayersInTheGame())){
                playersWithPoints.put(player.getNickname(),player.getPoints());
            }
        }
        int i=1;
        for (Player p : playersInOrder){ //p1, p2, p3, p4
            if(i==1) {
                updateLabel(points1, p.getNickname() + ": " + playersWithPoints.get(p.getNickname()) + " pt");
            }
            if(i==2) {
                updateLabel(points2, p.getNickname() + ": " + playersWithPoints.get(p.getNickname()) + " pt");
            }
            if(i==3) {
                updateLabel(points3, p.getNickname() + ": " + playersWithPoints.get(p.getNickname()) + " pt");
            }
            if(i==4) {
                updateLabel(points4, p.getNickname() + ": " + playersWithPoints.get(p.getNickname()) + " pt");
            }
            i++;
        }
    }



    /**
     * This method sets the cards of the first player
     */
    private void setPlayer1Cards() {
        String path;
        path = "/images/cards/front/  (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        player1Card1.setImage(card1);

        path = "/images/cards/front/  (" + playersInOrder.get(0).getPlayerDeck()[1].getId() + ").png";
        Image card2 = new Image(getClass().getResourceAsStream(path));
        player1Card2.setImage(card2);

        path = "/images/cards/front/  (" + playersInOrder.get(0).getPlayerDeck()[2].getId() + ").png";
        Image card3 = new Image(getClass().getResourceAsStream(path));
        player1Card3.setImage(card3);
    }



    /**
     * This method sets the cards of the second player
     */
    private void setPlayer2Cards() {
            String path;

            path = "/images/cards/back/  (" + playersInOrder.get(1).getPlayerDeck()[0].getId() + ").png";
            Image card1 = new Image(getClass().getResourceAsStream(path));
            player2Card1.setImage(card1);

            path = "/images/cards/back/  (" + playersInOrder.get(1).getPlayerDeck()[1].getId() + ").png";
            Image card2 = new Image(getClass().getResourceAsStream(path));
            player2Card2.setImage(card2);

            path = "/images/cards/back/  (" + playersInOrder.get(1).getPlayerDeck()[2].getId() + ").png";
            Image card3 = new Image(getClass().getResourceAsStream(path));
            player2Card3.setImage(card3);
    }



    /**
     * This method sets the cards of the third player
     */
    private void setPlayer3Cards() {
        String path;
        path = "/images/cards/back/  (" + playersInOrder.get(2).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        player3Card1.setImage(card1);

        path = "/images/cards/back/  (" + playersInOrder.get(2).getPlayerDeck()[1].getId() + ").png";
        Image card2 = new Image(getClass().getResourceAsStream(path));
        player3Card2.setImage(card2);

        path = "/images/cards/back/  (" + playersInOrder.get(2).getPlayerDeck()[2].getId() + ").png";
        Image card3 = new Image(getClass().getResourceAsStream(path));
        player3Card3.setImage(card3);
    }



    /**
     * This method sets the cards of the fourth player
     */
    private void setPlayer4Cards() {
        String path;
        path = "/images/cards/back/  (" + playersInOrder.get(3).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        player4Card1.setImage(card1);

        path = "/images/cards/back/  (" + playersInOrder.get(3).getPlayerDeck()[1].getId() + ").png";
        Image card2 = new Image(getClass().getResourceAsStream(path));
        player4Card2.setImage(card2);

        path = "/images/cards/back/  (" + playersInOrder.get(3).getPlayerDeck()[2].getId() + ").png";
        Image card3 = new Image(getClass().getResourceAsStream(path));
        player4Card3.setImage(card3);
    }



    /**
     * Sets all the parameters when the game begins.
     * This method is called by the previous GUI Objective Controller
     */
    public void setAllFeatures() {
        System.out.println("STO SETTANDO TUTTE LE FEATURES");

        if (this.network == 1) {
            this.dimension=rmiClient.getPersonalPlayer().getBoard().getBoardDimensions();
            System.out.println("dimension/2 " + dimension/2);

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


            // SETTING THE PERSONAL OBJECTIVE
            String path;
            path = "/images/cards/front/  (" + objectiveCardselected.getId() + ").png";
            Image persObj = new Image(getClass().getResourceAsStream(path));
            personalObj.setImage(persObj);


            // SETTING THE 2 COMMON OBJECTIVES
            path = "/images/cards/front/  (" + rmiClient.getCommonObjective1().getId() + ").png";
            Image obj1 = new Image(getClass().getResourceAsStream(path));
            commonObj1.setImage(obj1);

            path = "/images/cards/front/  (" + rmiClient.getCommonObjective2().getId() + ").png";
            Image obj2 = new Image(getClass().getResourceAsStream(path));
            commonObj2.setImage(obj2);


            // SETTING THE 2 DECKS AND THE 4 MARKET'S CARDS
            path = "/images/cards/back/  (" + rmiClient.getGoldDeck().checkFirstCard().getId() + ").png";
            Image gd = new Image(getClass().getResourceAsStream(path));
            goldDeck.setImage(gd);

            path = "/images/cards/back/  (" + rmiClient.getResourceDeck().checkFirstCard().getId() + ").png";
            Image rd = new Image(getClass().getResourceAsStream(path));
            resourceDeck.setImage(rd);

            path = "/images/cards/front/  (" + rmiClient.getResourceCard1().getId() + ").png";
            Image rc1 = new Image(getClass().getResourceAsStream(path));
            resourceCard1.setImage(rc1);

            path = "/images/cards/front/  (" + rmiClient.getResourceCard2().getId() + ").png";
            Image rc2 = new Image(getClass().getResourceAsStream(path));
            resourceCard2.setImage(rc2);

            path = "/images/cards/front/  (" + rmiClient.getGoldCard1().getId() + ").png";
            Image gc1 = new Image(getClass().getResourceAsStream(path));
            goldCard1.setImage(gc1);

            path = "/images/cards/front/  (" + rmiClient.getGoldCard2().getId() + ").png";
            Image gc2 = new Image(getClass().getResourceAsStream(path));
            goldCard2.setImage(gc2);


            // SETTING THE ORIENTATION OF MY 3 CARDS
            orientationCard1 = true;
            orientationCard2 = true;
            orientationCard3 = true;


        } else if (this.network == 2) {
            this.dimension=clientSCK.getPersonalPlayer().getBoard().getBoardDimensions();
            System.out.println("dimension/2 " + dimension/2);

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
            playersInOrder = newList;
            System.out.println("ECCO I NUOVI PLAYERS IN ORDER CON ME COME PRIMA POSIZIONE!");


            // SETTING THE PERSONAL OBJECTIVE
            String path;
            path = "/images/cards/front/  (" + objectiveCardselected.getId() + ").png";
            Image persObj = new Image(getClass().getResourceAsStream(path));
            personalObj.setImage(persObj);


            // SETTING THE 2 COMMON OBJECTIVES
            path = "/images/cards/front/  (" + clientSCK.getCommonObjective1().getId() + ").png";
            Image obj1 = new Image(getClass().getResourceAsStream(path));
            commonObj1.setImage(obj1);

            path = "/images/cards/front/  (" + clientSCK.getCommonObjective2().getId() + ").png";
            Image obj2 = new Image(getClass().getResourceAsStream(path));
            commonObj2.setImage(obj2);


            // SETTING THE 2 DECKS AND THE 4 MARKET'S CARDS
            path = "/images/cards/back/  (" + clientSCK.getGoldDeck().checkFirstCard().getId() + ").png";
            Image gd = new Image(getClass().getResourceAsStream(path));
            goldDeck.setImage(gd);

            path = "/images/cards/back/  (" + clientSCK.getResourceDeck().checkFirstCard().getId() + ").png";
            Image rd = new Image(getClass().getResourceAsStream(path));
            resourceDeck.setImage(rd);

            path = "/images/cards/front/  (" + clientSCK.getResourceCard1().getId() + ").png";
            Image rc1 = new Image(getClass().getResourceAsStream(path));
            resourceCard1.setImage(rc1);

            path = "/images/cards/front/  (" + clientSCK.getResourceCard2().getId() + ").png";
            Image rc2 = new Image(getClass().getResourceAsStream(path));
            resourceCard2.setImage(rc2);

            path = "/images/cards/front/  (" + clientSCK.getGoldCard1().getId() + ").png";
            Image gc1 = new Image(getClass().getResourceAsStream(path));
            goldCard1.setImage(gc1);

            path = "/images/cards/front/  (" + clientSCK.getGoldCard2().getId() + ").png";
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
            if (playersInOrder.get(0).getChosenColor() == Pawn.RED) {
                player1Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.BLUE) {
                player1Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.YELLOW) {
                player1Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.GREEN) {
                player1Nickname.setStyle("-fx-text-fill: green;");
            }
            player2Nickname.setText(playersInOrder.get(1).getNickname());
            if (playersInOrder.get(1).getChosenColor() == Pawn.RED) {
                player2Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.BLUE) {
                player2Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.YELLOW) {
                player2Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.GREEN) {
                player2Nickname.setStyle("-fx-text-fill: green;");
            }
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
        }

        // 3 players
        else if (((this.network == 1)&&(rmiClient.getPlayersInTheGame().size() == 3))||((this.network == 2)&&(clientSCK.getPlayersInTheGame().size() == 3))) {
            this.nPlayers=3;
            points1.setOpacity(1);
            points2.setOpacity(1);
            points3.setOpacity(1);
            points4.setOpacity(0);
            player1Nickname.setText(playersInOrder.get(0).getNickname());
            if (playersInOrder.get(0).getChosenColor() == Pawn.RED) {
                player1Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.BLUE) {
                player1Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.YELLOW) {
                player1Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.GREEN) {
                player1Nickname.setStyle("-fx-text-fill: green;");
            }
            player2Nickname.setText(playersInOrder.get(1).getNickname());
            if (playersInOrder.get(1).getChosenColor() == Pawn.RED) {
                player2Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.BLUE) {
                player2Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.YELLOW) {
                player2Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.GREEN) {
                player2Nickname.setStyle("-fx-text-fill: green;");
            }
            player3Nickname.setText(playersInOrder.get(2).getNickname());
            if (playersInOrder.get(2).getChosenColor() == Pawn.RED) {
                player3Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(2).getChosenColor() == Pawn.BLUE) {
                player3Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(2).getChosenColor() == Pawn.YELLOW) {
                player3Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(2).getChosenColor() == Pawn.GREEN) {
                player3Nickname.setStyle("-fx-text-fill: green;");
            }
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
        }

        // 4 players
        else if (((this.network == 1)&&(rmiClient.getPlayersInTheGame().size() == 4))||((this.network == 2)&&(clientSCK.getPlayersInTheGame().size() == 4))) {
            this.nPlayers=4;
            points1.setOpacity(1);
            points2.setOpacity(1);
            points3.setOpacity(1);
            points4.setOpacity(1);
            player1Nickname.setText(playersInOrder.get(0).getNickname());
            if (playersInOrder.get(0).getChosenColor() == Pawn.RED) {
                player1Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.BLUE) {
                player1Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.YELLOW) {
                player1Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(0).getChosenColor() == Pawn.GREEN) {
                player1Nickname.setStyle("-fx-text-fill: green;");
            }
            player2Nickname.setText(playersInOrder.get(1).getNickname());
            if (playersInOrder.get(1).getChosenColor() == Pawn.RED) {
                player2Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.BLUE) {
                player2Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.YELLOW) {
                player2Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(1).getChosenColor() == Pawn.GREEN) {
                player2Nickname.setStyle("-fx-text-fill: green;");
            }
            player3Nickname.setText(playersInOrder.get(2).getNickname());
            if (playersInOrder.get(2).getChosenColor() == Pawn.RED) {
                player3Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(2).getChosenColor() == Pawn.BLUE) {
                player3Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(2).getChosenColor() == Pawn.YELLOW) {
                player3Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(2).getChosenColor() == Pawn.GREEN) {
                player3Nickname.setStyle("-fx-text-fill: green;");
            }
            player4Nickname.setText(playersInOrder.get(3).getNickname());
            if (playersInOrder.get(3).getChosenColor() == Pawn.RED) {
                player4Nickname.setStyle("-fx-text-fill: red;");
            } else if (playersInOrder.get(3).getChosenColor() == Pawn.BLUE) {
                player4Nickname.setStyle("-fx-text-fill: blue;");
            } else if (playersInOrder.get(3).getChosenColor() == Pawn.YELLOW) {
                player4Nickname.setStyle("-fx-text-fill: #d0ff00;");
            } else if (playersInOrder.get(3).getChosenColor() == Pawn.GREEN) {
                player4Nickname.setStyle("-fx-text-fill: green;");
            }
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
        }

        // SETTING THE TABLE AND ITS DIMENSIONS
        int i=1;
        for (Player p: playersInOrder) {
            System.out.println("nickname: " + p.getNickname());
            if(i==1){
                tableP1=p.getBoard().getTable();
                cardsOnP1Board=new HashMap<>();
                cardsOnP1Board.put(p1Counter,(tableP1[dimension/2][dimension/2])); //baseCard
            }
            if(i==2){
                tableP2=p.getBoard().getTable();
                cardsOnP2Board=new HashMap<>();
                cardsOnP2Board.put(p2Counter,(tableP2[dimension/2][dimension/2])); //baseCard
            }
            if(i==3){
                tableP3=p.getBoard().getTable();
                cardsOnP3Board=new HashMap<>();
                cardsOnP3Board.put(p3Counter,(tableP3[dimension/2][dimension/2])); //baseCard
            }
            if(i==4){
                tableP4=p.getBoard().getTable();
                cardsOnP4Board=new HashMap<>();
                cardsOnP4Board.put(p4Counter,(tableP4[dimension/2][dimension/2])); //baseCard
            }
            i++;
        }


        // SETTING THE CORRECT NUMBER OF POINTS (UPDATE)
        updatePoints();


        // SETTING THE LABEL WITH THE CORRECT NAME OF THE PLAYER IN TURN
        setTurnLabel(false);


        // SHOWING MY OWN BOARD
        showP1Board();

    }



    /**
     * This method is used to turn the first card in the player's deck
     */
    public synchronized void turnCard1() {
        if(player1Card1.getImage()!=null) {
            selectedCard = 1;
            selectedCardLabel.setText("Selected Card: LEFT");
            if (orientationCard1) {
                orientationCard1 = false;
                String path;
                path = "/images/cards/back/  (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
                Image card1 = new Image(getClass().getResourceAsStream(path));
                player1Card1.setImage(card1);
            } else {
                orientationCard1 = true;
                String path;
                path = "/images/cards/front/  (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
                Image card1 = new Image(getClass().getResourceAsStream(path));
                player1Card1.setImage(card1);
            }
        }
    }



    /**
     * This method is used to turn the second card in the player's deck
     */
    public synchronized void turnCard2() {
        if(player1Card2.getImage()!=null) {
            selectedCard = 2;
            selectedCardLabel.setText("Selected Card: CENTER");
            if (orientationCard2) {
                orientationCard2 = false;
                String path;
                path = "/images/cards/back/  (" + playersInOrder.get(0).getPlayerDeck()[1].getId() + ").png";
                Image card1 = new Image(getClass().getResourceAsStream(path));
                player1Card2.setImage(card1);
            } else {
                orientationCard2 = true;
                String path;
                path = "/images/cards/front/  (" + playersInOrder.get(0).getPlayerDeck()[1].getId() + ").png";
                Image card1 = new Image(getClass().getResourceAsStream(path));
                player1Card2.setImage(card1);
            }
        }
    }



    /**
     * This method is used to turn the third card in the player's deck
     */
    public synchronized void turnCard3() {
        if(player1Card3.getImage()!=null) {
            selectedCard = 3;
            selectedCardLabel.setText("Selected Card: RIGHT");
            if (orientationCard3) {
                orientationCard3 = false;
                String path;
                path = "/images/cards/back/  (" + playersInOrder.get(0).getPlayerDeck()[2].getId() + ").png";
                Image card1 = new Image(getClass().getResourceAsStream(path));
                player1Card3.setImage(card1);
            } else {
                orientationCard3 = true;
                String path;
                path = "/images/cards/front/  (" + playersInOrder.get(0).getPlayerDeck()[2].getId() + ").png";
                Image card1 = new Image(getClass().getResourceAsStream(path));
                player1Card3.setImage(card1);
            }
        }
    }



    /**
     * This method shows the board of the first player
     */
    public void showP1Board() {
        currentBoard=new String(playersInOrder.get(0).getNickname());
        updateLabel(boardLabel, playersInOrder.get(0).getNickname() + "'s board");
        grid.getChildren().clear();
        initializeGridPaneCells(true);

        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        // SETTING THE CORRECT POSITION: IT DEPENDS ON THE NUMBER OF THE PLAYERS IN THE GAME

        boardPane.setHvalue((75.0*(dimension/2.0))/(75.0*83));
        boardPane.setVvalue((44.775*(81.0-dimension/2.0))/(44.775*81));

        for(Integer integer:cardsOnP1Board.keySet()){
            System.out.println(integer);
            String path=null;
            if(cardsOnP1Board.get(integer).getOrientation()) {
                path = "/images/cards/front/  (" + cardsOnP1Board.get(integer).getId() + ").png";
            }else {
                path = "/images/cards/back/  (" + cardsOnP1Board.get(integer).getId() + ").png";
            }
            Image card1 = new Image(getClass().getResourceAsStream(path));
            ImageView imageView = new ImageView(card1);
            imageView.setFitWidth(100.0);
            imageView.setFitHeight(68.25);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
            Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5); // margin regulations
            GridPane.setMargin(imageView, insets);
            this.grid.add(imageView, cardsOnP1Board.get(integer).getPosition().getX(), 81-cardsOnP1Board.get(integer).getPosition().getY());
        }

    }



    /**
     * This method shows the board of the second player
     */
    public void showP2Board(){

        currentBoard=new String(playersInOrder.get(1).getNickname());
        updateLabel(boardLabel, playersInOrder.get(1).getNickname() + "'s board");
        grid.getChildren().clear();
        initializeGridPaneCells(false);

        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        boardPane.setHvalue((75.0*(dimension /2))/(75.0*83));
        boardPane.setVvalue((44.775*(81-dimension /2))/(44.775*81));

        // SHOWING THE CARDS
        for(Integer integer:cardsOnP2Board.keySet()){
            String path=null;
            if(cardsOnP2Board.get(integer).getOrientation()) {
                path = "/images/cards/front/  (" + cardsOnP2Board.get(integer).getId() + ").png";
            }else {
                path = "/images/cards/back/  (" + cardsOnP2Board.get(integer).getId() + ").png";
            }Image card1 = new Image(getClass().getResourceAsStream(path));
            ImageView imageView = new ImageView(card1);
            imageView.setFitWidth(100.0);
            imageView.setFitHeight(68.25);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
            Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5);
            GridPane.setMargin(imageView, insets);
            this.grid.add(imageView, cardsOnP2Board.get(integer).getPosition().getX(), 81-cardsOnP2Board.get(integer).getPosition().getY());
        }
    }



    /**
     * This method shows the board of the third player
     */
    public void showP3Board(){
        currentBoard=new String(playersInOrder.get(2).getNickname());
        updateLabel(boardLabel, playersInOrder.get(2).getNickname() + "'s board");
        grid.getChildren().clear();
        initializeGridPaneCells(false);

        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        boardPane.setHvalue((75.0*(dimension /2))/(75.0*83));
        boardPane.setVvalue((44.775*(81-dimension /2))/(44.775*81));

        for(Integer integer:cardsOnP3Board.keySet()){
            String path=null;
            if(cardsOnP3Board.get(integer).getOrientation()) {
                path = "/images/cards/front/  (" + cardsOnP3Board.get(integer).getId() + ").png";
            }else {
                path = "/images/cards/back/  (" + cardsOnP3Board.get(integer).getId() + ").png";
            }
            Image card1 = new Image(getClass().getResourceAsStream(path));
            ImageView imageView = new ImageView(card1);
            imageView.setFitWidth(100.0);
            imageView.setFitHeight(68.25);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
           Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5);
            GridPane.setMargin(imageView, insets);
            this.grid.add(imageView, cardsOnP3Board.get(integer).getPosition().getX(), 81-cardsOnP3Board.get(integer).getPosition().getY());
        }

    }



    /**
     * This method shows the board of the fourth player
     */
    public void showP4Board(){
        currentBoard=new String(playersInOrder.get(3).getNickname());
        updateLabel(boardLabel, playersInOrder.get(3).getNickname() + "'s board");
        grid.getChildren().clear();
        initializeGridPaneCells(false);

        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        boardPane.setHvalue((75.0*(dimension /2))/(75.0*83));
        boardPane.setVvalue((44.775*(81-dimension /2))/(44.775*81));

        for(Integer integer:cardsOnP4Board.keySet()){
            String path=null;
            if(cardsOnP4Board.get(integer).getOrientation()) {
                path = "/images/cards/front/  (" + cardsOnP4Board.get(integer).getId() + ").png";
            }else {
                path = "/images/cards/back/  (" + cardsOnP4Board.get(integer).getId() + ").png";
            }
            Image card1 = new Image(getClass().getResourceAsStream(path));
            ImageView imageView = new ImageView(card1);
            imageView.setFitWidth(100.0);
            imageView.setFitHeight(68.25);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
           Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5);
            GridPane.setMargin(imageView, insets);
            this.grid.add(imageView, cardsOnP4Board.get(integer).getPosition().getX(), 81-cardsOnP4Board.get(integer).getPosition().getY());
        }

    }



    /**
     * This method draws a card from resource deck
     */
    public synchronized void drawCardFromRD() {
        if(resourceDeck.getImage()!=null) {
            if (emptySpace != 0 && !lastRound) {
                Integer temp = emptySpace;
                emptySpace = 0;
                String path = null;
                if (network == 1) {
                    path = "/images/cards/front/  (" + rmiClient.getResourceDeck().checkFirstCard().getId() + ").png";
                } else if (network == 2) {
                    path = "/images/cards/front/  (" + clientSCK.getResourceDeck().checkFirstCard().getId() + ").png";
                }
                Image newCard = new Image(getClass().getResourceAsStream(path));
                if (temp == 1) {
                    orientationCard1 = true;
                    player1Card1.setImage(newCard);
                } else if (temp == 2) {
                    orientationCard2 = true;
                    player1Card2.setImage(newCard);
                } else if (temp == 3) {
                    orientationCard3 = true;
                    player1Card3.setImage(newCard);
                }
                if (network == 2) {

                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getResourceDeck().checkFirstCard());

                } else if (network == 1) {


                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getResourceDeck().checkFirstCard());


                }
            }
        }

    }



    /**
     * This method draws a card from gold deck
     */
    public synchronized void drawCardFromGD() {

        if(goldDeck.getImage()!=null) {
            if (emptySpace != 0 && !lastRound) {
                Integer temp = emptySpace;
                emptySpace = 0;
                String path = null;
                if (network == 1) {
                    path = "/images/cards/front/  (" + rmiClient.getGoldDeck().checkFirstCard().getId() + ").png";
                } else if (network == 2) {
                    path = "/images/cards/front/  (" + clientSCK.getGoldDeck().checkFirstCard().getId() + ").png";
                }
                Image newCard = new Image(getClass().getResourceAsStream(path));
                if (temp == 1) {
                    orientationCard1 = true;
                    player1Card1.setImage(newCard);
                } else if (temp == 2) {
                    orientationCard2 = true;
                    player1Card2.setImage(newCard);
                } else if (temp == 3) {
                    orientationCard3 = true;
                    player1Card3.setImage(newCard);
                }
                if (network == 2) {

                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getGoldDeck().checkFirstCard());

                } else if (network == 1) {


                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getGoldDeck().checkFirstCard());


                }
            }
        }
    }



    /**
     * This method draws a card from resource card 1
     */
    public synchronized void drawCardFromRC1(){
        if(emptySpace!=0&&!lastRound){
            Integer temp=emptySpace;
            emptySpace=0;
            if(resourceCard1.getImage()!=null) {
                if (temp == 1) {
                    orientationCard1 = true;
                    player1Card1.setImage(resourceCard1.getImage());
                } else if (temp == 2) {
                    orientationCard2 = true;
                    player1Card2.setImage(resourceCard1.getImage());
                } else if (temp == 3) {
                    orientationCard3 = true;
                    player1Card3.setImage(resourceCard1.getImage());
                }
                if (network == 2) {

                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getResourceCard1());

                } else if (network == 1) {


                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getResourceCard1());


                }
            }

        }

    }



    /**
     * This method draws a card from resource card 2
     */
    public synchronized void drawCardFromRC2(){
        if(emptySpace!=0&&!lastRound){
            Integer temp=emptySpace;
            emptySpace=0;
            if(resourceCard2.getImage()!=null) {
                if (temp == 1) {
                    orientationCard1 = true;
                    player1Card1.setImage(resourceCard2.getImage());
                } else if (temp == 2) {
                    orientationCard2 = true;
                    player1Card2.setImage(resourceCard2.getImage());
                } else if (temp == 3) {
                    orientationCard3 = true;
                    player1Card3.setImage(resourceCard2.getImage());
                }
                if (network == 2) {

                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getResourceCard2());

                } else if (network == 1) {


                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getResourceCard2());


                }
            }
        }

    }



    /**
     * This method draws a card from gold card 1
     */
    public synchronized void drawCardFromGC1(){
        if(emptySpace!=0&&!lastRound){
            Integer temp=emptySpace;
            emptySpace=0;
            if(goldCard1.getImage()!=null) {
                if (temp == 1) {
                    orientationCard1 = true;
                    player1Card1.setImage(goldCard1.getImage());
                } else if (temp == 2) {
                    orientationCard2 = true;
                    player1Card2.setImage(goldCard1.getImage());
                } else if (temp == 3) {
                    orientationCard3 = true;
                    player1Card3.setImage(goldCard1.getImage());
                }
                if (network == 2) {

                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getGoldCard1());

                } else if (network == 1) {


                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getGoldCard1());


                }
            }
        }

    }



    /**
     * This method draws a card from gold card 2
     */
    public synchronized void drawCardFromGC2(){
        if(emptySpace!=0&&!lastRound){
            Integer temp=emptySpace;
            emptySpace=0;
            if(goldCard2.getImage()!=null) {
                if (temp == 1) {
                    orientationCard1 = true;
                    player1Card1.setImage(goldCard2.getImage());
                } else if (temp == 2) {
                    orientationCard2 = true;
                    player1Card2.setImage(goldCard2.getImage());
                } else if (temp == 3) {
                    orientationCard3 = true;
                    player1Card3.setImage(goldCard2.getImage());
                }
                if (network == 2) {

                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getGoldCard2());

                } else if (network == 1) {


                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getGoldCard2());


                }
            }

        }

    }



    /**
     * Setter method
     * @param scheduler
     */
    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }



    /**
     * Setter method
     * @param disconnectionLock
     */
    public void setDisconnectionLock(Object disconnectionLock) {
        this.disconnectionLock = disconnectionLock;
    }



    /**
     * This method initializes all the buttons in the gridpane
     * @param myBoard
     */
    public void initializeGridPaneCells(boolean myBoard) { // true = your board [you may play]     false = other player's board [you can't play]
        if (myBoard && network == 1 && rmiClient.getPersonalPlayer().getNickname().equals(rmiClient.getPlayersInTheGame().get(0).getNickname()) && !cardPlaced) { // RMI && player is in turn
            playablePositions = rmiClient.getPersonalPlayer().getBoard().getPlayablePositions();
        }
        else if(myBoard&&network == 2 && clientSCK.getPersonalPlayer().getNickname().equals(clientSCK.getPlayersInTheGame().get(0).getNickname())&& !cardPlaced){
            playablePositions = clientSCK.getPersonalPlayer().getBoard().getPlayablePositions();
        }
        else {
            playablePositions = null;
        }

        if(playablePositions != null){
            for(Coordinates coordinates:this.playablePositions) {
                System.out.println("Playable position (x, y): " + coordinates.getX() + " " + coordinates.getY());
                Button button = new Button();
                button.setOpacity(0.5);
                button.setPrefSize(70, 38.25);
                button.setText(coordinates.getX() + "," + coordinates.getY());
                button.setOnAction(event -> buttonClicked((Button) event.getSource()));
                grid.add(button, coordinates.getX(), 81-coordinates.getY());
            }
        }

    }



    /**
     * This method is invoked when the player clicks a button to play a card on his board
     * @param button clicked
     */
    private synchronized void buttonClicked(Button button) {
         if(!cardPlaced) {
             String buttonText = button.getText();
             String[] coordinates = buttonText.split(",");
             try {
                 int col = Integer.parseInt(coordinates[0]);
                 int row = Integer.parseInt(coordinates[1]);

                 String path = null;
                 if (selectedCard != 0) {
                     if (network == 1) {
                         PlayableCard[] playerDeck = rmiClient.getPersonalPlayer().getPlayerDeck();
                         if (selectedCard == 1) {
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 1");
                             try {

                                     rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), playerDeck[0], new Coordinates(col, row), orientationCard1);

                                 if(!rmiClient.getADisconnectionHappened()) {
                                     p1Counter++;
                                     PlayableCard playableCard = playerDeck[0];
                                     playableCard.setPosition(new Coordinates(col, row));
                                     playableCard.setOrientation(orientationCard1);
                                     cardsOnP1Board.put(p1Counter, playableCard);
                                     if (orientationCard1) {
                                         path = "/images/cards/front/  (" + playerDeck[0].getId() + ").png";
                                     } else {
                                         path = "/images/cards/back/  (" + playerDeck[0].getId() + ").png";
                                     }
                                     player1Card1.setImage(null);
                                     emptySpace = 1;
                                 }else {
                                     cardPlaced = false;
                                     updateLabel(selectedCardLabel, "Sorry, a disconnection happened");
                                 }
                             }catch (IllegalArgumentException e) {
                                 System.out.println("Qui non ho abbastanza risorse");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                             }
                         } else if (selectedCard == 2) {
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 2");
                             try {

                                     rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), playerDeck[1], new Coordinates(col, row), orientationCard2);

                                 if(!rmiClient.getADisconnectionHappened()) {
                                     p1Counter++;
                                     PlayableCard playableCard = playerDeck[1];
                                     playableCard.setPosition(new Coordinates(col, row));
                                     playableCard.setOrientation(orientationCard2);
                                     cardsOnP1Board.put(p1Counter, playableCard);
                                     if (orientationCard1) {
                                         path = "/images/cards/front/  (" + playerDeck[1].getId() + ").png";
                                     } else {
                                         path = "/images/cards/back/  (" + playerDeck[1].getId() + ").png";
                                     }
                                     player1Card2.setImage(null);
                                     emptySpace = 2;
                                 }else {
                                     cardPlaced = false;
                                     updateLabel(selectedCardLabel, "Sorry, a disconnection happened");
                                 }
                             } catch (IllegalArgumentException e) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                             }
                         } else if (selectedCard == 3) {
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 3");
                             try {

                                     rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), playerDeck[2], new Coordinates(col, row), orientationCard3);

                                 if(!rmiClient.getADisconnectionHappened()) {
                                     p1Counter++;
                                     PlayableCard playableCard = playerDeck[2];
                                     playableCard.setPosition(new Coordinates(col, row));
                                     playableCard.setOrientation(orientationCard3);
                                     cardsOnP1Board.put(p1Counter, playableCard);
                                     if (orientationCard1) {
                                         path = "/images/cards/front/  (" + playerDeck[2].getId() + ").png";
                                     } else {
                                         path = "/images/cards/back/  (" + playerDeck[2].getId() + ").png";
                                     }
                                     player1Card3.setImage(null);
                                     emptySpace = 3;
                                 }else {
                                     cardPlaced = false;
                                     updateLabel(selectedCardLabel, "Sorry, a disconnection happened");
                                 }
                             } catch (IllegalArgumentException e) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                             }
                         }
                     }
                     if (network == 2) {
                         PlayableCard[] playerDeck = clientSCK.getPersonalPlayer().getPlayerDeck();
                         if (selectedCard == 1) {
                             //cardPlaced = true;
                             System.out.println("PLAYED CARD 1");

                                 clientSCK.setErrorState(false);
                                 clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), playerDeck[0], new Coordinates(col, row), orientationCard1);

                             if (clientSCK.getErrorState()) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                                 clientSCK.setErrorState(false);
                             } else if(!clientSCK.getADisconnectionHappened()){
                                 cardPlaced = true;
                                 p1Counter++;
                                 PlayableCard playableCard = playerDeck[0];
                                 playableCard.setPosition(new Coordinates(col, row));
                                 playableCard.setOrientation(orientationCard1);
                                 cardsOnP1Board.put(p1Counter, playableCard);
                                 for (Integer i : cardsOnP1Board.keySet()) {
                                     System.out.println(i);
                                 }
                                 if (orientationCard1) {
                                     path = "/images/cards/front/  (" + playerDeck[0].getId() + ").png";
                                 } else {
                                     path = "/images/cards/back/  (" + playerDeck[0].getId() + ").png";
                                 }
                                 player1Card1.setImage(null);
                                 emptySpace = 1;
                             }else if(clientSCK.getADisconnectionHappened()){
                                 updateLabel(selectedCardLabel, "Sorry, a disconnection happened");
                             }
                         } else if (selectedCard == 2) {
                             //cardPlaced = true;
                             System.out.println("PLAYED CARD 2");

                                 clientSCK.setErrorState(false);
                                 clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), playerDeck[1], new Coordinates(col, row), orientationCard2);

                             if (clientSCK.getErrorState()) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                                 clientSCK.setErrorState(false);
                             } else if(!clientSCK.getADisconnectionHappened()){
                                 cardPlaced = true;
                                 p1Counter++;
                                 PlayableCard playableCard = playerDeck[1];
                                 playableCard.setPosition(new Coordinates(col, row));
                                 playableCard.setOrientation(orientationCard2);
                                 cardsOnP1Board.put(p1Counter, playableCard);
                                 if (orientationCard1) {
                                     path = "/images/cards/front/  (" + playerDeck[1].getId() + ").png";
                                 } else {
                                     path = "/images/cards/back/  (" + playerDeck[1].getId() + ").png";
                                 }
                                 player1Card2.setImage(null);
                                 emptySpace = 2;
                             }else if(clientSCK.getADisconnectionHappened()){
                             updateLabel(selectedCardLabel, "Sorry, a disconnection happened");
                             }
                         } else if (selectedCard == 3) {
                             System.out.println("PLAYED CARD 3");

                                 clientSCK.setErrorState(false);
                                 clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), playerDeck[2], new Coordinates(col, row), orientationCard3);

                             if (clientSCK.getErrorState()) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                                 clientSCK.setErrorState(false);
                             } else if(!clientSCK.getADisconnectionHappened()){
                                 cardPlaced = true;
                                 p1Counter++;
                                 PlayableCard playableCard = playerDeck[2];
                                 playableCard.setPosition(new Coordinates(col, row));
                                 playableCard.setOrientation(orientationCard3);
                                 cardsOnP1Board.put(p1Counter, playableCard);
                                 if (orientationCard1) {
                                     path = "/images/cards/front/  (" + playerDeck[2].getId() + ").png";
                                 } else {
                                     path = "/images/cards/back/  (" + playerDeck[2].getId() + ").png";
                                 }
                                 player1Card3.setImage(null);
                                 emptySpace = 3;
                             }else if(clientSCK.getADisconnectionHappened()){
                                 updateLabel(selectedCardLabel, "Sorry, a disconnection happened");
                             }
                         }
                     }
                     if (cardPlaced) {
                         Image card1 = new Image(getClass().getResourceAsStream(path));
                         ImageView imageView = new ImageView(card1);
                         imageView.setFitWidth(100.0);
                         imageView.setFitHeight(68.25);
                         imageView.setPreserveRatio(true);
                         imageView.setSmooth(true);
                         Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5);
                         GridPane.setMargin(imageView, insets);
                         grid.add(imageView, col, 81 - row);
                         showP1Board();

                         if (lastRound) {
                             selectedCardLabel.setText("This was your last round: YOU CAN'T DRAW ANY CARDS");
                         }
                     }

                 }
             } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
             }
         }
    }



    /**
     * This method updates the first resource card
     * @param card new card
     */
    public void updateResourceCard1(PlayableCard card) {
        Platform.runLater(() -> {
            if (card != null) {
                String path = "/images/cards/front/  (" + card.getId() + ").png";
                Image newCard = new Image(getClass().getResourceAsStream(path));
                resourceCard1.setImage(newCard);
            } else {
                resourceCard1.setImage(null);
            }
        });
    }



    /**
     * This method updates the second resource card
     * @param card new card
     */
    public void updateResourceCard2(PlayableCard card) {
        Platform.runLater(() -> {
            if (card != null) {
                String path = "/images/cards/front/  (" + card.getId() + ").png";
                Image newCard = new Image(getClass().getResourceAsStream(path));
                resourceCard2.setImage(newCard);
            } else {
                resourceCard2.setImage(null);
            }
        });
    }



    /**
     * This method updates the first gold card
     * @param card new card
     */
    public void updateGoldCard1(PlayableCard card) {
        Platform.runLater(() -> {
            if (card != null) {
                String path = "/images/cards/front/  (" + card.getId() + ").png";
                Image newCard = new Image(getClass().getResourceAsStream(path));
                goldCard1.setImage(newCard);
            } else { // when both deck are finished -> it returns a null card to the market
                goldCard1.setImage(null);
            }
        });
    }



    /**
     * This method updates the second gold card
     * @param card new card
     */
    public void updateGoldCard2(PlayableCard card) {
        Platform.runLater(() -> {
            if (card != null) {
                String path = "/images/cards/front/  (" + card.getId() + ").png";
                Image newCard = new Image(getClass().getResourceAsStream(path));
                goldCard2.setImage(newCard);
            } else {
                goldCard2.setImage(null);
            }
        });
    }



    /**
     * This method is invoked when it's last round
     * @param lastRound true if it's the last turn
     */
    public void updateRound(boolean lastRound) {
        Platform.runLater(()->{
                setTurnLabel(lastRound);
        });
    }



    /**
     * This method is invoked to update a board of a player
     * @param boardOwner player
     * @param newCard added card
     */
    public void updateBoard(String boardOwner, PlayableCard newCard) {
        if(boardOwner.equals(playersInOrder.get(1).getNickname())){
            System.out.println("sto aggiornando la board del player p2");
            p2Counter++;
            cardsOnP2Board.put(p2Counter, newCard);
            if((currentBoard!=null)&&currentBoard.equals(playersInOrder.get(1).getNickname())){
                Platform.runLater(() -> {
                    showP2Board();
                });
            }
        } else if (playersInOrder.size() >= 3 && boardOwner.equals(playersInOrder.get(2).getNickname())) {
            System.out.println("sto aggiornando la board del player p3");
            p3Counter++;
            cardsOnP3Board.put(p3Counter, newCard);
            if((currentBoard!=null)&&currentBoard.equals(playersInOrder.get(2).getNickname())){
                Platform.runLater(() -> {
                showP3Board();
                });
            }
        } else if (playersInOrder.size() >= 4 && boardOwner.equals(playersInOrder.get(3).getNickname())) {
            System.out.println("sto aggiornando la board del player p4");
            p4Counter++;
            cardsOnP4Board.put(p4Counter, newCard);
            if((currentBoard!=null)&&currentBoard.equals(playersInOrder.get(3).getNickname())){
                Platform.runLater(() -> {
                showP4Board();
                });
            }
        }
    }



    /**
     * This method is invoked to update the resource deck
     */
    public void updateResourceDeck() {
        Platform.runLater(() -> {
            String path=null;
            if(network==1){
                try {
                    path = "/images/cards/back/  (" + rmiClient.getResourceDeck().checkFirstCard().getId() + ").png";
                    Image newCard = new Image(getClass().getResourceAsStream(path));
                    resourceDeck.setImage(newCard);
                } catch (EmptyStackException e) {
                    resourceDeck.setImage(null);
                }
            }else if(network==2){
                try {
                    path = "/images/cards/back/  (" + clientSCK.getResourceDeck().checkFirstCard().getId() + ").png";
                    Image newCard = new Image(getClass().getResourceAsStream(path));
                    resourceDeck.setImage(newCard);
                } catch (EmptyStackException e) {
                    resourceDeck.setImage(null);
                }
            }
        });
    }



    /**
     * This method is invoked to update the gold deck
     */
    public void updateGoldDeck() {
        Platform.runLater(() -> {
            String path=null;
            if(network==1){
                try {
                    path = "/images/cards/back/  (" + rmiClient.getGoldDeck().checkFirstCard().getId() + ").png";
                    Image newCard = new Image(getClass().getResourceAsStream(path));
                    goldDeck.setImage(newCard);
                } catch (EmptyStackException e) {
                    goldDeck.setImage(null);
                }
            }else if(network==2){
                try {
                    path = "/images/cards/back/  (" + clientSCK.getGoldDeck().checkFirstCard().getId() + ").png";
                    Image newCard = new Image(getClass().getResourceAsStream(path));
                    goldDeck.setImage(newCard);
                } catch (EmptyStackException e) {
                    goldDeck.setImage(null);
                }
            }
        });
    }




    /**
     * This method is invoked to update the player's deck
     * @param nickname is player's nickname
     * @param playerDeck is player's deck
     */
    public void updatePlayerDeck(String nickname,PlayableCard[] playerDeck) {
        Platform.runLater(() -> {
            String path=null;
            Image newCard=null;
            int i=1;
            for (Player p : playersInOrder) {
                if(i==2){
                    if(p.getNickname().equals(nickname)){
                        if(playerDeck[0]!=null) {
                            path = "/images/cards/back/  (" + playerDeck[0].getId() + ").png";
                            newCard = new Image(getClass().getResourceAsStream(path));
                            player2Card1.setImage(newCard);
                        }else {
                            player2Card1.setImage(null);
                        }
                        if(playerDeck[1]!=null) {
                            path = "/images/cards/back/  (" + playerDeck[1].getId() + ").png";
                            newCard = new Image(getClass().getResourceAsStream(path));
                            player2Card2.setImage(newCard);
                        }else {
                            player2Card2.setImage(null);
                        }
                        if(playerDeck[2]!=null) {
                            path = "/images/cards/back/  (" + playerDeck[2].getId() + ").png";
                            newCard = new Image(getClass().getResourceAsStream(path));
                            player2Card3.setImage(newCard);
                        }else {
                            player2Card3.setImage(null);
                        }
                    }
                }
                if(i==3){
                    if(p.getNickname().equals(nickname)){
                        if(playerDeck[0]!=null) {
                        path = "/images/cards/back/  (" + playerDeck[0].getId() + ").png";
                        newCard = new Image(getClass().getResourceAsStream(path));
                        player3Card1.setImage(newCard);
                        }else {
                            player3Card1.setImage(null);
                        }
                        if(playerDeck[1]!=null) {
                        path = "/images/cards/back/  (" + playerDeck[1].getId() + ").png";
                        newCard = new Image(getClass().getResourceAsStream(path));
                        player3Card2.setImage(newCard);
                        }else {
                            player3Card2.setImage(null);
                        }
                        if(playerDeck[2]!=null) {
                            path = "/images/cards/back/  (" + playerDeck[2].getId() + ").png";
                            newCard = new Image(getClass().getResourceAsStream(path));
                            player3Card3.setImage(newCard);
                        }else {
                            player3Card3.setImage(null);
                        }

                    }
                }if(i==4){
                    if(p.getNickname().equals(nickname)){
                        if(playerDeck[0]!=null) {
                            path = "/images/cards/back/  (" + playerDeck[0].getId() + ").png";
                            newCard = new Image(getClass().getResourceAsStream(path));
                            player4Card1.setImage(newCard);
                        }else {
                            player4Card1.setImage(null);
                        }
                        if(playerDeck[1]!=null) {
                            path = "/images/cards/back/  (" + playerDeck[1].getId() + ").png";
                            newCard = new Image(getClass().getResourceAsStream(path));
                            player4Card2.setImage(newCard);
                        }else {
                            player4Card2.setImage(null);
                        }
                        if(playerDeck[2]!=null) {
                            path = "/images/cards/back/  (" + playerDeck[2].getId() + ").png";
                            newCard = new Image(getClass().getResourceAsStream(path));
                            player4Card3.setImage(newCard);
                        }else {
                            player4Card3.setImage(null);
                        }
                    }
                }
                i++;
            }
        });
    }



    /**
     * This method changes the scene
     * @param winners list of players who won the game or who tied
     */
    public void changeScene(List<Player> winners) {
        // let's show the new window: winners and losers
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/winners.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException ignored) {
        }

        GUIWinnersController ctr = null;
        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }
        stage.setOnCloseRequest(event -> this.leaveGame());

        // setting the parameters in the new controller
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);
        ctr.setWinners(winners);
        ctr.setAllFeatures();

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
        int seconds=40;
        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(event -> {
            stage.close();
            if (network==1){

                    rmiClient.handleDisconnectionFunction();

            }
            else if(network==2){

                    clientSCK.handleDisconnectionFunction();


            }
        });
        delay.play();
    }



    /**
     * This method updates the list of winners
     * @param winners of the game
     */
    public void updateWinners(List<Player> winners) {
        if (winners.size() == 1) {
            System.out.println(winners.get(0).getNickname() + " WON!!!");
        } else if (winners.size() > 1) {
            for (Player p : winners) {
                System.out.print(p.getNickname() + ", ");
            }
            System.out.println("tied!");
        }
        Platform.runLater(() -> changeScene(winners));
    }



    /**
     * This method is used when a player clicks on the button "MUSIC ON/OFF" to play or stop the music
     */
    public void musicONOFF() {
        if (playingMusic) {
            mediaPlayer.stop();
            playingMusic = false;
        }else{
            mediaPlayer.play();
            playingMusic = true;
        }
    }



    /**
     * Setter method
     * @param mp media player
     */
    public void setMediaPlayer(MediaPlayer mp) {
        this.mediaPlayer = mp;
    }



    /**
     * Setter method
     * @param objectiveCardselected obj card
     */
    public void setObjectiveCardselected(ObjectiveCard objectiveCardselected) {
        this.objectiveCardselected = objectiveCardselected;
    }



    /**
     * Setter method
     * @param playersInOrder list of players
     */
    public void setPlayersInOrder(List<Player> playersInOrder) {
        this.playersInOrder = playersInOrder;
    }



    /**
     * Setter method
     * @param stage of the windows
     */
    public void setStage(Stage stage) {
        this.stage=stage;
    }



    /**
     * Setter method
     * @param network 1 for rmi and 2 for tcp
     */
    public void setNetwork(int network) {
        this.network=network;
    }



    /**
     * Setter method
     * @param clientSCK client sck
     */
    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK=clientSCK;
    }



    /**
     * Setter method
     * @param rmiClient client rmi
     */
    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }

}
