package controller;

import org.model.Game;
import java.util.*;
public class ServerController {
    private static int firstAvailableId;
    private Map<Integer, Game> allGames;
    public static int getFirstAvailableId(){
        return firstAvailableId;
    }
}
