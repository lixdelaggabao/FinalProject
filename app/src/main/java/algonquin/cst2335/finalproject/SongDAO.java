package algonquin.cst2335.finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDAO {
    @Insert
    void insertSong(Song song);

    @Query("SELECT * FROM Song")
    List<Song> getAllSongs();
   @Query("select title from Song")
    List<String> getAllSong();
    @Delete
    void deleteSong(Song song);
}
