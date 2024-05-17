package view.TUI;

import org.model.*;
import utils.Observable;

import java.io.Serializable;
import java.util.*;

public class InterfaceTUI implements Serializable { //I don't think it has to extend 

       /*System.out.println("Welcome to "+ANSIFormatter.ANSI_GREEN+"C"+ANSIFormatter.ANSI_CYAN+"O"+ANSIFormatter.ANSI_RED+
            "D"+ANSIFormatter.ANSI_YELLOW+"E"+ANSIFormatter.ANSI_PURPLE+"X"+ANSIFormatter.ANSI_RESET+" NATURALIS");
*/

    /**
     * This method prints the welcome message to a new Player in the lobby
     */
    public void printWelcome(){
        System.out.println("\n\n\n\n\n\n\n\n\n"+ANSIFormatter.ANSI_RED+"Welcome to");
         System.out.println("\n" +
                ANSIFormatter.ANSI_GREEN+" ▄████████ "+ANSIFormatter.ANSI_CYAN+" ▄██████▄ "+ANSIFormatter.ANSI_RED+" ████████▄ "+ANSIFormatter.ANSI_YELLOW+"    ▄████████"+ANSIFormatter.ANSI_PURPLE+" ▀████    ▐████▀"+ANSIFormatter.ANSI_RESET+"      ███▄▄▄▄      ▄████████     ███     ███    █▄     ▄████████    ▄████████  ▄█        ▄█     ▄████████ \n" +
                ANSIFormatter.ANSI_GREEN+"███    ███ "+ANSIFormatter.ANSI_CYAN+"███    ███"+ANSIFormatter.ANSI_RED+" ███   ▀███"+ANSIFormatter.ANSI_YELLOW+"   ███    ███"+ANSIFormatter.ANSI_PURPLE+"   ███▌   ████▀ "+ANSIFormatter.ANSI_RESET+"      ███▀▀▀██▄   ███    ███ ▀█████████▄ ███    ███   ███    ███   ███    ███ ███       ███    ███    ███ \n" +
                ANSIFormatter.ANSI_GREEN+"███    █▀  "+ANSIFormatter.ANSI_CYAN+"███    ███"+ANSIFormatter.ANSI_RED+" ███    ███"+ANSIFormatter.ANSI_YELLOW+"   ███    █▀ "+ANSIFormatter.ANSI_PURPLE+"    ███  ▐███   "+ANSIFormatter.ANSI_RESET+"      ███   ███   ███    ███    ▀███▀▀██ ███    ███   ███    ███   ███    ███ ███       ███▌   ███    █▀  \n" +
                ANSIFormatter.ANSI_GREEN+"███        "+ANSIFormatter.ANSI_CYAN+"███    ███"+ANSIFormatter.ANSI_RED+" ███    ███"+ANSIFormatter.ANSI_YELLOW+"  ▄███▄▄▄    "+ANSIFormatter.ANSI_PURPLE+"    ▀███▄███▀   "+ANSIFormatter.ANSI_RESET+"      ███   ███   ███    ███     ███   ▀ ███    ███  ▄███▄▄▄▄██▀   ███    ███ ███       ███▌   ███        \n" +
                ANSIFormatter.ANSI_GREEN+"███        "+ANSIFormatter.ANSI_CYAN+"███    ███"+ANSIFormatter.ANSI_RED+" ███    ███"+ANSIFormatter.ANSI_YELLOW+" ▀▀███▀▀▀    "+ANSIFormatter.ANSI_PURPLE+"    ████▀██▄    "+ANSIFormatter.ANSI_RESET+"      ███   ███ ▀███████████     ███     ███    ███ ▀▀███▀▀▀▀▀   ▀███████████ ███       ███▌ ▀███████████ \n" +
                ANSIFormatter.ANSI_GREEN+"███    █▄  "+ANSIFormatter.ANSI_CYAN+"███    ███"+ANSIFormatter.ANSI_RED+" ███    ███"+ANSIFormatter.ANSI_YELLOW+"   ███    █▄ "+ANSIFormatter.ANSI_PURPLE+"   ▐███  ▀███   "+ANSIFormatter.ANSI_RESET+"      ███   ███   ███    ███     ███     ███    ███ ▀███████████   ███    ███ ███       ███           ███ \n" +
                ANSIFormatter.ANSI_GREEN+"███    ███ "+ANSIFormatter.ANSI_CYAN+"███    ███"+ANSIFormatter.ANSI_RED+" ███   ▄███"+ANSIFormatter.ANSI_YELLOW+"   ███    ███"+ANSIFormatter.ANSI_PURPLE+"  ▄███     ███▄ "+ANSIFormatter.ANSI_RESET+"      ███   ███   ███    ███     ███     ███    ███   ███    ███   ███    ███ ███▌    ▄ ███     ▄█    ███ \n" +
                ANSIFormatter.ANSI_GREEN+"████████▀  "+ANSIFormatter.ANSI_CYAN+" ▀██████▀ "+ANSIFormatter.ANSI_RED+" ████████▀ "+ANSIFormatter.ANSI_YELLOW+"   ██████████"+ANSIFormatter.ANSI_PURPLE+" ████       ███▄"+ANSIFormatter.ANSI_RESET+"       ▀█   █▀    ███    █▀     ▄████▀   ████████▀    ███    ███   ███    █▀  █████▄▄██ █▀    ▄████████▀  \n" +
                "                                                                                                                   ███    ███              ▀                           \n");
    }
    /**
     * This method is used to print on the TUI a request for nickname
     * @return the nickname selected
     */
    public String askNickname(Scanner sc){
        boolean selected= false;
        String nickname=null;
        while(!selected){
            System.out.println("Insert your nickname: ");
            nickname= sc.nextLine();
            if(!nickname.isBlank()){
                selected=true;
            }else{
                System.out.println("Insert a non-empty nickname: ");
            }
        }
        return nickname;
    }

    /**
     * This method prints a menu containing the action the player can perform
     * @param inTurn is true if the player is the one playing, false otherwise
     */
    public void gameTurn(boolean inTurn) {
        System.out.println("These are the action you can perform");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "0" + ANSIFormatter.ANSI_RESET + " - Leave the game                     |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "1" + ANSIFormatter.ANSI_RESET + " - Check the cards in your hand       |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "2" + ANSIFormatter.ANSI_RESET + " - Check the objective cards          |");// si possono unire
        System.out.println("| " + ANSIFormatter.ANSI_RED + "3" + ANSIFormatter.ANSI_RESET + " - Check the drawable cards           |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "4" + ANSIFormatter.ANSI_RESET + " - Check someone else's board         |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "5" + ANSIFormatter.ANSI_RESET + " - Check the points scored            |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "6" + ANSIFormatter.ANSI_RESET + " - Send a message in the chat         |");
        if (inTurn) {
            System.out.println("| " + ANSIFormatter.ANSI_RED + "7" + ANSIFormatter.ANSI_RESET + " - Play a card                        |"); //the draw will be called after the play action
        }
    }

    /**
     * This method is used to ask the player what action he wants to do during his turn (passive/active)
     * @param sc is the player' Scanner
     * @param inTurn is true if the player is the one playing, false otherwise
     * @return the index of the selected action
     */
    public int askAction(Scanner sc, boolean inTurn){
    boolean ok=false;
    int value=0;
        while(!ok){
            try {
                value = sc.nextInt();
                sc.nextLine();
                //lasciando lo scanner, wrappedObserver aspetta una risposta prima di inviare
                //update agli altri client (non ha ancora esaurito la chiamata a funzione
                if((inTurn)&&(value==7)) {
                    ok=true;
                }else if((value<0)||(value>6)){
                    System.out.println("Please, insert one of the possible values. ");
                }else{
                    ok=true;
                }
            }catch (InputMismatchException e){
                System.out.println("Please type a number. ");
                sc.next();
            }
        }
        return value;
    }

    /**
     * This method let the player select how to play his baseCard
     * @param sc is the player' Scanner
     * @param baseCard is the player's baseCard
     * @return true if the card is played face-up, false otherwise
     */
    public boolean askPlayBaseCard(Scanner sc, PlayableCard baseCard) {
        int selection = 0;
        System.out.println("This is your base card: " + baseCard.getId());
        System.out.println("Would you like to play it upwards(1) or downwards(2)?");
        while (true) {
            try {
                selection = sc.nextInt();
                if(selection==1){
                    return true;
                }else if(selection==2){
                    return false;
                }else{
                    System.out.println("Please write 1 to play it upwards, 2 to play it downwards");
                }
            }catch (InputMismatchException e){
                System.out.println("Please insert a number! 1 to play the card upwards, 2 to play the card downwards.");
                sc.next();
            }
        }
    }
    public ObjectiveCard askChoosePersonalObjective(Scanner sc, List<ObjectiveCard> objectiveCards) {
        int choice=-1;
        while (true) {
            System.out.println("These are the 2 objective card you can choose between: \n1_ " + objectiveCards.get(0).getId() + "\n2_ " + objectiveCards.get(1).getId());
            try {
                choice=sc.nextInt();
                if(choice==1){
                    return objectiveCards.get(0);
                }else if(choice==2){
                    return objectiveCards.get(1);
                }else{
                    System.out.println("Please insert a valid number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please type a number.");
                sc.next();
            }
        }
    }

    public PlayableCard askPlayCard(Scanner sc, Player personalPlayer){
        int cardIndex=-1;
        System.out.println("Hand:");
        printHand(personalPlayer.getPlayerDeck());
        System.out.println("Table:");
        printTable(personalPlayer.getBoard());
        System.out.println();
        System.out.println("Which card you've got do you want to play? Type the index of the card.");
        try{
            cardIndex=sc.nextInt();
            if(personalPlayer.getPlayerDeck()[cardIndex]!=null){
                return personalPlayer.getPlayerDeck()[cardIndex];
            }else{
                System.out.println("Not a valid index, returning to menu.");
                return null;
            }
        }catch (InputMismatchException e){
            System.out.println("Not a valid index, returning to menu.");
            sc.next();
            return null;
        }
    }

    public boolean askCardOrientation(Scanner sc, PlayableCard card){
        int selection = 0;
        System.out.println("Would you like to play it upwards(1) or downwards(2)?");
        while (true) {
            try {
                selection = sc.nextInt();
                sc.nextLine();
                if(selection==1){
                    return true;
                }else if(selection==2){
                    return false;
                }else{
                    System.out.println("Please write 1 to play it upwards, 2 to play it downwards");
                }
            }catch (InputMismatchException e){
                System.out.println("Please insert a number! 1 to play the card upwards, 2 to play the card downwards.");
                sc.next();
            }
        }
    }
    public Coordinates askCoordinates(Scanner sc, PlayableCard playableCard, Board board){
        String coordinates;
        System.out.println("In which coordinates do you want to play the card? Type in (x,y) format.");
        coordinates=sc.nextLine();
        String[] partition=coordinates.split(",");

        Coordinates coordinates1=new Coordinates();
        try {
            coordinates1.setX(Integer.parseInt(partition[0].trim().substring(1).trim()));
            coordinates1.setY(Integer.parseInt(partition[1].trim().substring(0, partition[1].trim().length() - 1).trim()));
            if(board.getPlayablePositions().contains(coordinates1)){
                return coordinates1;
            }else{
                System.out.println(ANSIFormatter.ANSI_RED+"You can't play a card here!"+ANSIFormatter.ANSI_RESET);
                return null;
            }
        }catch (NumberFormatException e){
            System.out.println("Not valid coordinates, returning to menu.");
            return null;
        }
        }

    public PlayableCard askCardToDraw(PlayableDeck goldDeck, PlayableDeck resourceDeck, List<PlayableCard> visibileCards, Scanner sc) {
        printDrawableCards(goldDeck, resourceDeck, visibileCards);
        int cardIndex = -1;
        while (true) {
            System.out.println("Which card do you want to draw? Type 1 for the top goldDeck, 2 for the top resourceDeck.");
            System.out.println("Type the index 3, 4, 5, 6 for one of the visible cards");
            try {
                cardIndex = sc.nextInt();
                if (cardIndex == 1) {
                    return goldDeck.checkFirstCard();
                } else if (cardIndex == 2) {
                    return resourceDeck.checkFirstCard();
                } else if ((cardIndex > 2) && (cardIndex < 7)) {
                    return visibileCards.get(cardIndex - 3);
                } else {
                    System.out.println("Not a valid index, try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Not a valid index, try again.");
                sc.next();
            }
        }
    }

    /**
     * This method is used to print the cards a Player is keeping in is hands
     * @param playerDeck is the collection of cards the Player has in his hands
     */
    public void printHand(PlayableCard[] playerDeck){
        System.out.println("The card you've got are: ");
        for(PlayableCard card: playerDeck) {
            System.out.println(card.getId()+" ");
        }
    }

    /**
     * This method prints the common Objective Cards and the personal Objective card of the player
     * @param objectiveCards are the 3 Objective cards to be printed
     */
    public void printObjectiveCard(List<ObjectiveCard> objectiveCards){
        boolean personalObjective=true;
        System.out.println("These are the objective cards: ");
        for(ObjectiveCard objCard: objectiveCards){
            if(personalObjective) {
                System.out.println("This is your PERSONAL objective card.");
            }
            System.out.print(objCard.getId()+" ");
            if(personalObjective){
                System.out.println("");
                personalObjective=false;
            }
        }
        System.out.println("");

    }

    /**
     * This method prints all the resources and objects that are "visible" on the player's board
     * @param board
     */
    public void printAvailableResources(Board board){
        System.out.println("These are the resources and objects you have available on the board: ");
        for(AngleType a: board.getNumResources().keySet()){
            if(board.getNumResources().get(a)!=0){
                System.out.println("- "+a+": "+board.getNumResources().get(a));
            }
        }
    }

    /**
     * This method is used to print the table of the selected board
     * @param board is the player' selected board
     */
    public void printTable(Board board){
        for(int i=0; i<board.getBoardDimensions(); i++){
            for(int j=0; j<board.getBoardDimensions(); j++){
                if(board.getTable()[i][j]==null){
                    System.out.print("");
                }
                else{
                    System.out.print(board.getTable()[i][j].getId());
                }
            }
        }
    }

    /**
     * This method prints an ordered scoreBoard of the game
     * @param players are the players in the game
     */
    public void printScoreBoard(List<Player> players){
        players.sort(Comparator.comparing(Player::getPoints));
        for(int i=0; i< players.size(); i++){
            System.out.println((i+1)+"_ "+players.get(i).getNickname()+" scored "+ players.get(i).getPoints()+" points!");
        }
    }
    public void printDrawableCards(PlayableDeck goldDeck, PlayableDeck resourceDeck, List<PlayableCard> visibileCards){
        System.out.println("This is the card on top of the goldDeck: "+goldDeck.checkFirstCard().getId());
        System.out.println("This is the card on top of the resourceDeck: "+resourceDeck.checkFirstCard().getId());
        for(PlayableCard card: visibileCards){
            System.out.print(card.getId()+" ");
        }
        System.out.println();
    }
    public void askNumPlayers(){

    }


    public void askCardToPlay(){

    }


    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }



    /*menuThread=new Thread(()->{ //that's needed only for tui
            Scanner sc=new Scanner(System.in);
            while(inGame){
                if(sc.nextLine().equals("menu")){
                    if(personalPlayer.getNickname().equals(playersInTheGame.get(0).getNickname())){
                        azioni giocatore di turno
                    }else{
                   azioni giocatore non di turno
                    }
                }
            }
        });
        menuThread.start();

    /**
     * 🪶 feather
     * 🫙 jar
     * 📃 scroll
     * 🍄 fungi
     * 🦋 insect
     * 🐺 animal
     * 🌿 plant
     */

}
