package distributed.RMI;

import distributed.messages.Message;
import org.model.*;
import org.model.Game.GameState;
import utils.Observable;
import utils.*;

public class WrappedObserver implements Observer {
    private RMIClient remoteClient;
    private String nickname;

    // ----------------- C O M E   A V V I E N E   L' U P D A T E ? --------------------
    // OBSERVABLE SI ACCORGE DEL CAMBIAMENTO (CHE PASSA DALLA VIEW, AL CONTROLLER, AL
    // MODEL, CHE è OBSERVABLE). A QUESTO PUNTO, INVOCA LE FUNZIONI DI UPDATE SUI VARI
    // OBSERVERS. QUESTI IN RMI, GRAZIE ALLA CLASSE WRAPPED OBSERVER, VANNO AD INVOCARE
    // DEI METODI DI CLIENTRMI CHE RICEVONO L'OGGETTO AGGIORNATO
    // ---------------------------------------------------------------------------------


    public WrappedObserver(RMIClient ro) {
        remoteClient = ro;
    }

    @Override
    public void update(Observable obs, Message arg) {
        switch(arg.getMessageEvent()){
            case UPDATED_BOARD: remoteClient.updateBoard((Board)(arg.getObj()));
                break;
            case UPDATED_RESOURCE_DECK: remoteClient.updateResourceDeck((PlayableDeck)(arg.getObj()));
                break;
            case UPDATED_GOLD_DECK: remoteClient.updateGoldDeck((PlayableDeck)(arg.getObj()));
                break;
            case UPDATED_PLAYER_DECK: remoteClient.updatePlayerDeck((Player)(arg.getObj().get(0)), (PlayableCard[])(arg.getObj().get(1)));
                break;
            case UPDATED_RESOURCE_CARD_1: remoteClient.updateResourceCard1((PlayableCard)(arg.getObj()));
                break;
            case UPDATED_RESOURCE_CARD_2: remoteClient.updateResourceCard2((PlayableCard)(arg.getObj()));
                break;
            case UPDATED_GOLD_CARD_1: remoteClient.updateGoldCard1((PlayableCard)(arg.getObj()));
                break;
            case UPDATED_GOLD_CARD_2: remoteClient.updateGoldCard2((PlayableCard)(arg.getObj()));
                break;
            case UPDATED_CHAT: remoteClient.updateChat((Chat) (arg.getObj()));
                break;
            case UPDATED_PAWNS: remoteClient.updatePawns((Player) (arg.getObj().get(0)), (Pawn)(arg.getObj().get(1)));
                break;
            case UPDATED_NICKNAME: remoteClient.updateNickname((Player) (arg.getObj().get(0)), (arg.getObj().get(1)).toString());
                break;
            case UPDATED_ROUND: remoteClient.updateRound((Player) (arg.getObj()));
                break;
            case GAME_STATE_CHANGED: remoteClient.updateGameState((Game)(arg.getObj()));
                break;
            case SETUP_PHASE_1, SETUP_PHASE_2: remoteClient.updatePlayerDeck((Player)(arg.getObj().get(0)), (PlayableCard[])(arg.getObj().get(1)));
                break;
                // SETUP PHASE 1 E 2: potremmo forse evitare di metterli e fare l'update del mazzo appena viene pescata la carta?
            default: throw new IllegalStateException("Unexpected message event: " + arg.getMessageEvent().toString());
        }
    }

    @Override
    public void setNickname(String nickname) {
       this.nickname = nickname;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    // DA IMPLEMENTARE

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
