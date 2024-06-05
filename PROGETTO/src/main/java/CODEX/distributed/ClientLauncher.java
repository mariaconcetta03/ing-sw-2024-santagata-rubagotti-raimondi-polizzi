package CODEX.distributed;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.view.GUI.InterfaceGUI;
import CODEX.view.TUI.ANSIFormatter;
import CODEX.view.TUI.InterfaceTUI;

import java.io.IOException;
import java.net.Inet4Address;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientLauncher {
    public static void main(String[] args) {
        boolean selected=false;
        int selection=0;
        String serverAddress="";
        Scanner sc=new Scanner(System.in);

        System.out.println("Insert the Server's Ip address: ");
        serverAddress=sc.nextLine(); //@TODO se blank è localHost, non fa ancora nulla
        System.out.println(ANSIFormatter.ANSI_BLUE+"Choose a connection protocol and an interface:"+ANSIFormatter.ANSI_RESET+": \n-Type 1 for RMI+TUI\n-Type 2 for RMI+GUI\n-Type 3 for TCP+TUI\n-Type 4 for TCP+GUI");
        do{
            try {
                selection = sc.nextInt();
            if(selection==1){
                selected=true;
                try {
                    System.setProperty("java.rmi.server.hostname", Inet4Address.getLocalHost().getHostAddress()); //don't if it works also for @mcs
                    RMIClient rmiClient = new RMIClient();
                    rmiClient.getNetworkSettings().setSERVER_NAME(serverAddress); //setting the Server Address
                    rmiClient.setSelectedView(1); //TUI
                    rmiClient.SRMIInterfaceFromRegistry();
                    InterfaceTUI.clearScreen(); //@TODO NON SERVE A NIENTE?
                    //connect the client
                    rmiClient.waitingRoom(); //selection of nicknames and lobby functionalities
                }catch (RemoteException | NotBoundException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    System.exit(-1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //generate client RMI
            }else if(selection == 2){
                RMIClient rmiClient=null; //bisogna gestirlo con un'eccezione se dopo il try rimane null
                try {
                    rmiClient = new RMIClient();
                    rmiClient.setSelectedView(2); //GUI
                    try {
                        rmiClient.SRMIInterfaceFromRegistry();
                    } catch (NotBoundException e) {
                        throw new RuntimeException(e);
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                selected=true;
                String[] network = new String[1];
                network[0] = "RMI";
                InterfaceGUI.main(network, null, rmiClient);
            }else if(selection==3){
                selected=true;
                try {
                    ClientSCK clientSCK = new ClientSCK(); //per TCP dobbiamo solo chiedere porte disponibili lato client
                    clientSCK.setSelectedView(1); //TUI

                    clientSCK.waitingRoom();
                    //connect the client
                }catch(IOException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    System.exit(-1);
                } //non so se vadano gestite in altra maniera, potremmo chiudere tutto e riprovare
            } else if (selection==4) {
                ClientSCK clientSCK=null;
                try {
                    clientSCK = new ClientSCK();
                    clientSCK.setSelectedView(2); //GUI
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                selected = true;
                String[] network = new String[1];
                network[0] = "TCP";
                InterfaceGUI.main(network, clientSCK, null);
            }
            else{
                System.out.println("Please type 1 (RMI+TUI), 2 (RMI+GUI), 3 (TCP+TUI), 4 (TCP+GUI)");
            }
            }catch (InputMismatchException e){
                sc.next();
                System.out.println("Please type 1 (RMI+TUI), 2 (RMI+GUI), 3 (TCP+TUI), 4 (TCP+GUI)");
            }
        }while(!selected);
    }
}
