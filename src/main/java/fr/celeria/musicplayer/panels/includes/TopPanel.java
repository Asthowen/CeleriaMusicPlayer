package fr.celeria.musicplayer.panels.includes;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.celeria.musicplayer.panels.util.Panel;
import fr.celeria.musicplayer.panels.util.PanelManager;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;

public class TopPanel extends Panel{
    private GridPane topBar;

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        this.topBar = this.layout;
        this.topBar.setMinHeight(50.0d);
        this.topBar.setMaxHeight(50.0d);

        GridPane topBarButton = new GridPane();
        this.layout.getChildren().add(topBarButton);

        topBarButton.setMinWidth(100.0d);
        topBarButton.setMaxWidth(100.0d);
        topBarButton.setMinHeight(50.0d);
        topBarButton.setMaxHeight(50.0d);

        GridPane.setHalignment(topBarButton, HPos.RIGHT);

        final MaterialDesignIconView close = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_CLOSE);
        final MaterialDesignIconView maximize = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MAXIMIZE);
        final MaterialDesignIconView hide = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MINIMIZE);

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
        close.setTranslateX(65.0d);

        maximize.setFill(Color.WHITE);
        maximize.setOpacity(0.70f);
        maximize.setSize("19.0px");
        maximize.setOnMouseEntered(e -> maximize.setOpacity(1.0f));
        maximize.setOnMouseExited(e -> maximize.setOpacity(0.70f));
        maximize.setOnMouseClicked(e-> {
            if(!this.panelManager.getStage().isMaximized()){
                this.panelManager.getStage().setMaximized(true);

                ObservableList<Screen> screens = Screen.getScreensForRectangle(new Rectangle2D(this.panelManager.getStage().getX(), this.panelManager.getStage().getY(), this.panelManager.getStage().getWidth(), this.panelManager.getStage().getHeight()));
                Rectangle2D bounds = screens.get(0).getVisualBounds();

                this.panelManager.getStage().setX(bounds.getMinX());
                this.panelManager.getStage().setY(bounds.getMinY());
                this.panelManager.getStage().setWidth(bounds.getWidth());
                this.panelManager.getStage().setHeight(bounds.getHeight());
            }else{
                this.panelManager.getStage().setMaximized(false);
            }
        });
        maximize.setTranslateX(25.0d);

        hide.setFill(Color.WHITE);
        hide.setOpacity(0.70f);
        hide.setSize("19.0px");
        hide.setOnMouseEntered(e -> hide.setOpacity(1.0f));
        hide.setOnMouseExited(e -> hide.setOpacity(0.70f));
        hide.setOnMouseClicked(e -> this.panelManager.getStage().setIconified(true));
        hide.setTranslateX(-15.0d);
        hide.setTranslateY(2);

        topBarButton.getChildren().addAll(close, maximize, hide);
    }
}
