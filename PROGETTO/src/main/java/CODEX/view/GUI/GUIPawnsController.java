package CODEX.view.GUI;

import CODEX.Exceptions.ColorAlreadyTakenException;
import CODEX.distributed.RMI.RMIClient;
import CODEX.distributed.Socket.ClientSCK;
import CODEX.org.model.Pawn;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class controls the window during the pawns scene
 */
public class GUIPawnsController {
    private GUIBaseCardController ctr;
    private Stage stage;
    private int network;
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private boolean choosen = false;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private boolean pawnSelected=false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Object disconnectionLock = new Object();
    
    @FXML
    private ImageView yellowPawn;
    @FXML
    private ImageView redPawn;
    @FXML
    private ImageView greenPawn;
    @FXML
    private ImageView bluePawn;
    @FXML
    private Label labelWithPlayerName;
    @FXML
    private Label retryLabel;



    /**
     * This method is used to change the scene
     */
    public void changeScene(){
        // let's show the new window
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/baseCard.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (ctr == null) {
            ctr = fxmlLoader.getController();
        }
        stage.setOnCloseRequest(event -> {
            if (network == 1) {

                    rmiClient.handleDisconnectionFunction();

            } else if (network == 2) {

                    clientSCK.handleDisconnectionFunction();

            }
        });

        // setting the parameters in the new controller, also the BASE CARD (front and back)
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);
        ctr.setScheduler(scheduler);
        ctr.setDisconnectionLock(disconnectionLock);
        if (network == 1) {
            while (rmiClient.getPersonalPlayer().getPlayerDeck()[0] == null) {
            }
            ctr.setBaseCard1(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setBaseCard2(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", which side do you");
        } else if (network == 2) {
            while (clientSCK.getPersonalPlayer().getPlayerDeck()[0] == null) {
            }
            ctr.setBaseCard1(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setBaseCard2(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setLabelWithPlayerName(clientSCK.getPersonalPlayer().getNickname() + ", which side do you");
        }

        // old dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // new scene
        Scene scene;
        scene = new Scene(root);
        if(network==1){
            synchronized (rmiClient.getGuiBaseCardControllerLock()) {
                rmiClient.setGuiBaseCardController(ctr);
                rmiClient.getGuiBaseCardControllerLock().notify();

            }
        }if(network==2){

            synchronized (clientSCK.getGuiBaseCardControllerLock()) {
                clientSCK.setGuiBaseCardController(ctr);
                clientSCK.getGuiBaseCardControllerLock().notify();

            }
        }

        stage.setScene(scene);

        // setting the od values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);

    }



    /**
     * This method is invoked when the player chooses the yellow pawn
     */
    synchronized public void selectedYellow() {
        if (yellowPawn.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {

                            rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.YELLOW);

                        choosen = true;

                        Platform.runLater(() -> {
                            greenPawn.setOpacity(0);
                            bluePawn.setOpacity(0);
                            redPawn.setOpacity(0);
                            greenPawn.disabledProperty();
                            bluePawn.disabledProperty();
                            redPawn.disabledProperty();
                            yellowPawn.disabledProperty();
                            retryLabel.setText("You have chosen. Now wait the others.");
                            retryLabel.setOpacity(1);
                        });

                        try {
                            rmiClient.getGameController().checkChosenPawnColor();
                        } catch (RemoteException e) {
                            rmiClient.setADisconnectionHappened(true);
                        }


                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }

                });

            } else if (network == 2) { // TCP
                executor.execute(() -> {

                        clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.YELLOW);
                        if(clientSCK.getErrorState()){
                            pawnSelected = false;
                            Platform.runLater(() -> {
                                retryLabel.setText("Color already taken, please choose another.");
                                retryLabel.setOpacity(1);
                                clientSCK.setErrorState(false);
                            });
                        }else {
                            choosen = true;
                            Platform.runLater(() -> {
                                greenPawn.setOpacity(0);
                                bluePawn.setOpacity(0);
                                redPawn.setOpacity(0);
                                greenPawn.disabledProperty();
                                bluePawn.disabledProperty();
                                redPawn.disabledProperty();
                                yellowPawn.disabledProperty();
                                retryLabel.setText("You have chosen. Now wait the others.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor();

                        }


                });
            }
        }
    }



    /**
     * This method is invoked when the player chooses the blue pawn
     */
    synchronized public void selectedBlue() {
        if (bluePawn.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {

                            rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.BLUE);

                        choosen = true;

                        Platform.runLater(() -> {
                            greenPawn.setOpacity(0);
                            yellowPawn.setOpacity(0);
                            redPawn.setOpacity(0);
                            greenPawn.disabledProperty();
                            bluePawn.disabledProperty();
                            redPawn.disabledProperty();
                            yellowPawn.disabledProperty();
                            retryLabel.setText("You have chosen. Now wait the others.");
                            retryLabel.setOpacity(1);
                        });
                        try {
                            rmiClient.getGameController().checkChosenPawnColor();
                        } catch (RemoteException e) {
                            rmiClient.setADisconnectionHappened(true);
                        }


                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }
                });

            } else if (network == 2) { //TCP
                executor.execute(() -> {

                        clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.BLUE);
                        if(clientSCK.getErrorState()){
                            pawnSelected = false;
                            Platform.runLater(() -> {
                                retryLabel.setText("Color already taken, please choose another.");
                                retryLabel.setOpacity(1);
                                clientSCK.setErrorState(false);
                            });
                        }else {
                            choosen = true;
                            Platform.runLater(() -> {
                                greenPawn.setOpacity(0);
                                yellowPawn.setOpacity(0);
                                redPawn.setOpacity(0);
                                greenPawn.disabledProperty();
                                bluePawn.disabledProperty();
                                redPawn.disabledProperty();
                                yellowPawn.disabledProperty();
                                retryLabel.setText("You have chosen. Now wait the others.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor();

                        }


                });
            }
        }
    }



    /**
     * This method is invoked when the player chooses the green pawn
     */
    synchronized public void selectedGreen() {
        if (greenPawn.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {

                            rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.GREEN);

                        choosen = true;
                        Platform.runLater(() -> {
                            yellowPawn.setOpacity(0);
                            bluePawn.setOpacity(0);
                            redPawn.setOpacity(0);
                            greenPawn.disabledProperty();
                            bluePawn.disabledProperty();
                            redPawn.disabledProperty();
                            yellowPawn.disabledProperty();
                            retryLabel.setText("You have chosen. Now wait the others.");
                            retryLabel.setOpacity(1);
                        });
                        rmiClient.getGameController().checkChosenPawnColor();


                    } catch (RemoteException alreadyCaught) {
                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }
                });

            } else if (network == 2) {
                executor.execute(() -> {

                        clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.GREEN);
                        if(clientSCK.getErrorState()){
                            pawnSelected = false;
                            Platform.runLater(() -> {
                                retryLabel.setText("Color already taken, please choose another.");
                                retryLabel.setOpacity(1);
                                clientSCK.setErrorState(false);
                            });
                        }else {
                            choosen = true;
                            Platform.runLater(() -> {
                                yellowPawn.setOpacity(0);
                                bluePawn.setOpacity(0);
                                redPawn.setOpacity(0);
                                greenPawn.disabledProperty();
                                bluePawn.disabledProperty();
                                redPawn.disabledProperty();
                                yellowPawn.disabledProperty();
                                retryLabel.setText("You have chosen. Now wait the others.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor();

                        }


                });
            }
        }
    }



    /**
     * This method is invoked when the player chooses the red pawn
     */
    synchronized public void selectedRed() {
        if (redPawn.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {

                            rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.RED);

                        choosen = true;
                        Platform.runLater(() -> {
                            greenPawn.setOpacity(0);
                            bluePawn.setOpacity(0);
                            yellowPawn.setOpacity(0);
                            greenPawn.disabledProperty();
                            bluePawn.disabledProperty();
                            redPawn.disabledProperty();
                            yellowPawn.disabledProperty();
                            retryLabel.setText("You have chosen. Now wait the others.");
                            retryLabel.setOpacity(1);
                        });
                        try {
                            rmiClient.getGameController().checkChosenPawnColor();
                        } catch (RemoteException  e) {
                            rmiClient.setADisconnectionHappened(true);
                        }


                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }
                });

            } else if (network == 2) { //TCP
                executor.execute(() -> {

                        clientSCK.choosePawnColor(clientSCK.getPersonalPlayer().getNickname(), Pawn.RED);
                        if(clientSCK.getErrorState()){
                            pawnSelected = false;
                            Platform.runLater(() -> {
                                retryLabel.setText("Color already taken, please choose another.");
                                retryLabel.setOpacity(1);
                                clientSCK.setErrorState(false);
                            });
                        }else {
                            choosen = true;
                            Platform.runLater(() -> {
                                greenPawn.setOpacity(0);
                                bluePawn.setOpacity(0);
                                yellowPawn.setOpacity(0);
                                greenPawn.disabledProperty();
                                bluePawn.disabledProperty();
                                redPawn.disabledProperty();
                                yellowPawn.disabledProperty();
                                retryLabel.setText("You have chosen. Now wait the others.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor();
                        }

                });
            }
        }
    }



    /**
     * This method is used to update the available pawns
     * @param pawn pawn to remove
     */
    synchronized public void updatePawns(Pawn pawn) {
        if(!choosen) {
            Platform.runLater(()-> {
                if (pawn == Pawn.BLUE) {

                    bluePawn.setOpacity(0);

                } else if (pawn == Pawn.YELLOW) {
                    yellowPawn.setOpacity(0);
                } else if (pawn == Pawn.GREEN) {
                    greenPawn.setOpacity(0);
                } else if (pawn == Pawn.RED) {
                    redPawn.setOpacity(0);
                }
            });
        }
    }



    /**
     * This method changes the scene when an update arrives
     */
    public void updateGameState() {
        Platform.runLater(this::changeScene);
    }


    /**
     * This method checks if there is a disconnection
     */
    public void startPeriodicDisconnectionCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (disconnectionLock) {
                if ( ((network==1) && (rmiClient.getADisconnectionHappened())) || ((network==2) && (clientSCK.getADisconnectionHappened())) ){
                    Platform.runLater(() -> {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/handleDisconnection.fxml"));
                        Parent root = null;
                        try {
                            root = fxmlLoader.load();
                        } catch (IOException ignored) {
                        }

                        double width = stage.getWidth();
                        double height = stage.getHeight();
                        double x = stage.getX();
                        double y = stage.getY();

                        // new scene
                        Scene scene;
                        scene = new Scene(root);

                        stage.setScene(scene);

                        // setting the old values of position and dimension
                        stage.setWidth(width);
                        stage.setHeight(height);
                        stage.setX(x);
                        stage.setY(y);

                        PauseTransition pause = new PauseTransition(Duration.seconds(3));
                        pause.setOnFinished(event -> {
                            stage.close();
                            if(network==1){

                                rmiClient.handleDisconnectionFunction();

                            }
                            if(network==2) {

                                clientSCK.handleDisconnectionFunction();

                            }
                        });
                        pause.play();
                    });
                    scheduler.shutdown(); // Stop the scheduler if there's the disconnection
                }
            }
        }, 0, 1, TimeUnit.SECONDS); // Check every second
    }



    /**
     * This method is used to show the images of the colored pawns in the beginning
     */
    public void setColoredPawns() {
        retryLabel.setOpacity(0);
        String path;
        yellowPawn.setOpacity(1);
        greenPawn.setOpacity(1);
        bluePawn.setOpacity(1);
        redPawn.setOpacity(1);

        path = "/images/pawns/Yellow_Pawn.png";
        Image y = new Image(getClass().getResourceAsStream(path));
        yellowPawn.setImage(y);

        path = "/images/pawns/Red_Pawn.png";
        Image r = new Image(getClass().getResourceAsStream(path));
        redPawn.setImage(r);

        path = "/images/pawns/Blue_Pawn.png";
        Image b = new Image(getClass().getResourceAsStream(path));
        bluePawn.setImage(b);

        path = "/images/pawns/Green_Pawn.png";
        Image g = new Image(getClass().getResourceAsStream(path));
        greenPawn.setImage(g);

    }



    /**
     * Setter method
     * @param rmiClient client RMI
     */
    public void setRmiClient(RMIClient rmiClient) {
        this.rmiClient = rmiClient;
    }



    /**
     * Setter method
     * @param clientSCK client SCK
     */
    public void setClientSCK(ClientSCK clientSCK) {
        this.clientSCK = clientSCK;
    }



    /**
     * Setter method
     * @param stage where it will be shown the next window
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }



    /**
     * Setter method
     * @param network 1 or 2 (rmi or tcp)
     */
    public void setNetwork(int network) {
        this.network = network;
    }



    /**
     * Setter method
     * @param s the string to put in the label
     */
    public void setLabelWithPlayerName(String s) {
        this.labelWithPlayerName.setText(s);
    }
}
