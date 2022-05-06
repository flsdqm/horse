package com.example.horsey;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class CARSMainActivity2 extends AppCompatActivity implements View.OnClickListener {

    public static final String KEYWORD="com.example.evasch";
    //存储所有题目对应的图片
    //private ArrayList<Bitmap> arrBitmap=new ArrayList<>();
    //存储每一个题目、选项及分数
    private ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
    //存储选中的选项
    private String[] chosenButton;
    private int[] splitScore;
    private boolean[] isAnswered;
    private String[] result=new String[4];

    private int count=1;
    private int totalScore=0;
    private ImageView image;
    private TextView tv;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button submit;
    private ImageButton left;
    private TextView number;
    private ImageButton right;
    private DrawerLayout drawerLayout;
    private GridLayout gridLayout;
    private Button back;

    private String str2,str4,str6;
    private TextView button2,button4,button6,button8,button10;

    private String datetime;
    //private Button getResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cars_activity_main2);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View decorView=getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        image=findViewById(R.id.image);
        tv=findViewById(R.id.problemtitle);
        b1=findViewById(R.id.never);
        b2=findViewById(R.id.seldom);
        b3=findViewById(R.id.often);
        b4=findViewById(R.id.always);
        submit=findViewById(R.id.submit);
        left=findViewById(R.id.leftbutton);
        number=findViewById(R.id.number);
        right=findViewById(R.id.rightbutton);
        drawerLayout=findViewById(R.id.main2);
        //DrawerLayout drawerLayout=findViewById(R.id.main2);
        //final View cView=findViewById(R.id.drawer);
        //DrawerLayout.LayoutParams params=new DrawerLayout.LayoutParams(500,ViewGroup.LayoutParams.MATCH_PARENT,Gravity.CENTER);
        //drawerLayout.setLayoutParams(params);
        //drawerLayout.openDrawer(findViewById(R.layout.drawer_layout));
        //getResult=findViewById(R.id.getresult);
        getAllWidgets();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.leftbutton:
                onClickLeftbutton(view);
                break;
            case R.id.rightbutton:
                onClickRightbutton(view);
                break;
            case R.id.never:
                onClickNever(view);
                break;
            case R.id.seldom:
                onClickSeldom(view);
                break;
            case R.id.often:
                onClickOften(view);
                break;
            case R.id.always:
                onClickAlways(view);
                break;
            case R.id.submit:
                onClickSubmit(view);
                break;
            case R.id.number:
                onClickShowPopBox(view);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void back(View view){
        finish();
    }

    public void home(View view){
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
    }

    //加载问题及处理事务
    public void getAllWidgets(){
        getProblems();
        loadTransactions();
    }

    //加载相关问题及逻辑关系
    public void getProblems(){
        try {
            StringBuffer buffer=new StringBuffer();
            InputStream inputStream =getAssets().open("CARS.json");
            BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
            byte[] b=new byte[1024];

            while(bufferedInputStream.read(b)!=-1){
                String sta=new String(b);
                buffer.append(sta);
            }

            String problems=buffer.toString();
            try {
                JSONArray jsonArray=new JSONArray(URLDecoder.decode(problems,"GB2312"));
                System.out.println(jsonArray.toString());

                //生成一个问题
                for(int i=0;i<jsonArray.length();i++){
                    HashMap<String,String> map=new HashMap<String,String>();
                    map.put("order",jsonArray.getJSONObject(i).getString("order"));
                    map.put("title",jsonArray.getJSONObject(i).getString("title"));

                    JSONObject j1=new JSONObject(URLDecoder.decode(jsonArray.getJSONObject(i).getString("option1"),"GB2312"));
                    JSONObject j2=new JSONObject(URLDecoder.decode(jsonArray.getJSONObject(i).getString("option2"),"GB2312"));
                    JSONObject j3=new JSONObject(URLDecoder.decode(jsonArray.getJSONObject(i).getString("option3"),"GB2312"));
                    JSONObject j4=new JSONObject(URLDecoder.decode(jsonArray.getJSONObject(i).getString("option4"),"GB2312"));

                    map.put("opt1name",j1.getString("name").toString());
                    map.put("opt1score",j1.getString("score").toString());
                    map.put("opt2name",j2.getString("name").toString());
                    map.put("opt2score",j2.getString("score").toString());
                    map.put("opt3name",j3.getString("name").toString());
                    map.put("opt3score",j3.getString("score").toString());
                    map.put("opt4name",j4.getString("name").toString());
                    map.put("opt4score",j4.getString("score").toString());
                    arrayList.add(map);
                }
                splitScore=new int[arrayList.size()];
                isAnswered=new boolean[arrayList.size()];
                chosenButton=new String[arrayList.size()];
                autoWrap();

                //for(int i=0;i<jsonArray.length();i++){
                //    String name="pic"+(i+1)+".jpg";
                //    BufferedInputStream bufferedInputStream1=new BufferedInputStream(getAssets().open(name));
                //    Bitmap bm= BitmapFactory.decodeStream(bufferedInputStream1);
                //    arrBitmap.add(bm);
                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(String name){
        BufferedInputStream bufferedInputStream1= null;
        try {
            bufferedInputStream1 = new BufferedInputStream(getAssets().open(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=3;
        options.inPreferredConfig=Bitmap.Config.RGB_565;
        Bitmap bm= BitmapFactory.decodeStream(bufferedInputStream1,null,options);
        return bm;
    }

    //加载处理事务
    public void loadTransactions(){
        String strs="CARSpic"+(count)+".jpg";
        Bitmap b=getBitmap(strs);

        image.setImageBitmap(b);
        tv.setText(arrayList.get(count-1).get("title"));
        b1.setText(arrayList.get(count-1).get("opt1name"));
        b2.setText(arrayList.get(count-1).get("opt2name"));
        b3.setText(arrayList.get(count-1).get("opt3name"));
        b4.setText(arrayList.get(count-1).get("opt4name"));
        number.setText(String.valueOf(count)+"/"+String.valueOf(arrayList.size()));
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        number.setOnClickListener(this);
    }

    //点击事件
    private void onClickAlways(View view) {
        splitScore[count-1]=4;
        isAnswered[count-1]=true;

        b1.setBackgroundColor(Color.parseColor("#65B1BF"));
        b2.setBackgroundColor(Color.parseColor("#65B1BF"));
        b3.setBackgroundColor(Color.parseColor("#65B1BF"));
        b4.setBackgroundColor(Color.parseColor("#187485"));

        chosenButton[count-1]= b4.getText().toString();
        System.out.println(chosenButton[count-1]+":"+b4.getText().toString());
        countTotal();
    }

    private void onClickOften(View view) {
        splitScore[count-1]=3;
        isAnswered[count-1]=true;

        b1.setBackgroundColor(Color.parseColor("#65B1BF"));
        b2.setBackgroundColor(Color.parseColor("#65B1BF"));
        b3.setBackgroundColor(Color.parseColor("#187485"));
        b4.setBackgroundColor(Color.parseColor("#65B1BF"));

        chosenButton[count-1]= b3.getText().toString();
        System.out.println(chosenButton[count-1]+":"+b3.getText().toString());
        countTotal();
    }
    private void onClickSeldom(View view) {
        splitScore[count-1]=2;
        isAnswered[count-1]=true;

        b1.setBackgroundColor(Color.parseColor("#65B1BF"));
        b2.setBackgroundColor(Color.parseColor("#187485"));
        b3.setBackgroundColor(Color.parseColor("#65B1BF"));
        b4.setBackgroundColor(Color.parseColor("#65B1BF"));

        chosenButton[count-1]= b2.getText().toString();
        System.out.println(chosenButton[count-1]+":"+b2.getText().toString());
        countTotal();
    }
    private void onClickNever(View view) {
        splitScore[count-1]=1;
        isAnswered[count-1]=true;

        b1.setBackgroundColor(Color.parseColor("#187485"));
        b2.setBackgroundColor(Color.parseColor("#65B1BF"));
        b3.setBackgroundColor(Color.parseColor("#65B1BF"));
        b4.setBackgroundColor(Color.parseColor("#65B1BF"));

        chosenButton[count-1]= b1.getText().toString();
        System.out.println(chosenButton[count-1]+":"+b1.getText().toString());
        countTotal();
    }

    private void onClickSubmit(View view) {
        setContentView(R.layout.click_to_see_result);
        prepareResult();
    }

    private void getResultList(){
        try {
            StringBuffer buffer=new StringBuffer();
            StringBuffer buffer1=new StringBuffer();
            InputStream inputStream = null;
            InputStream resultIS=null;

            inputStream = getAssets().open("CARSRESULT.json");
            resultIS=getAssets().open("CARSEVA.json");

            BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
            BufferedInputStream bufferedInputStream1=new BufferedInputStream(resultIS);

            byte[] b=new byte[1024];
            while(bufferedInputStream.read(b)!=-1){
                String sta=new String(b);
                buffer.append(sta);
            }
            String problems=buffer.toString();

            byte[] b1=new byte[1024];
            while(bufferedInputStream1.read(b1)!=-1){
                String sta=new String(b1);
                buffer1.append(sta);
            }
            String problems1=buffer1.toString();

            JSONArray jsonArray= null;
            JSONArray jsonArray1=null;

            try {
                jsonArray = new JSONArray(URLDecoder.decode(problems,"GB2312"));
                System.out.println(jsonArray.toString());
                str2=jsonArray.getJSONObject(0).getString("nickname");
                str4=jsonArray.getJSONObject(0).getString("sex");
                str6=jsonArray.getJSONObject(0).getString("age");

                jsonArray1=new JSONArray(URLDecoder.decode(problems1,"GB2312"));
                System.out.println(jsonArray1.toString());
                result=new String[4];
                result[0]=jsonArray1.getJSONObject(0).getString("testResult1");
                result[1]=jsonArray1.getJSONObject(0).getString("testResult2");
                result[2]=jsonArray1.getJSONObject(0).getString("testResult3");
                result[3]=jsonArray1.getJSONObject(0).getString("testResult4");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCurrentTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long currenttimemillis=System.currentTimeMillis();
        datetime=sdf.format(currenttimemillis);
    }

    private void prepareResultList(){
        button2=findViewById(R.id.button2);
        button4=findViewById(R.id.button4);
        button6=findViewById(R.id.button6);
        button8=findViewById(R.id.button8);
        button10=findViewById(R.id.button10);
        button2.setText(str2);
        button4.setText(str4);
        button6.setText(str6);
        getCurrentTime();
        button8.setText(datetime);
        judgeSituation();
        makeChart();
    }

    private void makeChart(){
        BarChart bar;                                            //*********该行代码需插入***********
        List<BarEntry> list=new ArrayList<>();                            //*********该行代码需插入***********
        //*************下面代码均插入*******************
        bar = (BarChart) findViewById(R.id.bar);
        //可以用循环，第一个数字是x轴的坐标
        list.add(new BarEntry(1,splitScore[0]));                                //将第2个数字改为第一题分数
        list.add(new BarEntry(2,splitScore[1]));                                //将第2个数字改为第二题分数
        list.add(new BarEntry(3,splitScore[2]));                                //将第2个数字改为第三题分数
        list.add(new BarEntry(4,splitScore[3]));                                //将第2个数字改为第四题分数
        list.add(new BarEntry(5,splitScore[4]));                                //将第2个数字改为第五题分数
        list.add(new BarEntry(6,splitScore[5]));                                //将第2个数字改为第六题分数
        list.add(new BarEntry(7,splitScore[6]));                                //将第2个数字改为第七题分数
        list.add(new BarEntry(8,splitScore[7]));                                //将第2个数字改为第八题分数
        list.add(new BarEntry(9,splitScore[8]));                                //将第2个数字改为第九题分数
        list.add(new BarEntry(10,splitScore[9]));                               //将第2个数字改为第十题分数
        list.add(new BarEntry(11,splitScore[10]));                               //将第2个数字改为第十一题分数
        list.add(new BarEntry(12,splitScore[11]));                               //将第2个数字改为第十二题分数
        list.add(new BarEntry(13,splitScore[12]));                               //将第2个数字改为第十三题分数
        list.add(new BarEntry(14,splitScore[13]));                               //将第2个数字改为第十四题分数
        list.add(new BarEntry(15,splitScore[14]));                               //将第2个数字改为第十五题分数


        BarDataSet barDataSet=new BarDataSet(list,"得分");
        BarData barData=new BarData(barDataSet);
        bar.setData(barData);

        bar.setBackgroundColor(Color.TRANSPARENT);
        bar.getXAxis().setDrawGridLines(false);
        bar.getAxisLeft().setDrawGridLines(false);


        bar.getDescription().setEnabled(false);


        Legend legend=bar.getLegend();
        legend.setEnabled(false);

        XAxis xAxis=bar.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.parseColor("#65B1BF"));
        xAxis.setAxisLineWidth(2);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if(v==1)
                    return "人际关系";
                if(v==2)
                    return "模仿";
                if(v==3)
                    return "情绪反应";
                if(v==4)
                    return "刻板行为";
                if(v==5)
                    return "物体运用";
                if(v==6)
                    return "转变适应";
                if(v==7)
                    return "视觉反应";
                if(v==8)
                    return "听觉反应";
                if(v==9)
                    return "味嗅触觉反应";
                if(v==10)
                    return "焦虑";
                if(v==11)
                    return "口语沟通";
                if(v==12)
                    return "非语言沟通";
                if(v==13)
                    return "活动量";
                if(v==14)
                    return "智力水平";
                if(v==15)
                    return "总体印象";
                return "";
            }
        });
        xAxis.setAxisMaximum(16);
        xAxis.setAxisMinimum(0);

        xAxis.setLabelCount(15,false);
        barData.setBarWidth(0.7f);
        bar.setScaleMinima(2f,1);

        YAxis AxisLeft=bar.getAxisLeft();
        AxisLeft.setDrawGridLines(false);
        AxisLeft.setAxisLineColor(Color.parseColor("#65B1BF"));
        AxisLeft.setAxisLineWidth(2);
        AxisLeft.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {

                for (int a=0;a<5;a++){
                    if (a==v){
                        return a+"";
                    }
                }
                return "";
            }
        });
        AxisLeft.setAxisMaximum(5);
        AxisLeft.setAxisMinimum(0);

        AxisLeft.setLabelCount(4,false);

        bar.getAxisRight().setEnabled(false);

        barDataSet.setColors(Color.rgb(148,41,35),Color.rgb(85,70,173),Color.rgb(222,125,44),Color.rgb(137,190,178),Color.rgb(209,73,78));

        barDataSet.setBarShadowColor(Color.RED);
        barDataSet.setHighlightEnabled(false);

        bar.setBackgroundColor(Color.parseColor("#fafafa"));
        bar.notifyDataSetChanged();
        bar.invalidate();
        bar.animateY(3000);

        //*************代码插入截至到这里*******************

    }

    private void judgeSituation() {
        if (totalScore==0){
            button10.setText("本次量表得分为："+totalScore+"分\n\n"+result[0]);
        }
        else if(totalScore<=13){
            button10.setText("本次量表得分为："+totalScore+"分\n\n"+result[1]);
        }
        else if(totalScore<=27){
            button10.setText("本次量表得分为："+totalScore+"分\n\n"+result[2]);
        }
        else {
            button10.setText("本次量表得分为："+totalScore+"分\n\n"+result[3]);
        }
    }

    private void showResultList(View view){
        //arrBitmap.clear();
        setContentView(R.layout.cars_result_list);
        getResultList();
        prepareResultList();
    }

    private void onClickLeftbutton(View view){
        if(count>1){
            count--;
            String strs="CARSpic"+(count)+".jpg";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            b3.setText(arrayList.get(count-1).get("opt3name"));
            b4.setText(arrayList.get(count-1).get("opt4name"));
            checkChosen(count);
        }
        else if(count==1){
            count=arrayList.size();
            String strs="CARSpic"+(count)+".jpg";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            b3.setText(arrayList.get(count-1).get("opt3name"));
            b4.setText(arrayList.get(count-1).get("opt4name"));
            checkChosen(count);
        }
        number.setText(String.valueOf(count)+"/"+String.valueOf(arrayList.size()));
    }

    private void onClickRightbutton(View view){
        if(count<arrayList.size()){
            count++;
            String strs="CARSpic"+(count)+".jpg";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            b3.setText(arrayList.get(count-1).get("opt3name"));
            b4.setText(arrayList.get(count-1).get("opt4name"));
            checkChosen(count);
        }
        else if (count==arrayList.size()){
            count=1;
            String strs="CARSpic"+(count)+".jpg";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            b3.setText(arrayList.get(count-1).get("opt3name"));
            b4.setText(arrayList.get(count-1).get("opt4name"));
            checkChosen(count);
        }
        number.setText(String.valueOf(count)+"/"+String.valueOf(arrayList.size()));
    }

    //计算总分
    private void countTotal(){
        for(boolean i:isAnswered){
            if(i==false){
                submit.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                return;
            }
        }
        submit.setBackgroundColor(Color.parseColor("#65B1BF"));
        submit.setOnClickListener(this);
    }

    private void checkChosen(int count){
        if(b1.getText().toString()==chosenButton[count-1]){
            System.out.println(chosenButton[count-1]+":"+b1.getText().toString());
            b1.setBackgroundColor(Color.parseColor("#187485"));
            b2.setBackgroundColor(Color.parseColor("#65B1BF"));
            b3.setBackgroundColor(Color.parseColor("#65B1BF"));
            b4.setBackgroundColor(Color.parseColor("#65B1BF"));
        }
        else if(b2.getText().toString()==chosenButton[count-1]){
            System.out.println(chosenButton[count-1]+":"+b2.getText().toString());
            b1.setBackgroundColor(Color.parseColor("#65B1BF"));
            b2.setBackgroundColor(Color.parseColor("#187485"));
            b3.setBackgroundColor(Color.parseColor("#65B1BF"));
            b4.setBackgroundColor(Color.parseColor("#65B1BF"));
        }
        else if(b3.getText().toString()==chosenButton[count-1]){
            System.out.println(chosenButton[count-1]+":"+b3.getText().toString());
            b1.setBackgroundColor(Color.parseColor("#65B1BF"));
            b2.setBackgroundColor(Color.parseColor("#65B1BF"));
            b3.setBackgroundColor(Color.parseColor("#187485"));
            b4.setBackgroundColor(Color.parseColor("#65B1BF"));
        }
        else if(b4.getText().toString()==chosenButton[count-1]){
            System.out.println(chosenButton[count-1]+":"+b3.getText().toString());
            b1.setBackgroundColor(Color.parseColor("#65B1BF"));
            b2.setBackgroundColor(Color.parseColor("#65B1BF"));
            b3.setBackgroundColor(Color.parseColor("#65B1BF"));
            b4.setBackgroundColor(Color.parseColor("#187485"));
        }
        else {
            b1.setBackgroundColor(Color.parseColor("#65B1BF"));
            b2.setBackgroundColor(Color.parseColor("#65B1BF"));
            b3.setBackgroundColor(Color.parseColor("#65B1BF"));
            b4.setBackgroundColor(Color.parseColor("#65B1BF"));
        }
    }

    //准备结果
    private void prepareResult(){
        Button getResult=findViewById(R.id.getresult);
        getResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int eachScore:splitScore) {
                    totalScore=totalScore+eachScore;
                }
                System.out.println("分数："+totalScore);
                showResultList(view);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void onClickShowPopBox(View view){
        back=findViewById(R.id.back);
        drawerLayout.setScrimColor(Color.parseColor("#80ffffff"));
        drawerLayout.openDrawer(findViewById(R.id.drawer));
        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawerLayout.closeDrawer(findViewById(R.id.drawer));
                    }
                }
        );
        checkIfAnswered();
    }

    private void checkIfAnswered() {
        gridLayout=findViewById(R.id.gridlayout);
        for(int i=0;i<arrayList.size();i++){
            TextView ts=findViewById(i);
            if(isAnswered[i]==true){
                ts.setBackgroundResource(R.drawable.problem_shape_answered);
                ts.setTextColor(Color.WHITE);

            }
        }
    }

    private void autoWrap(){
        gridLayout=findViewById(R.id.gridlayout);
        for(int i=0;i<arrayList.size();i++){
            Button t=new Button(this);
            t.setId(i);
            //GradientDrawable g=new GradientDrawable();
            //g.setShape(GradientDrawable.OVAL);
            //g.setColor(Color.TRANSPARENT);
            //g.setStroke(5,Color.BLACK);
            //g.setSize(130,130);
            //t.setBackgroundDrawable(g);
            t.setBackgroundResource(R.drawable.problem_shape);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(130,130);
            params.gravity=Gravity.CENTER_VERTICAL;
            t.setLayoutParams(params);
            int j=i+1;
            if(j<10){
                t.setText(String.format(getString(R.string.add_zero),j));
            } else {
                t.setText(String.format(getString(R.string.whole),j));
            }
            t.setTextSize(40);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int i=t.getId();
                            count=i+1;
                            String strs="CARSpic"+(count)+".jpg";
                            Bitmap b=getBitmap(strs);

                            image.setImageBitmap(b);
                            tv.setText(arrayList.get(count-1).get("title"));
                            b1.setText(arrayList.get(count-1).get("opt1name"));
                            b2.setText(arrayList.get(count-1).get("opt2name"));
                            b3.setText(arrayList.get(count-1).get("opt3name"));
                            b4.setText(arrayList.get(count-1).get("opt3name"));
                            checkChosen(count);
                            number.setText(String.valueOf(count)+"/"+String.valueOf(arrayList.size()));
                        }
                    }
            );
            gridLayout.addView(t);
        }
    }
}
