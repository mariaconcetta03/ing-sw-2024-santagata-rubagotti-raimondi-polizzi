package org.server;


public class Player {

    private enum Pawn {
        RED,
        BLUE,
        GREEN,
        YELLOW
    }

    private enum PlayerState{
        IS_PLAYING,
        IS_WAITING,
        IS_DISCONNECTED
    }

    private String nickname;
    private int points;
    private int playOrder;
    private int [] playerDeck;
    private boolean isFirst;
    private Pawn color;
    private Objective_card personalObjective;
    private PlayerState state;
    private Game game;

    public Player (boolean isFirst){
        this.isFIrst = isFirst;
    }

    //SETTERS
    public void setColor(Pawn color, int playOrder){ //Playorder starts from 1!!
        this.color = color;
        this.playOrder = playOrder;
    }

    public void setNickname(String nickname, boolean isAvailable){ // check if nickname isAvailable for Codex class and then set nickname
        if(isAvailable) {
            this.nickname = nickname;
        }
    }

    //GETTER
    public boolean isFirst(){
        return this.isFirst;
    }

    public String getNickname(){
         return this.nickname;
    }

    public Pawn getColor(){
        return this.Color;
    }

    public int getPoints(){
        return this.points;
    }

    public int getPlayOrder(){
        return playOrder;
    }

    //OTHER METHODS
    public void drawCard(int cardId){

        Deck resourceDeck = game.getResourceDeck(); //deck
        Deck goldDeck = game.getGoldDeck(); //deck
        int resourceCard1 = game.getResourceCard1(); //market
        int resourceCard2 = game.getResourceCard2(); //market
        int goldCard1 = game.getGoldCard1(); //market
        int goldCard2 = game.getGoldCard2(); //market

        if( cardId == goldCard1 ){
            //setter di gold
            cardId
            //remove from gold deck???

        } else if ( cardId == goldCard2 ) {
            //setter di gold
            goldCard2 = goldDeck;
            //remove from gold deck???
        } else if ( cardId == resourceCard1 ) {
            //setter di gold
            resourceCard1 = resourceDeck.getFirstCard();
            //remove from gold deck???
        } else if ( cardId == resourceCard2 ) {
            //setter di gold
            resourceCard2 = resourceDeck.getFirstCard();
            //remove from gold deck???
        }
        // where id card == 0, the new card is placed
        for( int i = 0; i < 3; i++ ){
            if( playerDeck[i] == 0){
                playerDeck[i] = cardId;
            }
        }



    }

    public int playCard(int cardId, Coordinates position){

        for(int i = 0; i < 3; i++){
            if( playerDeck[i] == cardId ){
                playerDeck[i] = 0;      // value 0 in playerDeck, as no id will be = 0
            }
        }

        return cardId;
    }

}
