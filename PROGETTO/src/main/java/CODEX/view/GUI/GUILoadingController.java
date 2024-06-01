package CODEX.view.GUI;

import javafx.fxml.FXML;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUILoadingController {

    private int network; // it means that user hasn't chosen (1 = rmi  2 = sck)
    private Stage stage;
    private RMIClient rmiClient=null;
    private ClientSCK clientSCK=null;




    public void startClicked () throws IOException {
        System.out.println("sono in startClicked()");

        stage.close();
        stage = new Stage();

        Image icon = new Image(getClass().getResourceAsStream("/images/others/Codex_Icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Codex Naturalis");
        System.out.println("sto per fare fxmlloader di nickname... arriverà un messaggio di conferma quando ho finito");
        FXMLLoader fxmlLoader = new FXMLLoader(GUILoadingController.class.getResource("/nickname.fxml"));
        Parent root = fxmlLoader.load();
        System.out.println("ho finito fxmlloader di nickname");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        GUINicknameController controller = fxmlLoader.getController();
        controller.setNetwork(network);
        controller.setStage(stage);
        stage.show();

        if(clientSCK!=null){
            controller.setClientSCK(clientSCK);
        }else if (rmiClient!=null){
            controller.setRmiClient(rmiClient);
        }
        stage.show();
    }




    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public void setClientSCK(ClientSCK clientSCK){
        this.clientSCK=clientSCK;
    }

    public void setRmiClient(RMIClient rmiClient){
        this.rmiClient=rmiClient;
    }

}
