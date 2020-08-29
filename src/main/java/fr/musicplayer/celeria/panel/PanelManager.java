package fr.musicplayer.celeria.panel;

import fr.musicplayer.celeria.ResizeHelper;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class PanelManager {
    private final Stage stage;

    private GridPane layout;
   // private TopPanel topPanel = new TopPanel();
    private GridPane centerPanel = new GridPane();


    public PanelManager(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        float width = gd.getDisplayMode().getWidth();
        float height = gd.getDisplayMode().getHeight();
        this.stage.setTitle("Celeria Music Player");this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.setWidth(width - width / 16);
        this.stage.setHeight(height - height / 16);
        this.stage.centerOnScreen();
        this.layout = new GridPane();
        this.stage.show();

        this.stage.setScene(new Scene(this.layout));

        this.layout.setStyle("-fx-background-image: url('https://minerp.tk/background.png');-fx-backgound-repeat: skretch;-fx-backgound-position: center center;-fx-background-size: cover;");



        /*RowConstraints topPanelConstraints = new RowConstraints();
        topPanelConstraints.setValignment(VPos.TOP);
        topPanelConstraints.setMinHeight(25);
        topPanelConstraints.setMaxHeight(25);
        this.layout.getRowConstraints().addAll(topPanelConstraints, new RowConstraints());
        this.layout.add(this.topPanel.getLayout(), 0, 0);
        this.topPanel.init(this);*/


        this.layout.add(this.centerPanel, 0, 1);
        GridPane.setVgrow(this.centerPanel, Priority.ALWAYS);
        GridPane.setHgrow(this.centerPanel, Priority.ALWAYS);
        ResizeHelper.addResizeListener(this.stage);
    }
}
