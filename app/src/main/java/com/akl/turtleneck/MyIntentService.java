package com.akl.turtleneck;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import android.os.Handler;
import java.util.logging.LogRecord;

public class MyIntentService extends IntentService {
    public Context appContext;
    public static final String ACTION = "com.akl.turtleneck.MyIntentService";

    int time=0;//전화면에서 설정한 시간
    int i =0;//백그라운드 실행 시간
    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service Start", Toast.LENGTH_SHORT).show();
        appContext=getBaseContext();//없으면 토스트값 표시안됨.액티비티의 context, 생성자나 context에서 기본설정된 context.
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        time=(Integer)intent.getExtras().get("TIME");//Main2Activity에서 받아온 시간 값.
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);

        int leastTime = time-5;//남은시간용변수
        try {
            while (i < time) {
                Thread.sleep(1000);//후에 분단위로 조정.
                if(i==leastTime){
                    showToast(leastTime);
                }
                i++;//1초마다 i값 증가.
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }//onHandleIntent end

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service Stop", Toast.LENGTH_SHORT).show();
        SharedPreferences save = getSharedPreferences("save",MODE_PRIVATE);
        SharedPreferences.Editor editor = save.edit();
        editor.putInt("icnt", i);
        editor.commit();

        //홈화면에서 다른 앱 실행시 이 앱이 종료되므로, 종료시 다시 실행하도록 설정.
        Intent it = new Intent(MyIntentService.this, LockActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }

    public void showToast(int t){
        if(appContext!= null){
            final int ti = t;//run()함수 안에 t값 전달 바로안됨
            Handler handler = new Handler(getMainLooper());//import값 주의.
            //import android.os.Handler;사용
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String time = String.valueOf(ti);
                    Toast.makeText(appContext, ti+"초 남음",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }//showToast end 돌아가는 동안 발생


}
