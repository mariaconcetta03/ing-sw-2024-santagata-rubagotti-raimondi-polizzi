package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Board;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;

/**
 * This event is useful to communicate that the board of one player has
 * been modified
 */
public class updateBoardEvent implements Event {
    private String boardOwner;
    private Board board;
    private PlayableCard newCard;
    public updateBoardEvent(String boardOwner, Board board, PlayableCard newCard){
        this.boardOwner=boardOwner;
        this.board= board;
        this.newCard = newCard;
    }

    @Override
    public boolean execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateBoard(boardOwner, board, newCard);
        return false;
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateBoard(boardOwner, board, newCard);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    public boolean executeSCKServerSide() { // returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
