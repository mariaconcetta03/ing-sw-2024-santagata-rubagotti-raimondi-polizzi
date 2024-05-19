package CODEX.view.GUI;
import javafx.fxml.FXML;
import java.awt.*;


public class GUIController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
