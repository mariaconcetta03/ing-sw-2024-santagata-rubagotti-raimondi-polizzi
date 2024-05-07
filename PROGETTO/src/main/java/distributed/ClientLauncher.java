package distributed;
import distributed.RMI.RMIClient;
import distributed.Socket.ClientSCK;
import view.TUI.ANSIFormatter;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

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
                    //connect the client
                    rmiClient.waitingRoom(); //selection of nicknames and lobby functionalities
                }catch (RemoteException | NotBoundException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    System.exit(-1);
                }
                //generate client RMI
            }else if(selection>=3&&selection<=4){
                selected=true;
                try {
                    ClientSCK clientSCK = new ClientSCK("address", 2); //li dobbiamo chiedere ?
                    if(selection==3){
                        clientSCK.setSelectedView(1);
                    }else{
                        clientSCK.setSelectedView(2);
                    }
                    //connect the client
                }catch(IOException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    System.exit(-1);
                } //non so se vadano gestite in altra maniera, potremmo chiudere tutto e riprovare
            }else{
                System.out.println("Please type 1 (TCP+TUI), 2 (TCP+GUI), 3 (RMI+TUI), 4 (RMI+GUI)");
            }
            }catch (InputMismatchException e){
                sc.next();
                System.out.println("Please type 1 (TCP+TUI), 2 (TCP+GUI), 3 (RMI+TUI), 4 (RMI+GUI)");
            }
        }while(!selected);
    }
}
