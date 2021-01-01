package fr.celeria.musicplayer.panels;

import fr.celeria.musicplayer.music.WavPlayer;
import fr.celeria.musicplayer.panels.includes.BottomPanel;
import fr.celeria.musicplayer.panels.util.Panel;
import fr.celeria.musicplayer.panels.util.PanelManager;
import fr.celeria.musicplayer.utils.FileMetadata;
import javafx.stage.FileChooser;

import java.io.File;

public class AlbumsPanel extends Panel {
    @Override
    public void init(PanelManager panelManager)
    {
        super.init(panelManager);

        // Test for playing a wav file.
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV", "*.wav"));
        fileChooser.setTitle("Open a WAV File");
        final File selected = fileChooser.showOpenDialog(panelManager.getStage());
        final FileMetadata metadata = new FileMetadata(selected);
        final BottomPanel bottomPanel = panelManager.getBottomPanel();
        bottomPanel.getTrackName().setText(metadata.getName());
        bottomPanel.getTrackAuthor().setText(metadata.getAuthor() + " - " + metadata.getAlbum());
        new Thread(() -> {
            final WavPlayer wavPlayer = new WavPlayer(selected);
            wavPlayer.play(player -> {
                bottomPanel.getProgressSoundBar().setProgress((((double)player.getPosition() * 100) / player.getLength()) / 100);
                if(!player.isRunning())
                    bottomPanel.getProgressSoundBar().setProgress(0);
            });
        }, selected.getName() + " - Player").start();
    }
}
