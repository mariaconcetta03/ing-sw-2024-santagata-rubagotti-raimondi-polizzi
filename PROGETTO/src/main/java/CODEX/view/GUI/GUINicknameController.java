package CODEX.view.GUI;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import javafx.fxml.FXML;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GUINicknameController {

    private int network = 0; // it means that user hasn't chosen (1 = rmi  2 = sck)
    RMIClient rmiClient = new RMIClient();
    {
        rmiClient.setSelectedView(1);
    }
    ClientSCK clientSCK;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;



    boolean correctNickname = false;


    {
        try {
            clientSCK = new ClientSCK();
            clientSCK.setSelectedView(2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private TextField nickname;

    @FXML
    private Label nicknameUsed;


    public GUINicknameController() throws RemoteException {
    }


//    public static void showNicknameScene() {
//        FXMLLoader fmxlLoader = new FXMLLoader(GUINicknameController.class.getResource("/nickname.fxml"));
//        Parent root;
//        try {
//            root = fmxlLoader.load();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Scene nicknameScene = new Scene(root);
//        stage.setScene(nicknameScene);
//        stage.show();
//    }


    public void setNetwork(int network) {
        this.network = network;
    }


    @FXML
    protected void sendNickname() throws IOException {

        System.out.println(nickname.getCharacters());
        System.out.println("prec NET" + network);

        if (network == 1) { //RMI
            correctNickname = rmiClient.setNickname(nickname.getCharacters().toString());
        }else if (network == 2){ //SCK
            clientSCK.setErrorState (false);
            try {
                clientSCK.chooseNickname(nickname.getCharacters().toString());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            }
            correctNickname = clientSCK.setNickname(nickname.getCharacters().toString());
        }

        if (!correctNickname) {
            nicknameUsed.setOpacity(1); // shows the message error
        } else {
            nicknameUsed.setOpacity(0); // not necessary because we will change our window!

            // let's show the new window!
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lobby.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            GUILobbyController ctr = fxmlLoader.getController();
            ctr.setStage(stage);

            // old dimensions and position
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();

            // new scene
            stage.setScene(scene);

            // setting the od values of position and dimension
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);

            // setting the dynamic parameters of the new window
            System.out.println("NET" + network);

            if (network == 1) {
                ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", now join a lobby");
                ctr.setAvailableLobbies(rmiClient.getAvailableLobbies());
                ctr.setRmiClient(rmiClient);
                ctr.setNetwork(1);
            } else if (network == 2) {
                clientSCK.checkAvailableLobby();
                ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", now join a lobby");
                ctr.setAvailableLobbies(clientSCK.getAvailableLobbies().stream().toList());
                ctr.setClientSCK(clientSCK);
                ctr.setNetwork(2);
            }
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


