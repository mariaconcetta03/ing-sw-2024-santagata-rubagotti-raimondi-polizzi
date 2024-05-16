package view.TUI;

import org.model.Board;
import org.model.ObjectiveCard;
import org.model.PlayableCard;
import org.model.Player;
import utils.Observable;

import java.io.Serializable;
import java.util.*;

public class InterfaceTUI implements Serializable { //I don't think it has to extend 
    Scanner sc=new Scanner(System.in);

       /*System.out.println("Welcome to "+ANSIFormatter.ANSI_GREEN+"C"+ANSIFormatter.ANSI_CYAN+"O"+ANSIFormatter.ANSI_RED+
            "D"+ANSIFormatter.ANSI_YELLOW+"E"+ANSIFormatter.ANSI_PURPLE+"X"+ANSIFormatter.ANSI_RESET+" NATURALIS");
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

    }

    public void printBoard(Board board){
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

    public void askNumPlayers(){
        sc=new Scanner(System.in);

    }

    public void askCardToDraw(){
        sc=new Scanner(System.in);

    }

    public void askCardToPlay(){

    }

    public void askCardOrientation(PlayableCard card){
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
