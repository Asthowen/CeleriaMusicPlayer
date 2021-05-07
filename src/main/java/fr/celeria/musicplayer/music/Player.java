package fr.celeria.musicplayer.music;

public interface Player{
    int BUFFER_SIZE = 128000;

    void play(MusicCallback callback);
    void pause();
    void resume();
    void stop();

    long getLength();
    long getPosition();

    boolean isRunning();
}
