package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class GUIGameController {


    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    private int network;
    private List<Player> playersInOrder;
    private ScheduledExecutorService scheduler;
    private Object disconnectionLock;
    private boolean finishedGD = false;
    private boolean finishedRD = false;
    private boolean orientationCard1;
    private boolean orientationCard2;
    private boolean orientationCard3;
    private int selectedCard = 0; // 1 = card1  2 = card 2  3 = card
    private int selectedBoard = 0; // n = player n
    private Integer dimension;
    private String currentBoard=null;
    private PlayableCard[][] tableP1;
    private PlayableCard[][] tableP2;
    private PlayableCard[][] tableP3;
    private PlayableCard[][] tableP4;
    private int nPlayers;
    private boolean cardDrawn = false;
    private boolean cardPlaced = false;
    private Coordinates coordinatesToPlay;
    private Set<Coordinates> playablePositions=null; //null quando non è il proprio turno (devono tornare a null quando la carta viene buttata)
    private Map<Integer,PlayableCard> cardsOnP1Board; // Map <turn number, card id>
    private Map<Integer,PlayableCard> cardsOnP2Board;
    private Map<Integer,PlayableCard> cardsOnP3Board;
    private Map<Integer,PlayableCard> cardsOnP4Board;
    private Integer currentPlayerCounter=1;

    // Counters for cards for each player
    private Integer p1Counter=1;
    private Integer p2Counter=1;
    private Integer p3Counter=1;
    private Integer p4Counter=1;
    private Integer emptySpace=0;
    private ObjectiveCard objectiveCardselected=null;

    //IDEA: confrontiamo le playable position precedenti con quelle nuove: controlliamo le posizioni della matrice dove è stata tolta una playable position per vedere se è stata aggiunta una carta
    //OPPURE: MOLTO MEGLIO -> ci facciamo arrivare assieme alla board la carta che è stata aggiunta (che ha già dentro di sè le coordinate giuste)
    private Set<Coordinates> p2PlayablePositions; //quelle di py le prendiamo direttamente dal personale player (perche non abbiamo bisogno di capire quale carta è stata aggiunta)
    private Set<Coordinates> p3PlayablePositions;
    private Set<Coordinates> p4PlayablePositions;
    private boolean lastRound=false;
    private MediaPlayer mediaPlayer;
    private boolean playingMusic = true;



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

//                int i=1;
//                for (Player p: playersInOrder) { //tolgo l'illuminazione da tutti i bottoni
//                    System.out.println("nickname: " + p.getNickname());
//                    if(i==1){
//                        buttonP1Board.setStyle("");}
//                    if(i==2){
//                        buttonP2Board.setStyle("");}
//                    if(i==3){
//                        buttonP3Board.setStyle("");}
//                    if(i==4){
//                        buttonP4Board.setStyle("");}
//                    i++;
//                }
                //mostriamo subito la board del giocatore in turno, se il giocatore in turno è p1 (quello a cui appartiene la view)
                showP1Board();

            } else {
                this.turnLabel.setText("It's " + clientSCK.getPlayersInTheGame().get(0).getNickname() + "'s turn!");
                /*
                if(currentBoard != null && currentBoard.equals(clientSCK.getPlayersInTheGame().get(0).getNickname())){
                    int i=1;
                    for(Player player:playersInOrder){
                        if(i==1){
                            if(player.getNickname().equals(currentBoard)){
                                showP1Board();
                                break;
                            }

                        }if(i==2){
                            if(player.getNickname().equals(currentBoard)){
                                showP2Board();
                                break;
                            }

                        }if(i==3){
                            if(player.getNickname().equals(currentBoard)){
                                showP3Board();
                                break;
                            }

                        }if(i==4){
                            if(player.getNickname().equals(currentBoard)){
                                showP4Board();
                                break;
                            }
                        }
                        i++;
                    }
                }

                 */
            }
        }
    }



    public void leaveGame() {
        synchronized (disconnectionLock) { //o sono in leaveGame() o in startPeriodicDisconnectionCheck()
            if ( ((network==1)&&(!rmiClient.getADisconnectionHappened())) || ((network==2)&&(!clientSCK.getADisconnectionHappened())) ){ //se è avvenuta una disconnessione leaveGame non esegue niente
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
                    } catch (RemoteException | NotBoundException e) { //nel caso in cui la disconnessione abbia già chiuso tutto
                        //throw new RuntimeException(e);
                    }
                } else if (network == 2) {
                    try { //in clientSCK viene fatta la exit
                        this.clientSCK.leaveGame(this.clientSCK.getPersonalPlayer().getNickname()); //lato server verrà mandato agli altri player evento disconnessione
                    } catch (RemoteException | NotBoundException e) { //nel caso in cui la disconnessione abbia già chiuso tutto
                        //throw new RuntimeException(e);
                    }
                }
            }
        }

    }

public void updateLabel(Label label, String text){
    Platform.runLater(() -> {
        label.setText(text);
    });
}

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
        for (Player p : playersInOrder){ //va in ordine: p1, p2, p3, p4
            if(i==1) { //primo giocatore di playersInOrder
                updateLabel(points1, p.getNickname() + ": " + playersWithPoints.get(p.getNickname()) + " pt");
            }
            if(i==2) { //secondo giocatore di playersInOrder
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

        System.out.println("A T T E N Z I O N E");
            System.out.println("STO SETTANDO I PUNTI DI TUTTI I PLAYER");
//            int rmiPlayer = 0;
//            int order = 0;
//            int sckPlayer = 0;
//
//            if (network == 1) {
//                for (Player p : playersInOrder) { //nella mia lista
//                    rmiPlayer = 0;
//                    for (Player p2 : rmiClient.getPlayersInTheGame()) { //lista dinamica
//                        if (p.getNickname().equals(p2.getNickname())) {
//                            // devo aggiornare i punti di quel player
//                            if (order == 0 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) { // p1
//                                System.out.println("I PUNTI DEL PLAYER 1 SONO: " + rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints());
//                                updateLabel(this.points1, p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
//                            } else if (order == 1 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) { // p2
//                                System.out.println("I PUNTI DEL PLAYER 2 SONO: " + rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints());
//                                updateLabel(this.points2, p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
//                            } else if (order == 2 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) { // p3
//                                System.out.println("I PUNTI DEL PLAYER 3 SONO: " + rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints());
//                                updateLabel(this.points3, p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
//                            } else if (order == 3 && rmiPlayer < rmiClient.getPlayersInTheGame().size()) { // p4
//                                System.out.println("I PUNTI DEL PLAYER 4 SONO: " + rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints());
//                                updateLabel(this.points4, p.getNickname() + ": " + (rmiClient.getPlayersInTheGame().get(rmiPlayer).getPoints()) + " pt");
//                            }
//                        }
//                        rmiPlayer++;
//                    }
//                    order++;
//                }
//
//            } else if (network == 2) {
//                for (Player p : playersInOrder) {
//                    sckPlayer = 0;
//                    for (Player p2 : clientSCK.getPlayersInTheGame()) {
//                        if (p.getNickname().equals(p2.getNickname())) {
//                            // devo aggiornare i punti di quel player
//                            if (order == 0 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
//                                System.out.println("I PUNTI DEL PLAYER 1 SONO: " + clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints());
//                                updateLabel(this.points1, p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
//                            } else if (order == 1 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
//                                System.out.println("I PUNTI DEL PLAYER 2 SONO: " + clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints());
//                                updateLabel(this.points2, p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
//                            } else if (order == 2 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
//                                System.out.println("I PUNTI DEL PLAYER 3 SONO: " + clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints());
//                                updateLabel(this.points3, p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
//                            } else if (order == 3 && sckPlayer < clientSCK.getPlayersInTheGame().size()) {
//                                System.out.println("I PUNTI DEL PLAYER 4 SONO: " + clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints());
//                                updateLabel(this.points4, p.getNickname() + ": " + (clientSCK.getPlayersInTheGame().get(sckPlayer).getPoints()) + " pt");
//                            }
//                        }
//                        sckPlayer++;
//                    }
//                    order++;
//                }
//            }
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
    public void setAllFeatures() throws RemoteException {
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
            this.dimension=clientSCK.getPersonalPlayer().getBoard().getBoardDimensions(); //le dimensioni sono uguali per tutti i giocatori
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
            playersInOrder = newList; // aggiungo in prima posizione il PERSONAL PLAYER
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
            // SETTING THE FIRST POSITION OF THE SCROLL IN THE SCROLLPANE
            // THANKS TO THIS THE PLAYER CAN IMMEDIATELY SEE HIS BASE CARD IN THE CENTER OF HI
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

        // INITIALIZING THE CELLS IN THE SCROLLPANE
        //initializeGridPaneCells();


        // dimensioni scrollpane: prefHeight="377.0" prefWidth="1028.0
        // SETTING THE CURRENT PLAYER (is the first in the list)
        // SETTING THE PAWNS
    }




    public synchronized void turnCard1() {
        if(player1Card1.getImage()!=null) { //se non c'è la carta non la giro
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



    public synchronized void turnCard2() {
        if(player1Card2.getImage()!=null) { //se non c'è la carta non la giro
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



    public synchronized void turnCard3() {
        if(player1Card3.getImage()!=null) { //se non c'è la carta non la giro
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



    public void showP1Board() { // togliamo la board precedente e stampiamo in ordine le carte della board del player 1
        currentBoard=new String(playersInOrder.get(0).getNickname());
        updateLabel(boardLabel, playersInOrder.get(0).getNickname() + "'s board");
        grid.getChildren().clear();// toglie pure i bottoni che vanno rimessi solo se è il turno di quel giocatore
        initializeGridPaneCells(true); //mette i bottoni solo se è il proprio turno e solo nelle nuove plyable positions

        selectedBoard = 1;
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        // SETTING THE CORRECT POSITION: IT DEPENDS ON THE NUMBER OF THE PLAYERS IN THE GAME

        /*
        if (nPlayers == 2) {
            boardPane.setHvalue(0.485);
            boardPane.setVvalue(0.535);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.3132);
           boardPane.setVvalue(0.32);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }

         */

        boardPane.setHvalue((75.0*(dimension/2))/(75.0*83));
        boardPane.setVvalue((44.775*(81-dimension/2))/(44.775*81));


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
            imageView.setFitWidth(100.0);  // larghezza desiderata
            imageView.setFitHeight(68.25); // altezza desiderata
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
            Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5); // Regola i margini come desiderato (top, left, bottom, right)
            GridPane.setMargin(imageView, insets); // in qualunque griglia lo vado a piazzare, questo avrà i suddetti margini all'incirca
            this.grid.add(imageView, cardsOnP1Board.get(integer).getPosition().getX(), 81-cardsOnP1Board.get(integer).getPosition().getY());
        }

/*
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
                         imageView.setFitWidth(100.0);  // larghezza desiderata
                         imageView.setFitHeight(68.25); // altezza desiderata
                         imageView.setPreserveRatio(true);
                         imageView.setSmooth(true);
                         imageView.toFront();
                         Insets insets = new Insets(-8.7375, -15, -8.7375, -15); // Regola i margini come desiderato
                         GridPane.setMargin(imageView, insets);
                         this.grid.add(imageView, i, j);
                         placed = true;
                     }
                 }
             }
        }

 */

    }



    public void showP2Board(){ //togliamo la board precedente e stampiamo in ordine le carte della board del player 2
        // UPDATING THE CARDS ON THE BOARD
//        boolean present = false;
//        if (network == 1) {
//            for(int playerIndex = 0; playerIndex < rmiClient.getPlayersInTheGame().size(); playerIndex++){
//                if(playersInOrder.get(1).getNickname().equals(rmiClient.getPlayersInTheGame().get(playerIndex).getNickname())){
//                    PlayableCard [][] table = rmiClient.getPlayersInTheGame().get(playerIndex).getBoard().getTable();
//                    for (int i=0; i<dimension; i++) {
//                        for (int j=0; j<dimension; j++) {
//                            present = false;
//                            if (table[i][j] != null) {
//                                // vedo se c'è nella map
//                                for (int k = 0; k < cardsOnP2Board.size() && !present; k++) {
//                                    if (cardsOnP2Board.get(k).equals(table[i][j])){
//                                        present = true;
//                                    } else {
//                                        // add the card to the MAP <Integer, PlayableCard>
//                                        cardsOnP2Board.put(cardsOnP2Board.size(),table[i][j]);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (network == 2) {
//            for(int playerIndex = 0; playerIndex < clientSCK.getPlayersInTheGame().size(); playerIndex++){
//                if(playersInOrder.get(1).getNickname().equals(clientSCK.getPlayersInTheGame().get(playerIndex).getNickname())){
//                    PlayableCard [][] table = clientSCK.getPlayersInTheGame().get(playerIndex).getBoard().getTable();
//                    for (int i=0; i<dimension; i++) {
//                        for (int j=0; j<dimension; j++) {
//                            present = false;
//                            if (table[i][j] != null) {
//                                // vedo se c'è nella map
//                                for (int k = 0; k < cardsOnP2Board.size() && !present; k++) {
//                                    if (cardsOnP2Board.get(k).equals(table[i][j])){
//                                        present = true;
//                                    } else {
//                                        // add the card to the MAP <Integer, PlayableCard>
//                                        cardsOnP2Board.put(cardsOnP2Board.size(),table[i][j]);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        currentBoard=new String(playersInOrder.get(1).getNickname());
        updateLabel(boardLabel, playersInOrder.get(1).getNickname() + "'s board");
        grid.getChildren().clear();//toglie pure i bottoni che vanno rimessi solo se è il turno di quel giocatore
        initializeGridPaneCells(false);

        selectedBoard = 2;
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());


        /*
        // SETTING THE CORRECT POSITION: IT DEPENDS FROM THE NUMBER OF THE PLAYERS IN THE GAME
        if (nPlayers == 2) {
            boardPane.setHvalue(0.485);
            boardPane.setVvalue(0.535);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.28447504302925986);
            boardPane.setVvalue(0.30693257359924053);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }

         */

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
            imageView.setFitWidth(100.0);  // larghezza desiderata
            imageView.setFitHeight(68.25); // altezza desiderata
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
            Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5); // Regola i margini come desiderato (top, left, bottom, right)
            GridPane.setMargin(imageView, insets);
            this.grid.add(imageView, cardsOnP2Board.get(integer).getPosition().getX(), 81-cardsOnP2Board.get(integer).getPosition().getY());
        }
/*

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
                        imageView.setFitWidth(100.0);  // larghezza desiderata
                        imageView.setFitHeight(68.25); // altezza desiderataimageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.toFront();
                        Insets insets = new Insets(-8.7375, -15, -8.7375, -15); // Regola i margini come desiderato
                        GridPane.setMargin(imageView, insets);
                        this.grid.add(imageView, i, j);
                        placed = true;
                    }
                }
            }
        }

 */

    }



    public void showP3Board(){ //togliamo la board precedente e stampiamo in ordine le carte della board del player 3
        currentBoard=new String(playersInOrder.get(2).getNickname());
        updateLabel(boardLabel, playersInOrder.get(2).getNickname() + "'s board");
        grid.getChildren().clear();//toglie pure i bottoni che vanno rimessi solo se è il turno di quel giocatore
        initializeGridPaneCells(false);

        selectedBoard = 3;
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        /*
        // SETTING THE CORRECT POSITION: IT DEPENDS FROM THE NUMBER OF THE PLAYERS IN THE GAME
        if (nPlayers == 2) {
            boardPane.setHvalue(0.485);
            boardPane.setVvalue(0.535);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.28447504302925986);
            boardPane.setVvalue(0.30693257359924053);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }


         */

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
            imageView.setFitWidth(100.0);  // larghezza desiderata
            imageView.setFitHeight(68.25); // altezza desiderata
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
           Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5); // Regola i margini come desiderato (top, left, bottom, right)
            GridPane.setMargin(imageView, insets);
            this.grid.add(imageView, cardsOnP3Board.get(integer).getPosition().getX(), 81-cardsOnP3Board.get(integer).getPosition().getY());
        }

/*
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
                        imageView.setFitWidth(100.0);  // larghezza desiderata
                        imageView.setFitHeight(68.25); // altezza desiderataimageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.toFront();
                        Insets insets = new Insets(-8.7375, -15, -8.7375, -15); // Regola i margini come desiderato
                        GridPane.setMargin(imageView, insets);
                        this.grid.add(imageView, i, j);
                        placed = true;
                    }
                }
            }
        }

 */
    }




    public void showP4Board(){ //togliamo la board precedente e stampiamo in ordine le carte della board del player 4
        currentBoard=new String(playersInOrder.get(3).getNickname());
        updateLabel(boardLabel, playersInOrder.get(3).getNickname() + "'s board");
        grid.getChildren().clear();//toglie pure i bottoni che vanno rimessi solo se è il turno di quel giocatore
        initializeGridPaneCells(false);

        selectedBoard = 4;
        // PRINTING THE CURRENT POSITIONS OF THE SCROLL PANE TO SET THEM!
        System.out.println("VERTICALE CORRENTE: " + boardPane.getVvalue());
        System.out.println("ORIZZONTALE CORRENTE: " + boardPane.getHvalue());

        /*
        // SETTING THE CORRECT POSITION: IT DEPENDS FROM THE NUMBER OF THE PLAYERS IN THE GAME
        if (nPlayers == 2) {
            boardPane.setHvalue(0.485);
            boardPane.setVvalue(0.535);
        } else if (nPlayers == 3) {
            boardPane.setHvalue(0.28447504302925986);
            boardPane.setVvalue(0.30693257359924053);
        } else if (nPlayers == 4) {
            boardPane.setHvalue(0.2099841521394612);
            boardPane.setVvalue(0.23585350854073317);
        }

         */
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
            imageView.setFitWidth(100.0);  // larghezza desiderata
            imageView.setFitHeight(68.25); // altezza desiderata
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.toFront();
           Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5); // Regola i margini come desiderato (top, left, bottom, right)
            GridPane.setMargin(imageView, insets);
            this.grid.add(imageView, cardsOnP4Board.get(integer).getPosition().getX(), 81-cardsOnP4Board.get(integer).getPosition().getY());
        }

/*
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
                        imageView.setFitWidth(100.0);  // larghezza desiderata
                        imageView.setFitHeight(68.25); // altezza desiderataimageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.toFront();
                        Insets insets = new Insets(-8.7375, -15, -8.7375, -15); // Regola i margini come desiderato
                        GridPane.setMargin(imageView, insets);
                        this.grid.add(imageView, i, j);
                        placed = true;
                    }
                }
            }
        }

 */
    }


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
                    try {
                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getResourceDeck().checkFirstCard());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                } else if (network == 1) {
                    try {
                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getResourceDeck().checkFirstCard());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                }


            }
        }

    }

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
                    try {
                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getGoldDeck().checkFirstCard());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                } else if (network == 1) {
                    try {
                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getGoldDeck().checkFirstCard());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

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
                    try {
                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getResourceCard1());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                } else if (network == 1) {
                    try {
                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getResourceCard1());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                }
            }

        }

    }

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
                    try {
                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getResourceCard2());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                } else if (network == 1) {
                    try {
                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getResourceCard2());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

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
                    try {
                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getGoldCard1());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                } else if (network == 1) {
                    try {
                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getGoldCard1());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

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
                    try {
                        clientSCK.drawCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getGoldCard2());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                } else if (network == 1) {
                    try {
                        rmiClient.drawCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getGoldCard2());
                    } catch (RemoteException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    } catch (NotBoundException e) {
                        System.out.println("errore nel pescaggio");
                        throw new RuntimeException(e);
                    }
                }
            }

        }

    }


    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public void setDisconnectionLock(Object disconnectionLock) {
        this.disconnectionLock = disconnectionLock;
    }



    public void mousePressed (MouseEvent mouseEvent) {
        System.out.println("SONO IN MOUSE PRESSED");

        String path = "/images/cards/front/  (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(card1);
        imageView.setFitWidth((card1.getWidth() / 5.8));  // larghezza desiderata
        imageView.setFitHeight((card1.getHeight() / 5.8)); // altezza desiderata
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        System.out.println("mouseEvent.getX() " +mouseEvent.getX()+" mouseEvent.getX()" + mouseEvent.getX());
        System.out.println("colonna " +mouseEvent.getX()/70.0+" riga" + mouseEvent.getX()/38.25);
        this.grid.add(imageView, (int) ((int) mouseEvent.getX()/70.0), (int) ((int) mouseEvent.getX()/38.25));

        /*
            int cellX;
            int cellY;
            double x = mouseEvent.getX();
            double y = mouseEvent.getX();
            System.out.println("DOUBLE X e Y: " + x + " " + y);
            cellX = (int) (x / 70.0);
            cellY = (int) (y / 38.25);

            System.out.println("COORDINATE MODIFICATE -->  x : " + cellX + " y: " + cellY);
            this.coordinatesToPlay = new Coordinates(cellX, cellY);
            playCard();

         */
    }


   /* public void playCard() {
        System.out.println("SONO IN PLAYCARD");

        if (network == 1 && rmiClient.getPersonalPlayer().getNickname().equals(rmiClient.getPlayersInTheGame().get(0).getNickname()) && !cardPlaced && selectedBoard == 1) { // RMI && player is in turn
            // CHECKING IF IT'S A PLAYABLE POSITION OR NOT
            playablePositions = rmiClient.getPersonalPlayer().getBoard().getPlayablePositions();
            for(Coordinates c : playablePositions) {
                if (c.getX() == coordinatesToPlay.getX() && c.getY() == coordinatesToPlay.getY() && !cardPlaced) {
                    if (playablePositions.contains(coordinatesToPlay)) {
                        if (selectedCard == 1) {
                            System.out.println("PLAYED CARD 1");
                            cardPlaced = true;
                            try {
                                rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[0], coordinatesToPlay, orientationCard1);
                            } catch (RemoteException | NotBoundException e) {
                                System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                updateLabel(selectedCardLabel, "You don't have enough resources!");
                            }
                            orientationCard1 = true;
                        } else if (selectedCard == 2) {
                            System.out.println("PLAYED CARD 2");
                            cardPlaced = true;
                            try {
                                rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[1], coordinatesToPlay, orientationCard2);
                            } catch (RemoteException | NotBoundException e) {
                                System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                updateLabel(selectedCardLabel, "You don't have enough resources!");
                            }
                            orientationCard2 = true;
                        } else if (selectedCard == 3) {
                            System.out.println("PLAYED CARD 3");
                            cardPlaced = true;
                            try {
                                rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[2], coordinatesToPlay, orientationCard3);
                            } catch (RemoteException | NotBoundException e) {
                                System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                updateLabel(selectedCardLabel, "You don't have enough resources!");
                            }
                            orientationCard3 = true;
                        } else if (selectedCard == 0) {
                            selectedCardLabel.setText("Please, select a card to play first!");
                            System.out.println("select a card!");
                        }
                    }
                }
            }
            if (!cardPlaced && selectedCard != 0) {
                System.out.println("This isn't a playable position");
            }

        } else if (network == 2 && clientSCK.getPersonalPlayer().getNickname().equals(clientSCK.getPlayersInTheGame().get(0).getNickname()) && !cardPlaced && selectedBoard == 1) { // SCK && player is in turn
            // CHECKING IF IT'S A PLAYABLE POSITION OR NOT
            playablePositions = clientSCK.getPersonalPlayer().getBoard().getPlayablePositions();
            for(Coordinates c : playablePositions) {
                if (c.getX() == coordinatesToPlay.getX() && c.getY() == coordinatesToPlay.getY() && !cardPlaced) {
                    if (selectedCard == 1) {
                        cardPlaced = true;
                        System.out.println("PLAYED CARD 1");
                        try {
                            clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[0], coordinatesToPlay, orientationCard1);
                        } catch (RemoteException | NotBoundException e) {
                            System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                            updateLabel(selectedCardLabel, "You don't have enough resources!");
                        }
                        orientationCard1 = true ;
                    } else if (selectedCard == 2) {
                        cardPlaced = true;
                        System.out.println("PLAYED CARD 2");
                        try {
                            clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[1], coordinatesToPlay, orientationCard2);
                        } catch (RemoteException | NotBoundException e) {
                            System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                            updateLabel(selectedCardLabel, "You don't have enough resources!");
                        }
                        orientationCard2 = true;
                    } else if (selectedCard == 3) {
                        cardPlaced = true;
                        System.out.println("PLAYED CARD 3");
                        try {
                            clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[2], coordinatesToPlay, orientationCard3);
                        } catch (RemoteException | NotBoundException e) {
                            System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                            updateLabel(selectedCardLabel, "You don't have enough resources!");
                        }
                        orientationCard3 = true;
                    } else if (selectedCard == 0) {
                        selectedCardLabel.setText("Please, select a card to play first!");
                        System.out.println("select a card!");
                    }
                }
            }
            if (!cardPlaced && selectedCard != 0) {
                System.out.println("This isn't a playable position");
                System.out.println("cardplaced = " + cardPlaced);
                System.out.println("selected card = " + selectedCard);
                System.out.println("selectedboard = " + selectedBoard);
                System.out.println("network = " + network);
                System.out.println("coordinates to play are (x, y) = " + coordinatesToPlay.getX() + " " + coordinatesToPlay.getY());
                System.out.println("selectedboard = " + selectedBoard);
                for(Coordinates c : playablePositions) {
                    System.out.println("playable positions (x, y) = " + c.getX() + " " + c.getY());
                }
            }
        } else if(selectedBoard != 1){
            System.out.println("Select your board!");
        } else if (network == 2 && !clientSCK.getPersonalPlayer().getNickname().equals(clientSCK.getPlayersInTheGame().get(0).getNickname())) {
            System.out.println("It's not your turn");
        } else if (network == 1 && !rmiClient.getPersonalPlayer().getNickname().equals(rmiClient.getPlayersInTheGame().get(0).getNickname())) {
        System.out.println("It's not your turn");
    }
    }
*/

/*

    public void initializeGridPaneCells() {
       // int count = 0;
        for (int row = 0; row < 81; row++) { //80
            for (int col = 0; col < 83; col++) { //82
                /*
                count++;
                Pane cell = new Pane();
                cell.setPrefSize(70, 38.25);
                cell.setAccessibleText(col+","+row);
                cell.setId("cell" + count); // cell1, cell2, cell3, ...
                cell.setOnMouseClicked(this::mousePressed);
                cell.setStyle("-fx-border-color: grey; -fx-border-width: 0.05px;");
                grid.add((Node)cell, col, row);

                 */
    /*
                Button button=new Button();
                button.setPrefSize(70, 38.25);
                button.setText(col+","+row);
                button.setOnAction( buttonClicked(button));
                grid.add(button,col, row);

            }
        }
        //grid.addEventFilter(MouseEvent.MOUSE_PRESSED, this::mousePressed);
    }

    private EventHandler<ActionEvent> buttonClicked(Button button) {
       String buttomText=button.getText();
       char[] x = new char[2];
        char[] y =new char[2];
        int i=0;
        while(buttomText.charAt(i)!=','){
            x[i]=buttomText.charAt(i);
            i++;
        }
        i++;//sorpasso la virgola
        int j=0;
        while(i<buttomText.length()){
            y[j]=buttomText.charAt(i);
            j++;
        }

        //di prova
        String path = "/images/cards/front/ (" + playersInOrder.get(0).getPlayerDeck()[0].getId() + ").png";
        Image card1 = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(card1);
        imageView.setFitWidth((card1.getWidth() / 5.8));  // larghezza desiderata
        imageView.setFitHeight((card1.getHeight() / 5.8)); // altezza desiderata
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        int row=0;
        for(int k=x.length;k<0;k--){
            row=row+(10^(k))*x[k];
        }
        int colomn=0;
        for(int k=y.length;k<0;k--){
            colomn=colomn+(10^(k))*y[k];
        }
        grid.add(imageView,row,colomn);


        return null;
    }

     */
public void initializeGridPaneCells(boolean myBoard) { // true = your board [you may play]     false = other player's board [you can't play]
    if (myBoard&&network == 1 && rmiClient.getPersonalPlayer().getNickname().equals(rmiClient.getPlayersInTheGame().get(0).getNickname()) && !cardPlaced) { // RMI && player is in turn
        playablePositions = rmiClient.getPersonalPlayer().getBoard().getPlayablePositions();
    }
    else if(myBoard&&network == 2 && clientSCK.getPersonalPlayer().getNickname().equals(clientSCK.getPlayersInTheGame().get(0).getNickname())&& !cardPlaced){
        playablePositions = clientSCK.getPersonalPlayer().getBoard().getPlayablePositions();
    }
    else { // non è il mio turno
        playablePositions = null;
    }


    if(playablePositions != null){ //se è il mio turno metto i bottoni
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

    /*
    for (int row = 0; row < 81; row++) {
        for (int col = 0; col < 83; col++) {
            Button button = new Button();
            button.setPrefSize(70, 38.25);
            button.setText(col + "," + row);
            button.setOnAction(event -> buttonClicked((Button) event.getSource()));
            grid.add(button, col, row);
        }
    }

     */
}




    private synchronized void buttonClicked(Button button) { //se ho potuto vedere e cliccare un bottone allora è il mio turno (io sono sempre p1)
         if(!cardPlaced) {
             String buttonText = button.getText();
             String[] coordinates = buttonText.split(",");
             try {
                 int col = Integer.parseInt(coordinates[0]);
                 int row = Integer.parseInt(coordinates[1]);

                 // Esegui le operazioni desiderate con 'row' e 'col'
                 // Esempio: aggiungi un'immagine
                 String path = null;
                 if (selectedCard != 0) { //è stata selezionata una carta
                     if (network == 1) {
                         PlayableCard[] playerDeck = rmiClient.getPersonalPlayer().getPlayerDeck();
                         if (selectedCard == 1) {
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 1");
                             try {
                                 rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), playerDeck[0], new Coordinates(col, row), orientationCard1);
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

                             } catch (RemoteException | NotBoundException e) {
                                 System.out.println("UNABLE TO COMMUNICATE W SERVER");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                             } catch (IllegalArgumentException e) {
                                 System.out.println("Qui non ho abbastanza risorse");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                             }
                         } else if (selectedCard == 2) {
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 2");
                             try {
                                 rmiClient.playCard(rmiClient.getPersonalPlayer().getNickname(), playerDeck[1], new Coordinates(col, row), orientationCard2);
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
                             } catch (RemoteException | NotBoundException e) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
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
                             } catch (RemoteException | NotBoundException e) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
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
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 1");
                             try {
                                 clientSCK.setErrorState(false);
                                 clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), playerDeck[0], new Coordinates(col, row), orientationCard1);
                             } catch (RemoteException | NotBoundException ignored) {
                             }
                             if (clientSCK.getErrorState()) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                                 clientSCK.setErrorState(false);
                             } else {
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
                             }
                         } else if (selectedCard == 2) {
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 2");
                             try {
                                 clientSCK.setErrorState(false);
                                 clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), playerDeck[1], new Coordinates(col, row), orientationCard2);
                             } catch (RemoteException | NotBoundException ignored) {
                             }
                             if (clientSCK.getErrorState()) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                                 clientSCK.setErrorState(false);
                             } else {
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
                             }
                         } else if (selectedCard == 3) {
                             cardPlaced = true;
                             System.out.println("PLAYED CARD 3");
                             try {
                                 clientSCK.setErrorState(false);
                                 clientSCK.playCard(clientSCK.getPersonalPlayer().getNickname(), playerDeck[2], new Coordinates(col, row), orientationCard3);
                             } catch (RemoteException | NotBoundException ignored) {
                             }
                             if (clientSCK.getErrorState()) {
                                 System.out.println("YOU DO NOT HAVE ENOUGH RESOURCES TO PLAY THIS CARD HERE");
                                 updateLabel(selectedCardLabel, "You don't have enough resources!");
                                 cardPlaced = false;
                                 clientSCK.setErrorState(false);
                             } else {
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
                             }
                         }
                     }
                     if (cardPlaced) {
                         Image card1 = new Image(getClass().getResourceAsStream(path));
                         ImageView imageView = new ImageView(card1);
                         imageView.setFitWidth(100.0);  // larghezza desiderata
                         imageView.setFitHeight(68.25); // altezza desiderata
                         imageView.setPreserveRatio(true);
                         imageView.setSmooth(true);
                         Insets insets = new Insets(-8.7375, -12.5, -8.7375, -12.5); // Regola i margini come desiderato (top, left, bottom, right)
                         GridPane.setMargin(imageView, insets);
                         grid.add(imageView, col, 81 - row);
                         showP1Board(); // RICARICO LA BOARD
                         //se è l'ultimo turno diciamo al giocatore che non può pescare una nuova carta
                         if (lastRound) {
                             selectedCardLabel.setText("This was your last round: YOU CAN'T DRAW ANY CARDS");
                         }
                     }

                 }
             } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                 // Gestisci eventuali eccezioni durante la conversione delle coordinate
                 e.printStackTrace();
             }
         }
    }


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

    public void updateRound(boolean lastRound) {
        Platform.runLater(()->{
                setTurnLabel(lastRound);
        });
    }

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
                    finishedRD = true;
                }
            }else if(network==2){
                try {
                    path = "/images/cards/back/  (" + clientSCK.getResourceDeck().checkFirstCard().getId() + ").png";
                    Image newCard = new Image(getClass().getResourceAsStream(path));
                    resourceDeck.setImage(newCard);
                } catch (EmptyStackException e) {
                    resourceDeck.setImage(null);
                    finishedRD = true;
                }
            }
        });
    }

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
                    //finishedGD = true;
                }
            }else if(network==2){
                try {
                    path = "/images/cards/back/  (" + clientSCK.getGoldDeck().checkFirstCard().getId() + ").png";
                    Image newCard = new Image(getClass().getResourceAsStream(path));
                    goldDeck.setImage(newCard);
                } catch (EmptyStackException e) {
                    goldDeck.setImage(null);
                    //finishedGD = true;
                }
            }
        });
    }

    public void updatePlayerDeck(String nickname,PlayableCard[] playerDeck) {
        Platform.runLater(() -> {
            String path=null;
            Image newCard=null;
            int i=1;
            for (Player p : playersInOrder) {
                if(i==2){ //non serve l'update del giocatore a cui appartiene la view
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



    public void changeScene(List<Player> winners) {
        // let's show the new window: winners and losers
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/winners.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GUIWinnersController ctr = null;
        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }
        stage.setOnCloseRequest(event -> this.leaveGame());
        // setting the parameters in the new controller
        ctr.setStage(stage);
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
                try {
                    rmiClient.handleDisconnectionFunction();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                ;
            }
            else if(network==2){
                try {
                    clientSCK.handleDisconnectionFunction();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        delay.play();
    }



    public void updateWinners(List<Player> winners) {
        if (winners.size() == 1) { //un solo vincitore
            System.out.println(winners.get(0).getNickname() + " WON!!!");
            //platform.runLater
        } else if (winners.size() > 1) { //pareggio
            for (Player p : winners) {
                System.out.print(p.getNickname() + ", ");
            }
            System.out.println("tied!");
        }
        Platform.runLater(() -> changeScene(winners));
    }



    public void musicONOFF() {
        if (playingMusic) {
            mediaPlayer.stop();
            playingMusic = false;
        }else{
            mediaPlayer.play();
            playingMusic = true;
        }
    }



    public void setMediaPlayer(MediaPlayer mp) {
        this.mediaPlayer = mp;
    }

    public void setObjectiveCardselected(ObjectiveCard objectiveCardselected) {
        this.objectiveCardselected = objectiveCardselected;
    }
}
