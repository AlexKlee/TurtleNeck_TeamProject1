package kr.co.gizmos.shop;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

import java.util.ArrayList;

//notification 띄우고.
//백그라운드에서 거리 정보 받아오기.
//도착했거나, 취소 했을 경우 서비스 취소.

public class MyService extends Service {
    private NotificationManager notifiM;
    private ServiceThread thread;
    private Notification noti;
    private LocationManager lm;

    double longit, latit, dist, shoplongit, shoplatit;
    String activity_name;
    SharedPreferences sepref;

    Location userLoca, shopLoca;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//백그라운드 실행 동작 들어감

        lm=(LocationManager)getSystemService(this.LOCATION_SERVICE);
        sepref=getSharedPreferences("appData",MODE_PRIVATE);
        longit=Double.parseDouble(sepref.getString("longt","0"));
        latit=Double.parseDouble(sepref.getString("latt","0"));
        activity_name=sepref.getString("activity_name","");
        shoplongit=Double.parseDouble(sepref.getString("shop_map_x","0"));
        shoplatit=Double.parseDouble(sepref.getString("shop_map_y","0"));
        userLoca=new Location("userLoca");
        shopLoca=new Location("shopLoca");
        userLoca.setLongitude(longit);
        userLoca.setLatitude(latit);
        shopLoca.setLongitude(shoplongit);
        shopLoca.setLatitude(shoplatit);
        notifiM=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler= new myServiceHandler();
        //mfrag2=new mapFragment2();


        thread=new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {//서비스가 종료될때 실행되는 함수 들어감
        thread.stopForever();
        thread=null;
    }

    public class myServiceHandler extends Handler{
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent= new Intent(MyService.this, MapReservation.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(MyService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            noti=new Notification.Builder(getApplicationContext())
                    .setContentTitle("그냥 이거 먹자").setContentText("누르면 지도화면으로")
                    .setSmallIcon(R.drawable.ic_map_no_02).setTicker("그냥 이거 먹자")
                    .setContentIntent(pendingIntent).build();
            notifiM.notify(777,noti);
        }

    }

    public class ServiceThread extends Thread {//쓰레드생성
        Handler handler;
        boolean isRun=true;
        //SharedPreferences stpref= getSharedPreferences("appData", MODE_PRIVATE);
        public ServiceThread(Handler handler){
            this.handler=handler;
        }

        public void stopForever(){
            synchronized (this){
                this.isRun=false;
            }
        }

        public void run(){
            Looper.prepare();

            while(isRun){//반복적으로 수행할 작업
                //handler.sendEmptyMessage(0);//핸들러의 notification실행
                try{
                    requestMyLocation();
                    switch ((int)dist){
                        case 200:
                            Toast.makeText(getApplicationContext(), "200m", Toast.LENGTH_SHORT).show();
                            break;
                        case 100:
                            Toast.makeText(getApplicationContext(), "100m", Toast.LENGTH_SHORT).show();
                            break;
                        case 50:
                            Toast.makeText(getApplicationContext(), "50m", Toast.LENGTH_SHORT).show();
                            isRun=false;
                            break;
                   /*     case "취소":
                            return;
                        case "곧 도착":
                            return;*/
                    }
                    Toast.makeText(getApplicationContext(), dist + "m", Toast.LENGTH_SHORT).show();

                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }//thread end
    //나의 위치 요청
    public void requestMyLocation(){
        if(ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        //요청
        //gps검색
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, mlocationListener);
        //네트워크(와이파이)검색
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, mlocationListener);

    }




    //위치정보 구하기 리스너
    private final LocationListener mlocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longit = location.getLongitude(); //경도
            latit = location.getLatitude();   //위도
            String provider = location.getProvider();   //위치제공자
            userLoca.setLongitude(longit);
            userLoca.setLatitude(latit);

            dist=userLoca.distanceTo(shopLoca);//거리계산

            //if(longitude==0||latitude==0){
            //latitude =37.49085971;
            //longitude  =126.72073882;
            //default부평역
            //
            // longit=String.valueOf(longitude);
            //latit=String.valueOf(latitude);
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            Toast.makeText(getApplicationContext(),"위치정보 : " + provider + "\n거리 : "  +dist,
                    Toast.LENGTH_SHORT).show();
            lm.removeUpdates(mlocationListener);

/*
            //임시저장
            mapref=getSharedPreferences("appData",MODE_PRIVATE);
            SharedPreferences.Editor mapeditor=mapref.edit();

            mapeditor.putString("longt",longit);
            mapeditor.putString("latt",latit);
            mapeditor.commit();*/

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { Log.d("gps", "onStatusChanged"); }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };


}
