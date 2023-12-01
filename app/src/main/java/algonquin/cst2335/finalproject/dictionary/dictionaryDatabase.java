package algonquin.cst2335.finalproject.dictionary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database( entities = { dictionaryDB.class}, version=1 )
public abstract class dictionaryDatabase extends RoomDatabase {

    public abstract dictionaryDAO dDAO();

}
