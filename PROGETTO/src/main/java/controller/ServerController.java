package controller;

import org.model.Game;
import org.model.Player;

import java.util.*;
public class ServerController {
    private static int firstAvailableId;
    private Map<Integer, Game> allGames;
    private List<Player> allPlayers;

    public ServerController() {
        allPlayers=new ArrayList();
        allGames=new HashMap<>();
    }

    public static int getFirstAvailableId(){
        return firstAvailableId;
    }

    public boolean isNicknameAvailable(String nickname){
        Player temp=null;
        for(int i=0; i<allPlayers.size();i++){
            temp=allPlayers.get(i);
            if(temp.getNickname().equals(nickname)){
                return false;
            }
        }
        return true;
    }
}
