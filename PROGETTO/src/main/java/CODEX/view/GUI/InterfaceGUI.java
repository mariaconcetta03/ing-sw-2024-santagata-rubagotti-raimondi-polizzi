package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static javafx.application.Application.launch;

// THIS IS THE CORRECT CLASS
public class InterfaceGUI extends Application {
    private static int network ; // 1 = RMI   2 = TCP


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
        mainStage.show();
        // GUINicknameController controller = fxmlLoader.getController(); // obtaining the GUI controller
//        while (controller == null) {
//            System.out.println("controller NULLO");
//        }

        GUILoadingController controller = fxmlLoader.getController();
        controller.setNetwork(network); // setting the network RMI or TCP in the GUI Nickname controller
        controller.setStage(mainStage);
    }




    public static void main(String[] args) {
        if (args[0].equals("RMI")) {
            network = 1;
            System.out.println("Hai scelto RMI");
        } else if (args[0].equals("TCP")) {
            network = 2;
            System.out.println("Hai scelto TCP");
        } else {
            System.out.println("NON E NESSUN CASO");
        }
        Application.launch((String) null); // COSA FA LAUNCH?? BISOGNA CAPIRLO
    }



//public InterfaceGUI (int network) {
  //      this.network = network;
//}

public InterfaceGUI () {

}


}





/*      E S E M P I O   D E L   P R O F


package it.polimi.javafxdemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.Console;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HelloApplication extends Application {
    final Timer bgTimer = new Timer();
    TimerTask crabSpin = null;

    Button button;
    Label label;
    ImageView crab;

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("nickname.fxml"));
        VBox vbox = new VBox();

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        label = new Label("Example label");
        button = new Button("An example button");

        top.getChildren().addAll(label, button);

        AnchorPane bottom = new AnchorPane();
        Image img = new Image(HelloApplication.class.getResource("crab.png").openStream());
        crab = new ImageView(img);
        crab.setFitHeight(128);
        crab.setFitWidth(128);
        crab.setSmooth(true);
        crab.setPreserveRatio(true);

        Label crabText = new Label("Crab:");

        bottom.getChildren().addAll(crab, crabText);
        bottom.setMaxSize(1024,1024);

        AnchorPane.setTopAnchor(crab, 100d);
        AnchorPane.setRightAnchor(crab, 100d);
        AnchorPane.setBottomAnchor(crabText, 30d);
        AnchorPane.setLeftAnchor(crabText, 30d);


        vbox.setAlignment(Pos.BASELINE_CENTER);
        TextField text = new TextField();
        vbox.getChildren().addAll(top, bottom, text);
        vbox.setStyle("-fx-font: 36 ClearSansMedium;");

        label.textProperty().bind(text.textProperty());

        Scene scene = new Scene(vbox, 800, 800);

        crab.visibleProperty().bind(button.armedProperty().not());

        this.setupHandlers();
        this.startSim();

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    private void startSim() {
        new Thread(() -> {
            Random rng = new Random();
            while (true) {
                try {
                    Thread.sleep(rng.nextInt(100, 5000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (crab != null) {
                    Platform.runLater(() -> {
                        crab.setTranslateX(rng.nextDouble(-100d, 100d));
                        crab.setTranslateY(rng.nextDouble(-100d, 100d));
                    });
                }
            }
        }).start();
    }

    private void setupHandlers() {
        button.setOnAction(e -> {
            System.out.println(e);
            label.setText(label.getText() + ".");
        });

        crab.setOnMouseEntered(e -> {
            crabSpin = new TimerTask() {
                @Override
                public void run() {
                    crab.setRotate(crab.getRotate() + 1.0);
                }
            };
            bgTimer.schedule(crabSpin, 0, 10);
        });
        crab.setOnMouseExited(e -> {
            crabSpin.cancel();
        });
        crab.setOnMouseClicked(e -> crab.setRotate(0));
    }

    public static void main(String[] args) {
        launch();
    }
}
 */