package fr.musicplayer.celeria.panels.includes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.musicplayer.celeria.panels.util.Panel;
import fr.musicplayer.celeria.panels.util.PanelManager;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TopPanel extends Panel {

    private GridPane topBar;


    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        this.topBar = this.layout;
        this.topBar.setMinHeight(40.0d);
        this.topBar.setMaxHeight(40.0d);
        GridPane.setHgrow(topBar, Priority.ALWAYS);
        GridPane.setVgrow(topBar, Priority.ALWAYS);

        ImageView iconView = new ImageView(panelManager.icon);
        GridPane.setHgrow(iconView, Priority.ALWAYS);
        GridPane.setVgrow(iconView, Priority.ALWAYS);
        GridPane.setHalignment(iconView, HPos.LEFT);
        GridPane.setValignment(iconView, VPos.CENTER);
        iconView.setTranslateX(6);
        iconView.setFitHeight(30.0d);
        iconView.setFitWidth(30.0d);
        this.topBar.getChildren().add(iconView);

        Label titleLabel = new Label("eleria Music Player");
        GridPane.setHgrow(titleLabel, Priority.ALWAYS);
        GridPane.setVgrow(titleLabel, Priority.ALWAYS);
        GridPane.setHalignment(titleLabel, HPos.LEFT);
        GridPane.setValignment(titleLabel, VPos.CENTER);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        titleLabel.setTranslateX(40);
        titleLabel.setMinWidth(25.0d);
        titleLabel.setFont(new Font(18));
        this.topBar.getChildren().add(titleLabel);



        GridPane topBarButton = new GridPane();
        this.layout.getChildren().add(topBarButton);

        topBarButton.setMinWidth(100.0d);
        topBarButton.setMaxWidth(100.0d);
        topBarButton.setMinHeight(50.0d);
        topBarButton.setMaxHeight(50.0d);

        GridPane.setHalignment(topBarButton, HPos.RIGHT);
        MaterialDesignIconView close = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_CLOSE);
        MaterialDesignIconView maximize = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MAXIMIZE);
        MaterialDesignIconView hide = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MINIMIZE);
        GridPane.setHgrow(topBarButton, Priority.ALWAYS);
        GridPane.setVgrow(topBarButton, Priority.ALWAYS);
        GridPane.setVgrow(close, Priority.ALWAYS);
        GridPane.setVgrow(maximize, Priority.ALWAYS);
        GridPane.setVgrow(hide, Priority.ALWAYS);


        close.setFill(Color.WHITE);
        close.setOpacity(0.70f);
        close.setSize("19.0px");
        close.setOnMouseEntered(e -> close.setOpacity(1.0f));
        close.setOnMouseExited(e -> close.setOpacity(0.70f));
        close.setOnMouseClicked(e -> System.exit(0));
        close.setTranslateX(70.0d);

        maximize.setFill(Color.WHITE);
        maximize.setOpacity(0.70f);
        maximize.setSize("19.0px");
        maximize.setOnMouseEntered(e -> maximize.setOpacity(1.0f));
        maximize.setOnMouseExited(e -> maximize.setOpacity(0.70f));
        maximize.setOnMouseClicked(e -> this.panelManager.getStage().setMaximized(!this.panelManager.getStage().isMaximized()));
        maximize.setTranslateX(45.0d);

        hide.setFill(Color.WHITE);
        hide.setOpacity(0.70f);
        hide.setSize("19.0px");
        hide.setOnMouseEntered(e -> hide.setOpacity(1.0f));
        hide.setOnMouseExited(e -> hide.setOpacity(0.70f));
        hide.setOnMouseClicked(e -> this.panelManager.getStage().setIconified(true));
        hide.setTranslateX(18.0d);


        topBarButton.getChildren().addAll(close, maximize, hide);
    }
}
