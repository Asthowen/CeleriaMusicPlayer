package fr.celeria.musicplayer.panels.util;

import fr.celeria.musicplayer.panels.includes.BottomPanel;
import fr.celeria.musicplayer.panels.includes.LeftPanel;
import fr.celeria.musicplayer.panels.includes.TopPanel;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class PanelManager {
    private final Stage stage;

    private TopPanel topPanel = new TopPanel();
    private BottomPanel bottomPanel = new BottomPanel();
    private LeftPanel leftPanel = new LeftPanel();
    private GridPane layout;
    private GridPane centerPanel = new GridPane();
    public Image icon = new Image(PanelManager.class.getResource("/image/logo.png").toExternalForm());

    public PanelManager(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = screen.getDisplayMode().getWidth();
        int height = screen.getDisplayMode().getHeight();
        this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.setWidth(width - (width >> 4));
        this.stage.setHeight(height - (height >> 4));
        this.stage.centerOnScreen();
        this.stage.setTitle("Celeria Music Player");
        this.layout = new GridPane();
        this.stage.show();
        this.stage.setScene(new Scene(this.layout));

        this.stage.getIcons().add(this.icon);

        this.layout.setStyle("-fx-background-image: url('" + this.getClass().getResource("/image/main.png") + "'); -fx-backgound-repeat: skretch; -fx-backgound-position: center center; -fx-background-size: cover;");

        ColumnConstraints leftPanelConstraints = new ColumnConstraints();
        leftPanelConstraints.setHalignment(HPos.LEFT);
        leftPanelConstraints.setMinWidth(300.0d);
        leftPanelConstraints.setMaxWidth(300.0d);
        this.layout.getColumnConstraints().addAll(leftPanelConstraints, new ColumnConstraints());
        this.layout.add(this.leftPanel.getLayout(), 0, 0);
        this.leftPanel.init(this);

        RowConstraints topPanelConstraints = new RowConstraints();
        topPanelConstraints.setValignment(VPos.TOP);
        topPanelConstraints.setMinHeight(40.0d);
        topPanelConstraints.setMaxHeight(40.0d);
        this.layout.getRowConstraints().addAll(topPanelConstraints, new RowConstraints());
        this.layout.add(this.topPanel.getLayout(), 1, 0);
        this.topPanel.init(this);

        this.layout.add(this.centerPanel,1,1);
        GridPane.setHgrow(this.centerPanel, Priority.ALWAYS);
        GridPane.setVgrow(this.centerPanel, Priority.ALWAYS);

        RowConstraints bottomPanelConstraints = new RowConstraints();
        bottomPanelConstraints.setValignment(VPos.BOTTOM);
        bottomPanelConstraints.setMinHeight(80.0d);
        bottomPanelConstraints.setMaxHeight(80.0d);
        this.layout.getRowConstraints().addAll(bottomPanelConstraints, new RowConstraints());
        this.layout.add(this.bottomPanel.getLayout(), 0, 2);
        this.bottomPanel.init(this);

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

    public BottomPanel getBottomPanel(){
        return this.bottomPanel;
    }
}