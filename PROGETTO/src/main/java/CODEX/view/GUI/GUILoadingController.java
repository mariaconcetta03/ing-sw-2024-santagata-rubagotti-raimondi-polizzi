package CODEX.view.GUI;

import javafx.fxml.FXML;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.fxml.FXMLLoader;
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

    @FXML
    Button startButton;


    private int network = 0; // it means that user hasn't chosen (1 = rmi  2 = sck)
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;





    public void startClicked () throws IOException {

        // CREATING THE CORRECT CLIENT
        if (network == 1) {
            try {
                rmiClient = new RMIClient();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            rmiClient.setSelectedView(2);
        } else if (network == 2) {
            try {
                clientSCK = new ClientSCK();
                clientSCK.setSelectedView(2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // CLOSING THE OLD WINDOW
        stage.close();

        // SETTING AND OPENING THE NEW WINDOW
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
        GUINicknameController controller = fxmlLoader.getController();
        controller.setNetwork(network);
        controller.setStage(stage);
        controller.setClientSCK(clientSCK);
        controller.setRmiClient(rmiClient);
        stage.show();
    }





    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setNetwork(int network) {
        this.network = network;
    }


}
