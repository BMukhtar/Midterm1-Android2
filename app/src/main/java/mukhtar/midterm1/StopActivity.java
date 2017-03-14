package mukhtar.midterm1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class StopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.statusOfDownloading = 4;
        Log.d("myLogs","in stop acitvity ");
        startActivity(new Intent(this,MainActivity.class));
        finish();

    }
}
