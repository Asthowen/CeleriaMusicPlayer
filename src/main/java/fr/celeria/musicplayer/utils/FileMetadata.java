package fr.celeria.musicplayer.utils;

import java.io.File;

public class FileMetadata
{
    private final String author;
    private final String name;
    private final String album;

    public FileMetadata(File file)
    {
        // TODO: get info from file
        this.author = "Amaranthe";
        this.name = "That song";
        this.album = "MAXIMALISM";
    }

    public String getAuthor()
    {
        return this.author;
    }

    public String getName()
    {
        return this.name;
    }

    public String getAlbum()
    {
        return this.album;
    }
}
