package distributed;

import java.util.*;

public class ClientLauncher {
    public static void main(String[] args) {
        boolean selected=false;
        int protocol=0;
        Scanner sc=new Scanner(System.in);

        System.out.println("Choose a connection protocol: \n-Type 1 for RMI\n-Type 2 for TCP");
        do{
            try {
                protocol = sc.nextInt();
            if(protocol==1){
                selected=true;
                //generate client RMI
            }else if(protocol==2){
                selected=true;
                //generate client TCP
            }else{
                System.out.println("Please type 1 (RMI) or 2 (TCP)");
            }
            }catch (IllegalArgumentException e){
                System.out.println("Please type 1 (RMI) or 2 (TCP)");
            }
        }while(!selected);
    }
}
