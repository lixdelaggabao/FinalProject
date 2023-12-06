package algonquin.cst2335.finalproject.dictionary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface dictionaryDAO {

    @Insert
    void insertDefinition(dictionaryDB d);

    @Query("Select * From dictionaryDB")
    List<dictionaryDB> getAllDefinitions();

    @Query("Select term From dictionaryDB")
    List<String> getAllTerms();

    @Delete
    void deleteDefinition(dictionaryDB d);

}
