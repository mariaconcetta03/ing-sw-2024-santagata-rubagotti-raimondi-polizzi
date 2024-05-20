package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

// THIS IS THE CORRECT CLASS
public class InterfaceGUI extends Application {

    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private int network = 0; // 1 = RMI   2 = TCP
    private GUIController guiController = new GUIController();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nickname.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Codex Naturalis");
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.show();
    }




    public static void main(String[] args) {
        launch((String) null);
      //  if (args[0].equals("TCP")) {
     //
     //   }
    }





    public InterfaceGUI(RMIClient client) {
        this.rmiClient = client;
        this.guiController.setInterfaceGUI(this);
    }

    public InterfaceGUI() {
        this.guiController.setInterfaceGUI(this);
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