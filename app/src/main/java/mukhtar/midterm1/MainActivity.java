package mukhtar.midterm1;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;
    int idOfFrameLayout;
    ServiceConnection sConn;
    final String LOG_TAG = "myLogs";
    static final int REQUEST_CODE = 1;
    static final int RESULT_CODE_FINISH = 3;
    static final int RESULT_CODE_DOWNLOADING = 2;
    static final int RESULT_CODE_CANCELLED = 4;
    static final String PARAM_PINTENT = "pending";
    static final String PARAM_COUNTER_DATA = "counter_data";
    static int statusOfDownloading = 1;
    FragmentDownloading currentFragmentDownloading;
    Intent intent;
    PendingIntent pi;


    static MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MyService.class));



        idOfFrameLayout =R.id.idOfFrameLayout;
        sConn = new ServiceConnection() {

            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivity onServiceConnected");
                myService = ((MyService.MyBinder) binder).getService();
                fragmentTransaction = getFragmentManager().beginTransaction();
                if(myService.isDownloading&&statusOfDownloading!=4){
                    currentFragmentDownloading = new FragmentDownloading();
                    fragmentTransaction.replace(idOfFrameLayout,currentFragmentDownloading);
                    fragmentTransaction.commit();
                }else{
                    fragmentTransaction.replace(idOfFrameLayout,new FragmentStable());
                    fragmentTransaction.commit();
                }

            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
            }
        };



    }

    @Override
    protected void onStart() {
        super.onStart();
        pi = createPendingResult(REQUEST_CODE, new Intent(), 0);
        intent = new Intent(this, MyService.class)
                .putExtra(PARAM_PINTENT, pi);
        bindService(intent, sConn, 0);.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sConn);
    }

    void replaceFragments(){
        fragmentTransaction = getFragmentManager().beginTransaction();
        currentFragmentDownloading = new FragmentDownloading();
        fragmentTransaction.replace(idOfFrameLayout,currentFragmentDownloading);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode==RESULT_CODE_DOWNLOADING){
                currentFragmentDownloading.mProgressBar.setProgress(data.getIntExtra(PARAM_COUNTER_DATA,0));
                statusOfDownloading = 2;
            }else if(resultCode==RESULT_CODE_FINISH){
                statusOfDownloading = 3;
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(idOfFrameLayout,new FragmentStable());
                fragmentTransaction.commit();
            }else if(resultCode==RESULT_CODE_CANCELLED){
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(idOfFrameLayout,new FragmentStable());
                fragmentTransaction.commit();
            }
        }
    }
}
