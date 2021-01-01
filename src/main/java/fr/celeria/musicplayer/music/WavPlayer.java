package fr.celeria.musicplayer.music;

import fr.celeria.musicplayer.Main;
import javafx.application.Platform;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavPlayer implements Player
{
    private static final int BUFFER_SIZE = 128000;
    private final AudioFormat audioFormat;
    private File soundFile;
    private AudioInputStream audioStream = null;
    private SourceDataLine dataLine = null;

    public WavPlayer(String fileName)
    {
        this(new File(fileName));
    }

    public WavPlayer(File file)
    {
        try
        {
            this.soundFile = file;
            this.audioStream = AudioSystem.getAudioInputStream(file);
        } catch (Exception e)
        {
            Main.LOGGER.printStackTrace("Error when get audio input : ", e);
        }

        this.audioFormat = this.audioStream.getFormat();
    }

    @Override
    public void play(IMusicCallback callback)
    {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, this.audioFormat);

        try {
            this.dataLine = (SourceDataLine) AudioSystem.getLine(info);
            Main.LOGGER.info("SourceDataLine loaded : " + this.dataLine + String.format(" (%s)", this.soundFile.getName()));
            this.dataLine.open(this.audioFormat);
        } catch (Exception e)
        {
            Main.LOGGER.printStackTrace("Error when open the data line", e);
        }

        this.dataLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1)
        {
            try
            {
                nBytesRead = audioStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0)
                    this.dataLine.write(abData, 0, nBytesRead);
            } catch (IOException e)
            {
                Main.LOGGER.printStackTrace(e);
            }
            Platform.runLater(() -> callback.update(this));
        }

        this.dataLine.drain();
        this.dataLine.close();
        try
        {
            this.audioStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Platform.runLater(() -> callback.update(this));
    }

    @Override
    public void stop()
    {
        if(this.dataLine != null)
        {
            this.dataLine.stop();
            this.dataLine.close();
            try
            {
                this.audioStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getLength()
    {
        return this.audioStream.getFrameLength();
    }

    @Override
    public long getPosition()
    {
        return this.dataLine.getLongFramePosition();
    }

    @Override
    public boolean isRunning()
    {
        return this.dataLine.isRunning();
    }
}
