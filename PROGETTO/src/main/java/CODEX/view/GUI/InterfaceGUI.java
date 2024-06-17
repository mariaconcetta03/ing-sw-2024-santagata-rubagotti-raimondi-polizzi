package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is the main class of the GUI, which launches the main application of JavaFX
 */
public class InterfaceGUI extends Application {
    private static int network ; // 1 = RMI   2 = TCP
    private static ClientSCK clientSCK;
    private static RMIClient rmiClient;



    /**
     * This method starts the first window on the screen
     * @param mainStage stage of the window
     * @throws IOException if the FXML loader returns an error
     */
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
        Scene scene = new Scene(fxmlLoader.load(), 700, 450);
        mainStage.setScene(scene);

        GUILoadingController controller = fxmlLoader.getController();
        controller.setNetwork(network); // setting the network RMI or TCP in the GUI Nickname controller
        controller.setClientSCK(clientSCK);
        controller.setRmiClient(rmiClient);
        controller.setStage(mainStage);

        mainStage.show();

    }



    /**
     * Main method which starts the application
     * @param args
     * @param clientSCK
     * @param rmiClient
     */
    public static void main(String[] args, ClientSCK clientSCK, RMIClient rmiClient) {
        if (args[0].equals("RMI")) {
            InterfaceGUI.network = 1;
            System.out.println("You have chosen RMI connection");
        } else if (args[0].equals("TCP")) {
            InterfaceGUI.network = 2;
            System.out.println("You have chosen TCP connection");
        } else {
            System.out.println("FATAL ERROR");
        }

        InterfaceGUI.clientSCK=clientSCK;
        InterfaceGUI.rmiClient=rmiClient;
        Application.launch((String) null);
    }

}
