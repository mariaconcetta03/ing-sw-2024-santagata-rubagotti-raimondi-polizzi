package CODEX.utils;
import CODEX.distributed.messages.Message;

import java.rmi.RemoteException;


//this object represents a player to put into the object Observable with maybe other player to define a group of player that can access a single instance of Observable
//example of use: a group of players takes part to a single chat
//another example: the player can see the other player decisions ('Observable') during the game session


public interface Observer {
    String nickname = null;

//    ---------------------------- QUESTI SONO PARAMETRI CHE VERRANNO MESSI NELLA VIEW -------------------------------
//    Board b1, b2, b3, b4; // the boards of the 4 players
//    PlayableDeck resourceDeck, goldDeck; // the two decks on the table
//    PlayableDeck p1Deck, p2Deck, p3Deck, p4Deck; // the decks of every player
//    PlayableCard resourceCard1, resourceCard2, goldCard1, goldCard2; // the cards on the market
//    int points1, points2, points3, points4; // the points of each player in the game
//    ----------------------------------------------------------------------------------------------------------------


    void update(Observable obs, Message arg) throws RemoteException;
    void setNickname(String nick) throws RemoteException;
    String getNickname() throws RemoteException;
}
