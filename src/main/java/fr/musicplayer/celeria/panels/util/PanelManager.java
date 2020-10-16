package fr.musicplayer.celeria.panels.util;

import fr.musicplayer.celeria.panels.includes.TopPanel;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class PanelManager {
    private final Stage stage;

    private TopPanel topPanel = new TopPanel();
    private GridPane layout;
    private GridPane centerPanel = new GridPane();
    public static Image icon = new Image(PanelManager.class.getResource("/logo.png").toExternalForm());




    public PanelManager(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = screen.getDisplayMode().getWidth();
        int height = screen.getDisplayMode().getHeight();
        System.out.println(width);
        System.out.println(height - height / 16);
        this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.setWidth(width - width / 16);
        this.stage.setHeight(height - height / 16);
        this.stage.centerOnScreen();
        this.stage.setTitle("Celeria Music Player");
        this.layout = new GridPane();
        this.stage.show();
        this.stage.setScene(new Scene(this.layout));

        this.stage.getIcons().add(this.icon);

        this.layout.setStyle("-fx-background-image: url('" + getClass().getResource("/main.png") + "');-fx-backgound-repeat: skretch;-fx-backgound-position: center center;-fx-background-size: cover;");

        RowConstraints topPanelConstraints = new RowConstraints();
        topPanelConstraints.setValignment(VPos.TOP);
        topPanelConstraints.setMinHeight(25);
        topPanelConstraints.setMaxHeight(25);
        this.layout.getRowConstraints().addAll(topPanelConstraints, new RowConstraints());
        this.layout.add(this.topPanel.getLayout(), 0, 0);
        this.topPanel.init(this);


        this.layout.add(this.centerPanel, 0, 1);
        GridPane.setVgrow(this.centerPanel, Priority.ALWAYS);
        GridPane.setHgrow(this.centerPanel, Priority.ALWAYS);
        ResizeHelper.addResizeListener(this.stage);

    }



    public Stage getStage() {
        return stage;
    }
    public void showPanel(IPanel panel) {
        this.centerPanel.getChildren().clear();
        this.centerPanel.getChildren().add(panel.getLayout());
        panel.init(this);
        panel.onShow();
    }
}
