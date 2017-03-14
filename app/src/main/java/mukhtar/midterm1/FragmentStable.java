package mukhtar.midterm1;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentStable extends Fragment {
    Button start;
    TextView tv;
    FragmentTransaction fragmentTransaction;
    String LOG_TAG  = "myLogs";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stable, container, false);
        start = (Button) v.findViewById(R.id.button_start);

        tv = (TextView) v.findViewById(R.id.textViewProgress);

        switch(MainActivity.statusOfDownloading ){
            case 1:
                tv.setText("");
                break;
            case 2:
                tv.setText("Downloading");
                break;
            case 3:
                tv.setText("FINISHED");
                start.setVisibility(View.GONE);
                break;
            case 4:
                tv.setText("CANCELLED");
                start.setVisibility(View.GONE);
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.statusOfDownloading = 2;
                ((MainActivity)getActivity()).replaceFragments();
                MainActivity.myService.startDownload();

            }
        });
        return v;
    }


}
