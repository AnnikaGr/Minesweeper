module com.example.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    //requires org.controlsfx.controls;
    //requires org.kordamp.ikonli.javafx;
    //requires org.kordamp.bootstrapfx.core;
    //requires eu.hansolo.tilesfx;

    opens com.example.game to javafx.fxml;
    exports com.example.game;
    exports controller;
    opens controller to javafx.fxml;
}