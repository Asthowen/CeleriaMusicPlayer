package fr.celeria.musicplayer.panels.util;

import javafx.scene.layout.GridPane;

public interface IPanel {
    void init(PanelManager panelManager);
    GridPane getLayout();
    void onShow();
}
