package CODEX.distributed;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.view.TUI.ANSIFormatter;
import CODEX.view.TUI.InterfaceTUI;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientLauncher {
    public static void main(String[] args) {
        boolean selected=false;
        int selection=0;
        Scanner sc=new Scanner(System.in);

        System.out.println(ANSIFormatter.ANSI_BLUE+"Choose a connection protocol and an interface:"+ANSIFormatter.ANSI_RESET+": \n-Type 1 for RMI+TUI\n-Type 2 for RMI+GUI\n-Type 3 for TCP+TUI\n-Type 4 for TCP+GUI");
        do{
            try {
                selection = sc.nextInt();
            if(selection>=1&&selection<=2){
                selected=true;
                try {
                    RMIClient rmiClient = new RMIClient();
                    rmiClient.setSelectedView(selection);
                    rmiClient.SRMIInterfaceFromRegistry();
                    InterfaceTUI.clearScreen();
                    //connect the client
                    rmiClient.waitingRoom(); //selection of nicknames and lobby functionalities
                }catch (RemoteException | NotBoundException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    System.exit(-1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //generate client RMI
            }else if(selection>=3&&selection<=4){
                selected=true;
                try {
                    ClientSCK clientSCK = new ClientSCK(); //per TCP dobbiamo solo chiedere porte disponibili lato client
                    if(selection==3){
                        clientSCK.setSelectedView(1);
                    }else{
                        clientSCK.setSelectedView(2);
                    }
                    clientSCK.waitingRoom();
                    //connect the client
                }catch(IOException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    System.exit(-1);
                } //non so se vadano gestite in altra maniera, potremmo chiudere tutto e riprovare
            }else{
                System.out.println("Please type 1 (RMI+TUI), 2 (RMI+GUI), 3 (TCP+TUI), 4 (TCP+GUI)");
            }
            }catch (InputMismatchException e){
                sc.next();
                System.out.println("Please type 1 (RMI+TUI), 2 (RMI+GUI), 3 (TCP+TUI), 4 (TCP+GUI)");
            }
        }while(!selected);
    }
}
