package controller;

import com.example.game.Welcome;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Board;
import model.Game;
import view.GameStatePopup;

import javax.sound.sampled.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameController  {
    @FXML private GridPane grid;
    @FXML private Button backButton;
    @FXML private Board board;
    @FXML private Text infoText;
    @FXML private Text buildingCount;
    @FXML private ProgressBar temperatureBar;
    @FXML private HBox waterBox;
    @FXML private HBox researchBox;
    @FXML private Text timer;

    private Game gameInstance;

    public GameController(Game model) {
        // ensure model is only set once:
        if (this.gameInstance != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.gameInstance = model ;
    }


    // initialize view and model
    // set event listeners
    @FXML
    public void initialize() {
        // set planet size
        int numRows=1;
        int numColumns =1;
        if ( gameInstance.isPlanetBig()){
            numRows = 15 ;
            numColumns = 26 ;
        }
        else{
            numRows = 10 ;
            numColumns = 19 ;
        }

        //set climate relevant area size = number of mines
        int numMines=100;
        if ( gameInstance.isProtectedBig()){
            numMines = 0;
        }
        else{
            numMines = 50;
        }

        int numWells=3;

        // create Model
        Board board = new Board (numRows, numColumns, numMines, numWells);
        this.board= board;

        // create grid of buttons
        // parts of this code are taken from the answers at https://stackoverflow.com/questions/35344702/how-do-i-get-buttons-to-fill-a-javafx-gridpane
        for (int row = 0 ; row < numRows ; row++ ){
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }
        for (int col = 0 ; col < numColumns; col++ ) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0; i< numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Button button = createButton("");
                String content="";
                if(board.grid[i][j].hasMine){
                    content= "mine";
                }
                else if (board.grid[i][j].hasWater){
                    content = "water";
                }
                else if (board.grid[i][j].hasWell){
                    content = "well";
                }
                else {
                    content = Integer.toString(board.grid[i][j].numSurroundingMines);
                }
                //

                Label label = createCellContent(content);
                grid.add(label,j,i);
                grid.add(button, j, i );
            }
        }

        // Other Event Listeners
        //back button
        backButton.setOnAction(e -> {
            returnToWelcome();
        });


            grid.getChildren().forEach(item -> {
                item.setOnScroll( new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent scrollEvent) {
                            Node tmp = (Node)scrollEvent.getSource();
                            int row= grid.getRowIndex(tmp);
                            int col= grid.getColumnIndex(tmp);

                            if(board.grid[row][col].exposed&& board.grid[row][col].hasWell){
                                board.grid[row][col].hasWell = false;
                                updateNodeViewAsEmptyWell(tmp);
                                gameInstance.increaseWaterAvailable(numWells);
                                updateWaterAvailableBar();
                            }
                    }
    });

                item.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        item.startFullDrag();
                    }
                });

                item.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
                    @Override
                    public void handle(MouseDragEvent event) {
                        if(gameInstance.getResearchAvailable()<=0){
                            event.consume();
                        }
                        else{
                            Node tmp = (Node)event.getSource();
                            int row= grid.getRowIndex(tmp);
                            int col= grid.getColumnIndex(tmp);
                            Label node = (Label)getNodeFromGridPane(grid, col, row, "Label");
                            tmp.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
                            delay(1500, () -> tmp.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF; -fx-effect: dropshadow( gaussian , rgba(255,255,02550.4) , 10,0,0,1 )") );
                            //TODO set maximum drag and drop distance
                        }
                    }
                });

                item.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        gameInstance.decreaseResearchAvailable();
                        updateResearchAvailableBar();

                    }
                });

    });


    }

    public boolean detectNoise(int detectionTime){

        //Audio Detection
        ByteArrayOutputStream byteArrayOutputStream;
        TargetDataLine targetDataLine;
        int cnt;
        byte tempBuffer[] = new byte[1024*4];
        int countzero, countdownTimer;
        short convert[] = new short[tempBuffer.length];



        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            countdownTimer = 0;
            while (countdownTimer<detectionTime) {
                AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, false);
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                byteArrayOutputStream.write(tempBuffer, 0, cnt);

                int level = 0;
                if (targetDataLine.read(tempBuffer, 0, tempBuffer.length) > 0) {
                    level = calculateRMSLevel(tempBuffer);
                    if (level > 40 ){
                        Thread.sleep(0);
                        targetDataLine.close();
                        return true;
                    }
                }

                try {
                    countzero = 0;
                    for (int i = 0; i < tempBuffer.length; i++) {
                        convert[i] = tempBuffer[i];
                        if (convert[i] == 0) {
                            countzero++;
                        }
                    }

                    countdownTimer++;

                    System.out.println(countzero + " " + countdownTimer + " Level "+ level);

                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
                Thread.sleep(0);
                targetDataLine.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public int calculateRMSLevel(byte[] audioData)
    {
        long lSum = 0;
        for(int i=0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for(int j=0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }


    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF; -fx-effect: dropshadow( gaussian , rgba(255,255,02550.4) , 10,0,0,1 )");

        //button.setStyle(" ");

        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //Event Listeners
        button.setOnMouseClicked(e -> {Button tmp = (Button)e.getSource();
            tmp.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");});

        // -- with help from https://stackoverflow.com/questions/34171841/javafx-minesweeper-how-to-tell-between-right-and-left-mouse-button-input
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //start timer on game start
                if (!board.isTimerSet) {
                    setTimer();
                    board.isTimerSet = true;
                }

                if(mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                    Button tmp = (Button)mouseEvent.getSource();
                    int row= grid.getRowIndex(tmp);
                    int col= grid.getColumnIndex(tmp);
                    if(board.grid[row][col].exposed || board.grid[row][col].mineExposed){
                        return;
                    }
                    if(!board.grid[row][col].marked){
                        tmp.setStyle("-fx-background-color: #99B931; -fx-border-color: #D94D3C; -fx-border-width: 3px;");
                    }
                    else{
                        tmp.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF;");
                    }
                    board.mark(col, row);
                }
                else if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    Button currentField = (Button)mouseEvent.getSource();
                    int row= grid.getRowIndex(currentField);
                    int col= grid.getColumnIndex(currentField);
                    if(!board.grid[row][col].exposed&& !board.grid[row][col].mineExposed){
                        //uncoverField(currentField);
                        currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
                        int status = board.expose(col, row);
                        if(status == -1 && gameInstance.getWaterAvailable()>0){
                            //View
                            GameStatePopup popup= new GameStatePopup("Oh no! You built on peatland!",
                                    " Fortunately you collected water before. Quickly blow in your mic to use it to restore the area and cool down the planet!", false,
                                    "");
                            popup.getPopup().show(Welcome.getPrimaryStage());
                            delay(3000, () -> startBlowingInteraction(popup, currentField));
                        }
                        handleGameState(status, row, col);
                    }
                }

                //TODO handle drag
            }
        });
        return button ;
    }

    private void startBlowingInteraction(GameStatePopup popup, Button currentField){
        if(detectNoise(5)){
                            popup.setTitle("Great! You restored the peatland using 1 water!");
                            popup.setSubtitle("");
                            delay(1500, () -> popup.getPopup().hide());
                            currentField.setStyle("-fx-background-color: #975C4E; -fx-border-color: #FFFFFF;");
                            gameInstance.decreaseWaterAvailable();
                            board.grid[grid.getRowIndex(currentField)][grid.getColumnIndex(currentField)].hasMine = false;
                            board.numBombsHit= board.numBombsHit-1;
                            updateWaterAvailableBar();
                            decreaseTemperatureBar();
                        }
                        else{
                            popup.setTitle("Oh no! You didn´t blow enough to restore this peatland!");
                            popup.setSubtitle("");
                            delay(1500, () -> popup.getPopup().hide());
                        }

    }

    private Label createCellContent(String text){
        Label label = new Label(text);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        //Cell content styling
        label.setTextFill(Color.color(1, 1, 1));
        if (text== "mine"){
            label.setStyle("-fx-background-color: #D74F4C;");
        }
        else if (text== "water"){
            label.setStyle("-fx-background-color: #0069D9;");
        }
        else if (text == "well"){
            label.setStyle("-fx-background-color: #4DA3FF;");
        }
        else {
            label.setStyle("-fx-background-color: #404040;");
        }

        //Event Listener

        /*ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = getClass().getResourceAsStream("/house.png");
        ImageView imageView = new ImageView(new Image(GameController.class.getClassLoader().getResourceAsStream("./com/example/game/images")));
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(grid.widthProperty().subtract(10d));
        imageView.fitHeightProperty().bind(grid.heightProperty().subtract(10d));
        return imageView;*/
        return label;
    }

    private void handleGameState (int status, int row, int col){
        if (status==-1) { // mine uncovered but game not lost yet
            increaseTemperatureBar();
        }
        else if(status ==-3){ // mine uncovered and game lost
            increaseTemperatureBar();
            GameStatePopup popup= new GameStatePopup("Oh no!",
                    "You disrespected too many peatlands during the construction of your planet. It cannot keep the balance like that.", true,
                    "Try again");

            // Event handler
            Button tryAgain= popup.getTryAgainButton();
            tryAgain.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    popup.getPopup().hide();
                    returnToWelcome();
                }
            });

            popup.getPopup().show(Welcome.getPrimaryStage());

        }
        else if(status ==-4){ //game won

            GameStatePopup popup= new GameStatePopup("Congratulations!",
                    "You successfully managed our planet accommodating everybody while keeping its balance.",true,
                    "Play again");
            popup.getPopup().centerOnScreen();

            // Event handler
            Button tryAgain= popup.getTryAgainButton();
            tryAgain.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    popup.getPopup().hide();
                    returnToWelcome();
                }
            });

            popup.getPopup().show(Welcome.getPrimaryStage());

        }
        else if (status==0){ //no surrounding bombs on this cell
                exposeSurroundings( col, row);
        }
        else {
            buildingCount.setText(Integer.toString(board.numExposedCells));
        }

    }

    private boolean exposeSurroundings(int col, int row){
        //TODO handle b
        System.out.println("Row: "+ row + "Column: "+ col);
        if(!board.isExposed(col-1, row)){
            int status= board.expose(col-1, row);
            Button currentField = (Button) getNodeFromGridPane(grid, col-1, row, "Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col-1, row);
            }
        }
        if(!board.isExposed(col, row-1)){
            int status=board.expose(col, row-1);
            Button currentField = (Button) getNodeFromGridPane(grid, col, row-1, "Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col, row-1);
            }
        }
        if(!board.isExposed(col+1, row)){
            int status=board.expose(col+1, row);
            Button currentField = (Button) getNodeFromGridPane(grid, col+1, row, "Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col+1, row);
            }
        }
        if(!board.isExposed(col, row+1)){
            int status=board.expose(col, row+1);
            Button currentField = (Button) getNodeFromGridPane(grid, col, row+1, "Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col, row+1);
            }
        }
        if(!board.isExposed(col+1, row+1)){
            int status=board.expose(col+1, row+1);
            Button currentField = (Button) getNodeFromGridPane(grid, col+1, row+1, "Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col+1, row+1);
            }
        }
        if(!board.isExposed(col-1, row-1)){
            int status=board.expose(col-1, row-1);
            Button currentField = (Button) getNodeFromGridPane(grid, col-1, row-1,"Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col-1, row-1);
            }
        }
        if(!board.isExposed(col+1, row-1)){
            int status=board.expose(col+1, row-1);
            Button currentField = (Button) getNodeFromGridPane(grid, col+1, row-1,"Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col+1, row-1);
            }
        }
        if(!board.isExposed(col-1, row+1)){
            int status= board.expose(col-1, row+1);
            Button currentField = (Button) getNodeFromGridPane(grid, col-1, row+1,"Button");
            if(currentField!=null){
                currentField.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
            }
            if(status==0){
                exposeSurroundings(col-1, row+1);
            }
        }
        return true;
    }


    // -- navigation methods ----------------------------------------------------------------------------------

    private void returnToWelcome(){
        restartTime();
        Parent newRoot = null;
        try {
            newRoot = FXMLLoader.load(Welcome.class.getResource("welcome-view.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Welcome.getPrimaryStage().getScene().setRoot(newRoot);
    }

        private Node getNodeFromGridPane(GridPane gridPane, int col, int row, String type) {
            for (Node node : gridPane.getChildren()) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    if(type.equals("Label")){
                        if(node instanceof Label){
                            return node;
                        }

                    }
                    else if (type.equals("Button")){
                        if(node instanceof Button){
                            return node;
                        }

                    }
                    else{
                        throw new IllegalArgumentException("No child of specified type found in grid cell");
                    }

                }
            }
            return null;
        }


    // --helper methods--------------------------------------------------------------------------------------------
    // code from https://stackoverflow.com/questions/26454149/make-javafx-wait-and-continue-with-code
    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }


    // --updating view methods------------------------------------------------------------------------------------
    public void updateResearchAvailableBar(){
        int researchAvailable = gameInstance.getResearchAvailable();
        ObservableList<Node> buttons = researchBox.getChildren();
        for (Node button:buttons
        ) {
            researchAvailable--;
            if(researchAvailable>=0){
                button.setStyle("-fx-background-color:  #78BBCF; -fx-border-color: #FFFFFF;");
            }
            else {
                button.setStyle("-fx-background-color:  #d3d3d3; -fx-border-color: #FFFFFF;");
            }
        }
    }

    public void updateWaterAvailableBar(){
        int waterAvailable = gameInstance.getWaterAvailable();
        ObservableList<Node> buttons = waterBox.getChildren();
        for (Node button:buttons
        ) {
            waterAvailable--;
            if(waterAvailable>=0){
                button.setStyle("-fx-background-color:  #78BBCF; -fx-border-color: #FFFFFF;");
            }
            else {
                button.setStyle("-fx-background-color:  #d3d3d3; -fx-border-color: #FFFFFF;");
            }
        }
    }

    private void increaseTemperatureBar(){
        double old = temperatureBar.getProgress();
        temperatureBar.setProgress(old+0.25);
    }

    private void decreaseTemperatureBar(){
        double old = temperatureBar.getProgress();
        temperatureBar.setProgress(old-0.25);
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

    // -- update view with empty well --------------------------------------------------------------------------
    private void updateNodeViewAsEmptyWell(Node tmp){
        tmp.setStyle("-fx-background-color: #4682B4; -fx-border-color: #FFFFFF;");
    };


}
