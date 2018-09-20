package com.akl.turtleneck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText edtID, edtPass;
    Button btnOK, btnReg;

    MyLogDBHelper logHelper;
    SQLiteDatabase sqlDB;

    String Lid, Lpass;

    //확인된 id, 비번 값 저장
    SharedPreferences save;
    SharedPreferences.Editor editor;
    private boolean D=true;//디버그용, 나중에 false로
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("환영합니다.");
        
        //디버그용 예제
        if(D){
            Log.d(TAG,"onCreate");
        }
        
        edtID=findViewById(R.id.edtID);
        edtPass=findViewById(R.id.edtPass);
        btnOK=findViewById(R.id.btnOK);
        btnReg=findViewById(R.id.btnReg);

        logHelper= new MyLogDBHelper(this);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eID = edtID.getText().toString();
                String ePass = edtPass.getText().toString();
                checkLog(eID,ePass);
            }
        });//btnOK end

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regit = new Intent(MainActivity.this, Registry.class);
                startActivity(regit);
            }
        });//btnReg end

    }//onCreate end

    public void checkLog(String id, String pw){
        String query = "SELECT * FROM logTbl WHERE id = '"+id+"';";
        sqlDB=logHelper.getReadableDatabase();

        Cursor cur;
        try{
            cur = sqlDB.rawQuery(query, null);
            while(cur.moveToNext()){
                Lid=cur.getString(0);
                Lpass=cur.getString(1);
            }
            cur.close();
            sqlDB.close();

            if(Lid.equals(id)&&Lpass.equals(pw)){
                Toast.makeText(getApplicationContext(), Lid+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(MainActivity.this, Main2Activity.class);
                it.putExtra("ID", Lid);
                it.putExtra("PW", Lpass);
                startActivity(it);

                //맞는 아이디와 비번값 전달
                save=getSharedPreferences("save",MODE_PRIVATE);//sharedPreference 생성, 파일명은 save
                editor=save.edit();
                editor.putString("ID", id);
                editor.putString("PW", pw);//save라는 sharedpreference에 저장
                editor.commit();//전달완료
                finish();//로그인화면 종료
            }else{
                Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ID가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }//checLog end
}//main end
