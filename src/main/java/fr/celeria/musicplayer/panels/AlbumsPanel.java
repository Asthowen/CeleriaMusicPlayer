package fr.celeria.musicplayer.panels;

import fr.celeria.musicplayer.music.WavPlayer;
import fr.celeria.musicplayer.panels.includes.BottomPanel;
import fr.celeria.musicplayer.panels.util.Panel;
import fr.celeria.musicplayer.panels.util.PanelManager;
import fr.celeria.musicplayer.utils.FileMetadata;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OGG Vorbis", "*.ogg"));
        //TODO: Make all codecs.
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Flac", "*.flac", "*.flc"));
        fileChooser.setTitle("Open a sound File");
        final File selected = fileChooser.showOpenDialog(panelManager.getStage());
        final FileMetadata metadata = new FileMetadata(selected);
        final BottomPanel bottomPanel = panelManager.getBottomPanel();
        bottomPanel.getTrackName().setText(metadata.getName());
        bottomPanel.getTrackAuthor().setText(metadata.getAuthor() + " - " + metadata.getAlbum());
        final MediaPlayer mediaPlayer = new MediaPlayer(new Media(selected.toURI().toString()));
        mediaPlayer.getMedia().getMetadata().forEach((s, o) -> System.out.println("Metadata found: '" + s + "' '" + o + '\''));
        mediaPlayer.setOnEndOfMedia(() -> bottomPanel.getProgressSoundBar().setProgress(0));
        mediaPlayer.setOnPlaying(() -> new Thread(() -> {
            while(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING)
            {
                Platform.runLater(() -> bottomPanel.getProgressSoundBar().setProgress(((mediaPlayer.getCurrentTime().toMillis() * 100) / mediaPlayer.getMedia().getDuration().toMillis())));
            }
        }).start());
        mediaPlayer.play();

        /*new Thread(() -> {
            final WavPlayer wavPlayer = new WavPlayer(selected);
            wavPlayer.play(player -> {
                bottomPanel.getProgressSoundBar().setProgress((((double)player.getPosition() * 100) / player.getLength()) / 100);
                if(!player.isRunning())
                    bottomPanel.getProgressSoundBar().setProgress(0);
            });
        }, selected.getName() + " - Player").start();*/
    }
}
