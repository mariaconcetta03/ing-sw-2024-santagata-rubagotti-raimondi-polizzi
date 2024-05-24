package CODEX.utils.executableMessages.events;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.Board;

import java.rmi.RemoteException;

public class updateBoardEvent implements Event {
    private String boardOwner;
    private Board board;
    public updateBoardEvent(String boardOwner, Board board){
        this.boardOwner=boardOwner;
        this.board= board;
    }
    @Override
    public void execute(ClientGeneralInterface client) throws RemoteException {
        client.updateBoard(boardOwner, board);
    }
    @Override
    public void executeSCK(ClientGeneralInterface client) {
        try {
            client.updateBoard(boardOwner, board);
        } catch (RemoteException ignored) { //è il modo migliore di gestire la cosa?
        }
    }

    @Override
    public boolean executeSCKServerSide() { //returns true when we are considering updateGameState and the new state is 'STARTED'
        return false;

    }
}
