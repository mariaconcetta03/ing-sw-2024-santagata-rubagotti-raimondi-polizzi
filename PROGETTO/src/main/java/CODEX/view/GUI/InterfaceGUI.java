package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static javafx.application.Application.launch;

// THIS IS THE CORRECT CLASS
public class InterfaceGUI extends Application {
    private static int network ; // 1 = RMI   2 = TCP
    private static ClientSCK clientSCK;
    private static RMIClient rmiClient;



    @Override
    public void start(Stage mainStage) throws IOException {
        Image icon = new Image(getClass().getResourceAsStream("/images/others/Codex_Icon.png"));
        mainStage.getIcons().add(icon);
        mainStage.setTitle("Codex Naturalis");
        mainStage.centerOnScreen();
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setResizable(false);
        mainStage.setAlwaysOnTop(true);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/loading.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setScene(scene);

        GUILoadingController controller = fxmlLoader.getController();
        controller.setNetwork(network); // setting the network RMI or TCP in the GUI Nickname controller
        controller.setClientSCK(clientSCK);
        controller.setRmiClient(rmiClient);
        controller.setStage(mainStage);

        mainStage.show();

    }




    public static void main(String[] args, ClientSCK clientSCK, RMIClient rmiClient) {
        if (args[0].equals("RMI")) {
            InterfaceGUI.network = 1;
            System.out.println("Hai scelto RMI");
        } else if (args[0].equals("TCP")) {
            InterfaceGUI.network = 2;
            System.out.println("Hai scelto TCP");
        } else {
            System.out.println("NON E NESSUN CASO");
        }

        InterfaceGUI.clientSCK=clientSCK;
        InterfaceGUI.rmiClient=rmiClient;
        Application.launch((String) null); // COSA FA LAUNCH?? BISOGNA CAPIRLO
    }


}
