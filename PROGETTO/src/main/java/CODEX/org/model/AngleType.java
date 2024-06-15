package CODEX.org.model;
import java.io.Serializable;


/**
 * This enum is used to keep trace of what element is present in the corners of a Playable card
 * and in its back-center.
 * These elements need to be counted for points and for objectives.
 *
 */
public enum AngleType implements Serializable {
        /**
         * This element represents the red mushroom in the game
         */
        FUNGI,


        /**
         * This element represents the green leaf in the game
         */
        NATURE,


        /**
         * This element represents the purple butterfly in the game
         */
        INSECT,


        /**
         * This element represents the blue wolf in the game
         */
        ANIMAL,


        /**
         * This element represents the gold feather in the game
         */
        FEATHER,


        /**
         * This element represents the gold jar mushroom in the game
         */
        JAR,


        /**
         * This element represents the gold manuscript in the game
         */
        SCROLL,


        /**
         * This element represents an empty angle in the game
         */
        NO_RESOURCE,


        /**
         * This element represents the absent angle in the game
         */
        ABSENT

}
