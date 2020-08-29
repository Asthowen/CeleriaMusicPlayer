package fr.musicplayer.celeria.panels.util;

import fr.musicplayer.celeria.panels.PanelManager;
import javafx.scene.layout.GridPane;

public interface IPanel {
    void init(PanelManager panelManager);
    GridPane getLayout();
    void onShow();
}
