package algonquin.cst2335.finalproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class DeezerViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Song>> searchResults = new MutableLiveData<>();
    public MutableLiveData<Song> selectedSong = new MutableLiveData<>();

    // Other relevant properties or methods for Deezer app can be added here
}
