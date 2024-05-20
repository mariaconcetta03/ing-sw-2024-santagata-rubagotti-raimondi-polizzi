package CODEX.view.TUI;

import CODEX.org.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class InterfaceTUI implements Serializable { //I don't think it has to extend 

    /*System.out.println("Welcome to "+ANSIFormatter.ANSI_GREEN+"C"+ANSIFormatter.ANSI_CYAN+"O"+ANSIFormatter.ANSI_RED+
         "D"+ANSIFormatter.ANSI_YELLOW+"E"+ANSIFormatter.ANSI_PURPLE+"X"+ANSIFormatter.ANSI_RESET+" NATURALIS");
*/
    private Map<AngleType, String> emojis;
    private Map<AngleType, String> abbreviations;
    private Map<AngleType, String> cardColors;
    String rst = ANSIFormatter.ANSI_RESET;

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
     * This method prints a menu containing the action the player can perform
     *
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
     *
     * @param sc     is the player' Scanner
     * @param inTurn is true if the player is the one playing, false otherwise
     * @return the index of the selected action
     */
    public int askAction(Scanner sc, boolean inTurn) {
        boolean ok = false;
        int value = 0;
        while (!ok) {
            try {
                value = sc.nextInt();
                sc.nextLine();
                //lasciando lo scanner, wrappedObserver aspetta una risposta prima di inviare
                //update agli altri client (non ha ancora esaurito la chiamata a funzione
                if ((inTurn) && (value == 7)) {
                    ok = true;
                } else if ((value < 0) || (value > 6)) {
                    System.out.println("Please, insert one of the possible values. ");
                } else {
                    ok = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please type a number. ");
                sc.next();
            }
        }
        return value;
    }

    /**
     * This method let the player select how to play his baseCard
     *
     * @param sc       is the player' Scanner
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

    public ObjectiveCard askChoosePersonalObjective(Scanner sc, List<ObjectiveCard> objectiveCards) {
        int choice = -1;
        while (true) {
            System.out.println("These are the 2 objective card you can choose between:");
            printObjectiveCard(objectiveCards);
            try {
                choice = sc.nextInt();
                if (choice == 1) {
                    return objectiveCards.get(0);
                } else if (choice == 2) {
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
            if (personalPlayer.getPlayerDeck()[cardIndex] != null) {
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
                secondRow = secondRow.concat(cardColor+"|  |               |  |"+rst);
                thirdRow = thirdRow.concat(cardColor+"|__|               |__|"+rst);

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
                fifthRow = fifthRow.concat(cardColor+"|__                 __|"+rst);
                sixthRow = sixthRow.concat(cardColor+"|  |               |  |"+rst);
                seventhRow = seventhRow.concat(cardColor+"|__|_______________|__|"+rst);
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
    }

    /**
     * This method is used to print the cards a Player is keeping in is hands
     *
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
                indexString=indexString.concat("          ("+index+")          ");
                index++;
            }
            if(objCard.getId()==87){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+objCard.getCardPoints()+"pt       |");
                thirdRow=thirdRow.concat("|          "+ANSIFormatter.ANSI_RED+"██"+rst+"     |");
                fourthRow=fourthRow.concat("|        "+ANSIFormatter.ANSI_RED+"██"+rst+"       |");
                fifthRow=fifthRow.concat("|      "+ANSIFormatter.ANSI_RED+"██"+rst+"         |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==88){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+objCard.getCardPoints()+"pt       |");
                thirdRow=thirdRow.concat("|     "+ANSIFormatter.ANSI_GREEN+"██"+rst+"          |");
                fourthRow=fourthRow.concat("|        "+ANSIFormatter.ANSI_GREEN+"██"+rst+"       |");
                fifthRow=fifthRow.concat("|          "+ANSIFormatter.ANSI_GREEN+"██"+rst+"     |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==89){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+objCard.getCardPoints()+"pt       |");
                thirdRow=thirdRow.concat("|          "+ANSIFormatter.ANSI_BLUE+"██"+rst+"     |");
                fourthRow=fourthRow.concat("|        "+ANSIFormatter.ANSI_BLUE+"██"+rst+"       |");
                fifthRow=fifthRow.concat("|      "+ANSIFormatter.ANSI_BLUE+"██"+rst+"         |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==90){
                firstRow=firstRow.concat(" _________________");
                secondRow=secondRow.concat("|       "+objCard.getCardPoints()+"pt       |");
                thirdRow=thirdRow.concat("|      "+ANSIFormatter.ANSI_PURPLE+"██"+rst+"         |");
                fourthRow=fourthRow.concat("|       "+ANSIFormatter.ANSI_PURPLE+"██"+rst+"        |");
                fifthRow=fifthRow.concat("|          "+ANSIFormatter.ANSI_PURPLE+"██"+rst+"     |");
                sixthRow=sixthRow.concat("|_________________|");
            }else if(objCard.getId()==91){

            }else if(objCard.getId()==92){

            }else if(objCard.getId()==93){

            }else if(objCard.getId()==94){

            }else if(objCard.getId()==95){

            }else if(objCard.getId()==96){

            }else if(objCard.getId()==97){

            }else if(objCard.getId()==98){

            }else if(objCard.getId()==99){

            }else if(objCard.getId()==100){

            }else if(objCard.getId()==101){

            }else if(objCard.getId()==102){

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
    }

    /*boolean personalObjective = true;
        System.out.println("These are the objective cards: ");
        for (ObjectiveCard objCard : objectiveCards) {
            if (personalObjective) {
                System.out.println("This is your PERSONAL objective card.");
            }
            System.out.print(objCard.getId() + " ");
            if (personalObjective) {
                System.out.println("");
                personalObjective = false;
            }
        }
        System.out.println("");
     */

    /**
     * This method prints all the resources and objects that are "visible" on the player's board
     *
     * @param board
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
     * This method is used to print the table of the selected board
     *
     * @param board is the player' selected board
     */
    public void printTable(Board board) {
        for (int i = 0; i < board.getBoardDimensions(); i++) {
            for (int j = 0; j < board.getBoardDimensions(); j++) {
                if (board.getTable()[i][j] == null) {
                    System.out.print("");
                } else {
                    System.out.print(board.getTable()[i][j].getId() + " ");
                }
            }
        }
    }

    /**
     * This method prints an ordered scoreBoard of the game
     *
     * @param players are the players in the game
     */
    public void printScoreBoard(List<Player> players) {
        players.sort(Comparator.comparing(Player::getPoints));
        for (int i = 0; i < players.size(); i++) {
            System.out.println((i + 1) + "_ " + players.get(i).getNickname() + " scored " + players.get(i).getPoints() + " points!");
        }
    }

    public void printDrawableCards(PlayableDeck goldDeck, PlayableDeck resourceDeck, List<PlayableCard> visibileCards) {
        System.out.println("This is the card on top of the goldDeck: " + goldDeck.checkFirstCard().getId());
        System.out.println("This is the card on top of the resourceDeck: " + resourceDeck.checkFirstCard().getId());
        for (PlayableCard card : visibileCards) {
            System.out.print(card.getId() + " ");
        }
        System.out.println();
    }

    public void askNumPlayers() {

    }


    public void askCardToPlay() {

    }


    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public int showMenuAndWaitForSelection(boolean isPlaying, BufferedReader console) {
        boolean ok = false;
        Integer value = 0;
        if (firstMenu) {
            System.out.println(firstMenu);
            System.out.println("These are the action you can perform");
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
            firstMenu = false;
        } else {
            if (previousIsPlayingAttribute != isPlaying) {
                System.out.println("attributo isPlaying cambiato");
                System.out.println("These are the action you can perform");
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
        }
        previousIsPlayingAttribute = isPlaying;

        try {
            if (console.ready()) { //ready restituisce true se c'è una riga da leggere
                value = Integer.parseInt(console.readLine());
                if (value != null) {
                    Integer intValue = value;
                    if ((isPlaying) && (intValue == 7)) {
                        System.out.println("value taken");
                    } else if ((intValue < 0) || (intValue > 6)) {
                        System.out.println("Please, insert one of the possible values. ");
                    } else {
                        System.out.println("Please type a number. ");
                        value = -1;
                    }
                } else {
                    value = -1;
                }
            } else { //se la console non è ready
                value = -1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return value;

    }
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
     */
