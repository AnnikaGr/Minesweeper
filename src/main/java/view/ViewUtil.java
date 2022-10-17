package view;

import javafx.scene.Node;

public final class ViewUtil {
    public static void updateNodeViewAsEmptyWell(Node node) {
        node.setStyle("-fx-background-color: #4682B4; -fx-border-color: #FFFFFF;");
    }

    public static void updateNodeViewAsUncovered(Node node) {
        node.setStyle("-fx-background-color: #00000000; -fx-border-color: #FFFFFF;");
    }

    public static void updateNodeViewAsNonMarked(Node node) {
        node.setStyle("-fx-background-color: #99B931; -fx-border-color: #D94D3C; -fx-border-width: 3px;");
    }

    public static void updateNodeViewAsMarked(Node node) {
        node.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF;");
    }

    public static void updateNodeAsUncoveredButton(Node node) {
        node.setStyle("-fx-background-color: #99B931; -fx-border-color: #FFFFFF; -fx-effect: dropshadow( gaussian , rgba(255,255,02550.4) , 10,0,0,1 )");
    }
}
