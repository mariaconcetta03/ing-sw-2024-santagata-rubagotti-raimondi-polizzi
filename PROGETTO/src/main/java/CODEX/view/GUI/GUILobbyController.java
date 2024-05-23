package CODEX.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GUILobbyController {

    @FXML
    Label labelWithPlayerName;

    public void setLabelWithPlayerName(String text) {
        this.labelWithPlayerName.setText(text);
    }
}
