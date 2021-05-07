package fr.celeria.musicplayer.panels;

import fr.celeria.musicplayer.panels.util.Panel;
import fr.celeria.musicplayer.panels.util.PanelManager;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;


public class AlbumsPanel extends Panel {

    private GridPane albumPanel;

    @Override
    public void init(PanelManager panelManager){
        super.init(panelManager);

        this.albumPanel = this.layout;

        GridPane.setValignment(albumPanel, VPos.TOP);
        GridPane.setHgrow(albumPanel, Priority.ALWAYS);
        GridPane.setVgrow(albumPanel, Priority.ALWAYS);

        albumPanel.setTranslateY(10.0d);


        Label titleLabel = new Label("Albums à votre disposition");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        titleLabel.setFont(new Font(35));
        titleLabel.setTranslateX(15.0d);
        titleLabel.setTranslateY(150.0d);


        // Test for playing a wav file.
        /*
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
        this.layout.getChildren().addAll(titleLabel);

    }
}
