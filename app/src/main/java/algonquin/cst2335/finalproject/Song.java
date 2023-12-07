package algonquin.cst2335.finalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song {

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "link")
    public String artist;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }
}