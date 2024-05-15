package distributed.RMI;

import distributed.messages.Message;
import org.model.*;
import org.model.Game.GameState;
import utils.Observable;
import utils.*;

import java.rmi.RemoteException;
import java.util.List;


/**
 * This class represents a CLIENT as an OBSERVER
 */
public class WrappedObserver implements Observer {
    private ClientRMIInterface remoteClient;
    private String nickname;


    // ----------------- C O M E   A V V I E N E   L' U P D A T E ? --------------------
    // OBSERVABLE SI ACCORGE DEL CAMBIAMENTO (CHE PASSA DALLA VIEW, AL CONTROLLER, AL
    // MODEL, CHE è OBSERVABLE). A QUESTO PUNTO, INVOCA LE FUNZIONI DI UPDATE SUI VARI
    // OBSERVERS. QUESTI IN RMI, GRAZIE ALLA CLASSE WRAPPED OBSERVER, VANNO AD INVOCARE
    // DEI METODI DI CLIENTRMI CHE RICEVONO L'OGGETTO AGGIORNATO
    // ---------------------------------------------------------------------------------



    /**
     * Class constructor
     * @param ro the RMIClient
     */
    public WrappedObserver(ClientRMIInterface ro) {
        remoteClient = ro;
    }



    /**
     * This is an update method
     * @param obs is the observable who called the notify
     * @param arg the message which contains the event
     */
    @Override
    public void update(Observable obs, Message arg) throws RemoteException {
        switch(arg.getMessageEvent()){
            case UPDATED_BOARD: remoteClient.updateBoard((Board)(arg.getObj()));
                break;
            case UPDATED_RESOURCE_DECK: remoteClient.updateResourceDeck((PlayableDeck)(arg.getObj()));
                break;
            case UPDATED_GOLD_DECK: remoteClient.updateGoldDeck((PlayableDeck)(arg.getObj()));
                break;
            case UPDATED_PLAYER_DECK: remoteClient.updatePlayerDeck((Player)(arg.getObj().get(0)), (PlayableCard[])(arg.getObj().get(1)));
                break;
            case UPDATED_RESOURCE_CARD_1: remoteClient.updateResourceCard1((PlayableCard)(arg.getObj()).get(0));
                break;
            case UPDATED_RESOURCE_CARD_2: remoteClient.updateResourceCard2((PlayableCard)(arg.getObj()).get(0));
                break;
            case UPDATED_GOLD_CARD_1: remoteClient.updateGoldCard1((PlayableCard)(arg.getObj()).get(0));
                break;
            case UPDATED_GOLD_CARD_2: remoteClient.updateGoldCard2((PlayableCard)(arg.getObj()).get(0));
                break;
            case UPDATED_CHAT: remoteClient.updateChat((Chat) (arg.getObj()));
                break;
            case UPDATED_PAWNS: remoteClient.updatePawns((Player) (arg.getObj().get(0)), (Pawn)(arg.getObj().get(1)));
                break;
            case UPDATED_NICKNAME: remoteClient.updateNickname((Player) (arg.getObj().get(0)), (arg.getObj().get(1)).toString());
                break;
            case UPDATED_ROUND: remoteClient.updateRound((List<Player>) (arg.getObj().get(0)));
                break;
            case UPDATED_COMMON_OBJECTIVES: remoteClient.updateCommonObjectives((ObjectiveCard)arg.getObj().get(0), (ObjectiveCard)arg.getObj().get(1));
                break;
            case UPDATED_PERSONAL_OBJECTIVE: remoteClient.updatePersonalObjective((ObjectiveCard) arg.getObj().get(0), (String) arg.getObj().get(1));
                break;
            case GAME_STATE_CHANGED: remoteClient.updateGameState((GameState) (arg.getObj().get(0)));
                break;
            case NEW_TURN: remoteClient.updateRound((List<Player>) arg.getObj().get(0));
                break;
            case UNABLE_TO_PLAY_CARD: remoteClient.showError(Event.UNABLE_TO_PLAY_CARD);
                break;
            //case SETUP_PHASE_1:
            //    break;

                    //SETUP_PHASE_2: remoteClient.updatePlayerDeck((Player)(arg.getObj().get(0)), (PlayableCard[])(arg.getObj().get(1)));
               // break;

                // SETUP PHASE 1 E 2: potremmo forse evitare di metterli e fare l'update del mazzo appena viene pescata la carta?
            //default: throw new IllegalStateException("Unexpected message event: " + arg.getMessageEvent().toString());
        }
    }



    /**
     * Setter method
     * @param nickname of the WrappedObserver
     */
    @Override
    public void setNickname(String nickname) throws RemoteException  {
       this.nickname = nickname;
    }



    /**
     * Getter method
     * @return nickname of the WrappedObserver
     */
    @Override
    public String getNickname() throws RemoteException {
        return this.nickname;
    }

}









// OLD COMMENTS
// avremo come listeners in observable una lista di clientHandler(thread) per TCP e wrappedObserver per RMI
// copia incolla da pdf:
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
