package CODEX.view.TUI;

import CODEX.org.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class InterfaceTUI implements Serializable { //I don't think it has to extend 

    /*System.out.println("Welcome to "+ANSIFormatter.ANSI_GREEN+"C"+ANSIFormatter.ANSI_CYAN+"O"+ANSIFormatter.ANSI_RED+
         "D"+ANSIFormatter.ANSI_YELLOW+"E"+ANSIFormatter.ANSI_PURPLE+"X"+ANSIFormatter.ANSI_RESET+" NATURALIS");
*/
    private Map<AngleType, String> emojis;
    private Map<AngleType, String> abbreviations;
    private Map<AngleType, String> cardColors;
    String rst = ANSIFormatter.ANSI_RESET;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Scanner sc = new Scanner(System.in);

    private boolean previousIsPlayingAttribute;
    private boolean firstMenu = true;
    private int  cornerDistance=15;

    /**
     * 🪶 feather
     * 🫙 jar
     * 📃 scroll
     * 🍄 fungi
     * 🦋 insect
     * 🐺 animal
     * 🌿 plant
     */
    public InterfaceTUI() {
        emojis = new HashMap<>();
        emojis.put(AngleType.FEATHER, "🪶");
        emojis.put(AngleType.JAR, "🫙");
        emojis.put(AngleType.SCROLL, "📃");
        emojis.put(AngleType.FUNGI, "🍄");
        emojis.put(AngleType.INSECT, "🦋");
        emojis.put(AngleType.ANIMAL, "🐺");
        emojis.put(AngleType.NATURE, "🌿");
        emojis.put(AngleType.NO_RESOURCE, " ");

        abbreviations = new HashMap<>();
        abbreviations.put(AngleType.FEATHER, "Q"); //QUILL (da manuale) al posto di feather
        abbreviations.put(AngleType.JAR, "J");  //sarebbe INKWILL
        abbreviations.put(AngleType.SCROLL, "S"); //sarebbe MANUSCRIPT
        abbreviations.put(AngleType.FUNGI, "F");
        abbreviations.put(AngleType.INSECT, "I");
        abbreviations.put(AngleType.ANIMAL, "A");
        abbreviations.put(AngleType.NATURE, "N");
        abbreviations.put(AngleType.NO_RESOURCE, " ");

        cardColors=new HashMap<>();
        cardColors.put(AngleType.FEATHER, ANSIFormatter.ANSI_YELLOW);
        cardColors.put(AngleType.JAR, ANSIFormatter.ANSI_YELLOW);
        cardColors.put(AngleType.SCROLL, ANSIFormatter.ANSI_YELLOW);
        cardColors.put(AngleType.FUNGI, ANSIFormatter.ANSI_RED);
        cardColors.put(AngleType.INSECT, ANSIFormatter.ANSI_PURPLE);
        cardColors.put(AngleType.ANIMAL, ANSIFormatter.ANSI_BLUE);
        cardColors.put(AngleType.NATURE, ANSIFormatter.ANSI_GREEN);
        cardColors.put(AngleType.NO_RESOURCE, " ");
    }

    /**
     * This method prints the welcome message to a new Player in the lobby
     */
    public void printWelcome() {
        System.out.println("\n\n\n\n\n\n\n\n\n" + ANSIFormatter.ANSI_RED + "Welcome to");
        System.out.println("\n" +
                ANSIFormatter.ANSI_GREEN + " ▄████████ " + ANSIFormatter.ANSI_CYAN + " ▄██████▄ " + ANSIFormatter.ANSI_RED + " ████████▄ " + ANSIFormatter.ANSI_YELLOW + "    ▄████████" + ANSIFormatter.ANSI_PURPLE + " ▀████    ▐████▀" + ANSIFormatter.ANSI_RESET + "      ███▄▄▄▄      ▄████████     ███     ███    █▄     ▄████████    ▄████████  ▄█        ▄█     ▄████████ \n" +
                ANSIFormatter.ANSI_GREEN + "███    ███ " + ANSIFormatter.ANSI_CYAN + "███    ███" + ANSIFormatter.ANSI_RED + " ███   ▀███" + ANSIFormatter.ANSI_YELLOW + "   ███    ███" + ANSIFormatter.ANSI_PURPLE + "   ███▌   ████▀ " + ANSIFormatter.ANSI_RESET + "      ███▀▀▀██▄   ███    ███ ▀█████████▄ ███    ███   ███    ███   ███    ███ ███       ███    ███    ███ \n" +
                ANSIFormatter.ANSI_GREEN + "███    █▀  " + ANSIFormatter.ANSI_CYAN + "███    ███" + ANSIFormatter.ANSI_RED + " ███    ███" + ANSIFormatter.ANSI_YELLOW + "   ███    █▀ " + ANSIFormatter.ANSI_PURPLE + "    ███  ▐███   " + ANSIFormatter.ANSI_RESET + "      ███   ███   ███    ███    ▀███▀▀██ ███    ███   ███    ███   ███    ███ ███       ███▌   ███    █▀  \n" +
                ANSIFormatter.ANSI_GREEN + "███        " + ANSIFormatter.ANSI_CYAN + "███    ███" + ANSIFormatter.ANSI_RED + " ███    ███" + ANSIFormatter.ANSI_YELLOW + "  ▄███▄▄▄    " + ANSIFormatter.ANSI_PURPLE + "    ▀███▄███▀   " + ANSIFormatter.ANSI_RESET + "      ███   ███   ███    ███     ███   ▀ ███    ███  ▄███▄▄▄▄██▀   ███    ███ ███       ███▌   ███        \n" +
                ANSIFormatter.ANSI_GREEN + "███        " + ANSIFormatter.ANSI_CYAN + "███    ███" + ANSIFormatter.ANSI_RED + " ███    ███" + ANSIFormatter.ANSI_YELLOW + " ▀▀███▀▀▀    " + ANSIFormatter.ANSI_PURPLE + "    ████▀██▄    " + ANSIFormatter.ANSI_RESET + "      ███   ███ ▀███████████     ███     ███    ███ ▀▀███▀▀▀▀▀   ▀███████████ ███       ███▌ ▀███████████ \n" +
                ANSIFormatter.ANSI_GREEN + "███    █▄  " + ANSIFormatter.ANSI_CYAN + "███    ███" + ANSIFormatter.ANSI_RED + " ███    ███" + ANSIFormatter.ANSI_YELLOW + "   ███    █▄ " + ANSIFormatter.ANSI_PURPLE + "   ▐███  ▀███   " + ANSIFormatter.ANSI_RESET + "      ███   ███   ███    ███     ███     ███    ███ ▀███████████   ███    ███ ███       ███           ███ \n" +
                ANSIFormatter.ANSI_GREEN + "███    ███ " + ANSIFormatter.ANSI_CYAN + "███    ███" + ANSIFormatter.ANSI_RED + " ███   ▄███" + ANSIFormatter.ANSI_YELLOW + "   ███    ███" + ANSIFormatter.ANSI_PURPLE + "  ▄███     ███▄ " + ANSIFormatter.ANSI_RESET + "      ███   ███   ███    ███     ███     ███    ███   ███    ███   ███    ███ ███▌    ▄ ███     ▄█    ███ \n" +
                ANSIFormatter.ANSI_GREEN + "████████▀  " + ANSIFormatter.ANSI_CYAN + " ▀██████▀ " + ANSIFormatter.ANSI_RED + " ████████▀ " + ANSIFormatter.ANSI_YELLOW + "   ██████████" + ANSIFormatter.ANSI_PURPLE + " ████       ███▄" + ANSIFormatter.ANSI_RESET + "       ▀█   █▀    ███    █▀     ▄████▀   ████████▀    ███    ███   ███    █▀  █████▄▄██ █▀    ▄████████▀  \n" +
                "                                                                                                                   ███    ███              ▀                           \n");
    }

    /**
     * This method is used to print on the TUI a request for nickname
     *
     * @return the nickname selected
     */
    public String askNickname(Scanner sc) {
        boolean selected = false;
        String nickname = null;
        while (!selected) {
            System.out.println("Insert your nickname: ");
            nickname = sc.nextLine();
            if (!nickname.isBlank()) {
                selected = true;
            } else {
                System.out.println("Insert a non-empty nickname: ");
            }
        }
        return nickname;
    }

    /**
     * This method prints the correct menu containing the action the player can perform
     * @param isPlaying is true if the player is the one playing, false otherwise
     */
    public void gameTurn(boolean isPlaying) {
        if (firstMenu) {
            printMenu(isPlaying);
            firstMenu = false;
        } else {
            if (previousIsPlayingAttribute != isPlaying) {
                printMenu(isPlaying);
            }
        }
        previousIsPlayingAttribute = isPlaying;
    }

    /**
     * This method actually prints the menu with the action the player can perform
     * @param isPlaying is true if the player is the one playing, false otherwise
     */
    public void printMenu(boolean isPlaying){
        System.out.println("These are the action you can perform");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "menu" + ANSIFormatter.ANSI_RESET + " - Print the menu                  |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "0" + ANSIFormatter.ANSI_RESET + " - Leave the game                     |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "1" + ANSIFormatter.ANSI_RESET + " - Check the cards in your hand       |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "2" + ANSIFormatter.ANSI_RESET + " - Check the objective cards          |");// si possono unire
        System.out.println("| " + ANSIFormatter.ANSI_RED + "3" + ANSIFormatter.ANSI_RESET + " - Check the drawable cards           |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "4" + ANSIFormatter.ANSI_RESET + " - Check someone else's board         |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "5" + ANSIFormatter.ANSI_RESET + " - Check the points scored            |");
        System.out.println("| " + ANSIFormatter.ANSI_RED + "6" + ANSIFormatter.ANSI_RESET + " - Send a message in the chat         |");
        if (isPlaying) {
            System.out.println("| " + ANSIFormatter.ANSI_RED + "7" + ANSIFormatter.ANSI_RESET + " - Play a card                        |"); //the draw will be called after the play action
        }
    }


    /**
     * This method let the player select how to play his baseCard
     * @param sc is the player' Scanner
     * @param baseCard is the player's baseCard
     * @return true if the card is played face-up, false otherwise
     */
    public boolean askPlayBaseCard(Scanner sc, PlayableCard baseCard) {
        int selection = 0;
        List <PlayableCard> tmp= new ArrayList<>();
        tmp.add(baseCard);

        System.out.println("This is your base card:");
        printPlayableCards(tmp);
        System.out.println("Would you like to play it upwards(1) or downwards(2)?");
        while (true) {
            try {
                selection = sc.nextInt();
                if (selection == 1) {
                    return true;
                } else if (selection == 2) {
                    return false;
                } else {
                    System.out.println("Please write 1 to play it upwards, 2 to play it downwards");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please insert a number! 1 to play the card upwards, 2 to play the card downwards.");
                sc.next();
            }
        }
    }

    /**
     * This method is used to ask the player which Objective card he wants to keep
     * @param sc is the player' Scanner
     * @param objectiveCards are the 2 possible objective Cards to choose between
     * @return the objectiveCard chosen by the player
     */
    public ObjectiveCard askChoosePersonalObjective(Scanner sc, List<ObjectiveCard> objectiveCards) {
        int choice = -1;
        while (true) {
            System.out.println("These are the 2 objective card you can choose between: please type the index of the one you want to select.");
            printObjectiveCard(objectiveCards);
            try {
                choice = sc.nextInt();
                if (choice == 0) {
                    return objectiveCards.get(0);
                } else if (choice == 1) {
                    return objectiveCards.get(1);
                } else {
                    System.out.println("Please insert a valid number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please type a number.");
                sc.next();
            }
        }
    }

    /**
     * This method is used to ask the player which card he wants to play
     * @param sc is the player' Scanner
     * @param personalPlayer is the player who wants to play a card
     * @return the chosen card, null if the user inserted an invalid index
     */
    public PlayableCard askPlayCard(Scanner sc, Player personalPlayer) {
        int cardIndex = -1;
        System.out.println("Hand:");
        printHand(personalPlayer.getPlayerDeck());
        System.out.println("Table:");
        printTable(personalPlayer.getBoard());
        System.out.println();
        System.out.println("Which card you've got do you want to play? Type the index of the card.");
        try {
            cardIndex = sc.nextInt();
            if ((cardIndex>=0)&&(cardIndex<3)&&(personalPlayer.getPlayerDeck()[cardIndex] != null)) {
                return personalPlayer.getPlayerDeck()[cardIndex];
            } else {
                System.out.println("Not a valid index, returning to menu.");
                return null;
            }
        } catch (InputMismatchException e) {
            System.out.println("Not a valid index, returning to menu.");
            sc.next();
            return null;
        }
    }

    /**
     * This method is used to ask the user which side he wants to play the card
     * @param sc is the player' Scanner
     * @param card is the card he is playing
     * @return true if the card has to be played face up, false if it has to be played face down
     */
    public boolean askCardOrientation(Scanner sc, PlayableCard card) {
        int selection = 0;
        System.out.println("Would you like to play it upwards(1) or downwards(2)?");
        while (true) {
            try {
                selection = sc.nextInt();
                sc.nextLine();
                if (selection == 1) {
                    return true;
                } else if (selection == 2) {
                    return false;
                } else {
                    System.out.println("Please write 1 to play it upwards, 2 to play it downwards");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please insert a number! 1 to play the card upwards, 2 to play the card downwards.");
                sc.next();
            }
        }
    }

    /**
     * This method is used to ask the user where he wants to play his card
     * @param sc is the player' Scanner
     * @param playableCard is the card he is playing
     * @param board is its board
     * @return the coordinates where the player wants to play the card, null if he inserted an invalid position
     */
    public Coordinates askCoordinates(Scanner sc, PlayableCard playableCard, Board board) {
        String coordinates;
        System.out.println("In which coordinates do you want to play the card? Type in (x,y) format.");
        coordinates = sc.nextLine();
        String[] partition = coordinates.split(",");

        Coordinates coordinates1 = new Coordinates();
        try {
            coordinates1.setX(Integer.parseInt(partition[0].trim().substring(1).trim()));
            coordinates1.setY(Integer.parseInt(partition[1].trim().substring(0, partition[1].trim().length() - 1).trim()));
            if (board.getPlayablePositions().contains(coordinates1)) {
                return coordinates1;
            } else {
                System.out.println(ANSIFormatter.ANSI_RED + "You can't play a card here!" + ANSIFormatter.ANSI_RESET);
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Not valid coordinates, returning to menu.");
            return null;
        }
    }

    /**
     * This method is used to ask the player which card he wants to draw after he played a card
     * @param goldDeck is the deck containing the gold cards
     * @param resourceDeck is the deck containing the resource cards
     * @param visibileCards are the 4 cards faced-up on the table
     * @param sc is the player' Scanner
     * @return the card the player wants to draw
     */
    public PlayableCard askCardToDraw(PlayableDeck goldDeck, PlayableDeck resourceDeck, List<PlayableCard> visibileCards, Scanner sc) {
        printDrawableCards(goldDeck, resourceDeck, visibileCards);
        int cardIndex = -1;
        while (true) {
            System.out.println("Which card do you want to draw? Type 0 for the top goldDeck, 1 for the top resourceDeck.");
            System.out.println("Type the index 2, 3, 4, 5 for one of the visible cards");
            try {
                cardIndex = sc.nextInt();
                if (cardIndex == 0) {
                    return resourceDeck.checkFirstCard();
                } else if (cardIndex == 1) {
                    return goldDeck.checkFirstCard();
                } else if ((cardIndex > 1) && (cardIndex < 6)) {
                    return visibileCards.get(cardIndex - 2);
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
     * This method is used to print some playableCard ( player Hand, drawable cards...)
     * @param cardsToPrint are the cards that have to be printed
     */
    public void printPlayableCards(List<PlayableCard> cardsToPrint) {
        String firstRow = "";
        String secondRow = "";
        String thirdRow = "";
        String fourthRow = "";
        String fifthRow = "";
        String sixthRow = "";
        String seventhRow = "";
        String pointsString = "";
        String indexString="";
        int index=0;
        int nSpaces = 0;
        int nDecks=2;
        boolean evenNSpaces = false;
        String cardColor;

        for (PlayableCard card : cardsToPrint) {
            if (card != null) {
                if((card.getId()<81)||(card.getId()>86)) {
                    cardColor = cardColors.get(card.getCentralResources().get(0));
                    indexString=indexString.concat("          ("+index+")          ");
                    index++;
                }else{
                    cardColor=ANSIFormatter.ANSI_WHITE;
                }
                if((cardsToPrint.size()==6)&&(nDecks>0)){
                    nDecks--;
                    firstRow=firstRow.concat(" _____________________");
                    secondRow=secondRow.concat("|          "+ANSIFormatter.ANSI_RED+"?"+rst+"          |");
                    thirdRow=thirdRow.concat("|    THIS SIDE WILL   |");
                    fourthRow=fourthRow.concat("|    BE SHOWN ONLY    |");
                    fifthRow=fifthRow.concat("|    WHEN THE CARD    |");
                    sixthRow=sixthRow.concat("|      IS DRAWN       |");
                    seventhRow=seventhRow.concat("|_____________________|");
                }else{
                firstRow = firstRow.concat(cardColor + " _____________________" + rst);
                secondRow = secondRow.concat(cardColor + "|" + rst);
                thirdRow = thirdRow.concat(cardColor + "|" + rst);
                if (!card.get_front_up_left().equals(AngleType.ABSENT)) {
                    secondRow = secondRow.concat(abbreviations.get(card.get_front_up_left()) + cardColor + " |" + rst);
                    thirdRow = thirdRow.concat(cardColor + "__|               " + rst);
                } else {
                    secondRow = secondRow.concat("   ");
                    thirdRow = thirdRow.concat("                  ");
                }
                pointsString="";
                if (card.getPoints() > 0) {
                    pointsString = pointsString.concat(card.getPoints() + "pt");
                    if (card.isScrollToReceivePoints()) {
                        pointsString = pointsString.concat("*" + abbreviations.get(AngleType.SCROLL));
                    } else if (card.isJarToReceivePoints()) {
                        pointsString = pointsString.concat("*" + abbreviations.get(AngleType.JAR));
                    } else if (card.isFeatherToReceivePoints()) {
                        pointsString = pointsString.concat("*" + abbreviations.get(AngleType.FEATHER));
                    } else if (card.isCoverAngleToReceivePoints()) {
                        pointsString = pointsString.concat("*angCov");
                    }
                    nSpaces = cornerDistance - pointsString.length();
                    if (nSpaces % 2 == 0) {
                        evenNSpaces = true;
                    } else {
                        evenNSpaces = false;
                    }
                    nSpaces = nSpaces / 2;
                    for (int i = 0; i < nSpaces; i++) {
                        pointsString = " " + pointsString + " ";
                    }
                    if (!evenNSpaces) {
                        pointsString = " " + pointsString;
                    }
                    secondRow = secondRow.concat(pointsString);
                } else {
                    secondRow = secondRow.concat("               ");
                }

                if (!card.get_front_up_right().equals(AngleType.ABSENT)) {
                    secondRow = secondRow.concat(cardColor + "| " + rst + abbreviations.get(card.get_front_up_right()) + cardColor + "|" + rst);
                    thirdRow = thirdRow.concat(cardColor + "|__|" + rst);
                } else {
                    secondRow = secondRow.concat(cardColor + "   |" + rst);
                    thirdRow = thirdRow.concat(cardColor + "   |" + rst);
                }

                //fourth Row
                fourthRow = fourthRow.concat(cardColor + "|                     |" + rst);

                //fifth Row
                fifthRow = fifthRow.concat(cardColor + "|" + rst);
                sixthRow = sixthRow.concat(cardColor + "|" + rst);
                seventhRow = seventhRow.concat(cardColor + "|" + rst);

                if (!card.get_front_down_left().equals(AngleType.ABSENT)) {
                    fifthRow = fifthRow.concat(cardColor + "__" + rst);
                    sixthRow = sixthRow.concat(abbreviations.get(card.get_front_down_left()) + cardColor + " |" + rst);
                    seventhRow = seventhRow.concat(cardColor + "__|_______________" + rst);
                } else {
                    fifthRow = fifthRow.concat("  ");
                    sixthRow = sixthRow.concat("   ");
                    seventhRow = seventhRow.concat(cardColor + "__________________" + rst);
                }
                fifthRow = fifthRow.concat("                 ");

                pointsString = "";
                nSpaces = 0;
                int k = 0;
                if (!card.getNeededResources().keySet().isEmpty()) {
                    for (AngleType a : card.getNeededResources().keySet()) {
                        if ((card.getNeededResources().keySet().size() != 1) && (k != card.getNeededResources().keySet().size() - 1)) {
                            k++;
                            pointsString = pointsString.concat(ANSIFormatter.ANSI_RED + card.getNeededResources().get(a) + "*" + abbreviations.get(a) + ", " + ANSIFormatter.ANSI_RESET);
                        } else {
                            pointsString = pointsString.concat(ANSIFormatter.ANSI_RED + card.getNeededResources().get(a) + "*" + abbreviations.get(a) + ANSIFormatter.ANSI_RESET);
                        }
                        nSpaces += 9;
                    }
                    nSpaces = nSpaces + cornerDistance - pointsString.length();
                    if (nSpaces % 2 == 0) {
                        evenNSpaces = true;
                    } else {
                        evenNSpaces = false;
                    }
                    nSpaces = nSpaces / 2;
                    for (int i = 0; i < nSpaces; i++) {
                        pointsString = " " + pointsString + " ";
                    }
                    if (!evenNSpaces) {
                        pointsString = " " + pointsString;
                    }
                    sixthRow = sixthRow.concat(pointsString);
                } else {
                    sixthRow = sixthRow.concat("               ");
                }

                if (!card.get_front_down_right().equals(AngleType.ABSENT)) {
                    fifthRow = fifthRow.concat(cardColor + "__|" + rst);
                    sixthRow = sixthRow.concat(cardColor + "| " + rst + abbreviations.get(card.get_front_down_right()) + cardColor + "|" + rst);
                    seventhRow = seventhRow.concat(cardColor + "|__|" + rst);
                } else {
                    fifthRow = fifthRow.concat(cardColor + "  |" + rst);
                    sixthRow = sixthRow.concat(cardColor + "   |" + rst);
                    seventhRow = seventhRow.concat(cardColor + "___|" + rst);
                }
                }

                indexString=indexString.concat("     ");
                firstRow = firstRow.concat("      ");
                secondRow = secondRow.concat("     ");
                thirdRow = thirdRow.concat("     ");
                fourthRow = fourthRow.concat("     ");
                fifthRow = fifthRow.concat("     ");
                sixthRow = sixthRow.concat("     ");
                seventhRow = seventhRow.concat("     ");

            }
        }
        System.out.println(ANSIFormatter.ANSI_CYAN + "FRONT:" + ANSIFormatter.ANSI_RESET);
        if(!indexString.isBlank()) {
            System.out.println(ANSIFormatter.ANSI_YELLOW + indexString + ANSIFormatter.ANSI_RESET);
        }
        System.out.println(firstRow);
        System.out.println(secondRow);
        System.out.println(thirdRow);
        System.out.println(fourthRow);
        System.out.println(fifthRow);
        System.out.println(sixthRow);
        System.out.println(seventhRow);
//BACK
        firstRow="";
        secondRow="";
        thirdRow="";
        fourthRow="";
        fifthRow="";
        sixthRow="";
        seventhRow="";

        for (PlayableCard card : cardsToPrint) {
            if (card != null) {
                if((card.getId()<81)||(card.getId()>86)) {
                    cardColor = cardColors.get(card.getCentralResources().get(0));
                }else{
                    cardColor=ANSIFormatter.ANSI_WHITE;
                }

                firstRow = firstRow.concat(cardColor+" _____________________"+rst);
                secondRow = secondRow.concat(cardColor + "|" + rst);
                thirdRow = thirdRow.concat(cardColor + "|" + rst);
                if (!card.get_back_up_left().equals(AngleType.ABSENT)) {
                    secondRow = secondRow.concat(abbreviations.get(card.get_back_up_left()) + cardColor + " |               " + rst);
                    thirdRow = thirdRow.concat(cardColor + "__|               " + rst);
                } else {
                    secondRow = secondRow.concat("                  ");
                    thirdRow = thirdRow.concat("                  ");
                }
                if (!card.get_back_up_right().equals(AngleType.ABSENT)) {
                    secondRow = secondRow.concat(cardColor + "| " + rst + abbreviations.get(card.get_back_up_right()) + cardColor + "|" + rst);
                    thirdRow = thirdRow.concat(cardColor + "|__|" + rst);
                } else {
                    secondRow = secondRow.concat(cardColor + "   |" + rst);
                    thirdRow = thirdRow.concat(cardColor + "   |" + rst);
                }

                pointsString = "|";

                int k = 0;
                for (AngleType a : card.getCentralResources()) {
                    if ((card.getCentralResources().size() != 1) && (k != card.getCentralResources().size() - 1)) {
                        k++;
                        pointsString = pointsString.concat(  abbreviations.get(a)+", ");
                    } else {
                        pointsString = pointsString.concat(abbreviations.get(a));
                    }
                }
                pointsString=pointsString.concat("|");
                nSpaces = cornerDistance - pointsString.length();
                if (nSpaces % 2 == 0) {
                    evenNSpaces = true;
                } else {
                    evenNSpaces = false;
                }
                nSpaces = nSpaces / 2;
                for (int i = 0; i < nSpaces; i++) {
                    pointsString = " " + pointsString + " ";
                }
                if (!evenNSpaces) {
                    pointsString = " " + pointsString;
                }
                fourthRow=fourthRow.concat(cardColor+"|   "+rst+pointsString+cardColor+"   |"+rst);

                fifthRow=fifthRow.concat(cardColor+"|"+rst);
                sixthRow=sixthRow.concat(cardColor+"|"+rst);
                seventhRow=seventhRow.concat(cardColor+"|"+rst);

                if (!card.get_back_down_left().equals(AngleType.ABSENT)) {
                    fifthRow = fifthRow.concat(cardColor + "__" + rst);
                    sixthRow = sixthRow.concat(abbreviations.get(card.get_back_down_left()) + cardColor + " |               " + rst);
                    seventhRow = seventhRow.concat(cardColor + "__|_______________" + rst);
                } else {
                    fifthRow = fifthRow.concat("  ");
                    sixthRow = sixthRow.concat("                  ");
                    seventhRow = seventhRow.concat(cardColor + "__________________" + rst);
                }
                fifthRow = fifthRow.concat("                 ");
                if (!card.get_back_down_right().equals(AngleType.ABSENT)) {
                    fifthRow = fifthRow.concat(cardColor + "__|" + rst);
                    sixthRow = sixthRow.concat(cardColor + "| " + rst + abbreviations.get(card.get_back_down_right()) + cardColor + "|" + rst);
                    seventhRow = seventhRow.concat(cardColor + "|__|" + rst);
                } else {
                    fifthRow = fifthRow.concat(cardColor + "  |" + rst);
                    sixthRow = sixthRow.concat(cardColor + "   |" + rst);
                    seventhRow = seventhRow.concat(cardColor + "___|" + rst);
                }

                firstRow = firstRow.concat("      ");
                secondRow = secondRow.concat("     ");
                thirdRow = thirdRow.concat("     ");
                fourthRow = fourthRow.concat("     ");
                fifthRow = fifthRow.concat("     ");
                sixthRow = sixthRow.concat("     ");
                seventhRow = seventhRow.concat("     ");
            }
        }
        System.out.println(ANSIFormatter.ANSI_CYAN + "BACK:" + ANSIFormatter.ANSI_RESET);
        System.out.println(firstRow);
        System.out.println(secondRow);
        System.out.println(thirdRow);
        System.out.println(fourthRow);
        System.out.println(fifthRow);
        System.out.println(sixthRow);
        System.out.println(seventhRow);
        if(cardsToPrint.size()==6){
            System.out.println(ANSIFormatter.ANSI_YELLOW+"     RESOURCE DECK                 GOLD DECK"+rst);
        }
    }

    /**
     * This method is used to print the cards a Player is keeping in is hands
     * @param playerDeck is the collection of cards the Player has in his hands
     */
    public void printHand(PlayableCard[] playerDeck) {
        System.out.println("The card you've got are:");
        List<PlayableCard> cards = new ArrayList<>(Arrays.asList(playerDeck));
        printPlayableCards(cards);
    }

    /**
     * This method prints the common Objective Cards and the personal Objective card of the player
     * @param objectiveCards are the 3 Objective cards to be printed
     */
    public void printObjectiveCard(List<ObjectiveCard> objectiveCards) {
        String firstRow = "";
        String secondRow = "";
        String thirdRow = "";
        String fourthRow = "";
        String fifthRow = "";
        String sixthRow = "";

        String indexString="";
        int index=0;

        for(ObjectiveCard objCard:objectiveCards){
            if(objectiveCards.size()==2){ //siamo nel caso della scelta iniziale
                indexString=indexString.concat("        ("+index+")        ");
                index++;
            }
            if(objCard.getId()==87){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|          "+ANSIFormatter.ANSI_RED+"███"+rst+"    |");
                fourthRow=fourthRow.concat("|       "+ANSIFormatter.ANSI_RED+"███"+rst+"       |");
                fifthRow=fifthRow.concat("|    "+ANSIFormatter.ANSI_RED+"███"+rst+"          |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==88){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|    "+ANSIFormatter.ANSI_GREEN+"███"+rst+"          |");
                fourthRow=fourthRow.concat("|       "+ANSIFormatter.ANSI_GREEN+"███"+rst+"       |");
                fifthRow=fifthRow.concat("|          "+ANSIFormatter.ANSI_GREEN+"███"+rst+"    |");
                sixthRow=sixthRow.concat("|_________________|"); //19
            }else if(objCard.getId()==89){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|          "+ANSIFormatter.ANSI_BLUE+"███"+rst+"    |");
                fourthRow=fourthRow.concat("|       "+ANSIFormatter.ANSI_BLUE+"███"+rst+"       |");
                fifthRow=fifthRow.concat("|    "+ANSIFormatter.ANSI_BLUE+"███"+rst+"          |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==90){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|    "+ANSIFormatter.ANSI_PURPLE+"███"+rst+"          |");
                fourthRow=fourthRow.concat("|       "+ANSIFormatter.ANSI_PURPLE+"███"+rst+"       |");
                fifthRow=fifthRow.concat("|          "+ANSIFormatter.ANSI_PURPLE+"███"+rst+"    |");
                sixthRow=sixthRow.concat("|_________________|"); //19
            }else if(objCard.getId()==91){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|      "+ANSIFormatter.ANSI_RED+"███"+rst+"        |");
                fourthRow=fourthRow.concat("|      "+ANSIFormatter.ANSI_RED+"███"+rst+"        |");
                fifthRow=fifthRow.concat("|        "+ANSIFormatter.ANSI_GREEN+"███"+rst+"      |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==92){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|        "+ANSIFormatter.ANSI_GREEN+"███"+rst+"      |");
                fourthRow=fourthRow.concat("|        "+ANSIFormatter.ANSI_GREEN+"███"+rst+"      |");
                fifthRow=fifthRow.concat("|      "+ANSIFormatter.ANSI_PURPLE+"███"+rst+"        |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==93){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|        "+ANSIFormatter.ANSI_RED+"███"+rst+"      |");
                fourthRow=fourthRow.concat("|      "+ANSIFormatter.ANSI_BLUE+"███"+rst+"        |");
                fifthRow=fifthRow.concat("|      "+ANSIFormatter.ANSI_BLUE+"███"+rst+"        |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==94){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"       |");
                thirdRow=thirdRow.concat("|      "+ANSIFormatter.ANSI_BLUE+"███"+rst+"        |");
                fourthRow=fourthRow.concat("|        "+ANSIFormatter.ANSI_PURPLE+"███"+rst+"      |");
                fifthRow=fifthRow.concat("|        "+ANSIFormatter.ANSI_PURPLE+"███"+rst+"      |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==95){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|     F           |");
                fourthRow=fourthRow.concat("|          -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+" |");
                fifthRow=fifthRow.concat("|  F     F        |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==96){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|     N           |");
                fourthRow=fourthRow.concat("|          -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+" |");
                fifthRow=fifthRow.concat("|  N     N        |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==97){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|     A           |");
                fourthRow=fourthRow.concat("|          -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+" |");
                fifthRow=fifthRow.concat("|  A     A        |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==98){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|     I           |");
                fourthRow=fourthRow.concat("|          -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+" |");
                fifthRow=fifthRow.concat("|  I     I        |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==99){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|     J           |");
                fourthRow=fourthRow.concat("|          -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+" |");
                fifthRow=fifthRow.concat("|  Q     S        |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==100){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|                 |");
                fourthRow=fourthRow.concat("| 2*SCROLL -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+" |");
                fifthRow=fifthRow.concat("|                 |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==101){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|                 |");
                fourthRow=fourthRow.concat("|   2*JAR -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+"  |");
                fifthRow=fifthRow.concat("|                 |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==102){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|                 |");
                thirdRow=thirdRow.concat("|                 |");
                fourthRow=fourthRow.concat("|  2*QUILL -> "+ANSIFormatter.ANSI_YELLOW+objCard.getCardPoints()+"pt"+rst+" |");
                fifthRow=fifthRow.concat("|                 |");
                sixthRow=sixthRow.concat("|_________________|");
            }
            indexString= indexString.concat("     ");
            firstRow = firstRow.concat("      ");
            secondRow = secondRow.concat("     ");
            thirdRow = thirdRow.concat("     ");
            fourthRow = fourthRow.concat("     ");
            fifthRow = fifthRow.concat("     ");
            sixthRow = sixthRow.concat("     ");
        }
        if(!indexString.isBlank()){
            System.out.println(ANSIFormatter.ANSI_YELLOW + indexString + ANSIFormatter.ANSI_RESET);
        }
        System.out.println(firstRow);
        System.out.println(secondRow);
        System.out.println(thirdRow);
        System.out.println(fourthRow);
        System.out.println(fifthRow);
        System.out.println(sixthRow);
        if(objectiveCards.size()==3){
            System.out.println(ANSIFormatter.ANSI_YELLOW+"   (PERSONAL OBJ)                   (COMMON OBJECTIVES)"+rst);
        }
    }


    /**
     * This method prints all the resources and objects that are "visible" on the player's board
     * @param board is the player's board
     */
    public void printAvailableResources(Board board) {
        System.out.println("These are the resources and objects you have available on the board: ");
        for (AngleType a : board.getNumResources().keySet()) {
            if (board.getNumResources().get(a) != 0) {
                System.out.println("- " + a + ": " + board.getNumResources().get(a));
            }
        }
    }

    /**
     * This method is used to print the positions where the player can play a card
     * @param board is the player's board
     */
    public void printPlayablePositions(Board board){
        System.out.println("These are the coordinates where you can play a card: ");
        for(Coordinates c: board.getPlayablePositions()){
            System.out.println("("+c.getX()+","+c.getY()+")  ");
        }
    }

    /**
     * This method is used to print the table of the selected board
     * @param board is the player' selected board
     */
    public void printTable(Board board) {
        String firstRow="";
        String secondRow="";
        String thirdRow="";
        String fourthRow="";
        String fifthRow="";
        String sixthRow="";
        String seventhRow="";
        String pointsString="";
        boolean firstCardToPrintFound=false;
        String cardColor;







        printAvailableResources(board);
        printPlayablePositions(board);
    }

    /**
     * This method prints an ordered scoreBoard of the game
     * @param players are the players in the game
     */
    public void printScoreBoard(List<Player> players) {
        players.sort(Comparator.comparingInt(Player::getPoints));
        Collections.reverse(players);
        for (int i = 0;i < players.size(); i++) {
            System.out.println((i + 1) + "_ " + players.get(i).getNickname() + " scored " + players.get(i).getPoints() + " points!");
        }
    }

    /**
     * This method prints the drawable cards on the play table
     * @param goldDeck is the deck containing the gold cards
     * @param resourceDeck is the deck containing the resource cards
     * @param visibileCards are the 4 cards faced-up on the table
     */
    public void printDrawableCards(PlayableDeck goldDeck, PlayableDeck resourceDeck, List<PlayableCard> visibileCards) {
        System.out.println(ANSIFormatter.ANSI_GREEN+"These are the drawable cards."+rst);
        List<PlayableCard> tmp= new ArrayList<>();
        tmp.add(resourceDeck.checkFirstCard());
        tmp.add(goldDeck.checkFirstCard());
        tmp.addAll(visibileCards);

        printPlayableCards(tmp);

    }
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * This method is used to correctly print the menu to the user and capture his input
     * @param isPlaying is true if the player is currently the one in turn
     * @param console is the BufferedReader of the player
     * @return the index of the action the player wants to perform, -1 if anything was inserted by the user or if there was a bad input
     */
    public int showMenuAndWaitForSelection(boolean isPlaying, BufferedReader console) {
        String value = "";
        int intValue = -1;
        gameTurn(isPlaying);
        try {
            if (console.ready()) { //ready restituisce true se c'è una riga da leggere
                value = console.readLine();
                if (value.equalsIgnoreCase("menu")) {
                    printMenu(isPlaying);
                } else {
                    intValue = Integer.parseInt(value);
                    if((intValue>=0)&&(intValue<=6)||(isPlaying&&intValue==7)) {
                        return intValue;
                    }else {
                        System.out.println("Please, insert one of the possible values.");
                        intValue = -1;
                    }
                }
            }else{ //se la console non è ready
                    intValue = -1;
            }
            } catch(IOException e){
                System.out.println("Error while reading the input, try again");
            }catch(NumberFormatException e){
                System.out.println("Please, insert one of the possible values.");
            }
            return intValue;
        }
}
