package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIObjectiveController {
    RMIClient rmiClient;
    ClientSCK clientSCK;
    Stage stage;

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
