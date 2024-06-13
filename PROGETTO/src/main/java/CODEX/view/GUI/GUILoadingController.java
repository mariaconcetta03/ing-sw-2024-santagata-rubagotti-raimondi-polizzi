package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 * This class represents the controller of the scene when choosing the nickname
 */
public class GUILoadingController {

    private int network; // 1 = rmi  2 = sck
    private Stage stage;
    private RMIClient rmiClient = null;
    private ClientSCK clientSCK = null;



    /**
     * This method sets the scene for choosing nickname
     * @throws IOException
     */
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
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        stage.setWidth(primaryScreenBounds.getWidth() * 0.8);
        stage.setHeight(primaryScreenBounds.getHeight() * 0.8);


        stage.setResizable(true);
        GUINicknameController controller = fxmlLoader.getController();
        controller.setNetwork(network);
        stage.setOnCloseRequest(event -> {
            if (network == 1) {

                    rmiClient.handleDisconnectionFunction();

            } else if (network == 2) {

                    clientSCK.handleDisconnectionFunction();

            }
        });

        if(clientSCK!=null){
            controller.setClientSCK(clientSCK);
        } else if (rmiClient!=null){
            controller.setRmiClient(rmiClient);
        }

        controller.setNicknameOnKeyPressed();
        controller.setStage(stage);
        stage.setScene(scene);
        stage.show();
    }



    /**
     * Setter method
     * @param stage of the scene which will be changed here
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }



    /**
     * Setter method
     * @param network which is the type of client of the player
     */
    public void setNetwork(int network) {
        this.network = network;
    }



    /**
     * Setter method
     * @param clientSCK which is the client of the player
     */
    public void setClientSCK(ClientSCK clientSCK){
        this.clientSCK=clientSCK;
    }



    /**
     * Setter method
     * @param rmiClient which is the client of the player
     */
    public void setRmiClient(RMIClient rmiClient){
        this.rmiClient=rmiClient;
    }

}
