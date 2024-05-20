package CODEX.view.GUI;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.view.TUI.InterfaceTUI;
import javafx.fxml.FXML;
import java.awt.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GUIController {
    @FXML
    private MenuButton menuButton;
    private int choose = 0; // it means that user hasn't chosen
    RMIClient rmiClient = null;
    ClientSCK clientSCK = null;

    @FXML
    protected void tcp() {
        this.menuButton.setText("TCP");
        choose = 1;
    }

    @FXML
    protected void rmi() {
        this.menuButton.setText("RMI");
        choose = 2;
    }



    @FXML
    protected void selectedOption () throws NotBoundException, IOException {
        if (choose == 1) { // tcp
            ClientSCK clientSCK = new ClientSCK();
            clientSCK.setSelectedView(2);

        } else if (choose == 2) { // rmi
            rmiClient = new RMIClient();
            rmiClient.setSelectedView(2);
            rmiClient.SRMIInterfaceFromRegistry();
            rmiClient.waitingRoom();
        }

        InterfaceGUI interfaceGUI= new InterfaceGUI();
        interfaceGUI.seconda(new Stage());
    }










}
