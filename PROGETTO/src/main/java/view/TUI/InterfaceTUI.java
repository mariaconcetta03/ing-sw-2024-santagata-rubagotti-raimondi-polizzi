package view.TUI;

import org.model.PlayableCard;
import utils.Observable;

import java.io.Serializable;
import java.util.*;

public class InterfaceTUI implements Serializable { //I don't think it has to extend 
    Scanner sc=new Scanner(System.in);

    public void setClient(){
        //choose interface
        //chose nickname
    }

    public void printWelcome(){
        System.out.println("Welcome to "+ANSIFormatter.ANSI_GREEN+"C"+ANSIFormatter.ANSI_CYAN+"O"+ANSIFormatter.ANSI_RED+
                "D"+ANSIFormatter.ANSI_YELLOW+"E"+ANSIFormatter.ANSI_PURPLE+"X"+ANSIFormatter.ANSI_RESET+" NATURALIS");
    }
    /**
     * This method is used to print on the TUI a request for nickname
     * @return the nickname selected
     */
    public String askNickname(){
        sc=new Scanner(System.in);
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

    public void gameTurn(boolean inTurn){
        if(inTurn){
            System.out.println("These are the action you can perform");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"0"+ANSIFormatter.ANSI_RESET+" - Leave the game                     |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"1"+ANSIFormatter.ANSI_RESET+" - Check the cards in your hand       |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"2"+ANSIFormatter.ANSI_RESET+" - Check your personal objective card |");// si possono unire
            System.out.println("| "+ANSIFormatter.ANSI_RED+"3"+ANSIFormatter.ANSI_RESET+" - Check the common objective cards   |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"4"+ANSIFormatter.ANSI_RESET+" - Check the drawable cards           |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"5"+ANSIFormatter.ANSI_RESET+" - Check someone else's board         |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"6"+ANSIFormatter.ANSI_RESET+" - Check the points scored            |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"7"+ANSIFormatter.ANSI_RESET+" - Send a message in the chat         |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"8"+ANSIFormatter.ANSI_RESET+" - Play a card                        |"); //the draw will be called after the play action
        }else{
            System.out.println("These are the action you can perform");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"0"+ANSIFormatter.ANSI_RESET+" - Leave the game                     |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"1"+ANSIFormatter.ANSI_RESET+" - Check the cards in your hand       |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"2"+ANSIFormatter.ANSI_RESET+" - Check your personal objective card |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"3"+ANSIFormatter.ANSI_RESET+" - Check the common objective cards   |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"4"+ANSIFormatter.ANSI_RESET+" - Check the drawable cards           |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"5"+ANSIFormatter.ANSI_RESET+" - Check someone else's board         |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"6"+ANSIFormatter.ANSI_RESET+" - Check the points scored            |");
            System.out.println("| "+ANSIFormatter.ANSI_RED+"7"+ANSIFormatter.ANSI_RESET+" - Send a message in the chat         |");

        }
        }

    public int askAction(){
    boolean ok=false;
    Scanner sc;
    int value=0;
        while(!ok){
            try {
                sc= new Scanner(System.in);
                value = sc.nextInt(); //lasciando lo scanner, wrappedObserver aspetta una risposta prima di inviare
                //update agli altri client (non ha ancora esaurito la chiamata a funzione
                if((value<0)||(value>6)){
                    System.out.println("Please, insert one of the possible values. ");
                }else{
                    ok=true;
                }
            }catch (InputMismatchException e){
                System.out.println("Please type a number. ");
            }
        }
        return value;
    }
    public boolean askPlayBaseCard(PlayableCard baseCard) {
        Scanner sc = new Scanner(System.in);
        int selection = 0;
        System.out.println("This is your base card: " + baseCard.getId());
        System.out.println("Would you like to play it upwards(1) or downwards(2)?");
        while (true) {
            sc=new Scanner(System.in);
            try {
                selection = sc.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Please insert a number! 1 to play the card upwards, 2 to play the card downwards.");
            }
            if(selection==1){
                return true;
            }else if(selection==2){
                return false;
            }else{
                System.out.println("Please write 1 to play it upwards, 2 to play it downwards");
            }
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

    public void askCardOrientation(){
    }

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
