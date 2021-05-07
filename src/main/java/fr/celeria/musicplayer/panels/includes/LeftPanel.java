package fr.celeria.musicplayer.panels.includes;

import fr.celeria.musicplayer.panels.util.Panel;
import fr.celeria.musicplayer.panels.util.PanelManager;
import fr.celeria.musicplayer.panels.util.BottomPanelUtils;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class LeftPanel extends Panel {
    private GridPane leftPanel;
    private final GridPane buttonPanel = new GridPane();
    private final BottomPanelUtils bottomPanelUtils = new BottomPanelUtils();


    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        this.leftPanel = this.layout;
        GridPane.setValignment(leftPanel, VPos.TOP);
        GridPane.setHgrow(leftPanel, Priority.ALWAYS);
        GridPane.setVgrow(leftPanel, Priority.ALWAYS);

        panelManager.getStage().heightProperty().addListener(e -> this.leftPanel.setMinHeight(panelManager.getStage().getHeight() - 80.0d));
        panelManager.getStage().heightProperty().addListener(e -> this.leftPanel.setMaxHeight(panelManager.getStage().getHeight() - 80.0d));

        this.leftPanel.setMinHeight(panelManager.getStage().getHeight() - 80.0d);
        this.leftPanel.setStyle("-fx-background-image: url('" + getClass().getResource("/image/leftPane.png") + "');-fx-backgound-repeat: skretch;-fx-backgound-position: center center;-fx-background-size: cover;");


        GridPane.setValignment(buttonPanel, VPos.CENTER);
        GridPane.setHalignment(buttonPanel, HPos.CENTER);
        GridPane.setHgrow(buttonPanel, Priority.ALWAYS);
        GridPane.setVgrow(buttonPanel, Priority.ALWAYS);
        this.buttonPanel.setMaxHeight(260.0d);
        this.buttonPanel.setMinHeight(260.0d);

        leftPanel.getChildren().add(buttonPanel);

        ImageView iconView = new ImageView(panelManager.icon);
        GridPane.setHgrow(iconView, Priority.ALWAYS);
        GridPane.setVgrow(iconView, Priority.ALWAYS);
        GridPane.setHalignment(iconView, HPos.LEFT);
        GridPane.setValignment(iconView, VPos.TOP);
        iconView.setTranslateY(-326.0d);
        iconView.setTranslateX(15.0d);
        iconView.setFitHeight(35.0d);
        iconView.setFitWidth(35.0d);

        Label titleLabel = new Label("eleria Music Player");
        GridPane.setHgrow(titleLabel, Priority.ALWAYS);
        GridPane.setVgrow(titleLabel, Priority.ALWAYS);
        GridPane.setHalignment(titleLabel, HPos.LEFT);
        GridPane.setValignment(titleLabel, VPos.TOP);
        titleLabel.setTranslateY(-320.0d);
        titleLabel.setTranslateX(53.0d);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        titleLabel.setMinWidth(25.0d);
        titleLabel.setFont(new Font(18));


        Button albumButton = bottomPanelUtils.buttonTab(0.0d);
        Label albumLabel = bottomPanelUtils.labelTab(new Label("Albums"), 17.0d);
        Separator albumSeparator = bottomPanelUtils.separatorTab(60.0d);

        Button tracksButton = bottomPanelUtils.buttonTab(60.0d);
        Label tracksLabel = bottomPanelUtils.labelTab(new Label("Morceaux"), 77.0d);
        Separator tracksSeparator = bottomPanelUtils.separatorTab(120.0d);

        Button playlistButton = bottomPanelUtils.buttonTab(120.0d);
        Label playlistLabel = bottomPanelUtils.labelTab(new Label("Playlists"), 136.0d);
        Separator playlistSeparator = bottomPanelUtils.separatorTab(180.0d);

        Button downloaderButton = bottomPanelUtils.buttonTab(180.0d);
        Label downloaderLabel = bottomPanelUtils.labelTab(new Label("Downloader"), 196.0d);


        buttonPanel.getChildren().addAll(iconView, titleLabel, albumButton, albumLabel, albumSeparator, tracksButton, tracksLabel, tracksSeparator, playlistButton, playlistLabel, playlistSeparator, downloaderButton, downloaderLabel);
    }

}
