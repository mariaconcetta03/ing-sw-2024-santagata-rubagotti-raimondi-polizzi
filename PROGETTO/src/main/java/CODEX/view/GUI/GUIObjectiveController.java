package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import java.io.IOException;

public class GUIObjectiveController {
    RMIClient rmiClient;
    ClientSCK clientSCK;
    Stage stage;

    @FXML
    Label labelWithPlayerName;
    @FXML
    private ImageView objCard1;
    @FXML
    private ImageView objCard2;
    @FXML
    private ImageView card1;

    private boolean orientationCard1 = true;
    private int card1ID;
    @FXML
    private ImageView card2;
    private boolean orientationCard2 = true;
    private int card2ID;

    @FXML
    private ImageView card3;
    private boolean orientationCard3 = true;
    private int card3ID;

    @FXML
    private ImageView baseCard;



    @FXML
    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }


    public void changeOrientationCard1(){
        if(orientationCard1) {
            String path;
            path = "/images/cards/back/ (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            objCard1.setImage(image);
        }else{
            String path;
            path = "/images/cards/front/ (" + card1ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            objCard1.setImage(image);
        }
    }


    public void changeOrientationCard2(){
        if(orientationCard2) {
            String path;
            path = "/images/cards/back/ (" + card2ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            objCard1.setImage(image);
        }else{
            String path;
            path = "/images/cards/front/ (" + card2ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            objCard1.setImage(image);
        }
    }

    public void changeOrientationCard3(){
        if(orientationCard3) {
            String path;
            path = "/images/cards/back/ (" + card3ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            objCard1.setImage(image);
        }else{
            String path;
            path = "/images/cards/front/ (" + card3ID + ").png";
            Image image = new Image(getClass().getResourceAsStream(path));
            objCard1.setImage(image);
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






    int network = 0; //1 = rmi  2 = sck

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    void setRmiClient(RMIClient client) {
        this.rmiClient = client;
    }


    void setClientSCK (ClientSCK client) {
        this.clientSCK = client;
    }

    void setNetwork (int network) {
        this.network = network;
    }




}
