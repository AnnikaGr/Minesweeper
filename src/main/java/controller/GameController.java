package controller;

import com.example.game.Welcome;
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

import java.io.IOException;

public class GameController  {
    @FXML private GridPane grid;
    @FXML private Button backButton;
    @FXML private Board board;
    @FXML private Text infoText;
    @FXML private Text buildingCount;
    @FXML private ProgressBar temperatureBar;
    @FXML private HBox waterBox;
    @FXML private HBox researchBox;

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
//            numRows = 10 ;
//            numColumns = 19 ;
            numRows = 2 ;
            numColumns = 2 ;
        }

        //set climate relevant area size = number of mines
        int numMines=100;
        if ( gameInstance.isProtectedBig()){
            numMines = 200;
        }
        else{
            //numMines = 100;
            numMines = 0;
        }

        int numWells=100;

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
                        System.out.println("Scroll on grid");

                            if(board.grid[row][col].exposed&& board.grid[row][col].hasWell){
                                System.out.println("Scroll on well");
                                board.grid[row][col].hasWell = false;
                                gameInstance.increaseWaterAvailable(numWells);

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
                            Label node = (Label)getNodeFromGridPane(grid, col, row);
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
                });

    });
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
                if(mouseEvent.getButton().equals(MouseButton.SECONDARY)){
                    Button tmp = (Button)mouseEvent.getSource();
                    int row= grid.getRowIndex(tmp);
                    int col= grid.getColumnIndex(tmp);
                    if(!board.grid[row][col].marked){
                        tmp.setStyle("-fx-background-color: #99B931; -fx-border-color: #D94D3C; -fx-border-width: 3px;");
                    }
                    else{
                        tmp.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF;");
                    }
                    board.mark(col, row);
                }
                else if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    Button tmp = (Button)mouseEvent.getSource();
                    tmp.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
                    int status = board.expose(grid.getColumnIndex(tmp), grid.getRowIndex(tmp));
                    handleGameState(status);
                }
            }
        });

        button.setOnScrollStarted(e -> {Button tmp = (Button)e.getSource();
            tmp.setStyle("-fx-background-color: #DBECFF;");
            System.out.println("mouse scroll happening");
        });



        return button ;
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
        label.setOnScroll(e -> {Label tmp = (Label)e.getSource();
            tmp.setStyle("-fx-background-color: #DBECFF;");
            System.out.println("mouse scroll happening");
        });

        /*ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = getClass().getResourceAsStream("/house.png");
        ImageView imageView = new ImageView(new Image(GameController.class.getClassLoader().getResourceAsStream("./com/example/game/images")));
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(grid.widthProperty().subtract(10d));
        imageView.fitHeightProperty().bind(grid.heightProperty().subtract(10d));
        return imageView;*/
        return label;
    }

    private void handleGameState (int status){
        if (status==-1) { // game over, exposed mine
            increaseTemperatureBar();
        }
        else if(status ==-3){
            increaseTemperatureBar();
            GameStatePopup popup= new GameStatePopup("Oh no!",
                    "You disrespected too many peatlands during the construction of your planet. It cannot keep the balance like that.",
                    "Try again");
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
        else if(status ==-4){

            GameStatePopup popup= new GameStatePopup("Congratulations!",
                    "You successfully managed our planet accommodating everybody while keeping its balance.",
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
        else {
            buildingCount.setText(Integer.toString(board.numExposedCells));
        }

    }

    private void increaseTemperatureBar(){
        double old = temperatureBar.getProgress();
        temperatureBar.setProgress(old+0.25);
    }

    private void returnToWelcome(){
        Parent newRoot = null;
        try {
            newRoot = FXMLLoader.load(Welcome.class.getResource("welcome-view.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Welcome.getPrimaryStage().getScene().setRoot(newRoot);
    }

        private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
            for (Node node : gridPane.getChildren()) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return node;
                }
            }
            return null;
        }


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


}
