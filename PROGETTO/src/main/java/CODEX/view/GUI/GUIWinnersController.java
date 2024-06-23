package CODEX.view.GUI;

import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Pawn;
import CODEX.org.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.*;

import java.util.List;
import java.util.Map;

/**
 * This class controls the window during the winner scene
 */
public class GUIWinnersController {
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

    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private int network;
    private Player personalPlayer;
    private Map<Integer, List<String>> winners;


    /**
     * This method sets the label on "YOU WON" or "YOU LOST"
     */
    private void setTitleLabel() {
        // CHECKING IF I WON THE MATCH OR NOT
        boolean iWon = false;
        for (String s : winners.get(1)) {
            // if the player is in the 1st position -> he won
            if (s.equals(personalPlayer.getNickname())) {
                iWon = true;
                break;
            }
        }

        // SETTING THE LABEL ON TOP OF THE SCREEN
        if (iWon) {
            titleLabel.setText("Y O U    W O N !");
        } else {
            titleLabel.setText("Y O U    L O S T !");
        }
    }


    /**
     * This method fills the table in the window with all the labels and the images
     */
    private void setGridPaneLabels() {
        // SETTING THE CORRECT OPACITY FOR THE LABELS
        if (network == 1) {
            if (rmiClient.getPlayersInTheGame().size() == 2) {
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


                player1.setText(winners.get(1).get(0));
                for (Player p : rmiClient.getPlayersInTheGame()) {
                    if (p.getNickname().equals(winners.get(1).get(0))) {
                        points1.setText(p.getPoints() + " pt");
                        obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                        String path = null;
                        if (p.getChosenColor().equals(Pawn.YELLOW)) {
                            path = "/images/pawns/Yellow_Pawn.png";
                        } else if (p.getChosenColor().equals(Pawn.RED)) {
                            path = "/images/pawns/Red_Pawn.png";
                        } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                            path = "/images/pawns/Blue_Pawn.png";
                        } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                            path = "/images/pawns/Green_Pawn.png";
                        }
                        Image image = new Image(getClass().getResourceAsStream(path));
                        pawn1.setImage(image);
                    }
                }


                if (winners.get(1).size() > 1) {
                    player2.setText(winners.get(1).get(1));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                } else {
                    player2.setText(winners.get(2).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(2).get(0))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }

                }

            } else if (rmiClient.getPlayersInTheGame().size() == 3) { //3 players
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

                if (winners.get(1).size() == 1) { // just 1 winner
                    player1.setText(winners.get(1).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    if (winners.get(2).size() == 1) { // one 2 position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(3).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(0))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }
                    } else if (winners.get(2).size() == 2) { // 2 second position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(2).get(1));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(1))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }
                    }

                } else if (winners.get(1).size() == 2) { // 2 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }


                    player3.setText(winners.get(3).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(3).get(0))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }


                } else if (winners.get(1).size() == 3) { //3 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }

                    player3.setText(winners.get(1).get(2));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(2))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }

                }


            } else if (rmiClient.getPlayersInTheGame().size() == 4) {
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

                if (winners.get(1).size() == 1) { //just 1 winner
                    player1.setText(winners.get(1).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    if (winners.get(2).size() == 1) { // one 2 position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        if (winners.get(3).size() == 1) {
                            player3.setText(winners.get(3).get(0));
                            for (Player p : rmiClient.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(3).get(0))) {
                                    points3.setText(p.getPoints() + " pt");
                                    obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn3.setImage(image);
                                }
                            }


                            player4.setText(winners.get(4).get(0));
                            for (Player p : rmiClient.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(4).get(0))) {
                                    points4.setText(p.getPoints() + " pt");
                                    obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn4.setImage(image);
                                }
                            }

                        } else { // 2 players in third position
                            player3.setText(winners.get(3).get(0));
                            for (Player p : rmiClient.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(3).get(0))) {
                                    points3.setText(p.getPoints() + " pt");
                                    obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn3.setImage(image);
                                }
                            }

                            player4.setText(winners.get(3).get(1));
                            for (Player p : rmiClient.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(3).get(1))) {
                                    points4.setText(p.getPoints() + " pt");
                                    obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn4.setImage(image);
                                }
                            }
                        }


                    } else if (winners.get(2).size() == 2) { // 2 second position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(2).get(1));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(1))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }


                        player4.setText(winners.get(4).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(4).get(0))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    } else if (winners.get(2).size() == 3) { // 3 second position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(2).get(1));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(1))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }

                        player4.setText(winners.get(2).get(2));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(2))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    }


                } else if (winners.get(1).size() == 2) { //2 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                    //how many players in 2 position?
                    if (winners.get(3).size() == 2) { //2 players in second position
                        player3.setText(winners.get(3).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(0))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }

                        player4.setText(winners.get(3).get(1));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(1))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    } else { // 1 player in 3 position
                        player3.setText(winners.get(3).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(0))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }
                        player4.setText(winners.get(4).get(0));
                        for (Player p : rmiClient.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(4).get(0))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    }

                } else if (winners.get(1).size() == 3) { //3 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                    player3.setText(winners.get(1).get(2));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(2))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }

                    player4.setText(winners.get(4).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(4).get(0))) {
                            points4.setText(p.getPoints() + " pt");
                            obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn4.setImage(image);
                        }
                    }

                } else if (winners.get(1).size() == 4) { //4 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                    player3.setText(winners.get(1).get(2));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(2))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }

                    player4.setText(winners.get(1).get(3));
                    for (Player p : rmiClient.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(3))) {
                            points4.setText(p.getPoints() + " pt");
                            obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn4.setImage(image);
                        }
                    }
                }
            }
        } else if (network == 2) {
            if (clientSCK.getPlayersInTheGame().size() == 2) {
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


                player1.setText(winners.get(1).get(0));
                for (Player p : clientSCK.getPlayersInTheGame()) {
                    if (p.getNickname().equals(winners.get(1).get(0))) {
                        points1.setText(p.getPoints() + " pt");
                        obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                        String path = null;
                        if (p.getChosenColor().equals(Pawn.YELLOW)) {
                            path = "/images/pawns/Yellow_Pawn.png";
                        } else if (p.getChosenColor().equals(Pawn.RED)) {
                            path = "/images/pawns/Red_Pawn.png";
                        } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                            path = "/images/pawns/Blue_Pawn.png";
                        } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                            path = "/images/pawns/Green_Pawn.png";
                        }
                        Image image = new Image(getClass().getResourceAsStream(path));
                        pawn1.setImage(image);
                    }
                }


                if (winners.get(1).size() > 1) {
                    player2.setText(winners.get(1).get(1));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                } else {
                    player2.setText(winners.get(2).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(2).get(0))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }

                }

            } else if (clientSCK.getPlayersInTheGame().size() == 3) { //3 players
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

                if (winners.get(1).size() == 1) { //just 1 winner
                    player1.setText(winners.get(1).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    if (winners.get(2).size() == 1) { // one 2 position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(3).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(0))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }
                    } else if (winners.get(2).size() == 2) { // 2 second position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(2).get(1));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(1))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }
                    }

                } else if (winners.get(1).size() == 2) { //2 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }


                    player3.setText(winners.get(3).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(3).get(0))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }


                } else if (winners.get(1).size() == 3) { //3 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }

                    player3.setText(winners.get(1).get(2));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(2))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }

                }


            } else if (clientSCK.getPlayersInTheGame().size() == 4) {
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

                if (winners.get(1).size() == 1) { //just 1 winner
                    player1.setText(winners.get(1).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    if (winners.get(2).size() == 1) { // one 2 position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        if (winners.get(3).size() == 1) {
                            player3.setText(winners.get(3).get(0));
                            for (Player p : clientSCK.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(3).get(0))) {
                                    points3.setText(p.getPoints() + " pt");
                                    obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn3.setImage(image);
                                }
                            }


                            player4.setText(winners.get(4).get(0));
                            for (Player p : clientSCK.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(4).get(0))) {
                                    points4.setText(p.getPoints() + " pt");
                                    obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn4.setImage(image);
                                }
                            }

                        } else { // 2 players in third position
                            player3.setText(winners.get(3).get(0));
                            for (Player p : clientSCK.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(3).get(0))) {
                                    points3.setText(p.getPoints() + " pt");
                                    obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn3.setImage(image);
                                }
                            }

                            player4.setText(winners.get(3).get(1));
                            for (Player p : clientSCK.getPlayersInTheGame()) {
                                if (p.getNickname().equals(winners.get(3).get(1))) {
                                    points4.setText(p.getPoints() + " pt");
                                    obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                    String path = null;
                                    if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                        path = "/images/pawns/Yellow_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.RED)) {
                                        path = "/images/pawns/Red_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                        path = "/images/pawns/Blue_Pawn.png";
                                    } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                        path = "/images/pawns/Green_Pawn.png";
                                    }
                                    Image image = new Image(getClass().getResourceAsStream(path));
                                    pawn4.setImage(image);
                                }
                            }
                        }


                    } else if (winners.get(2).size() == 2) { // 2 second position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(2).get(1));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(1))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }


                        player4.setText(winners.get(4).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(4).get(0))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    } else if (winners.get(2).size() == 3) { // 3 second position
                        player2.setText(winners.get(2).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(0))) {
                                points2.setText(p.getPoints() + " pt");
                                obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn2.setImage(image);
                            }
                        }

                        player3.setText(winners.get(2).get(1));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(1))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }

                        player4.setText(winners.get(2).get(2));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(2).get(2))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    }


                } else if (winners.get(1).size() == 2) { //2 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                    //how many players in 2 position?
                    if (winners.get(3).size() == 2) { //2 players in second position
                        player3.setText(winners.get(3).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(0))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }

                        player4.setText(winners.get(3).get(1));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(1))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    } else { // 1 player in 3 position
                        player3.setText(winners.get(3).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(3).get(0))) {
                                points3.setText(p.getPoints() + " pt");
                                obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn3.setImage(image);
                            }
                        }
                        player4.setText(winners.get(4).get(0));
                        for (Player p : clientSCK.getPlayersInTheGame()) {
                            if (p.getNickname().equals(winners.get(4).get(0))) {
                                points4.setText(p.getPoints() + " pt");
                                obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                                String path = null;
                                if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                    path = "/images/pawns/Yellow_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.RED)) {
                                    path = "/images/pawns/Red_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                    path = "/images/pawns/Blue_Pawn.png";
                                } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                    path = "/images/pawns/Green_Pawn.png";
                                }
                                Image image = new Image(getClass().getResourceAsStream(path));
                                pawn4.setImage(image);
                            }
                        }
                    }

                } else if (winners.get(1).size() == 3) { //3 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                    player3.setText(winners.get(1).get(2));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(2))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }

                    player4.setText(winners.get(4).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(4).get(0))) {
                            points4.setText(p.getPoints() + " pt");
                            obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn4.setImage(image);
                        }
                    }

                } else if (winners.get(1).size() == 4) { //4 first position
                    player1.setText(winners.get(1).get(0));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(0))) {
                            points1.setText(p.getPoints() + " pt");
                            obj1.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn1.setImage(image);
                        }
                    }
                    player2.setText(winners.get(1).get(1));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(1))) {
                            points2.setText(p.getPoints() + " pt");
                            obj2.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn2.setImage(image);
                        }
                    }
                    player3.setText(winners.get(1).get(2));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(2))) {
                            points3.setText(p.getPoints() + " pt");
                            obj3.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn3.setImage(image);
                        }
                    }

                    player4.setText(winners.get(1).get(3));
                    for (Player p : clientSCK.getPlayersInTheGame()) {
                        if (p.getNickname().equals(winners.get(1).get(3))) {
                            points4.setText(p.getPoints() + " pt");
                            obj4.setText(String.valueOf(p.getNumObjectivesReached()));
                            String path = null;
                            if (p.getChosenColor().equals(Pawn.YELLOW)) {
                                path = "/images/pawns/Yellow_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.RED)) {
                                path = "/images/pawns/Red_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.BLUE)) {
                                path = "/images/pawns/Blue_Pawn.png";
                            } else if (p.getChosenColor().equals(Pawn.GREEN)) {
                                path = "/images/pawns/Green_Pawn.png";
                            }
                            Image image = new Image(getClass().getResourceAsStream(path));
                            pawn4.setImage(image);
                        }
                    }
                }
            }
        }


        // SETTING THE CORRECT TEXT FOR THE POSITIONS
        if (winners.get(1).size() == 4) { // all players are #1
            position1.setText("#1");
            position2.setText("#1");
            position3.setText("#1");
            position4.setText("#1");

        } else if (winners.get(1).size() == 3) { // 3 players are #1 and 1 player is #2
            position1.setText("#1");
            position2.setText("#1");
            position3.setText("#1");
            position4.setText("#4");

        } else if (winners.get(1).size() == 2) { // 2 players are #1
            position1.setText("#1");
            position2.setText("#1");
            position3.setText("#3");
            if (winners.get(2) != null && winners.get(2).size() == 2) { // 2 players are #2
                position4.setText("#3");
            } else { // 1 player is #2 and 1 player is #3
                position4.setText("#4");
            }

        } else if (winners.get(1).size() == 1) { // 1 player is #1
            position1.setText("#1");
            if (winners.get(2) != null && winners.get(2).size() == 1) { // 1 player is #2
                position2.setText("#2");
                if (winners.get(3) != null && winners.get(3).size() == 1) { // 1 player is #3
                    position3.setText("#3");
                    position4.setText("#4");
                } else if (winners.get(3) != null && winners.get(3).size() == 2) { // 2 players are #3
                    position3.setText("#3");
                    position4.setText("#3");
                }
            } else if (winners.get(2) != null && winners.get(2).size() == 2) { // 2 players are #2
                position2.setText("#2");
                position3.setText("#2");
                position4.setText("#4");
            } else if (winners.get(2) != null && winners.get(2).size() == 3) { // 3 players are #2
                position2.setText("#2");
                position3.setText("#2");
                position4.setText("#2");
            }
        }


        // HIGHLIGHTING THE POSITION OF THE CURRENT PLAYER: HIS LINE HAS A DIFFERENT COLOUR
        if (personalPlayer.getNickname().equals(player1.getText())) {
            position1.setStyle("-fx-text-fill: #a20062;");
            points1.setStyle("-fx-text-fill: #a20062;");
            player1.setStyle("-fx-text-fill: #a20062;");
            obj1.setStyle("-fx-text-fill: #a20062;");

        } else if (personalPlayer.getNickname().equals(player2.getText())) {
            position2.setStyle("-fx-text-fill: #a20062;");
            points2.setStyle("-fx-text-fill: #a20062;");
            player2.setStyle("-fx-text-fill: #a20062;");
            obj2.setStyle("-fx-text-fill: #a20062;");

        } else if (personalPlayer.getNickname().equals(player3.getText())) {
            position3.setStyle("-fx-text-fill: #a20062;");
            points3.setStyle("-fx-text-fill: #a20062;");
            player3.setStyle("-fx-text-fill: #a20062;");
            obj3.setStyle("-fx-text-fill: #a20062;");

        } else if (personalPlayer.getNickname().equals(player4.getText())) {
            position4.setStyle("-fx-text-fill: #a20062;");
            points4.setStyle("-fx-text-fill: #a20062;");
            player4.setStyle("-fx-text-fill: #a20062;");
            obj4.setStyle("-fx-text-fill: #a20062;");

        }

    }


    /**
     * This method sets all the features in the winner scene
     */
    public void setAllFeatures() {
        // GETTING THE PERSONAL PLAYER AND ALL THE PLAYERS
        if (network == 1) {
            this.personalPlayer = rmiClient.getPersonalPlayer();
        } else if (network == 2) {
            this.personalPlayer = clientSCK.getPersonalPlayer();
        }

        // CALLING OTHER METHODS TO SET ALL THE GRAPHIC CONTENT
        setTitleLabel();
        setGridPaneLabels();
    }


    /**
     * Setter method
     *
     * @param winners is a map
     */
    public void setWinners(Map<Integer, List<String>> winners) {
        this.winners = winners;
    }


    /**
     * Setter method
     *
     * @param rmiClient client RMI
     */
    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }


    /**
     * Setter method
     *
     * @param clientSCK client SCK
     */
    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK = clientSCK;
    }


    /**
     * Setter method
     *
     * @param network 1 or 2 (rmi or tcp)
     */
    public void setNetwork(int network) {
        this.network = network;
    }

}
