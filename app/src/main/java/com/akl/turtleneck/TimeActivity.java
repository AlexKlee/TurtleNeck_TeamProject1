package com.akl.turtleneck;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeActivity extends AppCompatActivity {
    SQLiteDatabase sqlDB;
    MyResultDBHelper resultHelper;

    Button btnMon, btnDay, btnYear;
    LinearLayout chart_time;
    SharedPreferences save;

    //막대그래프관련
    int type = 0;//월별, 일별, 연도별 구분변수
    private GraphicalView mChartView;
    private String[] mMonth = {"2개월 전", "1개월 전", "이번 달", "월 평균"};//월평균
    private String[] mDay = {"이틀 전", "하루 전", "오늘", "일 평균"};//일 평균
    private String[] mYear = {"2년 전", "1년 전", "올해", "연 평균"};//연 평균
    private String[] mX = mMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        setTitle("스마트폰 사용시간 비교");
        //단위별 사용시간 비교(하루, 한달, 한해)
        //그래프작성
        btnYear=findViewById(R.id.btnYear);
        btnMon=findViewById(R.id.btnMon);
        btnDay=findViewById(R.id.btnDay);
        chart_time=findViewById(R.id.chart_time);

        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawChart(2);
            }
        });

        btnMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawChart(1);
            }
        });
        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawChart(0);
            }
        });
    }//onCreate end

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0,1,0, "돌아가기");
//        menu.add(0,2,0,"로그아웃");

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        /*Intent tIt = new Intent(TimeActivity.this, Main2Activity.class);
        startActivity(tIt);
        */
        return true;
        //super.onOptionsItemSelected(item);
    }

    public void drawChart(int type){
        save = getSharedPreferences("save",MODE_PRIVATE);
        String id = save.getString("ID","");

        int[] x = {0,1,2,3};//가로축 개수
        //각각의 가로축에 해당하는 세로축의 값.
        /*{"2개월 전", "1개월 전", "이번 달", "월 평균"};//월평균
        {"이틀 전", "하루 전", "오늘", "일 평균"};//일 평균
        {"2년 전", "1년 전", "올해", "연 평균"};//연 평균*/
        int tbefore=0, obefore=0, now=0, aver=0;//2일(혹은 개월, 혹은 년) 전, 1일 전, 오늘, 평균)

        //총 시간 값 계산

        int div=3;//나눗셈값
        int[] y = {tbefore,obefore,now,aver};
        String typeText = null;
        if(type==0){
            typeText="연도별";
            for(int i=0; i<y.length-1;i++){
                y[i]= checkTimeDB(id,type, i);
                if(y[i]==0){
                    --div;
                }
            }
            if(div==0) {
                y[3] = 0;
            }else{
                y[3]=(y[0]+y[1]+y[2])/div;//평균값.
            }
        }else if(type==1){
            typeText="월별";
            for(int i=0; i<y.length-1;i++){
                y[i]= checkTimeDB(id,type, i);
                if(y[i]==0){
                    --div;
                }
            }
            if(div==0) {
                y[3] = 0;
            }else{
                y[3]=(y[0]+y[1]+y[2])/div;//평균값.
            }
        }else if(type==2){
            typeText="일별";
            for(int i=0; i<y.length-1;i++){
                y[i]= checkTimeDB(id,type, i);
                if(y[i]==0){
                    --div;
                }
            }
            if(div==0) {
                y[3] = 0;
            }else{
                y[3]=(y[0]+y[1]+y[2])/div;//평균값.
            }
        }
        XYSeries series = new XYSeries(typeText);

        for(int i=0;i<x.length;i++){//확인요망.,
            series.add(i, y[i]);
        }

        //각 시리즈에 해당하는 dataset생성
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(Color.BLUE);
        renderer.setFillPoints(true);
        renderer.setLineWidth(1);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("평균 사용량");
        multiRenderer.setXTitle(typeText);//?
        multiRenderer.setYTitle("사용 시간");
        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setPanEnabled(false,false);
        multiRenderer.setShowGrid(false);
        multiRenderer.setAntialiasing(true);
        multiRenderer.setLegendHeight(25);
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);
        multiRenderer.setYLabels(5);//y값 항목 수
        multiRenderer.setYAxisMax(500);//y값 최대치
        multiRenderer.setXAxisMin(-1);//맨왼쪽의 거리
        multiRenderer.setBackgroundColor(Color.LTGRAY);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setMargins(new int[]{20,20,20,20});
        multiRenderer.setXAxisMax(4);//가로줄 항목 개수
        multiRenderer.setLabelsTextSize(15);
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        multiRenderer.setDisplayChartValues(true);

        if(type==0){//연도
            mX=mYear;
        }else if(type==1){
            mX=mMonth;
        }else if(type==2){
            mX=mDay;
        }

        for(int i=0; i<x.length;i++){
            multiRenderer.addTextLabel(i, mX[i]);
        }
        multiRenderer.addSeriesRenderer(renderer);
        chart_time.removeAllViews();

        mChartView=ChartFactory.getBarChartView(this, dataset, multiRenderer, BarChart.Type.DEFAULT);

        chart_time.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }


    public int checkTimeDB(String id, int type, int type2){//type= 연도,월, 일 선택, type2= 2년전, 1년전, 올해(월, 일 별) 설정
        int alltime=0;

        //현재날짜
        Calendar calendar = new GregorianCalendar(Locale.KOREA);//한국 날짜
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        resultHelper= new MyResultDBHelper(TimeActivity.this);
        String query = "SELECT * FROM resultTbl WHERE id='"+id +"' ";

        if(type==0){//연단위
            if(type2==0){//2년전
                query += "and year = "+(year-2)+";";
            }else if(type2==1){//1년전
                query += "and year = "+(year-1)+";";
            }else if(type2==2){
                query += "and year = "+year+";";
            }
        }else if(type==1){
            query += "and year = "+year+" ";
            if(type2==0){//2년전
                query += "and month = "+(month-2)+";";
            }else if(type2==1){//1년전
                query += "and month = "+(month-1)+";";
            }else if(type2==2){
                query += "and month = "+month+";";
            }
        }else if(type==2){
            query += "and year = "+year+" and month = "+month+" ";
            if(type2==0){//2년전
                query += "and day = "+(day-2)+";";
            }else if(type2==1){//1년전
                query += "and day = "+(day-1)+";";
            }else if(type2==2){
                query += "and day = "+day+";";
            }
        }
        sqlDB=resultHelper.getReadableDatabase();
        Cursor cur;

        try{
            cur = sqlDB.rawQuery(query, null);

            while(cur.moveToNext()){
                int time = cur.getInt(2);
                alltime +=time;
            }
            cur.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        sqlDB.close();
        return alltime;
    }
}//main end
