package CODEX.view.GUI;
import javafx.fxml.FXML;
import java.awt.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GUIController {
    @FXML
    private Label welcomeText;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    protected void onHelloButtonClick() {
        this.welcomeText.setText("Welcome to JavaFX Application!");
    }
}
