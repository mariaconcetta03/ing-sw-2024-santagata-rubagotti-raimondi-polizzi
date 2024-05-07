package view.TUI;

import utils.Observable;

import java.io.Serializable;
import java.util.*;

public class InterfaceTUI implements Serializable { //I don't think it has to extend 
    Scanner sc=new Scanner(System.in);

    public void setClient(){
        //choose interface
        //chose nickname
    }
    public void askNickname(){
        sc=new Scanner(System.in);
        System.out.println("Welcome to "+ANSIFormatter.ANSI_GREEN+"C"+ANSIFormatter.ANSI_CYAN+"O"+ANSIFormatter.ANSI_RED+
                "D"+ANSIFormatter.ANSI_YELLOW+"E"+ANSIFormatter.ANSI_PURPLE+"X"+ANSIFormatter.ANSI_RESET+" NATURALIS");

    }

    public void askNumPlayers(){
        sc=new Scanner(System.in);

    }

    public void askCardToDraw(){
        sc=new Scanner(System.in);

    }

    public void askCardToPlay(){

    }

    public void askCardOrientation(){}


}
