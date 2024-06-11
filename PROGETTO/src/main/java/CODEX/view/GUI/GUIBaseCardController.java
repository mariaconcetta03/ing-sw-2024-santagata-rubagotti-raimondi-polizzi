package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Coordinates;
import javafx.animation.PauseTransition;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.Label;
import javafx.util.Duration;

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
    private Object disconnectionLock=new Object();

    private Stage stage;
    private boolean baseCardPlayed = false;
    private GUIObjectiveController ctr;
    private int network = 0; //1 = rmi  2 = sck


    public void setBaseCard1(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard1.setImage(image);
    }

    public void setBaseCard2(int cardID) {
        String path;
        path = "/images/cards/back/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard2.setImage(image);
    }

    public synchronized void selectedFront() {
        baseCard1.disabledProperty();
        baseCard2.disabledProperty();
// third thread to change the scene always in JAVA FX thread
        Platform.runLater(() -> {
            stateLabel.setText("Front side selected! Now wait for everyone to choose.");
            System.out.println("selezionato fronte");
            baseCardPlayed = true;
            //changeScene();
         /* PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
            pause.setOnFinished(event -> changeScene());
            pause.play();*/
        });

        // second general thread (executed after the first one)
        if (network == 1 && !baseCardPlayed) {
            try {
                rmiClient.playBaseCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[0], true);
                rmiClient.getGameController().checkBaseCardPlayed();
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2 && !baseCardPlayed) {
            try {
                clientSCK.playBaseCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[0], true);
                clientSCK.checkBaseCardPlayed(); //to be implemented
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }


        /*PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
        pause.setOnFinished(event -> {
            changeScene();
        });
        pause.play();*/

        // Platform.runLater(()->{changeScene();});

    }


    public synchronized void selectedBack() {
        baseCard1.disabledProperty();
        baseCard2.disabledProperty();
    // third thread to change the scene always in JAVA FX thread
        if (!baseCardPlayed) {
            baseCardPlayed = true;

            Platform.runLater(() -> {
                stateLabel.setText("Back side selected! Now wait for everyone to choose.");
                System.out.println("Selezionato retro");
                //changeScene();
              /* PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
                pause.setOnFinished(event -> changeScene());
                pause.play(); */
            });

            // second general thread (executed after the first one)

            if (network == 1) {
                try {
                    rmiClient.playBaseCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPlayerDeck()[0], false);
                    rmiClient.getGameController().checkBaseCardPlayed();
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (network == 2) {
                try {
                    clientSCK.playBaseCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPlayerDeck()[0], false);
                    clientSCK.checkBaseCardPlayed(); //to be implemented
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }

           /*  PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
            pause.setOnFinished(event -> {
                changeScene();
            });
            pause.play();*/

//        Platform.runLater(()->{changeScene();});
        }
    }



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
        // setting the parameters in the new controller, also the BASE CARD (front and back)
        ctr.setScheduler(scheduler);
        ctr.setDisconnectionLock(disconnectionLock);
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);

        if (network == 1) {
            /*
            Object guiLock=rmiClient.getGuiLock();
            synchronized (guiLock) {
                while (rmiClient.getPersonalPlayer().getPersonalObjectives().size() < 2) { //arriva l'update delle due objective card tra cui scegliere dopo l'update del player deck
                    try {
                        guiLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

             */
            ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", now choose your");
            ctr.setCard1(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setCard2(rmiClient.getPersonalPlayer().getPlayerDeck()[1].getId());
            ctr.setCard3(rmiClient.getPersonalPlayer().getPlayerDeck()[2].getId());
            ctr.setObjCard1(rmiClient.getPersonalPlayer().getPersonalObjectives().get(0).getId()); // 1 --> 0
            ctr.setObjCard2(rmiClient.getPersonalPlayer().getPersonalObjectives().get(1).getId()); // 2 --> 1
            ctr.setBaseCard(rmiClient.getPersonalPlayer().getBoard().getTable()[rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2][rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2].getId(), rmiClient.getPersonalPlayer().getBoard().getTable()[rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2][rmiClient.getPersonalPlayer().getBoard().getBoardDimensions()/2].getOrientation());
            // (0,0) because our base card is always in the center of the table!

        } else if (network == 2) {
            /*
            Object guiLock=clientSCK.getGuiLock();
            synchronized (guiLock) {
                if(!clientSCK.getADisconnectionHappened()) {
                    while (clientSCK.getPersonalPlayer().getPersonalObjectives().size() < 2) { //arriva l'update delle due objective card tra cui scegliere dopo l'update del player deck

                        try {
                            guiLock.wait(); //la notify viene messa sia nell'evento atteso sia nell' handleDisconnection in SCK
                            if(clientSCK.getADisconnectionHappened()){
                                break; //usciamo dal ciclo while
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }

             */
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

            // old dimensions and position
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


            stage.setScene(scene); //viene già qui mostrata la scena : nel caso in in cui arrivi prima un evento di disconnessione questa scena non verrà mai mostrata

            // setting the od values of position and dimension
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);


            stage.show();
        }
    }


    public void checkDisconnection() {  //da migliorare: blocca la scelta della carta
        /*

        synchronized (clientSCK.getDisconnectionLock()) {
            if(!clientSCK.getADisconnectionHappened()) {
                while (!clientSCK.getADisconnectionHappened()) {
                    try {
                        clientSCK.getDisconnectionLock().wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        System.out.println("rilevata disconnessione");
        //changeScene():
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/handleDisconnection.fxml"));
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

        PauseTransition pause = new PauseTransition(Duration.seconds(9)); // 2 secondi di ritardo
        pause.setOnFinished(event -> stageClose());
        pause.play();

         */
    }

    public void startPeriodicDisconnectionCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (disconnectionLock) {
                if ( ((network==1)&&(rmiClient.getADisconnectionHappened())) || ((network==2)&&(clientSCK.getADisconnectionHappened())) ){
                    Platform.runLater(() -> {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/handleDisconnection.fxml"));
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

                        PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 3 secondi di ritardo
                        pause.setOnFinished(event -> stageClose());
                        pause.play();
                    });
                    scheduler.shutdown(); // Stop the scheduler if the condition is met
                }
            }
        }, 0, 1, TimeUnit.SECONDS); // Check every second
    }



    private void stageClose(){
        stage.close();
        if(network==1){
            try {
                rmiClient.handleDisconnectionFunction();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        if(network==2) {
            try {
                clientSCK.handleDisconnectionFunction();
            } catch (RemoteException ignored) {

            }
        }
    }


    public void updateGameState() {
        Platform.runLater(this::changeScene);
    }
}
