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
    RMIClient rmiClient;
    private Stage stage;

    {
        try {
            rmiClient = new RMIClient();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        rmiClient.setSelectedView(1);
    }

    ClientSCK clientSCK;
    {
        try {
            clientSCK = new ClientSCK();
            clientSCK.setSelectedView(2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNetwork(int network) {
        this.network = network;
    }



    public void startClicked () throws IOException {
        stage.close();
        stage = new Stage();
        Image icon = new Image(getClass().getResourceAsStream("/images/others/Codex_Icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Codex Naturalis");
        stage.centerOnScreen();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nickname.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        GUINicknameController controller = fxmlLoader.getController();
        controller.setNetwork(network);
        controller.setStage(stage);
        stage.show();
    }
}
