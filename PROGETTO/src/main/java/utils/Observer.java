package utils;
import org.model.*;
import distributed.Server;


//this object represents a player to put into the object Observable with maybe other player to define a group of player that can access a single instance of Observable
//example of use: a group of players takes part to a single chat
//another example: the player can see the other player decisions ('Observable') during the game session


public interface Observer {

//    ---------------------------- QUESTI SONO PARAMETRI CHE VERRANNO MESSI NELLA VIEW -------------------------------
//    Board b1, b2, b3, b4; // the boards of the 4 players
//    PlayableDeck resourceDeck, goldDeck; // the two decks on the table
//    PlayableDeck p1Deck, p2Deck, p3Deck, p4Deck; // the decks of every player
//    PlayableCard resourceCard1, resourceCard2, goldCard1, goldCard2; // the cards on the market
//    int points1, points2, points3, points4; // the points of each player in the game
//    ----------------------------------------------------------------------------------------------------------------



    void updateBoard(Board board, Player player);
    void updateResourceDeck(PlayableDeck resourceDeck);
    void updateGoldDeck(PlayableDeck goldDeck);
    void updatePlayerDeck(Player player, PlayableCard[] playerDeck);
    void updateResourceCard1();
    void updateResourceCard2();
    void updateGoldCard2();
    void updateGoldCard1();
    void updateChat(Chat chat);
    void updatePawns(Player player, Pawn pawn);
    void updateNickname(Player player, String nickname);
    void updateRound();
}
