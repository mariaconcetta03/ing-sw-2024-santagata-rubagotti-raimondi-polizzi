package CODEX.org.model;

import java.io.Serializable;
import java.util.*;


/**
 * This class represents a board for a player. This is a space where the player can place his cards during the
 * game. It has dynamic dimensions, depending on how many players there are in a game.
 */
public class Board implements Serializable {

    private Coordinates upperLimit;
    private Coordinates bottomLimit;
    private Coordinates leftLimit;
    private Coordinates rightLimit;
    private int boardDimensions;

    private static final int numCarte = 80;
    private int playOrder = 0;
    private Set<Coordinates> playablePositions;
    private Set<Coordinates> unPlayablePositions;
    private Map<Coordinates, AngleType> playedCards; // useful for objective cards
    private Map<AngleType, Integer> numResources;
    private PlayableCard table[][];
    private Player player; // board's owner


    /**
     * Class constructor
     *
     * @param player board's owner
     */
    public Board(Player player) {
        // main initializations
        this.boardDimensions = 0;
        this.playablePositions = new HashSet<>();
        this.unPlayablePositions = new HashSet<>();
        this.playedCards = new HashMap<>();
        this.table = null;
        this.player = player;

        // initialization of the resource Map
        numResources = new HashMap<>();
        numResources.put(AngleType.FUNGI, 0);
        numResources.put(AngleType.INSECT, 0);
        numResources.put(AngleType.ANIMAL, 0);
        numResources.put(AngleType.NATURE, 0);
        numResources.put(AngleType.JAR, 0);
        numResources.put(AngleType.SCROLL, 0);
        numResources.put(AngleType.FEATHER, 0);
        numResources.put(AngleType.NO_RESOURCE, 0);
        numResources.put(AngleType.ABSENT, 0);
    }


    /**
     * This method creates and initialises the table of the specific Player, after the number of players is set
     *
     * @param nPlayers is the number of players that are playing the Game
     */
    public void setBoard(int nPlayers) {
        this.boardDimensions = 1 + 2 * (numCarte / nPlayers);
        this.table = new PlayableCard[boardDimensions][boardDimensions];
        for (int i = 0; i < boardDimensions; i++) {
            for (int j = 0; j < boardDimensions; j++) {
                table[i][j] = null;
            }
        }
    }


    /**
     * This method places the first card for a player in the middle of the table (boardDimension/2) and adds
     * related resources
     *
     * @param baseCard is the base card the Player has placed
     */
    public void placeBaseCard(PlayableCard baseCard) {
        this.table[boardDimensions / 2][boardDimensions / 2] = baseCard;
        baseCard.setPosition(new Coordinates(boardDimensions / 2, boardDimensions / 2));
        unPlayablePositions.add(new Coordinates(boardDimensions / 2, boardDimensions / 2));

        if (baseCard.getOrientation()) {
            // adding the new card resources if played on the front (only 4 angles)
            numResources.put(baseCard.get_front_down_left(), numResources.get(baseCard.get_front_down_left()) + 1);
            numResources.put(baseCard.get_front_down_right(), numResources.get(baseCard.get_front_down_right()) + 1);
            numResources.put(baseCard.get_front_up_left(), numResources.get(baseCard.get_front_up_left()) + 1);
            numResources.put(baseCard.get_front_up_right(), numResources.get(baseCard.get_front_up_right()) + 1);
        } else {
            // adding the new card resources if played on the back side (4 angles and central resources)
            for (AngleType t : baseCard.getCentralResources()) {
                numResources.put(t, numResources.get(t) + 1);
            }
            numResources.put(baseCard.get_back_down_left(), numResources.get(baseCard.get_back_down_left()) + 1);
            numResources.put(baseCard.get_back_down_right(), numResources.get(baseCard.get_back_down_right()) + 1);
            numResources.put(baseCard.get_back_up_left(), numResources.get(baseCard.get_back_up_left()) + 1);
            numResources.put(baseCard.get_back_up_right(), numResources.get(baseCard.get_back_up_right()) + 1);
        }

        // the base card is always the first card played
        baseCard.setPlayOrder(-1);

        upperLimit = new Coordinates(boardDimensions / 2, boardDimensions / 2);
        bottomLimit = new Coordinates(boardDimensions / 2, boardDimensions / 2);
        leftLimit = new Coordinates(boardDimensions / 2, boardDimensions / 2);
        rightLimit = new Coordinates(boardDimensions / 2, boardDimensions / 2);

        updateUnplayablePositions(baseCard);
        updatePlayablePositions(baseCard);
    }


    /**
     * Places a card in the table and updates playable/unplayable positions
     *
     * @param card     is the card (gold/resource) the Player wants to play
     * @param position is the place where the Player wants to play the card
     * @return true if the card can be placed, false if the card can't be placed in that position
     */
    public boolean placeCard(PlayableCard card, Coordinates position) {
        int coveredAngles = 0;
        if (playablePositions.contains(position) && enoughResources(card)) {
            // all the angles of the adjacent cards we could cover with the one we are placing
            AngleType upLeft = null;
            AngleType upRight = null;
            AngleType downLeft = null;
            AngleType downRight = null;

            // inserting the card
            playablePositions.remove(position);
            unPlayablePositions.add(position);
            card.setPosition(position);
            this.table[position.getX()][position.getY()] = card;
            playedCards.put(position, card.getCentralResources().get(0));
            // "0" because only the base card (here not considered) has more than one resource in the center

            // adding the new card resources
            if (card.getOrientation()) {
                // adds the front angle resources if played on the front side
                numResources.put(card.get_front_down_left(), numResources.get(card.get_front_down_left()) + 1);
                numResources.put(card.get_front_down_right(), numResources.get(card.get_front_down_right()) + 1);
                numResources.put(card.get_front_up_left(), numResources.get(card.get_front_up_left()) + 1);
                numResources.put(card.get_front_up_right(), numResources.get(card.get_front_up_right()) + 1);
            } else {
                // adds the back resource if played on the back side
                numResources.put(card.getCentralResources().get(0), numResources.get(card.getCentralResources().get(0)) + 1);
            }

            // checks adjacent card's angles we might be covering with the new card (checking also the side where
            // the card temp we are checking is played)
            PlayableCard temp;
            if (table[position.getX() - 1][position.getY() + 1] != null) {
                temp = table[position.getX() - 1][position.getY() + 1];
                if (temp.getOrientation()) {
                    upLeft = temp.get_front_down_right();
                } else {
                    upLeft = temp.get_back_down_right();
                }
            }

            if (table[position.getX() + 1][position.getY() + 1] != null) {
                temp = table[position.getX() + 1][position.getY() + 1];
                if (temp.getOrientation()) {
                    upRight = temp.get_front_down_left();
                } else {
                    upRight = temp.get_back_down_left();
                }
            }

            if (table[position.getX() - 1][position.getY() - 1] != null) {
                temp = table[position.getX() - 1][position.getY() - 1];
                if (temp.getOrientation()) {
                    downLeft = temp.get_front_up_right();
                } else {
                    downLeft = temp.get_back_up_right();
                }
            }

            if (table[position.getX() + 1][position.getY() - 1] != null) {
                temp = table[position.getX() + 1][position.getY() - 1];
                if (temp.getOrientation()) {
                    downRight = temp.get_front_up_left();
                } else {
                    downRight = temp.get_back_up_left();
                }
            }

            // subtracting the resource we are loosing when placing the new card
            if (upLeft != null) {
                numResources.put(upLeft, numResources.get(upLeft) - 1);
                coveredAngles++;
            }
            if (upRight != null) {
                numResources.put(upRight, numResources.get(upRight) - 1);
                coveredAngles++;
            }
            if (downLeft != null) {
                numResources.put(downLeft, numResources.get(downLeft) - 1);
                coveredAngles++;
            }
            if (downRight != null) {
                numResources.put(downRight, numResources.get(downRight) - 1);
                coveredAngles++;
            }

            // setting the index the Player played the card
            card.setPlayOrder(playOrder);
            playOrder++;

            // updating the extreme positions
            if ((position.getY() > upperLimit.getY()) || ((position.getY() == upperLimit.getY()) && position.getX() < upperLimit.getX())) {
                upperLimit = position;
            }
            if (position.getY() < bottomLimit.getY()) {
                bottomLimit = position;
            }
            if (position.getX() < leftLimit.getX()) {
                leftLimit = position;
            }
            if (position.getX() > rightLimit.getX()) {
                rightLimit = position;
            }

            // updating the playable and unplayable positions
            updateUnplayablePositions(card);
            updatePlayablePositions(card);
            if (card.getOrientation()) { //only if played on the front side
                player.addPoints(cardPoints(card, coveredAngles));
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * When passed a resourceCard, the method gives the points scored by that specific card
     * When passed a goldCard, the method checks the "scoring condition" (angles coverage/number of objects)
     *
     * @param card          is the card the Player has just placed
     * @param coveredAngles is the number of angles covered by the PlayableCard just placed
     * @return points are the points actually scored thanks to card
     */
    public int cardPoints(PlayableCard card, int coveredAngles) {
        // if the card gives you points in any case
        int points = card.getPoints();

        // checking
        if (card.isCoverAngleToReceivePoints()) {
            points = coveredAngles * points;
        } else if (card.isJarToReceivePoints()) {
            points = points * numResources.get(AngleType.JAR);
        } else if (card.isFeatherToReceivePoints()) {
            points = points * numResources.get(AngleType.FEATHER);
        } else if (card.isScrollToReceivePoints()) {
            points = points * numResources.get(AngleType.SCROLL);
        }
        return points;
    }


    /**
     * This method checks if the Player has enough resources to place the card
     *
     * @param card is the card the Player has just placed
     * @return boolean that indicates if the Player has enough resources to play the specific card
     */
    public boolean enoughResources(PlayableCard card) {
        if (card.getOrientation()) {
            // if played on the front
            if ((card.getNeededResources() != null)) {
                for (AngleType t : card.getNeededResources().keySet()) {
                    if (!numResources.containsKey(t) || (numResources.get(t) < card.getNeededResources().get(t))) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        } else {
            // if played on the back no need to check for resources
            return true;
        }
    }


    /**
     * Adding all the positions where the angle is ABSENT (players can't use that angle)
     *
     * @param card is the card for which we are updating the unplayable positions
     */
    public void updateUnplayablePositions(PlayableCard card) {
        if (card.getOrientation()) {
            if (card.get_front_up_right() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findUpRight());
                playablePositions.remove(card.getPosition().findUpRight());
            }
            if (card.get_front_up_left() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findUpLeft());
                playablePositions.remove(card.getPosition().findUpLeft());
            }
            if (card.get_front_down_right() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findDownRight());
                playablePositions.remove(card.getPosition().findDownRight());
            }
            if (card.get_front_down_left() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findDownLeft());
                playablePositions.remove(card.getPosition().findDownLeft());
            }
        } else {
            if (card.get_back_up_right() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findUpRight());
                playablePositions.remove(card.getPosition().findUpRight());
            }
            if (card.get_back_up_left() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findUpLeft());
                playablePositions.remove(card.getPosition().findUpLeft());
            }
            if (card.get_back_down_right() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findDownRight());
                playablePositions.remove(card.getPosition().findDownRight());
            }
            if (card.get_back_down_left() == AngleType.ABSENT) {
                unPlayablePositions.add(card.getPosition().findDownLeft());
                playablePositions.remove(card.getPosition().findDownLeft());
            }
        }
    }


    /**
     * Adding all the positions where the player can place a card in the future
     *
     * @param card is the card for which we are updating the playable positions
     */
    public void updatePlayablePositions(PlayableCard card) {
        // if it is not present an unplayable position, it adds all the corners != ABSENT
        if (card.getOrientation()) {
            if (card.get_front_up_right() != AngleType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findUpRight())) {
                playablePositions.add(card.getPosition().findUpRight());
            }
            if (card.get_front_up_left() != AngleType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findUpLeft())) {
                playablePositions.add(card.getPosition().findUpLeft());
            }
            if (card.get_front_down_right() != AngleType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findDownRight())) {
                playablePositions.add(card.getPosition().findDownRight());
            }
            if (card.get_front_down_left() != AngleType.ABSENT &&
                    !unPlayablePositions.contains(card.getPosition().findDownLeft())) {
                playablePositions.add(card.getPosition().findDownLeft());
            }
        } else {
            // if played on the back side, all the angles are present
            if (!unPlayablePositions.contains(card.getPosition().findUpRight())) {
                playablePositions.add(card.getPosition().findUpRight());
            }
            if (!unPlayablePositions.contains(card.getPosition().findUpLeft())) {
                playablePositions.add(card.getPosition().findUpLeft());
            }
            if (!unPlayablePositions.contains(card.getPosition().findDownRight())) {
                playablePositions.add(card.getPosition().findDownRight());
            }
            if (!unPlayablePositions.contains(card.getPosition().findDownLeft())) {
                playablePositions.add(card.getPosition().findDownLeft());
            }
        }
    }


    /**
     * Getter method
     *
     * @return a Set containing the positions where it's possible to play a card
     */
    public Set<Coordinates> getPlayablePositions() {
        return playablePositions;
    }


    /**
     * Getter method
     *
     * @return a Set containing the positions where it's impossible to play a card
     */
    public Set<Coordinates> getUnPlayablePositions() {
        return unPlayablePositions;
    }


    /**
     * Getter method
     *
     * @return the board dimension (calculated by the number of players)
     */
    public int getBoardDimensions() {
        return boardDimensions;
    }


    /**
     * Getter method
     *
     * @return the num of resources
     */
    public Map<AngleType, Integer> getNumResources() {
        return numResources;
    }


    /**
     * Getter method
     *
     * @return the table with the cards played
     */
    public PlayableCard[][] getTable() {
        return table;
    }


    /**
     * Getter method
     *
     * @return a Map containing all the cards played by the Player with their Coordinates
     */
    public Map<Coordinates, AngleType> getPlayedCards() {
        return playedCards;
    }


    /**
     * Getter method
     *
     * @return the upper limit of the board
     */
    public Coordinates getUpperLimit() {
        return upperLimit;
    }


    /**
     * Getter method
     *
     * @return the bottom limit of the board
     */
    public Coordinates getBottomLimit() {
        return bottomLimit;
    }


    /**
     * Getter method
     *
     * @return the left limit of the board
     */
    public Coordinates getLeftLimit() {
        return leftLimit;
    }


    /**
     * Getter method
     *
     * @return the right limit of the board
     */
    public Coordinates getRightLimit() {
        return rightLimit;
    }


    /**
     * Getter method
     *
     * @return the right player who owns this Board
     */
    public Player getPlayer() {
        return player;
    }
}