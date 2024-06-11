package CODEX.view.GUI;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Pawn;
import CODEX.org.model.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.image.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class GUIWinnersController {

    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private Stage stage;
    private int network;
    private List<Player> allPlayers; // IN ORDER OF POINTS, FROM HIGHEST TO LOWEST [setAllFeatures()]
    private Player personalPlayer;
    private List<Player> winners;


    @FXML
    private Label titleLabel;
    @FXML
    private Label position1;
    @FXML
    private Label position2;
    @FXML
    private Label position3;
    @FXML
    private Label position4;
    @FXML
    private Label player1;
    @FXML
    private Label player2;
    @FXML
    private Label player3;
    @FXML
    private Label player4;
    @FXML
    private Label points1;
    @FXML
    private Label points2;
    @FXML
    private Label points3;
    @FXML
    private Label points4;
    @FXML
    private Label obj1;
    @FXML
    private Label obj2;
    @FXML
    private Label obj3;
    @FXML
    private Label obj4;
    @FXML
    private ImageView pawn1;
    @FXML
    private ImageView pawn2;
    @FXML
    private ImageView pawn3;
    @FXML
    private ImageView pawn4;



    private void setTitleLabel(){
        // CHECKING IF I WON THE MATCH OR NOT
        boolean iWon = false;
        for (Player p: winners) {
            if (p.getNickname().equals(personalPlayer.getNickname())) {
                iWon = true;
                break;
            }
        }

        // SETTING THE LABEL ON TOP OF THE SCREEN
        if(iWon){
            titleLabel.setText("Y O U    W O N !");
        }else{
            titleLabel.setText("Y O U    L O S T !");
        }
    }






    private void setGridPaneLabels() {
        // SETTING THE CORRECT OPACITY FOR THE LABELS AND THEIR TEXTS FOR POINTS AND NICKNAMES + SETTING THE PAWNS
        // ---------------------------------------  W A R N I N G !  ------------------------------------------
        // The boards of the table will remain the same with empty fields, even if there will be 2 or 3 players
        if (allPlayers.size() == 2) {
            position1.setOpacity(1);
            position2.setOpacity(1);
            position3.setOpacity(0);
            position4.setOpacity(0);
            player1.setOpacity(1);
            player2.setOpacity(1);
            player3.setOpacity(0);
            player4.setOpacity(0);
            points1.setOpacity(1);
            points2.setOpacity(1);
            points3.setOpacity(0);
            points4.setOpacity(0);
            obj1.setOpacity(1);
            obj2.setOpacity(1);
            obj3.setOpacity(0);
            obj4.setOpacity(0);
            player1.setText(allPlayers.get(0).getNickname());
            player2.setText(allPlayers.get(1).getNickname());
            points1.setText(String.valueOf(allPlayers.get(0).getPoints()) + " pt");
            points2.setText(String.valueOf(allPlayers.get(1).getPoints()) + " pt");
            obj1.setText(String.valueOf(allPlayers.get(0).getNumObjectivesReached()));
            obj2.setText(String.valueOf(allPlayers.get(1).getNumObjectivesReached()));
            String path = null;

            for(int i = 0; i< 2; i++){
                if (allPlayers.get(i).getChosenColor().equals(Pawn.YELLOW)) {
                    path = "/images/pawns/Yellow_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.RED)) {
                    path = "/images/pawns/Red_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.BLUE)) {
                    path = "/images/pawns/Blue_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.GREEN)) {
                    path = "/images/pawns/Green_Pawn.png";
                }

                Image image = new Image(getClass().getResourceAsStream(path));

                if(i == 0){
                    pawn1.setImage(image);
                }else if (i == 2){
                    pawn2.setImage(image);
                }
            }

        } else if (allPlayers.size() == 3) {
            position1.setOpacity(1);
            position2.setOpacity(1);
            position3.setOpacity(1);
            position4.setOpacity(0);
            player1.setOpacity(1);
            player2.setOpacity(1);
            player3.setOpacity(1);
            player4.setOpacity(0);
            points1.setOpacity(1);
            points2.setOpacity(1);
            points3.setOpacity(1);
            points4.setOpacity(0);
            obj1.setOpacity(1);
            obj2.setOpacity(1);
            obj3.setOpacity(1);
            obj4.setOpacity(0);
            player1.setText(allPlayers.get(0).getNickname());
            player2.setText(allPlayers.get(1).getNickname());
            player3.setText(allPlayers.get(2).getNickname());
            points1.setText(String.valueOf(allPlayers.get(0).getPoints()) + " pt");
            points2.setText(String.valueOf(allPlayers.get(1).getPoints()) + " pt");
            points3.setText(String.valueOf(allPlayers.get(2).getPoints()) + " pt");
            obj1.setText(String.valueOf(allPlayers.get(0).getNumObjectivesReached()));
            obj2.setText(String.valueOf(allPlayers.get(1).getNumObjectivesReached()));
            obj3.setText(String.valueOf(allPlayers.get(2).getNumObjectivesReached()));
            String path = null;

            for(int i = 0; i< 3; i++){
                if (allPlayers.get(i).getChosenColor().equals(Pawn.YELLOW)) {
                    path = "/images/pawns/Yellow_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.RED)) {
                    path = "/images/pawns/Red_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.BLUE)) {
                    path = "/images/pawns/Blue_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.GREEN)) {
                    path = "/images/pawns/Green_Pawn.png";
                }

                Image image = new Image(getClass().getResourceAsStream(path));

                if(i == 0){
                    pawn1.setImage(image);
                }else if (i == 2){
                    pawn2.setImage(image);
                }else if (i == 3){
                    pawn3.setImage(image);
                }
            }
        } else if (allPlayers.size() == 4) {
            position1.setOpacity(1);
            position2.setOpacity(1);
            position3.setOpacity(1);
            position4.setOpacity(1);
            player1.setOpacity(1);
            player2.setOpacity(1);
            player3.setOpacity(1);
            player4.setOpacity(1);
            points1.setOpacity(1);
            points2.setOpacity(1);
            points3.setOpacity(1);
            points4.setOpacity(1);
            obj1.setOpacity(1);
            obj2.setOpacity(1);
            obj3.setOpacity(1);
            obj4.setOpacity(1);
            player1.setText(allPlayers.get(0).getNickname());
            player2.setText(allPlayers.get(1).getNickname());
            player3.setText(allPlayers.get(2).getNickname());
            player4.setText(allPlayers.get(3).getNickname());
            points1.setText(String.valueOf(allPlayers.get(0).getPoints()) + " pt");
            points2.setText(String.valueOf(allPlayers.get(1).getPoints()) + " pt");
            points3.setText(String.valueOf(allPlayers.get(2).getPoints()) + " pt");
            points4.setText(String.valueOf(allPlayers.get(3).getPoints()) + " pt");
            obj1.setText(String.valueOf(allPlayers.get(0).getNumObjectivesReached()));
            obj2.setText(String.valueOf(allPlayers.get(1).getNumObjectivesReached()));
            obj3.setText(String.valueOf(allPlayers.get(2).getNumObjectivesReached()));
            obj4.setText(String.valueOf(allPlayers.get(3).getNumObjectivesReached()));
            String path = null;

            for(int i = 0; i< 4; i++){
                if (allPlayers.get(i).getChosenColor().equals(Pawn.YELLOW)) {
                    path = "/images/pawns/Yellow_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.RED)) {
                    path = "/images/pawns/Red_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.BLUE)) {
                    path = "/images/pawns/Blue_Pawn.png";
                } else if (allPlayers.get(i).getChosenColor().equals(Pawn.GREEN)) {
                    path = "/images/pawns/Green_Pawn.png";
                }

                Image image = new Image(getClass().getResourceAsStream(path));

                if(i == 0){
                    pawn1.setImage(image);
                }else if (i == 2){
                    pawn2.setImage(image);
                }else if (i == 2){
                    pawn3.setImage(image);
                }else if (i == 4){
                pawn4.setImage(image);
            }
            }
        }

        // SETTING THE CORRECT POSITION (TEXT) LABELS FOR THE FIRST POSITION TIE
        if (winners.size() == 1) {
            position1.setText("#1");
            position2.setText("#2");
            position3.setText("#3");
            position4.setText("#4");

        } else if (winners.size() == 2) {
            position1.setText("#1");
            position2.setText("#1");
            position3.setText("#2");
            position4.setText("#3");

        } else if (winners.size() == 3) {
            position1.setText("#1");
            position2.setText("#1");
            position3.setText("#1");
            position4.setText("#2");

        } else if (winners.size() == 4) {
            position1.setText("#1");
            position2.setText("#1");
            position3.setText("#1");
            position4.setText("#1");

        }

        // HIGHLIGHTING THE POSITION OF THE CURRENT PLAYER: HIS LINE HAS A DIFFERENT COLOUR
        if (personalPlayer.getNickname().equals(allPlayers.get(0).getNickname())) {
            position1.setStyle("-fx-text-fill: #a20062;");
            points1.setStyle("-fx-text-fill: #a20062;");
            player1.setStyle("-fx-text-fill: #a20062;");
            obj1.setStyle("-fx-text-fill: #a20062;");

        } else if (personalPlayer.getNickname().equals(allPlayers.get(1).getNickname())) {
            position2.setStyle("-fx-text-fill: #a20062;");
            points2.setStyle("-fx-text-fill: #a20062;");
            player2.setStyle("-fx-text-fill: #a20062;");
            obj2.setStyle("-fx-text-fill: #a20062;");

        } else if (personalPlayer.getNickname().equals(allPlayers.get(2).getNickname())) {
            position3.setStyle("-fx-text-fill: #a20062;");
            points3.setStyle("-fx-text-fill: #a20062;");
            player3.setStyle("-fx-text-fill: #a20062;");
            obj3.setStyle("-fx-text-fill: #a20062;");

        } else if (personalPlayer.getNickname().equals(allPlayers.get(3).getNickname())) {
            position4.setStyle("-fx-text-fill: #a20062;");
            points4.setStyle("-fx-text-fill: #a20062;");
            player4.setStyle("-fx-text-fill: #a20062;");
            obj4.setStyle("-fx-text-fill: #a20062;");

        }

    }





    private void startTimerToClose() {

    }



    

    public void setAllFeatures() {
        // GETTING THE PERSONAL PLAYER AND ALL THE PLAYERS
        if (network == 1) {
            this.personalPlayer = rmiClient.getPersonalPlayer();
            this.allPlayers = rmiClient.getPlayersInTheGame();
        } else if (network == 2) {
            this.personalPlayer = clientSCK.getPersonalPlayer();
            this.allPlayers = clientSCK.getPlayersInTheGame();
        }


        // ORDERING THE allPlayers LIST FROM HIGHEST POINTS TO THE LOWEST
        List<Player> orderedList = new ArrayList<>();

        while (!allPlayers.isEmpty()) { // finchè allplayers non è vuota
            int maxIndex = 0;
            for (int i = 1; i < allPlayers.size(); i++) { // controllo dalla posizione successiva allo 0
                if (allPlayers.get(i).getPoints() > allPlayers.get(maxIndex).getPoints()) {
                    maxIndex = i;
                }
            }
            orderedList.add(allPlayers.get(maxIndex));
            allPlayers.remove(maxIndex);
        }
        allPlayers = orderedList;


        // CALLING OTHER METHODS TO SET ALL THE GRAPHIC CONTENT
        setTitleLabel();
        setGridPaneLabels();
        startTimerToClose();
    }






    public void setWinners(List<Player> winners) {
        this.winners = winners;
    }


    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }


    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK = clientSCK;
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setNetwork(int network) {
        this.network = network;
    }
}
