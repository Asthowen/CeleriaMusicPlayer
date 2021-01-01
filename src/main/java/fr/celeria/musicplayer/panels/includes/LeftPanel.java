package fr.celeria.musicplayer.panels.includes;

import fr.celeria.musicplayer.panels.util.Panel;
import fr.celeria.musicplayer.panels.util.PanelManager;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


public class LeftPanel extends Panel
{

    private GridPane leftPanel;

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        this.leftPanel = this.layout;
        GridPane.setValignment(leftPanel, VPos.TOP);

        this.leftPanel.setMaxWidth(250.0d);
        this.leftPanel.setMinWidth(250.0d);
        this.leftPanel.setMinHeight(panelManager.getStage().getHeight() - 85.0d);
        panelManager.getStage().heightProperty().addListener(e -> leftPanel.setMinHeight(panelManager.getStage().getHeight() - 85.0d));
        this.leftPanel.setStyle("-fx-background-image: url('" + getClass().getResource("/image/leftPane.png") + "');-fx-backgound-repeat: skretch;-fx-backgound-position: center center;-fx-background-size: cover;");

        GridPane leftPanelButtons = new GridPane();
        this.layout.getChildren().add(leftPanelButtons);

        leftPanelButtons.setMinHeight(80.0d);
        leftPanelButtons.setMaxHeight(80.0d);

        GridPane.setHalignment(leftPanelButtons, HPos.RIGHT);
        GridPane.setValignment(leftPanelButtons, VPos.BOTTOM);
        GridPane.setHgrow(leftPanelButtons, Priority.ALWAYS);
        GridPane.setVgrow(leftPanelButtons, Priority.ALWAYS);




        leftPanelButtons.getChildren().addAll();
    }

}
