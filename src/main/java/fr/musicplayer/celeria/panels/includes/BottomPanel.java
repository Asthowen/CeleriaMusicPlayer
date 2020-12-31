package fr.musicplayer.celeria.panels.includes;

import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import fr.musicplayer.celeria.panels.util.Panel;
import fr.musicplayer.celeria.panels.util.PanelManager;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class BottomPanel extends Panel {

    private GridPane bottomPanel;
    private Rectangle coverImage = new Rectangle();
    private Label trackName = new Label();
    private Label trackAuthor = new Label();
    private MaterialDesignIconView play = new MaterialDesignIconView(MaterialDesignIcon.PAUSE);
    private MaterialDesignIconView skipNext = new MaterialDesignIconView(MaterialDesignIcon.SKIP_NEXT);
    private MaterialDesignIconView skipPrevious = new MaterialDesignIconView(MaterialDesignIcon.SKIP_PREVIOUS);
    private MaterialDesignIconView sound = new MaterialDesignIconView(MaterialDesignIcon.VOLUME_HIGH);
    private JFXProgressBar progressSoundBar = new JFXProgressBar();


    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        this.bottomPanel = this.layout;
        GridPane.setValignment(bottomPanel, VPos.BOTTOM);


        ImageView iconView = new ImageView(getClass().getResource("/image/backgroundPlayer.png").toExternalForm());
        GridPane.setHgrow(iconView, Priority.ALWAYS);
        GridPane.setVgrow(iconView, Priority.ALWAYS);
        GridPane.setHalignment(iconView, HPos.LEFT);
        GridPane.setValignment(iconView, VPos.BOTTOM);
        this.bottomPanel.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #053F5A, #053F5A, #006050);");
        this.bottomPanel.setMinHeight(80.0d);
        this.getLayout().getStylesheets().add(getClass().getResource("/css/BottomPanel.css").toExternalForm());



        GridPane bottomBarButton = new GridPane();
        this.layout.getChildren().add(bottomBarButton);

        bottomBarButton.setMinHeight(80.0d);
        bottomBarButton.setMaxHeight(80.0d);

        GridPane.setHalignment(bottomBarButton, HPos.RIGHT);
        GridPane.setValignment(bottomBarButton, VPos.BOTTOM);
        GridPane.setHgrow(bottomBarButton, Priority.ALWAYS);
        GridPane.setVgrow(bottomBarButton, Priority.ALWAYS);

        GridPane.setHgrow(coverImage, Priority.ALWAYS);
        GridPane.setVgrow(coverImage, Priority.ALWAYS);
        GridPane.setHgrow(trackName, Priority.ALWAYS);
        GridPane.setVgrow(trackName, Priority.ALWAYS);
        GridPane.setHgrow(play, Priority.ALWAYS);
        GridPane.setVgrow(play, Priority.ALWAYS);
        GridPane.setHgrow(skipNext, Priority.ALWAYS);
        GridPane.setVgrow(skipNext, Priority.ALWAYS);
        GridPane.setHgrow(skipPrevious, Priority.ALWAYS);
        GridPane.setVgrow(skipPrevious, Priority.ALWAYS);
        GridPane.setHgrow(progressSoundBar, Priority.ALWAYS);
        GridPane.setVgrow(progressSoundBar, Priority.ALWAYS);

        coverImage.setWidth(60);
        coverImage.setHeight(60);
        coverImage.setArcWidth(30.0d);
        coverImage.setArcHeight(30.0d);
        coverImage.setFill(new ImagePattern(new Image(getClass().getResource("/image/tempAlbumCover.jpg").toExternalForm())));
        coverImage.setOnMouseEntered(e -> coverImage.setOpacity(0.8f));
        coverImage.setOnMouseExited(e -> coverImage.setOpacity(1.0f));
        coverImage.setTranslateX(25.0d);
        GridPane.setHalignment(coverImage, HPos.LEFT);
        GridPane.setValignment(coverImage, VPos.CENTER);


        trackName.setText("That Song");
        trackName.setStyle("-fx-text-fill: white;-fx-font-weight: bold;");
        trackName.setFont(new Font(15));
        trackName.setTranslateX(100.0d);
        trackName.setTranslateY(-10.0d);
        GridPane.setHalignment(trackName, HPos.LEFT);
        GridPane.setValignment(trackName, VPos.CENTER);

        trackAuthor.setText("Amaranthe - MAXIMALISM");
        trackAuthor.setStyle("-fx-text-fill: white;-fx-font-weight: bold;");
        trackAuthor.setFont(new Font(13));
        trackAuthor.setOpacity(0.6d);
        trackAuthor.setTranslateX(100.0d);
        trackAuthor.setTranslateY(12.0d);
        GridPane.setHalignment(trackAuthor, HPos.LEFT);
        GridPane.setValignment(trackAuthor, VPos.CENTER);

        play.setFill(Color.WHITE);
        play.setOpacity(0.70f);
        play.setSize("40.0px");
        play.setOpacity(0.50f);
        play.setOnMouseEntered(e -> play.setOpacity(0.8f));
        play.setOnMouseExited(e -> play.setOpacity(0.50f));
        GridPane.setHalignment(play, HPos.CENTER);
        GridPane.setValignment(play, VPos.CENTER);

        skipNext.setFill(Color.WHITE);
        skipNext.setOpacity(0.70f);
        skipNext.setSize("25.0px");
        skipNext.setTranslateX(50.0d);
        skipNext.setOpacity(0.50f);
        skipNext.setOnMouseEntered(e -> skipNext.setOpacity(0.8f));
        skipNext.setOnMouseExited(e -> skipNext.setOpacity(0.50f));
        GridPane.setHalignment(skipNext, HPos.CENTER);
        GridPane.setValignment(skipNext, VPos.CENTER);

        skipPrevious.setFill(Color.WHITE);
        skipPrevious.setOpacity(0.70f);
        skipPrevious.setSize("25.0px");
        skipPrevious.setTranslateX(-50.0d);
        skipPrevious.setOpacity(0.50f);
        skipPrevious.setOnMouseEntered(e -> skipPrevious.setOpacity(0.8f));
        skipPrevious.setOnMouseExited(e -> skipPrevious.setOpacity(0.50f));
        GridPane.setHalignment(skipPrevious, HPos.CENTER);
        GridPane.setValignment(skipPrevious, VPos.CENTER);

        sound.setFill(Color.WHITE);
        sound.setOpacity(0.70f);
        sound.setSize("25.0px");
        sound.setOpacity(0.50f);
        sound.setTranslateX(-175.0d);
        GridPane.setHalignment(sound, HPos.RIGHT);
        GridPane.setValignment(sound, VPos.CENTER);

        progressSoundBar.setTranslateY(-3.0d);
        progressSoundBar.setStyle("-fx-background-color: black;");
        progressSoundBar.setMinWidth(panelManager.getStage().getWidth());
        progressSoundBar.setProgress(0.1);
        GridPane.setHalignment(progressSoundBar, HPos.LEFT);
        GridPane.setValignment(progressSoundBar, VPos.TOP);

        panelManager.getStage().widthProperty().addListener(e -> progressSoundBar.setMinWidth(panelManager.getStage().getWidth()));



        bottomBarButton.getChildren().addAll(coverImage, trackName, trackAuthor, skipPrevious, play, skipNext, sound, progressSoundBar);
    }
    public MaterialDesignIconView getPlay() {
        return play;
    }

    public MaterialDesignIconView getSkipNext() {
        return skipNext;
    }

    public MaterialDesignIconView getSkipPrevious() {
        return skipPrevious;
    }

    public Rectangle getCoverImage() {
        return coverImage;
    }

    public Label getTrackName() {
        return trackName;
    }

    public Label getTrackAuthor() {
        return trackAuthor;
    }

    public JFXProgressBar getProgressSoundBar() {
        return progressSoundBar;
    }
}
