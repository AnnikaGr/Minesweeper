package controller;

import com.example.game.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController  {
    @FXML private GridPane grid;



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

        for (int i = 0 ; i < numRows*numColumns ; i++) {
            Button button = createButton("");
            Label label = createLabel(" house");
            grid.add(label,i%26,i/26);
            grid.add(button, i % 26, i / 26);
        }



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
