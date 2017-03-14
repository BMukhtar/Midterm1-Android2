package mukhtar.midterm1;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDownloading extends Fragment {
    ProgressBar mProgressBar;
    Button up;
    Button down;


    public FragmentDownloading() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_downloading, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        up = (Button) v.findViewById(R.id.button_up);
        down = (Button) v.findViewById(R.id.button_down);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.myService.downInterval(100);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.myService.upInterval(100);
            }
        });

        return v;
    }

}
