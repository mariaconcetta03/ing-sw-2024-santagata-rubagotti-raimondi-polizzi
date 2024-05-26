package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.image.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class GUIBaseCardController {
    RMIClient rmiClient;
    ClientSCK clientSCK;
    Stage stage;

    @FXML
    Label labelWithPlayerName;

    @FXML
    ImageView baseCard1;

    public void setBaseCard1(int cardID) {
        String path;
        path = "/images/cards/front/" + cardID + ".png";
        Image image = new Image(getClass().getResourceAsStream(path));
        baseCard1.setImage(image);
    }


    GUIObjectiveController ctr;

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

    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }




    public void changeScene(){
        // let's show the new window!
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/objectiveCard.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // old dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // new scene
        stage.setScene(scene);

        // setting the od values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);

        // setting the parameters in the new controller
        ctr = fxmlLoader.getController();
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);
        stage.show();
    }

}
