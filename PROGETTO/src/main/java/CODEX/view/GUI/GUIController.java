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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GUIController {



    @FXML
    private MenuButton menuButton;


    private int network = 0; // it means that user hasn't chosen
    RMIClient rmiClient = new RMIClient();
    ClientSCK clientSCK;

    {
        try {
            clientSCK = new ClientSCK();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private TextField nickname;

    @FXML
    private Label nicknameUsed;

    public GUIController() throws RemoteException {
    }



    public void setNetwork(int network) {
        this.network = network;
    }



    @FXML
    protected void sendNickname() {

        boolean correctNickname = false;
        System.out.println(nickname.getCharacters());

        if (network == 1) { //RMI
            correctNickname = rmiClient.setNickname(nickname.getCharacters().toString());
        }else{ //SCK
            correctNickname = clientSCK.setNickname(nickname.getCharacters().toString());
        }

        if (!correctNickname) {
            nicknameUsed.setOpacity(1);
        } else {
            nicknameUsed.setOpacity(0);
        }
    }





    // RIVEDERE FUNZIONAMENTO DI JAVA FX!!!
    // IL MAIN DEVE PROSEGUIRE, E DOPO DI ESSO IN RMI CLIENT NON VA FATTO ESEGUIRE NIENTE!
    // I CONTROLLI SUL NICKNAME VALIDO OPPURE NO SI DEVONO FARE DIRETTAMENTE DA INTERFACCIA GRAFICA
    // (possibilità di avere 2 interfacce una per rmi e una per sck, oppure ho 2 attributi, vedo se sono rmi o se
    // sono socket). COSI DOPO QUESTA COSA GESTISCE TUTTO L'INTERFACCIA GRAFICA.
    // MAIN RITORNA STATIC










        // QUI DEVO FAR CAMBIARE LA SCHERMATA!
    }


