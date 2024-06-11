package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.ObjectiveCard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class controls the window during the objective scene
 */
public class GUIObjectiveController {

    @FXML
    private Label labelWithPlayerName;
    @FXML
    private ImageView objCard1; // LEFT
    @FXML
    private ImageView objCard2; // RIGHT
    @FXML
    private ImageView card1;
    @FXML
    private ImageView card2;
    @FXML
    private ImageView card3;
    @FXML
    private ImageView baseCard;
    @FXML
    private Label selectionLabel;
    // 1. "Press the objective you prefer!"
    // 2. "Right objective selected. Now wait for everyone to choose."
    // 3. "Left objective selected. Now wait for everyone to choose."

    private ScheduledExecutorService scheduler;
    private Object disconnectionLock;
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    private boolean orientationCard1 = true;
    private int card1ID;
    private boolean orientationCard2 = true;
    private int card2ID;
    private boolean orientationCard3 = true;
    private int card3ID;
    private int network = 0; //1 = rmi  2 = sck
    private boolean objectiveSelected = false;
    private ObjectiveCard objectiveCardselected=null;



    /**
     * This method is invoked when the player chooses the right objective
     */
    public synchronized void selectedRightObjective () {
        objCard1.disabledProperty();
        objCard2.disabledProperty();
        if (!objectiveSelected) {
            new Thread(() -> {
                if (network == 1) {

                    try {
                        rmiClient.chooseObjectiveCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPersonalObjectives().get(1));
                        rmiClient.getGameController().checkObjectiveCardChosen();
                        objectiveCardselected = rmiClient.getPersonalPlayer().getPersonalObjectives().get(1);
                    } catch (RemoteException | NotBoundException alreadyCaught) {}
                } else if (network == 2) {
                    try {
                        clientSCK.chooseObjectiveCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPersonalObjectives().get(1));
                    } catch (Exception ignored) {}
                    if(!clientSCK.getErrorState()){
                        clientSCK.checkObjectiveCardChosen();
                        objectiveCardselected = clientSCK.getPersonalPlayer().getPersonalObjectives().get(1);
                    }
                }

                Platform.runLater(() -> {
                    System.out.println("sto per chiamare il selectionLabel");
                    selectionLabel.setText("Right objective selected. Now wait for everyone to choose.");
                    System.out.println("selezionato destra");
                    objectiveSelected = true;
                });
            }).start();
        }
    }



    /**
     * This method is invoked when the player chooses the left objective
     */
    public synchronized void selectedLeftObjective () {
        objCard1.disabledProperty();
        objCard2.disabledProperty();
        if (!objectiveSelected) {
            objectiveSelected = true;
            new Thread(() -> {
                if (network == 1) {
                    try {
                        rmiClient.chooseObjectiveCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPersonalObjectives().get(0));
                        rmiClient.getGameController().checkObjectiveCardChosen();
                        objectiveCardselected = rmiClient.getPersonalPlayer().getPersonalObjectives().get(0); // objectiveCard 1 --> get(0)
                    } catch (RemoteException | NotBoundException alreadyCaught) {}
                } else if (network == 2) {
                    try {
                        clientSCK.chooseObjectiveCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPersonalObjectives().get(0));
                    } catch (Exception ignored) {}
                    if(!clientSCK.getErrorState()){
                        clientSCK.checkObjectiveCardChosen();
                        objectiveCardselected = clientSCK.getPersonalPlayer().getPersonalObjectives().get(0);
                    }
                }

                // thread to change the scene always in JAVA FX thread
                Platform.runLater(() -> {
                    selectionLabel.setText("Left objective selected. Now wait for everyone to choose.");
                    System.out.println("selezionato sinistra");
                });
            }).start();
        }
    }



    /**
     * This method changes the scene to the next one
     * @throws RemoteException
     */
    public void changeScene() {
        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/game.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GUIGameController ctr = null;
        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }

        // setting the parameters in the new controller
        ctr.setDisconnectionLock(disconnectionLock);
        ctr.setScheduler(scheduler);
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);

        if (network == 1) {
            System.out.println("il player ha scelto obiettivo con ID: " + rmiClient.getPersonalPlayer().getPersonalObjective());

        } else if (network == 2) {
            System.out.println("il player ha scelto obiettivo con ID: " + clientSCK.getPersonalPlayer().getPersonalObjective());

        }
        if ((network == 1)||(network == 2 && !clientSCK.getADisconnectionHappened())) {
            ctr.setObjectiveCardselected(objectiveCardselected);
            ctr.setAllFeatures();

            // old dimensions and position
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();

            // new scene
            Scene scene;
            scene = new Scene(root);

            ctr.setAllFeatures();

            GUIGameController finalCtr = ctr;
            stage.setOnCloseRequest(event -> finalCtr.leaveGame());
            stage.setScene(scene);

            // setting the old values of position and dimension
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);

            stage.show();

            // setting the background music
            String musicPath = getClass().getResource("/gameMusic.mp3").toString();
            Media sound = new Media(musicPath);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
            ctr.setMediaPlayer(mediaPlayer);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });
        }
    }



    /**
     * This method changes the orientation of the first card of the player deck
     */
    public synchronized void changeOrientationCard1(){
        if(orientationCard1) {
            String path;
            path = "/images/cards/back/  (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card1.setImage(image);
            orientationCard1 = false;
        } else {
            String path;
            path = "/images/cards/front/  (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card1.setImage(image);
            orientationCard1 = true;
        }
    }



    /**
     * This method changes the orientation of the second card of the player deck
     */
    public synchronized void changeOrientationCard2(){
        if(orientationCard2) {
            String path;
            path = "/images/cards/back/  (" + card2ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card2.setImage(image);
            orientationCard2 = false;
        }else{
            String path;
            path = "/images/cards/front/  (" + card2ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card2.setImage(image);
            orientationCard2 = true;
        }
    }




    /**
     * This method changes the orientation of the third card of the player deck
     */
    public synchronized void changeOrientationCard3(){
        if(orientationCard3) {
            String path;
            path = "/images/cards/back/  (" + card3ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card3.setImage(image);
            orientationCard3 = false;
        }else{
            String path;
            path = "/images/cards/front/  (" + card3ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card3.setImage(image);
            orientationCard3 = true;
        }
    }



    /**
     * This method updates the game state
     */
    public void updateGameState() {
        Platform.runLater(()->{
            changeScene();
        });
    }



    /**
     * This method sets the nickname of the player
     * @param text
     */
    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }



    /**
     * Setter method
     * @param cardID which has to be shown to the player
     */
    public void setObjCard1(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        objCard1.setImage(image);
    }



    /**
     * Setter method
     * @param cardID which has to be shown to the player
     */
    public void setObjCard2(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        objCard2.setImage(image);
    }



    /**
     * Setter method
     * @param cardID which belongs to the player's deck (first card)
     */
    public void setCard1(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card1.setImage(image);
        card1ID = cardID;
    }



    /**
     * Setter method
     * @param cardID which belongs to the player's deck (second card)
     */
    public void setCard2(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card2.setImage(image);
        card2ID = cardID;
    }



    /**
     * Setter method
     * @param cardID which belongs to the player's deck (third card)
     */
    public void setCard3(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card3.setImage(image);
        card3ID = cardID;
    }



    /**
     * Setter method
     * @param cardID which the player chose (base card)
     */
    public void setBaseCard(int cardID, boolean orientation) {
        String path;
        if (orientation) {
            path = "/images/cards/front/  (" + cardID + ").png";
        } else {
            path = "/images/cards/back/  (" + cardID + ").png";
        }
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard.setImage(image);
    }



    /**
     * Setter method
     * @param stage here will be shown the next scenes
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }



    /**
     * Setter method
     * @param client which is the client of the player
     */
    public void setRmiClient(RMIClient client) {
        this.rmiClient = client;
    }



    /**
     * Setter method
     * @param client which is the client of the player
     */
    public void setClientSCK (ClientSCK client) {
        this.clientSCK = client;
    }



    /**
     * Setter method
     * @param network rmi or tcp (1 or 2)
     */
    public void setNetwork (int network) {
        this.network = network;
    }



    /**
     * Setter method
     * @param scheduler scheduler
     */
    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }



    /**
     * Setter method
     * @param disconnectionLock lock
     */
    public void setDisconnectionLock(Object disconnectionLock) {
        this.disconnectionLock = disconnectionLock;
    }

}
