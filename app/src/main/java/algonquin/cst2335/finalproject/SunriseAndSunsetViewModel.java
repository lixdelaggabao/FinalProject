package algonquin.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class SunriseAndSunsetViewModel extends ViewModel {
    private SunriseAndSunsetDAO sDAO;

    private LiveData<List<SunriseAndSunset3>> favoriteLocations;

    public MutableLiveData<ArrayList<SunriseAndSunset3>> weather = new MutableLiveData<>();

    public MutableLiveData<SunriseAndSunset3> selected = new MutableLiveData<>();

    public LiveData<List<SunriseAndSunset3>> getFavoriteLocations(){
        if (favoriteLocations == null && sDAO != null){
            favoriteLocations = sDAO.getFavoriteLocations();
        }
        return favoriteLocations;
    }
    public void setSunriseAndSunsetDAO(SunriseAndSunsetDAO dao) {
        this.sDAO = dao;
    }



}