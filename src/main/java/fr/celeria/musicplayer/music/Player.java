package fr.celeria.musicplayer.music;

public interface Player
{
    void play(IMusicCallback callback);
    void stop();

    long getLength();
    long getPosition();

    boolean isRunning();
}
