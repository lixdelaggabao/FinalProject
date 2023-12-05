package algonquin.cst2335.finalproject.dictionary;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class dictionaryVM extends ViewModel {

    public MutableLiveData<ArrayList<dictionaryDB>> dictionary = new MutableLiveData< >();
    public MutableLiveData<dictionaryDB> selectedTerm = new MutableLiveData< >();
}
