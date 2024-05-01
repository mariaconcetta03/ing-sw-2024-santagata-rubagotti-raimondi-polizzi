package distributed.RMI;

import org.model.Board;
import org.model.Player;
import utils.Observer;

public class WrappedObserver implements Observer {

    //attributo che è il ClientRMI Remoto
    public void updateBoard(Board board){
        //clientRMI.updateBoard(Board board);
    }
    public void updateResourceDeck(){}
    public void updateGoldDeck(){}
    public void updatePlayerDeck(Player player){}
    public void updateResourceCard1(){}
    public void updateResourceCard2(){}
    public void updateGoldCard2(){}
    public void updateGoldCard1(){}
    public void updateChat(int chatID){}
    public void updatePawns(){}
    public void updateNickname(){}
    public void updateRound(){}

    //copia incolla da pdf:
    /*
    private final RMIClient remoteClient;
    public WrappedObserver(RMIClient ro) {
        remoteClient = ro;
    }
    public void update(Observable o, Object arg) {
        try {
            remoteClient.remoteUpdate(o.toString(), arg);    //da vedere come differenziare gli update
        } catch (RemoteException e) {    //è quando cade la connessione?
            System.err.println(
                    "Remote exception; removing observer: " + this
            );
            o.deleteObserver(this);
        }
    }

*/




} //avremo come listeners in observable una lista di clientHandler(thread) per TCP e wrappedObserver per RMI
