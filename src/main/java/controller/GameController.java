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
import model.Board;
import model.Game;

import java.io.IOException;

public class GameController  {
    @FXML private GridPane grid;
    @FXML private Button backButton;
    @FXML
    Board board;

    private Game gameInstance;


    // initialize view and model
    // set event listeners
    @FXML
    public void initialize() {
        // parts of this code are taken from the answers at https://stackoverflow.com/questions/35344702/how-do-i-get-buttons-to-fill-a-javafx-gridpane
        int numRows = 15 ;
        int numColumns = 26 ;
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

        Board board = new Board (numRows, numColumns, 100);
        this.board= board;

        for (int i = 0; i< numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Button button = createButton("");
                String content="";
                if(board.grid[i][j].hasMine){
                    content= "mine";
                }
                else {
                    content = Integer.toString(board.grid[i][j].numSurroundingMines);
                }
                Label label = createLabel(content);
                grid.add(label,j,i);
                grid.add(button, j, i );
            }
        }
        for (int i = 0 ; i < numRows*numColumns ; i++) {

        }


        // Event Listeners
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

        // TODO put number when exposed




    }



    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF; -fx-effect: dropshadow( gaussian , rgba(255,255,02550.4) , 10,0,0,1 )");

        //button.setStyle(" ");

        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setOnAction(e -> {Button tmp = (Button)e.getSource();
            tmp.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");});
        return button ;
    }

    private Label createLabel(String text){
        Label label = new Label(text);
        label.setStyle("-fx-background-color: #FFFFFF;");
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return label;
    }

}
