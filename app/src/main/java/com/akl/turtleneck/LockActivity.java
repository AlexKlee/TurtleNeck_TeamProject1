package com.akl.turtleneck;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class LockActivity extends AppCompatActivity {
    //잠금화면




    int pageType = 1; //잠금화면으로 넘어올때 번호값

    String pw;
    int time=0;
    int img=0;//이미지 주소저장
    int type=0;//성인,학생구분

    EditText edtLock;
    ImageView imgView;
    View vDialog;

    MyResultDBHelper mrhelper;
    SQLiteDatabase sqlDB;

    SharedPreferences save;

    //배경음악실행
    private static MediaPlayer mp;

    @Override
    public void onBackPressed() {
        //뒤로가기버튼 무력화
    }


    @Override//수정!
    protected void onUserLeaveHint() {
        if(pageType==1){
            if(type==0){
                Toast.makeText(getApplicationContext(), "확인버튼을 눌러야 종료됩니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "비밀번호가 입력되지않아 5초 후 재실행됩니다.",Toast.LENGTH_SHORT).show();
            }

            Intent it2 = new Intent(this, LockActivity.class);
            it2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//가장 최우선으로 보이기.//Intent.FLAG_ACTIVITY_CLEAR_TASK |
            startActivity(it2);
            //?서비스 추가 설정?
        }else if(pageType==2){

        }
    }//onUserLeaveHint end

    @Override//수정!
    protected void onDestroy() {//홈화면 이동 후 다른 앱실행시 발생
        super.onDestroy();

        if(pageType==1){
            if(type==0){
                Toast.makeText(getApplicationContext(), "확인버튼을 눌러야 종료됩니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "비밀번호가 입력되지않아 5초 후 재실행됩니다.",Toast.LENGTH_SHORT).show();
            }
            Intent it2 = new Intent(this, LockActivity.class);
            //it2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//가장 최우선으로 보이기.
            startActivity(it2);
        }else if (pageType==2){
            //finish가 입력될 경우 종료실행 반복

        }
    }//onDestroy End


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);//잠금화면 해제, 이 액티비티가 먼저 화면에 나타나게 설정
        setContentView(R.layout.activity_lock);
        setTitle("잠금화면");

        //혹시모를 서비스 종료
        Intent it= new Intent(this, MyIntentService.class);
        stopService(it);

        mp= MediaPlayer.create(this,R.raw.shark);
        mp.setLooping(true);
        mp.start();

        //sharedPreferences호출

        //다이얼로그 xml 만들 것.
        //푸쉬바 설정 할것.

        save = getSharedPreferences("save",MODE_PRIVATE);
        img=save.getInt("IMG",0);//이미지 주소
        type=save.getInt("type", 0);
        vDialog=(View)View.inflate(LockActivity.this, R.layout.dialog, null);

        edtLock=vDialog.findViewById(R.id.edtLock);
        imgView=vDialog.findViewById(R.id.imgView);
        imgView.setImageResource(img);

        if(type==1){//학생
            edtLock.setVisibility(View.VISIBLE);
        }else{//성인
            edtLock.setVisibility(View.INVISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(LockActivity.this);
        builder.setTitle("잠금화면").setCancelable(false)//다이얼로그 밖 터치시 취소 금지
        .setView(vDialog)
        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //이 항목에 로그인창을 만들경우, 잘못된 값이어도 종료버튼을 누르면 취소됨.
                //아래 다이얼로그 새로 설정 후 버튼 항목 새로 설정

            }
        });//확인버튼
        final AlertDialog dlg = builder.create();
        dlg.show();

        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pass = edtLock.getText().toString();
                String sqpass = null;


                pw=save.getString("PW","");//원래비밀번호
                sqpass=pw;

                Intent it = new Intent(LockActivity.this, Main2Activity.class);

                if(type==0){//성인은 암호입력 필요없음.
                    Toast.makeText(getApplicationContext(), "확인되었습니다.",Toast.LENGTH_SHORT).show();
                    dlg.dismiss();
                    NotificationManager mangager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE ) ;
                    mangager.cancel( 0 ) ;  // 정지할 Notification(ID 0 notification 취소)*/
                    startActivity(it);
                    mp.stop();
                    updateTimeDB();
                    pageType=2;
                    finish();
                }else{//학생
                    if(pass.equals(sqpass)){//db의 비밀번호와 값이 같다면.
                        Toast.makeText(getApplicationContext(), "확인되었습니다.",Toast.LENGTH_SHORT).show();
                        dlg.dismiss();
                        pageType=2;
                        //it.putExtra("TYPE", "2");
                        //db에 저장.(만들것.)
                        NotificationManager mangager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE ) ;
                        mangager.cancel( 0 ) ;  // 정지할 Notification(ID 0 notification 취소)*/
                        mp.stop();
                        updateTimeDB();
                        startActivity(it);
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(), "잘못 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });//확인버튼 메소드설정

    }//onCreate end

    public void updateTimeDB(){
        //사용시간(time값), 현재 날짜(연,월,일) db등록
        save = getSharedPreferences("save",MODE_PRIVATE);
        String id = save.getString("ID","");
        time= save.getInt("icnt",0);//사용시간

        Calendar calendar = new GregorianCalendar(Locale.KOREA);//한국 날짜
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mrhelper= new MyResultDBHelper(LockActivity.this);

        String query = "INSERT INTO resultTbl VALUES ("+null+", '"+id+"', "+time+", "+year+", "+month+", "+day+");";

        sqlDB = mrhelper.getWritableDatabase();
        try{
            sqlDB.execSQL(query);

            Toast.makeText(getApplicationContext(), "시간저장", Toast.LENGTH_SHORT).show();
            sqlDB.close();
        }catch (SQLException se){
            se.printStackTrace();

            Toast.makeText(getApplicationContext(), "오류발생", Toast.LENGTH_SHORT).show();
        }

    }//updateDB




}//main end
