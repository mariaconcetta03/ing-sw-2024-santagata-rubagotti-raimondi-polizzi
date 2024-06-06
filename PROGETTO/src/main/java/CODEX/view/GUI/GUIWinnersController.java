package CODEX.view.GUI;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;


public class GUIWinnersController {



    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    private int network;
    List<Player> winners;
    @FXML
    private Label winnerLabel;
    @FXML
    private Label titleLabel;


    public void setWinnerLabel(){
        if (network == 1) {
            try {
                winners = rmiClient.getGameController().getGame().winner();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        } else if (network == 2) {
            //winners = clientSCK.
        }
        if(true){
            winnerLabel.setText("Y O U  W O N");
        }else{

            winnerLabel.setText("The winner is :" );

        }
    }

    public void setTitleLabel(){

    }



    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }

    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK = clientSCK;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNetwork(int network) {
        this.network = network;
    }
}
