package algonquin.cst2335.finalproject;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SunriseAndSunset3.class}, version =1)
public abstract class SunriseAndSunsetDatabase extends RoomDatabase {

    public abstract SunriseAndSunsetDAO sasDAO();

}
