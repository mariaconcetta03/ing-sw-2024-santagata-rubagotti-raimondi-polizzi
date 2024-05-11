package distributed.Socket;

import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Timer;



import Exceptions.FullLobbyException;
import Exceptions.GameAlreadyStartedException;
import Exceptions.GameNotExistsException;
import distributed.Socket.ClientSCK;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Timer;

    public class Test {
        public static void main(String[] args) {
            try {
                method();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (GameAlreadyStartedException e) {
                throw new RuntimeException(e);
            } catch (FullLobbyException e) {
                throw new RuntimeException(e);
            } catch (GameNotExistsException e) {
                throw new RuntimeException(e);
            }
        }
        public static void method() throws IOException, NotBoundException, InterruptedException, GameAlreadyStartedException, FullLobbyException, GameNotExistsException {
            Timer t=new Timer();
            ClientSCK a = new ClientSCK();
            ClientSCK b = new ClientSCK();
            ClientSCK c = new ClientSCK();
            ClientSCK d = new ClientSCK();
            a.createLobby("a",4);
            t.wait(1000);
            b.addPlayerToLobby("b",a.gameID);
            t.wait(1000);
            c.addPlayerToLobby("b",a.gameID);
            t.wait(1000);
            d.addPlayerToLobby("b",a.gameID);

        }
    }

