package distributed.RMI;

import distributed.messages.Message;
import org.model.*;
import org.model.Game.GameState;
import utils.Observable;
import utils.*;

public class WrappedObserver implements Observer {
    RMIClient remoteClient;

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
            case UPDATED_GOLD_DECK: remoteClient.updateGoldDeck((PlayableDeck)(arg.getObj()));
                break;
            case UPDATED_PLAYER_DECK: remoteClient.updatePlayerDeck((Player) (arg.getObj().get(0)), (PlayableCard[])(arg.getObj().get(1)));
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
            // SETUP PHASE 1 E 2 CHE COSA SONO ? VANNO MESSI ?
            default: throw new IllegalStateException("Unexpected message event: " + arg.getMessageEvent().toString());
        }
    }

    @Override
    public void setNickname() {

    }

    @Override
    public String getNickname() {
        return null;
    }

    // DA IMPLEMENTARE
    public void updateBoard(Board board, Player player){

    }
    public void updateResourceDeck(PlayableDeck resourceDeck){}
    public void updateGoldDeck(PlayableDeck goldDeck){}
    public void updatePlayerDeck(Player player, PlayableCard[] playerDeck){}
    public void updateResourceCard1(){}
    public void updateResourceCard2(){}
    public void updateGoldCard2(){}
    public void updateGoldCard1(){}
    public void updateChat(Chat chat){}
    public void updatePawns(Player player, Pawn pawn){}
    public void updateNickname(Player player, String nickname){}
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
