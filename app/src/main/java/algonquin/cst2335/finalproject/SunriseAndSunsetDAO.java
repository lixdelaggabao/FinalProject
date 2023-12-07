package algonquin.cst2335.finalproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SunriseAndSunsetDAO {

    @Insert
    public long insertLocation(SunriseAndSunset3 s);

    @Query("SELECT * FROM SunriseAndSunset3")
    public List<SunriseAndSunset3> getAllLocations();

    @Query("SELECT * FROM SunriseAndSunset3 WHERE is_favorite = 1")
    public LiveData<List<SunriseAndSunset3>> getFavoriteLocations();


    @Delete
    void deleteLocation(SunriseAndSunset3 s);


}
