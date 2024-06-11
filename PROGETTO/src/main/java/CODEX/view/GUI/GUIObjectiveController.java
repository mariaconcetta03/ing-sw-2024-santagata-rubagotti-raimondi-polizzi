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



    // ------------ C O M E    F U N Z I O N A    U P D A T E R O U N D ? ----------------
    // QUANDO SCELGO LA CARTA ALLORA FACCIO CHOOSEOBJCARD
    // PARTONO GLI UPDATE (quando tutti hanno sceltop la carta obiettivo allora inizia il gioco effettivo)
    // arriva update di finishedsetupphase (nella tui chiama updateround), se nella gui serve altro allora aggiuntare roba senza
    // modificare robe della tui (selectedview = 1).
    // questo va a chiamare update rpound nella terza volta, e questo è quello che dice chi sta giocando e chi no e la partita
    // è iniziata. ora si puo iniziare a giocare


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
                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                } else if (network == 2) {
                    try {
                        clientSCK.chooseObjectiveCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPersonalObjectives().get(1));

                    } catch (Exception ignored) {
                    }
                    if(!clientSCK.getErrorState()){
                        clientSCK.checkObjectiveCardChosen();
                        objectiveCardselected = clientSCK.getPersonalPlayer().getPersonalObjectives().get(1);

                    }

                }


                // third thread to change the scene always in JAVA FX thread
                Platform.runLater(() -> {
                    System.out.println("sto per chiamare il selectionLabel");
                    selectionLabel.setText("Right objective selected. Now wait for everyone to choose.");
                    System.out.println("selezionato destra");
                    objectiveSelected = true;
                    //changeScene();
                /*
                // Aggiungi un ritardo prima di cambiare scena
                PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    try {
                        changeScene();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                pause.play();

                 */
                });
            }).start();
        }
    }


    public synchronized void selectedLeftObjective () { // objectiveCard 1 --> get(0)
        objCard1.disabledProperty();
        objCard2.disabledProperty();
        if (!objectiveSelected) {
            objectiveSelected = true;
            new Thread(() -> {
                if (network == 1) {
                    try {
                        rmiClient.chooseObjectiveCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPersonalObjectives().get(0));
                        rmiClient.getGameController().checkObjectiveCardChosen();
                        objectiveCardselected = rmiClient.getPersonalPlayer().getPersonalObjectives().get(0);
                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                } else if (network == 2) {
                    try {
                        clientSCK.chooseObjectiveCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPersonalObjectives().get(0));

                    } catch (Exception ignored) {
                    }
                    if(!clientSCK.getErrorState()){
                        clientSCK.checkObjectiveCardChosen(); //to be implemented
                        objectiveCardselected = clientSCK.getPersonalPlayer().getPersonalObjectives().get(0);

                    }
                }


                // third thread to change the scene always in JAVA FX thread
                Platform.runLater(() -> {
                    selectionLabel.setText("Left objective selected. Now wait for everyone to choose.");
                    System.out.println("selezionato sinistra");
                    //changeScene();

                    /*
                    // Aggiungi un ritardo prima di cambiare scena
                    PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
                    pause.setOnFinished(event -> {
                        try {
                            changeScene();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    pause.play();

                     */
                });
            }).start();
        }
    }


    public void changeScene() throws RemoteException {
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
            /*
            Object guiLock = rmiClient.getGuiLock();
            synchronized (guiLock) {
                boolean finishedSetup=rmiClient.getFinishedSetup(); //se qui è già true non ho bisogno di entrare nel while che fa la wait
                while (!finishedSetup) { //finchè non sono stati ricevuti tutti gli update affinchè il gioco possa iniziare
                    try {
                        guiLock.wait();
                        finishedSetup=rmiClient.getFinishedSetup();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

             */
        } else if (network == 2) {
            System.out.println("il player ha scelto obiettivo con ID: " + clientSCK.getPersonalPlayer().getPersonalObjective());
            /*
            Object guiLock = clientSCK.getGuiLock();
            synchronized (guiLock) {
                boolean finishedSetup=clientSCK.getFinishedSetup(); //se qui è già true non ho bisogno di entrare nel while che fa la wait
                if(!clientSCK.getADisconnectionHappened()) {
                    while (!finishedSetup) { //finchè non sono stati ricevuti tutti gli update affinchè il gioco possa iniziare
                        try {
                            guiLock.wait();
                            finishedSetup = clientSCK.getFinishedSetup();
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

            try {
                ctr.setAllFeatures();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            //passo al ClientSCK il ctr
            //      if(network==2){ //tcp
            //        clientSCK.setGuiGameController(ctr);
            //  }

            GUIGameController finalCtr = ctr;
            stage.setOnCloseRequest(event -> finalCtr.leaveGame());
            stage.setScene(scene); //viene già qui mostrata la scena : nel caso in in cui arrivi prima un evento di disconnessione questa scena non verrà mai mostrata

            // setting the od values of position and dimension
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);


            stage.show();
            String musicPath = getClass().getResource("/gameMusic.mp3").toString();
            Media sound = new Media(musicPath);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
            ctr.setMediaPlayer(mediaPlayer);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO); // Riavvia dall'inizio
                mediaPlayer.play(); // Riproduci di nuovo
            });
        }
    }



    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }


    public synchronized void changeOrientationCard1(){
        if(orientationCard1) {
            String path;
            path = "/images/cards/back/  (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card1.setImage(image);
            orientationCard1 = false;
        }else{
            String path;
            path = "/images/cards/front/  (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card1.setImage(image);
            orientationCard1 = true;
        }
    }


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


    public void setObjCard1(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        objCard1.setImage(image);
    }


    public void setObjCard2(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        objCard2.setImage(image);
    }


    public void setCard1(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card1.setImage(image);
        card1ID = cardID;
    }


    public void setCard2(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card2.setImage(image);
        card2ID = cardID;
    }


    public void setCard3(int cardID) {
        String path;
        path = "/images/cards/front/  (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card3.setImage(image);
        card3ID = cardID;
    }


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



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRmiClient(RMIClient client) {
        this.rmiClient = client;
    }

    public void setClientSCK (ClientSCK client) {
        this.clientSCK = client;
    }

    public void setNetwork (int network) {
        this.network = network;
    }


    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public void setDisconnectionLock(Object disconnectionLock) {
        this.disconnectionLock = disconnectionLock;
    }

    public void updateGameState() {
        Platform.runLater(()->{
            try {
                changeScene();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
