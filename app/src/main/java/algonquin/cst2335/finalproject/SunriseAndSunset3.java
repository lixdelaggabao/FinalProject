package algonquin.cst2335.finalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SunriseAndSunset3 {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "sunrise")
    public String sunrise;

    @ColumnInfo(name = "sunset")
    public String sunset;

    @ColumnInfo(name = "latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    public String longitude;

    @ColumnInfo(name = "is_favorite")
    public boolean isFavorite;

    // Default Constructor
    public SunriseAndSunset3() {
    }

    // Constructor
    public SunriseAndSunset3(String rise, String set, String latitude, String longitude) {
        this.sunrise = rise;
        this.sunset = set;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFavorite = false; // Default value for isFavorite
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }
    public String getLatitude(){return latitude;}
    public String getLongitude(){return longitude;}

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
