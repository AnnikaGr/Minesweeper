package controller;

import com.example.game.Main;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Board;
import model.Game;
import view.GameStatePopup;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static controller.GameFlowUtil.delay;
import static controller.NoiseDetectionUtil.detectNoise;
import static view.ViewUtil.*;

public class GameController {
    @FXML
    private GridPane grid;
    @FXML
    private Board board;
    @FXML
    private Button backButton;
    @FXML
    private Text buildingCount;
    @FXML
    private ProgressBar temperatureBar;
    @FXML
    private HBox waterBox;
    @FXML
    private HBox researchBox;
    @FXML
    private Text timer;

    private Game gameInstance;
    private long dragStarted;

    public GameController(Game model) {
        this.gameInstance = model;
    }

    // -- create game basis ------------------------------------------------------------------------------------
    @FXML
    public void initialize() {
        // set planet size
        int numRows;
        int numColumns;
        if (gameInstance.isPlanetBig()) {
            numRows = 15;
            numColumns = 26;
        } else {
            numRows = 10;
            numColumns = 19;
        }

        //set peatland frequency = number of mines
        int numMines;
        if (gameInstance.isProtectedBig()) {
            numMines = 100;
        } else {
            numMines = 50;
        }

        // set number of wells available on planet
        int numWells = 3;

        // create Model
        Board board = new Board(numRows, numColumns, numMines, numWells);
        this.board = board;

        // create View: Grid with Buttons
        for (int row = 0; row < numRows; row++) {
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }
        for (int col = 0; col < numColumns; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Button button = createButton("");
                String content = "";
                if (board.grid[i][j].hasMine) {
                    content = "mine";
                } else if (board.grid[i][j].hasWater) {
                    content = "water";
                } else if (board.grid[i][j].hasWell) {
                    content = "well";
                } else {
                    content = Integer.toString(board.grid[i][j].numSurroundingMines);
                }
                Label label = createCellContent(content);
                grid.add(label, j, i);
                grid.add(button, j, i);
            }
        }

        // Set Event Listeners
        backButton.setOnAction(e -> {
            returnToWelcome();
        });
        initializeWellInteraction(numWells);
        initializeResearchInteraction();
    }

    private Label createCellContent(String text) {
        Label label = new Label(text);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        //Cell content styling
        label.setTextFill(Color.color(1, 1, 1));
        if (text.equals("mine")) {
            label.setStyle("-fx-background-color: #D74F4C;");
        } else if (text.equals("water")) {
            label.setStyle("-fx-background-color: #0069D9;");
        } else if (text.equals("well")) {
            label.setStyle("-fx-background-color: #4DA3FF;");
        } else {
            label.setStyle("-fx-background-color: #404040;");
        }
        return label;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        updateNodeAsUncoveredButton(button);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //start timer on game start
                if (!board.isTimerSet) {
                    setTimer();
                    board.isTimerSet = true;
                }
                // flag on right click
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    Button tmp = (Button) mouseEvent.getSource();
                    int row = GridPane.getRowIndex(tmp);
                    int col = GridPane.getColumnIndex(tmp);
                    if (board.grid[row][col].exposed || board.grid[row][col].mineExposed) {
                        return;
                    }
                    if (!board.grid[row][col].marked) {
                        updateNodeViewAsNonMarked(tmp);
                    } else {
                        updateNodeViewAsMarked(tmp);
                    }
                    board.mark(col, row);

                    // uncover on left click
                } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Button currentField = (Button) mouseEvent.getSource();
                    int row = GridPane.getRowIndex(currentField);
                    int col = GridPane.getColumnIndex(currentField);
                    if (!board.grid[row][col].exposed && !board.grid[row][col].mineExposed) {
                        updateNodeViewAsUncovered(currentField);
                        int status = board.expose(col, row);
                        handleGameState(status, row, col, currentField);
                    }
                }
            }
        });
        return button;
    }

    // -- handle gameplay ------------------------------------------------------------------------------------------------

    private void handleGameState(int status, int row, int col, Button currentField) {
        // mine uncovered and water available
        if (status == -1 && gameInstance.getWaterAvailable() > 0) {
            //View
            GameStatePopup popup = new GameStatePopup("Oh no! You built on peatland!",
                    " Fortunately you collected water before. Quickly blow in your mic to use it to restore the area and cool down the planet!", false,
                    "");
            popup.getPopup().show(Main.getPrimaryStage());
            delay(3000, () -> startBlowingInteraction(popup, currentField));
        }
        // mine uncovered but game not lost yet
        if (status == -1) {
            increaseTemperatureBar();
        }
        // mine uncovered and game lost
        else if (status == -3) {
            increaseTemperatureBar();
            GameStatePopup popup = new GameStatePopup("Oh no!",
                    "You disrespected too many peatlands during the construction of your planet. It cannot keep the balance like that.", true,
                    "Try again");
            Button tryAgain = popup.getTryAgainButton();
            tryAgain.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    popup.getPopup().hide();
                    returnToWelcome();
                }
            });
            popup.getPopup().show(Main.getPrimaryStage());
            //game won
        } else if (status == -4) {
            gameWon();
            //no surrounding bombs on this cell
        } else if (status == 0) {
            boolean won = exposeSurroundings(col, row);
            if (won) {
                gameWon();
            }
        } else {
            buildingCount.setText(Integer.toString(board.numExposedCells));
        }
    }

    private boolean exposeSurroundings(int col, int row) {
        if (!board.isExposed(col - 1, row)) {
            int status = board.expose(col - 1, row);
            Button currentField = (Button) getNodeFromGridPane(grid, col - 1, row, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col - 1, row);
            }
            if (status == -4) {
                return true;
            }
        }
        if (!board.isExposed(col, row - 1)) {
            int status = board.expose(col, row - 1);
            Button currentField = (Button) getNodeFromGridPane(grid, col, row - 1, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col, row - 1);
            }
            if (status == -4) {
                return true;
            }
        }
        if (!board.isExposed(col + 1, row)) {
            int status = board.expose(col + 1, row);
            Button currentField = (Button) getNodeFromGridPane(grid, col + 1, row, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col + 1, row);
            }
            if (status == -4) {
                return true;
            }
        }
        if (!board.isExposed(col, row + 1)) {
            int status = board.expose(col, row + 1);
            Button currentField = (Button) getNodeFromGridPane(grid, col, row + 1, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col, row + 1);
            }
            if (status == -4) {
                return true;
            }
        }
        if (!board.isExposed(col + 1, row + 1)) {
            int status = board.expose(col + 1, row + 1);
            Button currentField = (Button) getNodeFromGridPane(grid, col + 1, row + 1, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col + 1, row + 1);
            }
            if (status == -4) {
                return true;
            }
        }
        if (!board.isExposed(col - 1, row - 1)) {
            int status = board.expose(col - 1, row - 1);
            Button currentField = (Button) getNodeFromGridPane(grid, col - 1, row - 1, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col - 1, row - 1);
            }
            if (status == -4) {
                return true;
            }
        }
        if (!board.isExposed(col + 1, row - 1)) {
            int status = board.expose(col + 1, row - 1);
            Button currentField = (Button) getNodeFromGridPane(grid, col + 1, row - 1, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col + 1, row - 1);
            }
            if (status == -4) {
                return true;
            }
        }
        if (!board.isExposed(col - 1, row + 1)) {
            int status = board.expose(col - 1, row + 1);
            Button currentField = (Button) getNodeFromGridPane(grid, col - 1, row + 1, "Button");
            if (currentField != null) {
                updateNodeViewAsUncovered(currentField);
            }
            if (status == 0) {
                exposeSurroundings(col - 1, row + 1);
            }
            if (status == -4) {
                return true;
            }
        }
        return false;
    }

    // -- Interactions -----------------------------------------------------------------------------------------

    private void startBlowingInteraction(GameStatePopup popup, Button currentField) {
        if (detectNoise(5)) {
            popup.setTitle("Great! You restored the peatland using 1 water!");
            popup.setSubtitle("");
            delay(1500, () -> popup.getPopup().hide());
            currentField.setStyle("-fx-background-color: #975C4E; -fx-border-color: #FFFFFF;");
            gameInstance.decreaseWaterAvailable();
            board.grid[GridPane.getRowIndex(currentField)][GridPane.getColumnIndex(currentField)].hasMine = false;
            board.numBombsHit = board.numBombsHit - 1;
            updateWaterAvailableBar();
            decreaseTemperatureBar();
        } else {
            popup.setTitle("Oh no! You didn´t blow enough to restore this peatland!");
            popup.setSubtitle("");
            delay(1500, () -> popup.getPopup().hide());
        }

    }

    private void initializeWellInteraction(int numWells) {
        grid.getChildren().forEach(item -> {
            // well interaction
            item.setOnScroll(new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent scrollEvent) {
                    Node tmp = (Node) scrollEvent.getSource();
                    int row = GridPane.getRowIndex(tmp);
                    int col = GridPane.getColumnIndex(tmp);

                    if (board.grid[row][col].exposed && board.grid[row][col].hasWell) {
                        board.grid[row][col].hasWell = false;
                        updateNodeViewAsEmptyWell(tmp);
                        gameInstance.increaseWaterAvailable(numWells);
                        updateWaterAvailableBar();
                    }
                }
            });
        });
    }

    private void initializeResearchInteraction() {
        grid.getChildren().forEach(item -> {
            // research interaction
            item.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    item.startFullDrag();
                    dragStarted = System.currentTimeMillis();
                }
            });
            item.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
                    if (gameInstance.getResearchAvailable() <= 0 || System.currentTimeMillis() - dragStarted > 1000) {
                        event.consume();
                    } else {
                        Node tmp = (Node) event.getSource();
                        updateNodeViewAsUncovered(tmp);
                        delay(1500, () -> updateNodeAsUncoveredButton(tmp));
                    }
                }
            });
            item.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
                    gameInstance.decreaseResearchAvailable();
                    updateResearchAvailableBar();
                }
            });
        });
    }


    // --update game status views ------------------------------------------------------------------------------------
    public void updateResearchAvailableBar() {
        int researchAvailable = gameInstance.getResearchAvailable();
        ObservableList<Node> buttons = researchBox.getChildren();
        for (Node button : buttons
        ) {
            researchAvailable--;
            if (researchAvailable >= 0) {
                displayItemAsAvailable(button);
            } else {
                displayItemAsUnavailable(button);
            }
        }
    }

    public void updateWaterAvailableBar() {
        int waterAvailable = gameInstance.getWaterAvailable();
        ObservableList<Node> buttons = waterBox.getChildren();
        for (Node button : buttons
        ) {
            waterAvailable--;
            if (waterAvailable >= 0) {
                displayItemAsAvailable(button);
            } else {
                displayItemAsUnavailable(button);
            }
        }
    }

    private void increaseTemperatureBar() {
        double old = temperatureBar.getProgress();
        temperatureBar.setProgress(old + 0.25);
    }

    private void decreaseTemperatureBar() {
        double old = temperatureBar.getProgress();
        temperatureBar.setProgress(old - 0.25);
    }

    // -- timer -----------------------------------------------------------------------------------------

    public void setTimer() {

        Board.timer = new Timer();
        TimerTask task = new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        timer.setText(i + "");
                        i++;
                    }
                });
            }
        };

        Board.timer.schedule(task, 0, 1000);
    }

    public void restartTime() {

        Platform.runLater(new Runnable() {
            public void run() {
                if (Board.isTimerSet) {
                    Board.timer.cancel();
                    Board.isTimerSet = false;
                    timer.setText("0");
                }
            }
        });
    }

    public void stopTime() {

        Platform.runLater(new Runnable() {
            public void run() {
                if (Board.isTimerSet) {
                    Board.timer.cancel();
                    Board.isTimerSet = false;
                }
            }
        });
    }

    // -- end game -----------------------------------------------------------------------------------------
    private void returnToWelcome() {
        restartTime();
        Parent newRoot = null;
        try {
            newRoot = FXMLLoader.load(Main.class.getResource("welcome-view.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Main.getPrimaryStage().getScene().setRoot(newRoot);
    }

    // -- util methods ----------------------------------------------------------------------------------
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row, String type) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                if (type.equals("Label")) {
                    if (node instanceof Label) {
                        return node;
                    }

                } else if (type.equals("Button")) {
                    if (node instanceof Button) {
                        return node;
                    }

                } else {
                    throw new IllegalArgumentException("No child of specified type found in grid cell");
                }

            }
        }
        return null;
    }

    private void gameWon() {
        GameStatePopup popup = new GameStatePopup("Congratulations!",
                "You successfully managed our planet accommodating everybody while keeping its balance.", true,
                "Play again");
        popup.getPopup().centerOnScreen();
        Button tryAgain = popup.getTryAgainButton();
        tryAgain.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.getPopup().hide();
                returnToWelcome();
            }
        });
        popup.getPopup().show(Main.getPrimaryStage());
    }
}
