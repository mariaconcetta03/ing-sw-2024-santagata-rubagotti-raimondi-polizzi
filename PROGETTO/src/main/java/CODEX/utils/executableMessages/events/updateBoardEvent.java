package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.distributed.RMI.WrappedObserver;
import CODEX.org.model.Board;
import CODEX.org.model.PlayableCard;

import java.rmi.RemoteException;

public class updateBoardEvent implements Event { //viene chiamato in playBaseCard per la prima volta
    private String boardOwner;
    private Board board;
    private PlayableCard newCard;
    public updateBoardEvent(String boardOwner, Board board, PlayableCard newCard){
        this.boardOwner=boardOwner;
        this.board= board;
        this.newCard = newCard;
    }

    @Override
    public void execute(ClientGeneralInterface client, WrappedObserver wrappedObserver) throws RemoteException {
        client.updateBoard(boardOwner, board, newCard);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateBoard(boardOwner, board, newCard);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
