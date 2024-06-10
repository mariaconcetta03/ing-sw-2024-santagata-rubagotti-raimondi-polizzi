package CODEX.distributed;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.view.GUI.InterfaceGUI;
import CODEX.view.TUI.ANSIFormatter;
import CODEX.view.TUI.InterfaceTUI;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientLauncher {
    public static void main(String[] args) {
        boolean selected=false;
        int selection=0;
        String serverAddress="";
        String clientAddress = "";
        Scanner sc=new Scanner(System.in);

        System.out.println("Insert the Server's Ip address (leave blank for LocalHost): ");
        serverAddress=sc.nextLine(); 
        System.out.println(ANSIFormatter.ANSI_BLUE+"Choose a connection protocol and an interface:"+ANSIFormatter.ANSI_RESET+": \n-Type 1 for RMI+TUI\n-Type 2 for RMI+GUI\n-Type 3 for TCP+TUI\n-Type 4 for TCP+GUI");
        do{
            try {
                selection = sc.nextInt();
                sc=new Scanner(System.in);
            if(selection==1){
                //Scanner sc2 =new Scanner(System.in);
                System.out.println("Insert your IP address (leave blank for localHost): ");
                clientAddress=sc.nextLine();
                selected=true;
                if (clientAddress.equals("")) {
                    System.setProperty("java.rmi.server.hostname", Inet4Address.getLocalHost().getHostAddress()); //don't know if it works also for @mcs
                } else {
                    System.setProperty("java.rmi.server.hostname", clientAddress); //don't know if it works also for @mcs
                }
                RMIClient rmiClient = new RMIClient();
                rmiClient.getNetworkSettings().setSERVER_NAME(serverAddress); //setting the Server Address
                rmiClient.setSelectedView(1); //TUI
                rmiClient.SRMIInterfaceFromRegistry();
                InterfaceTUI.clearScreen();
                //connect the client
                rmiClient.waitingRoom(); //selection of nicknames and lobby functionalities

                //generate client RMI
            }else if(selection == 2){
                RMIClient rmiClient;
                System.out.println("Insert your IP address (leave blank for localHost): ");
                clientAddress=sc.nextLine();
                selected=true;
                    if (clientAddress.equals("")) {
                        System.setProperty("java.rmi.server.hostname", Inet4Address.getLocalHost().getHostAddress()); //don't know if it works also for @mcs
                    } else {
                        System.setProperty("java.rmi.server.hostname", clientAddress); //don't know if it works also for @mcs
                    }
                    rmiClient=new RMIClient(); //bisogna gestirlo con un'eccezione se dopo il try rimane null
                    rmiClient.getNetworkSettings().setSERVER_NAME(serverAddress); //setting the Server Address
                    rmiClient.setSelectedView(2); //GUI
                    rmiClient.SRMIInterfaceFromRegistry();

                selected=true;
                String[] network = new String[1];
                network[0] = "RMI";
                InterfaceGUI.main(network, null, rmiClient);
            }else if(selection==3){
                selected=true;
                try {
                    ClientSCK clientSCK = null;
                    if (serverAddress.equals("")) { // se la stringa è vuota, allora metto il localhost
                        ClientSCK.Settings.setServerName("127.0.0.1"); // non so se serve
                        clientSCK = new ClientSCK("127.0.0.1"); //per TCP dobbiamo solo chiedere porte disponibili lato client
                        System.out.println("Set server address: 127.0.0.1");
                    } else {
                        clientSCK = new ClientSCK(serverAddress); //per TCP dobbiamo solo chiedere porte disponibili lato client
                        ClientSCK.Settings.setServerName(serverAddress); // non so se serve
                        System.out.println("Set server address: " + serverAddress);
                    }
                    clientSCK.setSelectedView(1); //TUI
                    clientSCK.waitingRoom();
                    //connect the client
                }catch(IOException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    e.printStackTrace();
                    System.exit(-1);
                } //non so se vadano gestite in altra maniera, potremmo chiudere tutto e riprovare
            } else if (selection==4) {
                ClientSCK clientSCK = null;
                try {
                    if (serverAddress.equals("")) { // se la stringa è vuota, allora metto il localhost
                        ClientSCK.Settings.setServerName("127.0.0.1"); // non so se serve
                        clientSCK = new ClientSCK("127.0.0.1"); //per TCP dobbiamo solo chiedere porte disponibili lato client
                        System.out.println("Set server address: 127.0.0.1");
                    } else {
                        clientSCK = new ClientSCK(serverAddress); //per TCP dobbiamo solo chiedere porte disponibili lato client
                        ClientSCK.Settings.setServerName(serverAddress); // non so se serve
                        System.out.println("Set server address: " + serverAddress);
                    }
                    clientSCK.setSelectedView(2); //GUI
                } catch (IOException e) {
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    e.printStackTrace();
                    System.exit(-1);
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
            } catch (RemoteException| NotBoundException| UnknownHostException e) {
                System.out.println("Cannot connect to Server. Shutting down. Try again.");
                e.printStackTrace(); //forse poi va rimosso in consegna
                System.exit(-1);
            }
        }while(!selected);
    }
}
