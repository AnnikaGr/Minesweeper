package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import static com.example.game.Main.getPrimaryStage;

public class GameStatePopup {

    private final Popup popup;
    private Button tryAgain;
    private Text title;
    private Text subtitle;

    public GameStatePopup(String titleText, String subtitleText, boolean setButton, String buttonText) {
        this.popup = new Popup();
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(1240);
        vbox.setPrefHeight(400);
        vbox.prefWidthProperty().bind(getPrimaryStage().getScene().widthProperty());
        vbox.prefHeightProperty().bind(getPrimaryStage().getScene().heightProperty());

        Button tryAgain = new Button();
        this.tryAgain = tryAgain;
        tryAgain.setFont(Font.font("Bauhaus 93", 36));
        tryAgain.setStyle("-fx-text-fill:#FFFFFF; -fx-background-color:  #975C4E; -fx-effect:  dropshadow( gaussian , rgba(0,0,0,0.4) , 10,0,0,1 )");
        tryAgain.setText(buttonText);


        Text title = new Text();
        title.setStyle("-fx-fill:#FFFFFF");
        title.setFont(Font.font("Bauhaus 93", 36));
        title.setText(titleText);
        this.title = title;

        Text subtitle = new Text();
        subtitle.setFont(Font.font("Bauhaus 93", 24));
        subtitle.setStyle("-fx-fill:#FFFFFF");
        subtitle.setText(subtitleText);
        this.subtitle = subtitle;

        if (setButton) {
            vbox.getChildren().addAll(title, subtitle, tryAgain);
        } else {
            vbox.getChildren().addAll(title, subtitle);
        }

        vbox.setStyle("-fx-background-color:#975C4E; -fx-border-width:2;-fx-border-radius:3;-fx-hgap:3;-fx-vgap:5;");


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

    public void setSubtitle(String subtitle) {
        this.subtitle.setText(subtitle);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

}
