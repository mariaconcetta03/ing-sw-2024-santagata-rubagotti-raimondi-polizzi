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


/**
 * This class is the launcher of both rmi and tcp clients.
 */
public class ClientLauncher {
    /**
     * Main method
     * @param args null
     */
    public static void main(String[] args) {
        boolean selected = false;
        int selection = 0;
        String serverAddress = "";
        String clientAddress = "";
        Scanner sc = new Scanner(System.in);

        System.out.println("Insert the Server's Ip address (leave blank for LocalHost): ");
        serverAddress=sc.nextLine(); 
        System.out.println(ANSIFormatter.ANSI_BLUE+"Choose a connection protocol and an interface:"+ANSIFormatter.ANSI_RESET+": \n-Type 1 for RMI+TUI\n-Type 2 for RMI+GUI\n-Type 3 for TCP+TUI\n-Type 4 for TCP+GUI");
        do{
            try {
                selection = sc.nextInt();
                sc = new Scanner(System.in);
            if(selection==1){
                //Scanner sc2 = new Scanner(System.in);
                System.out.println("Insert your IP address (leave blank for localHost): ");
                clientAddress = sc.nextLine();
                selected = true;
                if (clientAddress.equals("")) {
                    System.setProperty("java.rmi.server.hostname", Inet4Address.getLocalHost().getHostAddress());
                } else {
                    System.setProperty("java.rmi.server.hostname", clientAddress);
                }
                RMIClient rmiClient = new RMIClient();
                rmiClient.getNetworkSettings().setSERVER_NAME(serverAddress); //setting the Server Address
                rmiClient.setSelectedView(1); //TUI
                rmiClient.SRMIInterfaceFromRegistry();
                InterfaceTUI.clearScreen();
                //connect the client
                rmiClient.waitingRoom(); //selection of nicknames and lobby functionalities
            }else if(selection == 2){
                RMIClient rmiClient; //generate RMI client
                System.out.println("Insert your IP address (leave blank for localHost): ");
                clientAddress = sc.nextLine();
                selected = true;
                    if (clientAddress.equals("")) {
                        System.setProperty("java.rmi.server.hostname", Inet4Address.getLocalHost().getHostAddress());
                    } else {
                        System.setProperty("java.rmi.server.hostname", clientAddress);
                    }
                    rmiClient = new RMIClient();
                    rmiClient.getNetworkSettings().setSERVER_NAME(serverAddress); //setting the Server Address
                    rmiClient.setSelectedView(2); //GUI
                    rmiClient.SRMIInterfaceFromRegistry();

                selected = true;
                String[] network = new String[1];
                network[0] = "RMI";
                InterfaceGUI.main(network, null, rmiClient);
            }else if(selection==3){
                selected = true;
                try {
                    ClientSCK clientSCK = null;
                    if (serverAddress.equals("")) { // if string is empty then it uses localhost
                        ClientSCK.Settings.setServerName("127.0.0.1");
                        clientSCK = new ClientSCK("127.0.0.1");
                    } else {
                        clientSCK = new ClientSCK(serverAddress);
                        ClientSCK.Settings.setServerName(serverAddress);
                    }
                    clientSCK.setSelectedView(1); //TUI
                    clientSCK.waitingRoom();
                    //connect the client
                }catch(IOException e){
                    System.out.println("Cannot connect to Server. Shutting down. Try again.");
                    e.printStackTrace();
                    System.exit(-1);
                }
            } else if (selection==4) {
                ClientSCK clientSCK = null;
                try {
                    if (serverAddress.equals("")) { // if string is empty then it uses localhost
                        ClientSCK.Settings.setServerName("127.0.0.1");
                        clientSCK = new ClientSCK("127.0.0.1");
                    } else {
                        clientSCK = new ClientSCK(serverAddress);
                        ClientSCK.Settings.setServerName(serverAddress);
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
                e.printStackTrace();
                System.exit(-1);
            }
        }while(!selected);
    }


    /**
     * Default constructor
     */
    public ClientLauncher(){}
}
