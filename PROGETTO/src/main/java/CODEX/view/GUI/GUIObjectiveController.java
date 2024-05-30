package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.animation.PauseTransition;
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

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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



    // ------------ C O M E    F U N Z I O N A    U P D A T E R O U N D ? ----------------
    // QUANDO SCELGO LA CARTA ALLORA FACCIO CHOOSEOBJCARD
    // PARTONO GLI UPDATE (quando tutti hanno sceltop la carta obiettivo allora inizia il gioco effettivo)
    // arriva update di finishedsetupphase (nella tui chiama updateround), se nella gui serve altro allora aggiuntare roba senza
    // modificare robe della tui (selectedview = 1).
    // questo va a chiamare update rpound nella terza volta, e questo è quello che dice chi sta giocando e chi no e la partita
    // è iniziata. ora si puo iniziare a giocare


    public void selectedRightObjective () {


        new Thread(() -> {
            if (network == 1 && !objectiveSelected) {

                try {
                    rmiClient.chooseObjectiveCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPersonalObjectives().get(1));
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }else if (network == 2 && !objectiveSelected) {
                try {
                    clientSCK.chooseObjectiveCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPersonalObjectives().get(1));
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }

            }


            // third thread to change the scene always in JAVA FX thread
            Platform.runLater(() -> {
                System.out.println("sto per chiamare il selectionLabel");
                selectionLabel.setText("Right objective selected. Now wait for everyone to choose.");
                System.out.println("selezionato destra");
                objectiveSelected = true;
                //changeScene();
                // Aggiungi un ritardo prima di cambiare scena
                PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
                pause.setOnFinished(event -> changeScene());
                pause.play();
              });
        }).start();
    }


    public void selectedLeftObjective () { // objectiveCard 1 --> get(0)

        new Thread(() -> {
            if (network == 1 && !objectiveSelected) {
                try {
                    rmiClient.chooseObjectiveCard(rmiClient.getPersonalPlayer().getNickname(), rmiClient.getPersonalPlayer().getPersonalObjectives().get(0));
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (network == 2 && !objectiveSelected) {
                try {
                    clientSCK.chooseObjectiveCard(clientSCK.getPersonalPlayer().getNickname(), clientSCK.getPersonalPlayer().getPersonalObjectives().get(0));
                } catch (RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }


            // third thread to change the scene always in JAVA FX thread
            Platform.runLater(() -> {
                selectionLabel.setText("Left objective selected. Now wait for everyone to choose.");
                System.out.println("selezionato sinistra");
                objectiveSelected = true;
                //changeScene();
                // Aggiungi un ritardo prima di cambiare scena
                PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 secondi di ritardo
                pause.setOnFinished(event -> changeScene());
                pause.play();
            });
        }).start();
    }


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
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);

        if (network == 1) {
            System.out.println("il player ha scelto obiettivo con ID: " + rmiClient.getPersonalPlayer().getPersonalObjective());
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
        } else if (network == 2) {
            System.out.println("il player ha scelto obiettivo con ID: " + clientSCK.getPersonalPlayer().getPersonalObjective());
            Object guiLock = clientSCK.getGuiLock();
            synchronized (guiLock) {
                boolean finishedSetup=clientSCK.getFinishedSetup(); //se qui è già true non ho bisogno di entrare nel while che fa la wait
                while (!finishedSetup) { //finchè non sono stati ricevuti tutti gli update affinchè il gioco possa iniziare
                    try {
                        guiLock.wait();
                        finishedSetup=clientSCK.getFinishedSetup();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        System.out.println("la wait è finita");

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
    }



    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }


    public void changeOrientationCard1(){
        if(orientationCard1) {
            String path;
            path = "/images/cards/back/ (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card1.setImage(image);
            orientationCard1 = false;
        }else{
            String path;
            path = "/images/cards/front/ (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card1.setImage(image);
            orientationCard1 = true;
        }
    }


    public void changeOrientationCard2(){
        if(orientationCard2) {
            String path;
            path = "/images/cards/back/ (" + card2ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card2.setImage(image);
            orientationCard2 = false;
        }else{
            String path;
            path = "/images/cards/front/ (" + card2ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card2.setImage(image);
            orientationCard2 = true;
        }
    }


    public void changeOrientationCard3(){
        if(orientationCard3) {
            String path;
            path = "/images/cards/back/ (" + card3ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card3.setImage(image);
            orientationCard3 = false;
        }else{
            String path;
            path = "/images/cards/front/ (" + card3ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            card3.setImage(image);
            orientationCard3 = true;
        }
    }


    public void setObjCard1(int cardID) {
        String path;
        path = "/images/cards/front/ (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        objCard1.setImage(image);
    }


    public void setObjCard2(int cardID) {
        String path;
        path = "/images/cards/front/ (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        objCard2.setImage(image);
    }


    public void setCard1(int cardID) {
        String path;
        path = "/images/cards/front/ (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card1.setImage(image);
        card1ID = cardID;
    }


    public void setCard2(int cardID) {
        String path;
        path = "/images/cards/front/ (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card2.setImage(image);
        card2ID = cardID;
    }


    public void setCard3(int cardID) {
        String path;
        path = "/images/cards/front/ (" + cardID + ").png";
        Image image = new Image(getClass().getResourceAsStream(path));
        card3.setImage(image);
        card3ID = cardID;
    }


    public void setBaseCard(int cardID, boolean orientation) {
        String path;
        if (orientation) {
            path = "/images/cards/front/ (" + cardID + ").png";
        } else {
            path = "/images/cards/back/ (" + cardID + ").png";
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


}
