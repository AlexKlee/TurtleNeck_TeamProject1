package com.akl.turtleneck;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registry extends AppCompatActivity {
    EditText edtRegID, edtRegPass;
    Button btnRegOK;
    SQLiteDatabase sqlDB;

    MyLogDBHelper logHelper;
    String Lid, Lpass;
    String eID, ePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        setTitle("회원가입");
        edtRegID=findViewById(R.id.edtRegID);
        edtRegPass=findViewById(R.id.edtRegPass);
        btnRegOK=findViewById(R.id.btnRegOK);

        logHelper=new MyLogDBHelper(this);



        btnRegOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eID= edtRegID.getText().toString();
                ePass = edtRegPass.getText().toString();
                String query = "INSERT INTO logTbl VALUES ('"+ eID + "', '"+ ePass+"');";

                sqlDB=logHelper.getWritableDatabase();
                try{
                    sqlDB.execSQL(query);
                    Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                    sqlDB.close();
                    finish();
                }catch (SQLException se){
                    se.printStackTrace();
                    Toast.makeText(getApplicationContext(), "이미 가입된 아이디입니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }//onCreate end




}//main end

