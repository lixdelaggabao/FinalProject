package algonquin.cst2335.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SunriseSunsetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SunriseSunsetFragment extends Fragment {

    private static final String ARG_SUNRISE_TIME = "sunrise_time";
    private static final String ARG_SUNSET_TIME = "sunset_time";

    private String sunriseTime;
    private String sunsetTime;

    public SunriseSunsetFragment() {
        // Required empty public constructor
    }

    public static SunriseSunsetFragment newInstance(String sunriseTime, String sunsetTime) {
        SunriseSunsetFragment fragment = new SunriseSunsetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUNRISE_TIME, sunriseTime);
        args.putString(ARG_SUNSET_TIME, sunsetTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sunriseTime = getArguments().getString(ARG_SUNRISE_TIME);
            sunsetTime = getArguments().getString(ARG_SUNSET_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunrise_sunset, container, false);

        TextView sunriseTextView = view.findViewById(R.id.fragmentSunrise);
        TextView sunsetTextView = view.findViewById(R.id.fragmentSunset);

        // Set sunrise and sunset times to TextViews
        sunriseTextView.setText("" + sunriseTime);
        sunsetTextView.setText("" + sunsetTime);

        return view;
    }
}
