package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.Label;
import javafx.util.Duration;


/**
 * This class represents the controller of the scene when choosing the base card
 */
public class GUIBaseCardController {

    @FXML
    private Label labelWithPlayerName;
    @FXML
    private ImageView baseCard1;
    @FXML
    private ImageView baseCard2;
    @FXML
    private Label stateLabel;

    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Object disconnectionLock = new Object();
    private Stage stage;
    private boolean baseCardPlayed = false;
    private GUIObjectiveController ctr;
    private int network = 0; //1 = rmi  2 = sck



    /**
     * This method sets the image of the left base card
     * @param cardID of the left card showned on the screen
     */
    public void setBaseCard1(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard1.setImage(image);
    }



    /**
     * This method sets the image of the right base card
     * @param cardID of the right card showned on the screen
     */
    public void setBaseCard2(int cardID) {
        String path;
        path = "/images/cards/back/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard2.setImage(image);
    }



    /**
     * This method selects the front of the base card
     */
    public synchronized void selectedFront() {
        baseCard1.disabledProperty();
        baseCard2.disabledProperty();
        if (!baseCardPlayed) {
            baseCardPlayed = true;
            Platform.runLater(() -> {
                stateLabel.setText("Front side selected! Now wait for everyone to choose.");
                System.out.println("selezionato fronte");

            });

            if (network == 1) {


                        rmiClient.playBaseCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[0], true);

                try {
                    rmiClient.getGameController().checkBaseCardPlayed();
                } catch (RemoteException e) {
                   rmiClient.setADisconnectionHappened(true);
                }

            } else if (network == 2) {

                    clientSCK.playBaseCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[0], true);
                    clientSCK.checkBaseCardPlayed();

            }
        }
    }



    /**
     * This method selects the back of the base card
     */
    public synchronized void selectedBack() {
        baseCard1.disabledProperty();
        baseCard2.disabledProperty();

        if (!baseCardPlayed) {
            baseCardPlayed = true;

            Platform.runLater(() -> {
                stateLabel.setText("Back side selected! Now wait for everyone to choose.");
                System.out.println("Selezionato retro");
            });

            if (network == 1) {


                    rmiClient.playBaseCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[0], false);

                try {
                    rmiClient.getGameController().checkBaseCardPlayed();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            } else if (network == 2) {

                    clientSCK.playBaseCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[0], false);
                    clientSCK.checkBaseCardPlayed();

            }
        }
    }



    /**
     * This method will change the scene to the next one, where the player has to choose the objective cards
     */
    public void changeScene() {
        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objective.fxml"));
        Parent root = null;

        try {
            root = fxmlLoader.load();
        } catch (IOException ignored) {
        }

        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }

        stage.setOnCloseRequest(event -> {
            if (network == 1) {

                    rmiClient.handleDisconnectionFunction();

            } else if (network == 2) {

                    clientSCK.handleDisconnectionFunction();

            }
        });

        // setting the parameters and the base card in the new controller
        ctr.setScheduler(scheduler);
        ctr.setDisconnectionLock(disconnectionLock);
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);

        if (network == 1) {

            ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", now choose your");
            ctr.setCard1(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setCard2(rmiClient.getPersonalPlayer().getPlayerDeck()[1].getId());
            ctr.setCard3(rmiClient.getPersonalPlayer().getPlayerDeck()[2].getId());
            ctr.setObjCard1(rmiClient.getPersonalPlayer().getPersonalObjectives().get(0).getId()); // 1 --> 0
            ctr.setObjCard2(rmiClient.getPersonalPlayer().getPersonalObjectives().get(1).getId()); // 2 --> 1
            ctr.setBaseCard(rmiClient.getPersonalPlayer().getBoard().getTable()[rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2][rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2].getId(), rmiClient.getPersonalPlayer().getBoard().getTable()[rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2][rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2].getOrientation());
            // the base card is always in the center of the table!

        } else if (network == 2) {

            if(!clientSCK.getADisconnectionHappened()) {
                ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", now choose your");
                ctr.setCard1(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId());
                ctr.setCard2(clientSCK.getPersonalPlayer().getPlayerDeck()[1].getId());
                ctr.setCard3(clientSCK.getPersonalPlayer().getPlayerDeck()[2].getId());
                ctr.setObjCard1(clientSCK.getPersonalPlayer().getPersonalObjectives().get(0).getId());
                ctr.setObjCard2(clientSCK.getPersonalPlayer().getPersonalObjectives().get(1).getId());
                ctr.setBaseCard(clientSCK.getPersonalPlayer().getBoard().getTable()[clientSCK.getPersonalPlayer().getBoard().getBoardDimensions() / 2][clientSCK.getPersonalPlayer().getBoard().getBoardDimensions() / 2].getId(), clientSCK.getPersonalPlayer().getBoard().getTable()[clientSCK.getPersonalPlayer().getBoard().getBoardDimensions() / 2][clientSCK.getPersonalPlayer().getBoard().getBoardDimensions() / 2].getOrientation());
            }
        }

        if ((network == 1)||(network == 2&&!clientSCK.getADisconnectionHappened())) {

            // setting old dimensions and position
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();

            // new scene
            Scene scene;
            scene = new Scene(root);
            if(network==1){
                synchronized (rmiClient.getGuiObjectiveControllerLock()) {
                    rmiClient.setGuiObjectiveController(ctr);
                    rmiClient.getGuiObjectiveControllerLock().notify();

                }
            }if(network==2){
                synchronized (clientSCK.getGuiObjectiveControllerLock()) {
                    clientSCK.setGuiObjectiveController(ctr);
                    clientSCK.getGuiObjectiveControllerLock().notify();

                }
            }


            stage.setScene(scene);

            // setting the od values of position and dimension
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);


            stage.show();
        }
    }



    /**
     * This method checks if there is a disconnection
     */
    public void startPeriodicDisconnectionCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (disconnectionLock) {
                if ( ((network==1) && (rmiClient.getADisconnectionHappened())) || ((network==2) && (clientSCK.getADisconnectionHappened())) ){
                    Platform.runLater(() -> {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/handleDisconnection.fxml"));
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

                        // setting the old values of position and dimension
                        stage.setWidth(width);
                        stage.setHeight(height);
                        stage.setX(x);
                        stage.setY(y);

                        PauseTransition pause = new PauseTransition(Duration.seconds(3));
                        pause.setOnFinished(event -> stageClose());
                        pause.play();
                    });
                    scheduler.shutdown(); // Stop the scheduler if there's the disconnection
                }
            }
        }, 0, 1, TimeUnit.SECONDS); // Check every second
    }



    /**
     * This method closes the scene when there is a disconnection
     */
    private void stageClose(){
        stage.close();
        if(network==1){

                rmiClient.handleDisconnectionFunction();

        }
        if(network==2) {

                clientSCK.handleDisconnectionFunction();

        }
    }



    /**
     * This method updates the state of the game
     */
    public void updateGameState() {
        Platform.runLater(this::changeScene);
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
     * @param client which is the client of the player
     */
    void setRmiClient(RMIClient client) {
        this.rmiClient = client;
    }



    /**
     * Setter method
     * @param client which is the client of the player
     */
    void setClientSCK(ClientSCK client) {
        this.clientSCK = client;
    }



    /**
     * Setter method
     * @param network which is the type of client of the player
     */
    void setNetwork(int network) {
        this.network = network;
    }



    /**
     * Setter method
     * @param text which is the name of the player
     */
    @FXML
    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }

}
