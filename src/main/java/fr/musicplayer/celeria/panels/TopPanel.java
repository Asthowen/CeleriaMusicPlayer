package fr.musicplayer.celeria.panels;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.musicplayer.celeria.panels.util.Panel;
import fr.musicplayer.celeria.panels.util.PanelManager;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class TopPanel extends Panel {

    private GridPane topBar;


    @Override
    public void init(PanelManager panelManager) {


        super.init(panelManager);

        this.topBar = this.layout;
        ImageView iconView = new ImageView(PanelManager.icon);
        GridPane.setHgrow(iconView, Priority.ALWAYS);
        GridPane.setVgrow(iconView, Priority.ALWAYS);
        GridPane.setHalignment(iconView, HPos.LEFT);
        GridPane.setValignment(iconView, VPos.TOP);
        iconView.setScaleX(0.08D);
        iconView.setScaleY(0.08D);
        //this.topBar.getChildren().add(iconView);



        GridPane topBarButton = new GridPane();
        this.layout.getChildren().add(topBarButton);

        topBarButton.setMinWidth(100.0d);
        topBarButton.setMaxWidth(100.0d);
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
        close.setSize("18.0px");
        close.setOnMouseEntered(e -> close.setOpacity(1.0f));
        close.setOnMouseExited(e -> close.setOpacity(0.70f));
        close.setOnMouseClicked(e -> System.exit(0));
        close.setTranslateX(70.0d);

        maximize.setFill(Color.WHITE);
        maximize.setOpacity(0.70f);
        maximize.setSize("16.0px");
        maximize.setOnMouseEntered(e -> maximize.setOpacity(1.0f));
        maximize.setOnMouseExited(e -> maximize.setOpacity(0.70f));
        maximize.setOnMouseClicked(e -> this.panelManager.getStage().setMaximized(!this.panelManager.getStage().isMaximized()));
        maximize.setTranslateX(50.0d);

        hide.setFill(Color.WHITE);
        hide.setOpacity(0.70f);
        hide.setSize("18.0px");
        hide.setOnMouseEntered(e -> hide.setOpacity(1.0f));
        hide.setOnMouseExited(e -> hide.setOpacity(0.70f));
        hide.setOnMouseClicked(e -> this.panelManager.getStage().setIconified(true));
        hide.setTranslateX(26.0d);


        topBarButton.getChildren().addAll(close, maximize, hide);
    }
}
