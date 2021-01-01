package fr.celeria.musicplayer;

import fr.celeria.musicplayer.panels.AlbumsPanel;
import fr.celeria.musicplayer.panels.util.PanelManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class FxApplication extends Application {
    @Override
    public void start(Stage stage) {
        PanelManager panelManager = new PanelManager(stage);
        panelManager.init();
        panelManager.showPanel(new AlbumsPanel());
    }
}
