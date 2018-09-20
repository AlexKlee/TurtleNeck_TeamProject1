package com.akl.turtleneck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class Main2Activity extends AppCompatActivity {
    RadioGroup rg;
    RadioButton rdAdult, rdKid;
    Spinner spinTimer, spinImage;

    Button btnStart, btnEnd, btnResult;
    ImageView imgV;

    MyLogDBHelper logHelper;


    //시간선택스피너 배열
    String[] timer = {"10초", "15초","30분", "1시간", "1시간30분", "2시간", "2시간30분", "3시간"};
    int[] timer2={10,15,30, 60, 90, 120, 150, 180};
    //이미지스피너 선택배열

    String[] image= new String[]{"성인1", "성인2", "성인3", "성인4", "성인5", "성인6"};
    int[] imageAddr=new int[]{R.drawable.good001, R.drawable.good002, R.drawable.good003,
            R.drawable.good004, R.drawable.good005, R.drawable.healing1};

    //  스피너용 어댑터
    ArrayAdapter<String> adTimer;
    ArrayAdapter<String> adImage;

    int posT, posI;  //  스피너의 아이템 선택 위치저장

    int type=0;//성인, 학생 구분변수

    SharedPreferences save;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle("TurtleNeck");
        rg= findViewById(R.id.rg);
        rdAdult=findViewById(R.id.rdAdult);
        rdKid=findViewById(R.id.rdKid);
        spinTimer=findViewById(R.id.spinTimer);
        spinImage=findViewById(R.id.spinImage);
        btnStart=findViewById(R.id.btnStart);
        btnEnd=findViewById(R.id.btnEnd);
        btnResult=findViewById(R.id.btnResult);
        imgV=findViewById(R.id.imgV);

        logHelper=new MyLogDBHelper(this);

        //라디오버튼(성인, 학생 선택)
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rdAdult://성인
                        image= new String[]{"성인1", "성인2", "성인3", "성인4", "성인5", "성인6"};
                        imageAddr=new int[]{R.drawable.good001, R.drawable.good002, R.drawable.good003,
                                R.drawable.good004, R.drawable.good005, R.drawable.healing1};
                        type=0;
                        break;
                    case R.id.rdKid://학생
                        image = new String[]{"아이1", "아이2", "아이3", "아이4", "아이5", "아이6"};
                        imageAddr= new int[]{R.drawable.prr001, R.drawable.prr002, R.drawable.prr003,
                                R.drawable.prr004, R.drawable.kids001, R.drawable.kid1};
                        type=1;

                        break;

                }
                adImage = new ArrayAdapter<String>(Main2Activity.this, R.layout.support_simple_spinner_dropdown_item, image);
                spinImage.setAdapter(adImage);//그림 스피너
                adImage.notifyDataSetChanged();
            }
        });//라디어버튼 선택 end

        //  스피너 어댑터 처리(처음)
        adTimer = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, timer);
        adImage = new ArrayAdapter<String>(Main2Activity.this, R.layout.support_simple_spinner_dropdown_item, image);

        //  스피너와 어댑터 연결
        spinTimer.setAdapter(adTimer);//시간스피너
        spinImage.setAdapter(adImage);//그림 스피너


        // 시간선택 스피너 시간연결
        spinTimer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                posT = timer2[position];  //  스피너로 선택한 시간 값 저장.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        //이미지 선택 스피너 이미지 연결
        spinImage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                posI = imageAddr[position];  //  스피너로 선택한 이미지 주소 저장
                imgV.setImageResource(posI);  // 스피너 선택시 이미지 뷰에 선택 이미지 출력
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = posT;
                int img=posI;
                save=getSharedPreferences("save",MODE_PRIVATE);
                editor=save.edit();
                editor.putInt("IMG",img);
                editor.putInt("type",type);
                editor.commit();
                //서비스에 설정된 시간값 전달
                Intent it = new Intent(Main2Activity.this, MyIntentService.class);
                it.putExtra("TIME", time);
                startService(it);
                finish();

                //notification 시작(푸시바)
                //푸시바출력
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                Intent notiintent = new Intent(Main2Activity.this, LockActivity.class);//푸시바 누르면 로그인 화면으로 이동해서 비밀번호 다시 입력
                PendingIntent pendingIntent = PendingIntent.getActivity(Main2Activity.this, 0, notiintent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(Main2Activity.this);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),android.R.drawable.star_on))
                    .setSmallIcon(android.R.drawable.star_on).setTicker("알람 간단한 설명").setContentTitle("TurtleNeck")
                    .setContentText("TurtleNeck 실행 중").setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent).setAutoCancel(false)//notification 터치시 화면 바뀌며 취소되지않게.
                    .setNumber(1);
                builder.setOngoing(true);//notification 계속유지(밀어서 해제 방지)
                notificationManager.notify(0, builder.build());//푸시바 출력(ID는 0)

                //finish();//우선 앱화면 종료, 백그라운드, notification 그대로 진행
            }
        });//폰 홈화면으로 이동 버튼(백그라운드 시작.)

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sharedPreferences 데이터 삭제,
                save = getSharedPreferences("save",MODE_PRIVATE);
                editor = save.edit();
                editor.clear();
                editor.commit();

                //백그라운드 기능 정지
                finish();
            }
        });//앱 종료 버튼(백그라운드 기능 종료)

    }//onCreate end

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,1,0, "사용시간확인");
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent tIt = new Intent(Main2Activity.this, TimeActivity.class);
        startActivity(tIt);
        return true;
    }

}//main end
