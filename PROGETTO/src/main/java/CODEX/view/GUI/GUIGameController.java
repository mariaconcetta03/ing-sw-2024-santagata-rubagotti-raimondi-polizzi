package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.ObjectiveCard;
import CODEX.org.model.PlayableCard;
import CODEX.org.model.PlayableDeck;
import javafx.stage.Stage;

public class GUIGameController {

    private PlayableDeck goldDeck;
    private PlayableDeck resourceDeck;
    private ObjectiveCard commonObj1;
    private ObjectiveCard commonObj2;
    private PlayableCard resourceCard1;
    private PlayableCard resourceCard2;
    private PlayableCard goldCard1;
    private PlayableCard goldCard2;

    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    private int network;




    public void setStage(Stage stage) {
        this.stage=stage;
    }

    public void setNetwork(int network) {
        this.network=network;
    }

    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK=clientSCK;
    }

    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }

    public void setAllFeatures() {
        // SETTING THE PLAYER DECKS
        // SETTING THE CURRENT PLAYER (is the first in the list)
        // SETTING THE PAWNS

    }
}
