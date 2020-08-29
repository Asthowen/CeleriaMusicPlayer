package fr.musicplayer.celeria.panels;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class PanelManager {
    private final Stage stage;

    private Dimension screenSize;
    private GridPane layout;
    private GridPane centerPanel = new GridPane();




    public PanelManager(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.setWidth(this.screenSize.getWidth() - this.screenSize.getWidth() / 16);
        this.stage.setHeight(this.screenSize.getHeight() - this.screenSize.getHeight() / 16);
        this.stage.centerOnScreen();
        this.layout = new GridPane();
        this.stage.show();
        this.stage.setScene(new Scene(this.layout));


    }


}
