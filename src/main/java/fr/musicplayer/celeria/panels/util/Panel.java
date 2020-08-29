package fr.musicplayer.celeria.panels.util;

import fr.musicplayer.celeria.panels.PanelManager;
import javafx.animation.FadeTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

public class Panel implements IPanel{

    protected GridPane gridPane = new GridPane();
    protected PanelManager panelManager;

    @Override
    public void init(PanelManager panelManager){
        this.panelManager = panelManager;
        GridPane.setHgrow(gridPane, Priority.ALWAYS);
        GridPane.setVgrow(gridPane, Priority.ALWAYS);

    }

    @Override
    public GridPane getLayout() {
        return gridPane;
    }

    @Override
    public void onShow() {
        FadeTransition transition = new FadeTransition(Duration.seconds(1.2), this.gridPane);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.setAutoReverse(true);
        transition.play();
    }


}
