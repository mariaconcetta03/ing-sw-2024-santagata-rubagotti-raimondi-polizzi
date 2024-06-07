package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Pawn;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GUIPawnsController {  
    private GUIBaseCardController ctr;
    private Stage stage;
    private int network;
    private RMIClient rmiClient;
    private ClientSCK clientSCK;

    @FXML
    private Pane yellowPane;
    @FXML
    private Pane greenPane;
    @FXML
    private Pane bluePane;
    @FXML
    private Pane redPane;
    @FXML
    private Label labelWithPlayerName;
    @FXML
    private Label retryLabel;


    public void changeScene(){
        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/baseCard.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }

        // setting the parameters in the new controller, also the BASE CARD (front and back)
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);
        if (network == 1) {
            while(rmiClient.getPersonalPlayer().getPlayerDeck()[0] == null) {}
            ctr.setBaseCard1(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId()); // OK
            ctr.setBaseCard2(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", which side do you");
        } else if (network == 2) {
            while(clientSCK.getPersonalPlayer().getPlayerDeck()[0] == null) {}
            ctr.setBaseCard1(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId()); // OK
            ctr.setBaseCard2(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", which side do you");
        }

        // old dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // new scene
        Scene scene;
        scene = new Scene(root);

        stage.setScene(scene); //questo è il momento in cui la nuova scena viene mostrata

        // setting the od values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);


        ctr.startPeriodicDisconnectionCheck();


        //stage.show(); //si fa solo se cambia lo stage
    }


    public void setLabelWithPlayerName(String s) {
        this.labelWithPlayerName.setText(s);
    }


    public void selectedYellow(){
        if(network == 1){
            try {
                rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.YELLOW);
                changeScene();
            } catch (ColorAlreadyTakenException) {
                retryLabel.setOpacity(1);
                // RICEVERE GLI UPDATE QUI !!!!
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2) {
            try {
                clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.YELLOW);
                changeScene();
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            } catch (ColorAlreadyTakenException e) {
                retryLabel.setOpacity(1);
                //RICEVERE UPDATE QUI !!!!!
            }
        }
    }


    public void selectedBlue(){
        if(network == 1){
            try {
                rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.BLUE);
                changeScene();
            } catch (ColorAlreadyTakenException) {
                retryLabel.setOpacity(1);
                // RICEVERE GLI UPDATE QUI !!!!
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2) {
            try {
                clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.BLUE);
                changeScene();
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            } catch (ColorAlreadyTakenException e) {
                retryLabel.setOpacity(1);
                //RICEVERE UPDATE QUI !!!!!
            }
        }
    }


    public void selectedGreen(){
        if(network == 1){
            try {
                rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.GREEN);
                changeScene();
            } catch (ColorAlreadyTakenException) {
                retryLabel.setOpacity(1);
                // RICEVERE GLI UPDATE QUI !!!!
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2) {
            try {
                clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.GREEN);
                changeScene();
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            } catch (ColorAlreadyTakenException e) {
                retryLabel.setOpacity(1);
                //RICEVERE UPDATE QUI !!!!!
            }
        }
    }


    public void selectedRed(){
        if(network == 1){
            try {
                rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.RED);
                changeScene();
            } catch (ColorAlreadyTakenException) {
                retryLabel.setOpacity(1);
                // RICEVERE GLI UPDATE QUI !!!!
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2) {
            try {
                clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.RED);
                changeScene();
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            } catch (ColorAlreadyTakenException e) {
                retryLabel.setOpacity(1);
                //RICEVERE UPDATE QUI !!!!!
            }
        }
    }


    public void setColoredPanes() {
        retryLabel.setOpacity(0);
        yellowPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, null)));
        greenPane.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, null)));
        redPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, null)));
        bluePane.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, null)));
    }


    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }


    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK = clientSCK;
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setNetwork(int network) {
        this.network = network;
    }
}
