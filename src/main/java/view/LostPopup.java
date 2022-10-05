package view;

import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import static com.example.game.Welcome.getPrimaryStage;

public class LostPopup {

    private final Popup popup;
    private Button tryAgain;

    public LostPopup(){
        this.popup = new Popup();
        VBox vbox= new VBox();

        Button tryAgain = new Button("Try again");
        this.tryAgain=tryAgain;

        Text title= new Text();
        title.setFont(Font.font("Bauhaus 93", 36));
        title.setStyle("-fx-text-fill:#FFFFFF");
        title.setText("Oh no!");

        Text subtitle = new Text();
        subtitle.setFont(Font.font("Bauhaus 93", 24));
        subtitle.setStyle("-fx-text-fill:#FFFFFF");
        subtitle.setText("You disrespected too many peatlands during the construction of your planet. It cannot keep the balance like that.");

        vbox.getChildren().addAll(title, subtitle, tryAgain);
        vbox.setStyle("-fx-background-color:#D74F4C; -fx-border-width:2;-fx-border-radius:3;-fx-hgap:3;-fx-vgap:5;");


        /*double width = getPrimaryStage().getWidth() / 1.5;
        double height = getPrimaryStage().getHeight() / 1.5;

        //vbox.prefWidthProperty().bind(getPrimaryStage().widthProperty().multiply(0.80));
        for(Node child : vbox.getChildren()) {
            VBox.setVgrow(child, Priority.ALWAYS);
        }
        vbox.setAlignment(Pos.CENTER);*/

        popup.getContent().addAll(vbox);
    }

    public Button getTryAgainButton() {
        return tryAgain;
    }

    public Popup getPopup() {
        return popup;
    }

}
