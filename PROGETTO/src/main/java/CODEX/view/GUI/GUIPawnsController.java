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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUIPawnsController {
    private GUIBaseCardController ctr;
    private Stage stage;
    private int network;
    private RMIClient rmiClient;
    private ClientSCK clientSCK;
    private boolean choosen = false;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private boolean pawnSelected=false;

    @FXML
    private Pane yellowPane;
    @FXML
    private Pane greenPane;
    @FXML
    private Pane bluePane;
    @FXML
    private Pane redPane;
    @FXML
    private Label labelWithPlayerName;
    @FXML
    private Label retryLabel;


    public void changeScene(){

        // let's show the new window!
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
                try {
                    rmiClient.handleDisconnectionFunction();
                } catch (RemoteException ignored) {

                }
            } else if (network == 2) {
                try {
                    clientSCK.handleDisconnectionFunction();
                } catch (RemoteException ignored) {

                }
            }
        });
        // setting the parameters in the new controller, also the BASE CARD (front and back)
        ctr.setStage(stage);
        ctr.setNetwork(network);
        ctr.setClientSCK(clientSCK);
        ctr.setRmiClient(rmiClient);
        if (network == 1) {
            while (rmiClient.getPersonalPlayer().getPlayerDeck()[0] == null) {
            }
            ctr.setBaseCard1(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId()); // OK
            ctr.setBaseCard2(rmiClient.getPersonalPlayer().getPlayerDeck()[0].getId());
            ctr.setLabelWithPlayerName(rmiClient.getPersonalPlayer().getNickname() + ", which side do you");
        } else if (network == 2) {
            while (clientSCK.getPersonalPlayer().getPlayerDeck()[0] == null) {
            }
            ctr.setBaseCard1(clientSCK.getPersonalPlayer().getPlayerDeck()[0].getId()); // OK
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

        stage.setScene(scene); //questo è il momento in cui la nuova scena viene mostrata

        // setting the od values of position and dimension
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);


        ctr.startPeriodicDisconnectionCheck();


        //stage.show(); //si fa solo se cambia lo stage

    }


    public void setLabelWithPlayerName(String s) {
        this.labelWithPlayerName.setText(s);
    }


    synchronized public void selectedYellow() {
        if (yellowPane.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {
                        rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.YELLOW);
                        choosen = true;

                        Platform.runLater(() -> {
                            greenPane.setOpacity(0);
                            bluePane.setOpacity(0);
                            redPane.setOpacity(0);
                            retryLabel.setText("You have chosen. Now wait a few.");
                            retryLabel.setOpacity(1);
                        });

                        rmiClient.getGameController().checkChosenPawnColor();
                        System.out.println("sto per entrare nella syn");
                        synchronized (rmiClient.getGuiPawnsControllerLock()) {
                            System.out.println("sono entrato nella syn");
                            if(!rmiClient.getDone()) {
                                System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                while (!rmiClient.getSecondUpdateRoundArrived()) {
                                    System.out.println("ho preso lock e sono in wait");
                                    try {
                                        rmiClient.getGuiPawnsControllerLock().wait();
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                System.out.println("sono uscito dal lock");
                            }
                        }
                        System.out.println("usciti dal syn");

                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }

                });

                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();

                // Platform.runLater(this::changeScene);
            } else if (network == 2) { //TCP
                executor.execute(() -> {
                    try {
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
                                greenPane.setOpacity(0);
                                bluePane.setOpacity(0);
                                redPane.setOpacity(0);
                                retryLabel.setText("You have chosen. Now wait a few.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor(); //to be implemented
                            System.out.println("sto per entrare nella syn");
                            synchronized (clientSCK.getGuiPawnsControllerLock()) {
                                System.out.println("sono entrato nella syn");
                                if (!clientSCK.getDone()) {
                                    System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                    while (!clientSCK.getSecondUpdateRoundArrived()) {
                                        System.out.println("ho preso lock e sono in wait");
                                        try {
                                            clientSCK.getGuiPawnsControllerLock().wait();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    System.out.println("sono uscito dal lock");
                                }
                            }
                            System.out.println("usciti dal syn");
                        }
                    } catch (Exception ignored) {
                    }

                });


                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();

            }
        }
    }

    synchronized public void selectedBlue() {
        if (bluePane.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {
                        rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.BLUE);
                        choosen = true;

                        Platform.runLater(() -> {
                            greenPane.setOpacity(0);
                            yellowPane.setOpacity(0);
                            redPane.setOpacity(0);
                            retryLabel.setText("You have chosen. Now wait a few.");
                            retryLabel.setOpacity(1);
                        });
                        rmiClient.getGameController().checkChosenPawnColor();
                        System.out.println("sto per entrare nella syn");
                        synchronized (rmiClient.getGuiPawnsControllerLock()) {
                            System.out.println("sono entrato nella syn");
                            if(!rmiClient.getDone()) {
                                System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                while (!rmiClient.getSecondUpdateRoundArrived()) {
                                    System.out.println("ho preso lock e sono in wait");
                                    try {
                                        rmiClient.getGuiPawnsControllerLock().wait();
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                System.out.println("sono uscito dal lock");
                            }
                        }
                        System.out.println("usciti dal syn");

                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }
                });

                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();
                //Platform.runLater(this::changeScene);
            } else if (network == 2) { //TCP
                executor.execute(() -> {
                    try {
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
                                greenPane.setOpacity(0);
                                yellowPane.setOpacity(0);
                                redPane.setOpacity(0);
                                retryLabel.setText("You have chosen. Now wait a few.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor(); //to be implemented
                            System.out.println("sto per entrare nella syn");
                            synchronized (clientSCK.getGuiPawnsControllerLock()) {
                                System.out.println("sono entrato nella syn");
                                if (!clientSCK.getDone()) {
                                    System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                    while (!clientSCK.getSecondUpdateRoundArrived()) {
                                        System.out.println("ho preso lock e sono in wait");
                                        try {
                                            clientSCK.getGuiPawnsControllerLock().wait();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    System.out.println("sono uscito dal lock");
                                }
                            }
                            System.out.println("usciti dal syn");
                        }
                    } catch (Exception ignored) {
                    }

                });


                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();

            }
        }
    }

    synchronized public void selectedGreen() {
        if (greenPane.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {
                        rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.GREEN);
                        choosen = true;
                        Platform.runLater(() -> {
                            yellowPane.setOpacity(0);
                            bluePane.setOpacity(0);
                            redPane.setOpacity(0);
                            retryLabel.setText("You have chosen. Now wait a few.");
                            retryLabel.setOpacity(1);
                        });
                        rmiClient.getGameController().checkChosenPawnColor();
                        System.out.println("sto per entrare nella syn");
                        synchronized (rmiClient.getGuiPawnsControllerLock()) {
                            System.out.println("sono entrato nella syn");
                            if(!rmiClient.getDone()) {
                                System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                while (!rmiClient.getSecondUpdateRoundArrived()) {
                                    System.out.println("ho preso lock e sono in wait");
                                    try {
                                        rmiClient.getGuiPawnsControllerLock().wait();
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                System.out.println("sono uscito dal lock");
                            }
                        }
                        System.out.println("usciti dal syn");

                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }
                });

                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();

                //   Platform.runLater(this::changeScene);
            } else if (network == 2) {
                executor.execute(() -> {
                    try {
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
                                yellowPane.setOpacity(0);
                                bluePane.setOpacity(0);
                                redPane.setOpacity(0);
                                retryLabel.setText("You have chosen. Now wait a few.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor(); //to be implemented
                            System.out.println("sto per entrare nella syn");
                            synchronized (clientSCK.getGuiPawnsControllerLock()) {
                                System.out.println("sono entrato nella syn");
                                if (!clientSCK.getDone()) {
                                    System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                    while (!clientSCK.getSecondUpdateRoundArrived()) {
                                        System.out.println("ho preso lock e sono in wait");
                                        try {
                                            clientSCK.getGuiPawnsControllerLock().wait();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    System.out.println("sono uscito dal lock");
                                }
                            }
                            System.out.println("usciti dal syn");
                        }
                    } catch (Exception ignored) {
                    }

                });


                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();

            }
        }
    }

    synchronized public void selectedRed() {
        if (redPane.getOpacity() != 0 && !pawnSelected) {
            pawnSelected = true;
            if (network == 1) {
                executor.execute(() -> {
                    try {
                        rmiClient.choosePawnColor(rmiClient.getPersonalPlayer().getNickname(), Pawn.RED);
                        choosen = true;
                        Platform.runLater(() -> {
                            greenPane.setOpacity(0);
                            bluePane.setOpacity(0);
                            yellowPane.setOpacity(0);
                            retryLabel.setText("You have chosen. Now wait a few.");
                            retryLabel.setOpacity(1);
                        });
                        rmiClient.getGameController().checkChosenPawnColor();
                        System.out.println("sto per entrare nella syn");
                        synchronized (rmiClient.getGuiPawnsControllerLock()) {
                            System.out.println("sono entrato nella syn");
                            if(!rmiClient.getDone()) {
                                System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                while (!rmiClient.getSecondUpdateRoundArrived()) {
                                    System.out.println("ho preso lock e sono in wait");
                                    try {
                                        rmiClient.getGuiPawnsControllerLock().wait();
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                System.out.println("sono uscito dal lock");
                            }
                        }
                        System.out.println("usciti dal syn");

                    } catch (RemoteException | NotBoundException e) {
                        throw new RuntimeException(e);
                    } catch (ColorAlreadyTakenException e) {
                        pawnSelected = false;
                        Platform.runLater(() -> {
                            retryLabel.setText("Color already taken, please choose another.");
                            retryLabel.setOpacity(1);
                        });
                    }
                });


                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();

                //  Platform.runLater(this::changeScene);
            } else if (network == 2) { //TCP
                executor.execute(() -> {
                    try {
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
                                greenPane.setOpacity(0);
                                bluePane.setOpacity(0);
                                yellowPane.setOpacity(0);
                                retryLabel.setText("You have chosen. Now wait a few.");
                                retryLabel.setOpacity(1);
                            });
                            clientSCK.checkChosenPawnColor(); //to be implemented
                            System.out.println("sto per entrare nella syn");
                            synchronized (clientSCK.getGuiPawnsControllerLock()) {
                                System.out.println("sono entrato nella syn");
                                if (!clientSCK.getDone()) {
                                    System.out.println("non ho ancora ricevuto l'update [done = false], quindi sto per prendere il lock");
                                    while (!clientSCK.getSecondUpdateRoundArrived()) {
                                        System.out.println("ho preso lock e sono in wait");
                                        try {
                                            clientSCK.getGuiPawnsControllerLock().wait();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    System.out.println("sono uscito dal lock");
                                }
                            }
                            System.out.println("usciti dal syn");
                        }
                    } catch (Exception ignored) {
                    }


                });


                PauseTransition pause = new PauseTransition(Duration.seconds(3)); // 2 secondi di ritardo
                pause.setOnFinished(event -> {
                    changeScene();
                });
                pause.play();

            }
        }
    }


    public void setColoredPanes() {
        retryLabel.setOpacity(0);
        yellowPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, null)));
        greenPane.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, null)));
        redPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, null)));
        bluePane.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, null)));
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

    synchronized public void updatePawns(Pawn pawn) {//se il giocatore ha già scelto non vede gli update degli altri
        if(!choosen) {//non mostro più il pawn arrivato
            Platform.runLater(()-> {
                if (pawn == Pawn.BLUE) {

                    bluePane.setOpacity(0);

                } else if (pawn == Pawn.YELLOW) {
                    yellowPane.setOpacity(0);
                } else if (pawn == Pawn.GREEN) {
                    greenPane.setOpacity(0);
                } else if (pawn == Pawn.RED) {
                    redPane.setOpacity(0);
                }
            });
        }
    }
}
