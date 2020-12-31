package fr.musicplayer.celeria.panels.includes;

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
import javafx.scene.text.Font;

public class BottomPanel extends Panel {

    private GridPane bottomPanel;
    private ImageView coverImage = new ImageView();
    private Label trackName = new Label();
    private Label trackAuthor = new Label();
    private MaterialDesignIconView play = new MaterialDesignIconView(MaterialDesignIcon.PAUSE);
    private MaterialDesignIconView skipNext = new MaterialDesignIconView(MaterialDesignIcon.SKIP_NEXT);
    private MaterialDesignIconView skipPrevious = new MaterialDesignIconView(MaterialDesignIcon.SKIP_PREVIOUS);
    private MaterialDesignIconView sound = new MaterialDesignIconView(MaterialDesignIcon.SOUNDCLOUD);



    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        this.bottomPanel = this.layout;
        GridPane.setValignment(bottomPanel, VPos.BOTTOM);


        ImageView iconView = new ImageView(getClass().getResource("/backgroundPlayer.png").toExternalForm());
        GridPane.setHgrow(iconView, Priority.ALWAYS);
        GridPane.setVgrow(iconView, Priority.ALWAYS);
        GridPane.setHalignment(iconView, HPos.LEFT);
        GridPane.setValignment(iconView, VPos.BOTTOM);
        this.bottomPanel.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #053F5A, #053F5A, #006050);");
        this.bottomPanel.setMinHeight(80.0d);


        GridPane topBarButton = new GridPane();
        this.layout.getChildren().add(topBarButton);

        topBarButton.setMinHeight(80.0d);
        topBarButton.setMaxHeight(80.0d);

        GridPane.setHalignment(topBarButton, HPos.RIGHT);
        GridPane.setValignment(topBarButton, VPos.BOTTOM);
        GridPane.setHgrow(topBarButton, Priority.ALWAYS);
        GridPane.setVgrow(topBarButton, Priority.ALWAYS);

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

        coverImage.setFitWidth(60);
        coverImage.setPreserveRatio(true);
        coverImage.setOnMouseEntered(e -> coverImage.setOpacity(0.8f));
        coverImage.setOnMouseExited(e -> coverImage.setOpacity(1.0f));
        coverImage.setImage(new Image(getClass().getResource("/tempAlbumCover.jpg").toExternalForm()));
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
        GridPane.setHalignment(sound, HPos.RIGHT);
        GridPane.setValignment(sound, VPos.CENTER);


        topBarButton.getChildren().addAll(coverImage, trackName, trackAuthor, skipPrevious, play, skipNext, sound);
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

    public ImageView getCoverImage() {
        return coverImage;
    }

    public Label getTrackName() {
        return trackName;
    }

    public Label getTrackAuthor() {
        return trackAuthor;
    }
}
