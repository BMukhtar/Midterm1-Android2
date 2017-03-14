package mukhtar.midterm1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {

    final String LOG_TAG = "myLogs";
    ExecutorService es;

    MyBinder binder = new MyBinder();
    MyRun myRun ;
    int counter;
    PendingIntent pi;

    long interval = 1000;
    boolean isDownloading = false;
    boolean isConnected = false;
    boolean isCancelled = false;


    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(1);
        Log.d(LOG_TAG, "MyService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MyService onStartCommand");
        pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
        return START_NOT_STICKY;
    }

    void startDownload(){
        myRun = new MyRun(pi);
        isDownloading = true;
        es.execute(myRun);

    }

    long upInterval(long gap) {
        if(interval<=1500)interval = interval + gap;
        return interval;
    }

    long downInterval(long gap) {

        if (interval >= 500) interval = interval - gap;
        return interval;
    }



    public IBinder onBind(Intent arg0) {
        Log.d(LOG_TAG, "MyService onBind");
        pi = arg0.getParcelableExtra(MainActivity.PARAM_PINTENT);
        isConnected = true;
        return binder;
    }
    public void onRebind(Intent intent) {

        pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
        isConnected = true;
        myRun.refreshPI(pi);
        Log.d(LOG_TAG, "MyService onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isConnected = false;
        pi = null;
        Log.d(LOG_TAG, "MyService onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "MyService onDestroy");
        super.onDestroy();
    }

    class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    class MyRun implements Runnable{
        PendingIntent pi;
        public MyRun(PendingIntent pi) {
            this.pi = pi;
            Log.d(LOG_TAG, "MyRun create");
        }
        void refreshPI(PendingIntent pi){
            this.pi = pi;
        }
        @Override
        public void run() {
            try {

                counter = 0;
                while(isDownloading&&++counter<=100){

                    Thread.sleep(interval);
                    Log.d(LOG_TAG,"myRun counter "+counter);
                    if(MainActivity.statusOfDownloading == 4){
                        isDownloading = false;
                        Intent intent = new Intent();
                        intent.putExtra(MainActivity.PARAM_COUNTER_DATA,counter);
                        pi.send(MyService.this,MainActivity.RESULT_CODE_CANCELLED,intent);
                        return;
                    }
                    if(isConnected){
                        Intent intent = new Intent();
                        Log.d("myLogs","in service connected");
                        intent.putExtra(MainActivity.PARAM_COUNTER_DATA,counter);
                        pi.send(MyService.this,MainActivity.RESULT_CODE_DOWNLOADING,intent);
                    }else{
                        sendNotif(""+ counter);
                    }

                }
                isDownloading = false;
                if(isConnected){

                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.PARAM_COUNTER_DATA,counter);
                    pi.send(MyService.this,MainActivity.RESULT_CODE_FINISH,intent);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    void sendNotif(String message) {
        Log.d(LOG_TAG,"in sendNotif");

        // Creates an explicit intent for an Activity in your app

        Intent resultIntent = new Intent(this, StopActivity.class);
        resultIntent.setAction("Cancel1");
        resultIntent.putExtra("result","cancel");
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.putExtra("result","process");
        intent2.setAction("OK1");
        PendingIntent okPendingIntent = PendingIntent.getActivity(this, 0, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle("My notification")
                        .setContentText("counter out of 25:  "+message)
                        .addAction(android.R.drawable.ic_menu_search,"Cancel",resultPendingIntent)
                        .addAction(android.R.drawable.ic_menu_search,"OK",okPendingIntent)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setAutoCancel(false);

        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
}