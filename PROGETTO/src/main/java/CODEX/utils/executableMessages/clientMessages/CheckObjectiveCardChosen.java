package CODEX.utils.executableMessages.clientMessages;

import CODEX.distributed.Socket.ClientHandlerThread;


/**
 * This class is used to represent a message which is sent when checking the objective cards chosen by the players
 */
public class CheckObjectiveCardChosen implements ClientMessage {

    /**
     * Default constructor
     */
    public CheckObjectiveCardChosen() {
    }


    /**
     * This method checks if all the players in the game have chosen their personal objective
     *
     * @param clientHandlerThread thread
     */
    @Override
    public void execute(ClientHandlerThread clientHandlerThread) {

        clientHandlerThread.getGameController().checkObjectiveCardChosen();

    }
}
