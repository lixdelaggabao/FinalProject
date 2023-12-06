package algonquin.cst2335.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import algonquin.cst2335.finalproject.databinding.FragmentSongDetailBinding;

public class SongDetailsFragment extends Fragment {

    private Song selected;
    public SongDetailsFragment(Song song) {selected = song;}

        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.onCreateView(inflater, container, saveInstanceState);
            FragmentSongDetailBinding binding = FragmentSongDetailBinding.inflate(inflater);

            binding.detailTitleTextView.setText(selected.getTitle());
            binding.detailArtistTextView.setText(selected.getArtist());
            return binding.getRoot();
    }
    public int getID() {
        return selected.id;
    }

}
