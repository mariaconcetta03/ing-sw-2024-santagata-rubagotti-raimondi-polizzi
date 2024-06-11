package CODEX.org.model;
import java.io.Serializable;


/**
 * This enum is used to keep trace of what element is present in the corners of a Playable card
 * and in its back-center.
 * These elements need to be counted for points and for objectives.
 */
public enum AngleType implements Serializable {
        FUNGI,
        NATURE,
        INSECT,
        ANIMAL,
        FEATHER,
        JAR,
        SCROLL,
        NO_RESOURCE,
        ABSENT
}
