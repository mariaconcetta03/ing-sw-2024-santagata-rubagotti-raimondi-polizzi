package CODEX.view.GUI;
import javafx.fxml.FXML;
import java.awt.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;

public class GUIController {
    @FXML
    private MenuButton menuButton;
    @FXML
    private AnchorPane anchorPane;
//    @FXML
//    protected void onHelloButtonClick() {
//        this.welcomeText.setText("Welcome to JavaFX Application!");
//    }
    @FXML
    protected void rmiGui() {
        this.menuButton.setText("RMI + GUI");
    }

    @FXML
    protected void rmiTui() {
        this.menuButton.setText("RMI + TUI");
    }

    @FXML
    protected void tcpGui() {
        this.menuButton.setText("TCP + GUI");
    }

    @FXML
    protected void tcpTui() {
        this.menuButton.setText("TCP + TUI");
    }
}
