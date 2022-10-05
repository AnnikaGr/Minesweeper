package controller;

import com.example.game.Welcome;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Board;
import model.Game;

import java.io.IOException;

public class GameController  {
    @FXML private GridPane grid;
    @FXML private Button backButton;
    @FXML private Board board;
    @FXML private Text infoText;

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
        // parts of this code are taken from the answers at https://stackoverflow.com/questions/35344702/how-do-i-get-buttons-to-fill-a-javafx-gridpane

        // set planet size
        int numRows=1;
        int numColumns =1;
        if ( gameInstance.isPlanetBig()){
            numRows = 20 ;
            numColumns = 32 ;
        }
        else{
            numRows = 15 ;
            numColumns = 26 ;
        }

        //set climate relevant area size = number of mines
        int numMines=100;
        if ( gameInstance.isProtectedBig()){
            numMines = 200;
        }
        else{
            numMines = 100;
        }

        // create Model
        Board board = new Board (numRows, numColumns, numMines);
        this.board= board;

        // create grid of buttons
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
            Parent newRoot = null;
            try {
                newRoot = FXMLLoader.load(Welcome.class.getResource("welcome-view.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Welcome.getPrimaryStage().getScene().setRoot(newRoot);
        });


    }



    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF; -fx-effect: dropshadow( gaussian , rgba(255,255,02550.4) , 10,0,0,1 )");

        //button.setStyle(" ");

        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //Event Listener
        button.setOnMouseClicked(e -> {Button tmp = (Button)e.getSource();
            tmp.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");});

        return button ;
    }

    private Label createCellContent(String text){
        Label label = new Label(text);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

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

}
