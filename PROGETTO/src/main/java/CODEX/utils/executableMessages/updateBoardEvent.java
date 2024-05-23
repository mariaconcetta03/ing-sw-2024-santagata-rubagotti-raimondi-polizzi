package CODEX.utils.executableMessages;

import CODEX.distributed.ClientGeneralInterface;
import CODEX.org.model.Board;

import java.rmi.RemoteException;

public class updateBoardEvent implements Event{
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
}
