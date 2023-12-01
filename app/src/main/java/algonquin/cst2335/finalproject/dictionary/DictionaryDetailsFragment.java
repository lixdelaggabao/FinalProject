package algonquin.cst2335.finalproject.dictionary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import algonquin.cst2335.finalproject.databinding.DictionaryFragmentBinding;

public class DictionaryDetailsFragment extends Fragment {

    dictionaryDB selected;

    public DictionaryDetailsFragment( dictionaryDB w) {
        selected = w;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DictionaryFragmentBinding binding = DictionaryFragmentBinding.inflate(inflater);

        binding.txtDictionaryFragmentID.setText( selected.getTerm());
        binding.txtDictionaryFragmentDefinition.setText( selected.getDefinition());
        return binding.getRoot();
    }
    public int getID() {
        return selected.id;
    }
} // end class
