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
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
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

public class PCDISentencesMainActivity2 extends AppCompatActivity implements View.OnClickListener {

    public static final String KEYWORD="com.example.evasch";
    //存储所有题目对应的图片
    //private ArrayList<BitmapDrawable> arrBitmap=new ArrayList<>();
    //存储每一个题目、选项及分数
    private ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
    //存储选中的选项
    private String[] chosenButton;
    private int[] splitScore;
    private boolean[] isAnswered;
    private String[] result=new String[4];

    private int noun=0;
    private int verb=0;
    private int other=0;

    private int count=1;
    private int totalScore=0;
    private ImageView image;
    private TextView tv;
    private Button b1;
    private Button b2;
    //private Button b3;
    //private Button b4;
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
        setContentView(R.layout.pcdisentences_activity_main2);
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
        //b3=findViewById(R.id.often);
        //b4=findViewById(R.id.always);
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
            InputStream inputStream =getAssets().open("PCDISentences.json");
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

                    map.put("opt1name",j1.getString("name").toString());
                    map.put("opt1score",j1.getString("score").toString());
                    map.put("opt2name",j2.getString("name").toString());
                    map.put("opt2score",j2.getString("score").toString());
                    map.put("sort",jsonArray.getJSONObject(i).getString("sort"));
                    arrayList.add(map);
                }
                splitScore=new int[arrayList.size()];
                isAnswered=new boolean[arrayList.size()];
                chosenButton=new String[arrayList.size()];
                autoWrap();

                //for(int i=0;i<jsonArray.length();i++){
                //    String name="pic"+(i+1)+".png";
                //    BufferedInputStream bufferedInputStream1=new BufferedInputStream(getAssets().open(name));
                //    BitmapFactory.Options options=new BitmapFactory.Options();
                //    options.inPreferredConfig=Bitmap.Config.RGB_565;
                //    options.inPurgeable = true;
                //    options.inInputShareable = true;
                //    Bitmap bm= BitmapFactory.decodeStream(bufferedInputStream1,null,options);
                //    BitmapDrawable bd=new BitmapDrawable(getResources(),bm);
                //    //image.setBackgroundDrawable(bd);
                //    arrBitmap.add(bd);
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
        String strs="PCDISentencespic"+(count)+".png";
        Bitmap b=getBitmap(strs);

        image.setImageBitmap(b);
        tv.setText(arrayList.get(count-1).get("title"));
        b1.setText(arrayList.get(count-1).get("opt1name"));
        b2.setText(arrayList.get(count-1).get("opt2name"));
        //b3.setText(arrayList.get(count-1).get("opt3name"));
        //b4.setText(arrayList.get(count-1).get("opt4name"));
        number.setText(String.valueOf(count)+"/"+String.valueOf(arrayList.size()));
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        //b3.setOnClickListener(this);
        //b4.setOnClickListener(this);
        number.setOnClickListener(this);
    }

    //点击事件

    private void onClickSeldom(View view) {
        splitScore[count-1]= Integer.parseInt(arrayList.get(count-1).get("opt2score"));
        isAnswered[count-1]=true;

        b1.setBackgroundColor(Color.parseColor("#65B1BF"));
        b2.setBackgroundColor(Color.parseColor("#187485"));
        //b3.setBackgroundColor(Color.parseColor("#65B1BF"));
        //b4.setBackgroundColor(Color.parseColor("#65B1BF"));

        if(arrayList.get(count-1).get("sort")=="名词"){
            noun++;
        }
        else if(arrayList.get(count-1).get("sort")=="动词"){
            verb++;
        }
        else{
            other++;
        }

        chosenButton[count-1]= b2.getText().toString();
        System.out.println(chosenButton[count-1]+":"+b2.getText().toString());
        countTotal();
    }

    private void onClickNever(View view) {
        splitScore[count-1]= Integer.parseInt(arrayList.get(count-1).get("opt1score"));
        isAnswered[count-1]=true;

        b1.setBackgroundColor(Color.parseColor("#187485"));
        b2.setBackgroundColor(Color.parseColor("#65B1BF"));
        //b3.setBackgroundColor(Color.parseColor("#65B1BF"));
        //b4.setBackgroundColor(Color.parseColor("#65B1BF"));

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

            inputStream = getAssets().open("PCDISentencesRESULT.json");
            resultIS=getAssets().open("PCDISentencesEVA.json");

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
        BarChart bar;                                                   //*********此行代码需插入************
        List<BarEntry> list;                                                     //*********此行代码需插入************
        List<BarEntry>list2;                                                     //*********此行代码需插入************
        //*********下面代码均插入***************
        bar = (BarChart) findViewById(R.id.bar);
        list=new ArrayList<>();
        list2=new ArrayList<>();


        list.add(new BarEntry(1,48));                                  //词汇及手势表：不变    词汇及句子表：将60改为48
        list.add(new BarEntry(2,25));                                  //词汇及手势表：不变    词汇及句子表：将16改为25
        list.add(new BarEntry(3,40));                                  //词汇及手势表：不变    词汇及句子表：将30改为40


        list2.add(new BarEntry(1,noun));                                 //词汇及手势表/词汇及句子表：将15改为‘名词’类别的分数
        list2.add(new BarEntry(2,verb));                                  //词汇及手势表/词汇及句子表：将6改为‘动词’类别的分数
        list2.add(new BarEntry(3,other));                                 //词汇及手势表/词汇及句子表：将24改为‘其他类别’类别的分数

        BarDataSet barDataSet=new BarDataSet(list,"理想分数");
        barDataSet.setColor(Color.TRANSPARENT);
        BarDataSet barDataSet2=new BarDataSet(list2,"实际分数");
        barDataSet2.setColor(Color.parseColor("#65B1BF"));
        BarData barData=new BarData(barDataSet);

        barData.addDataSet(barDataSet2);

        bar.setData(barData);
        bar.getXAxis().setDrawGridLines(false);

        Legend legend=bar.getLegend();
        legend.setEnabled(false);

        barData.setBarWidth(0.4f);

        XAxis xAxis=bar.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if(v==1)
                    return "名词";
                if(v==2)
                    return "动词";
                if(v==3)
                    return "其他类别词";
                return "";
            }
        });


        barDataSet.setBarBorderColor(Color.GRAY);
        barDataSet.setBarBorderWidth(1);
        barDataSet.setHighlightEnabled(false);

        bar.setBackgroundColor(Color.parseColor("#fafafa"));
        bar.getAxisLeft().setAxisMaximum(65);
        bar.getAxisLeft().setAxisMinimum(0);

        bar.getAxisLeft().setLabelCount(10,false);
        bar.getXAxis().setAxisMaximum(4);
        bar.getXAxis().setAxisMinimum(0);

        bar.getXAxis().setLabelCount(3,false);
        bar.getDescription().setEnabled(false);
        bar.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        bar.getAxisRight().setEnabled(false);

        bar.animateY(3000);
        //************截止在这里****************
    }

    private int judgeRules(){
        //age按月计算
        int age=17;
        int sign=0;
        if(age==16&&totalScore<=27||age==17&&totalScore<=33||age==18&&totalScore<=44||age==19&&totalScore<=48||age==20&&totalScore<=61||age==21&&totalScore<=63||age==22&&totalScore<=77||age>=23&&age<=24&&totalScore<=78||age>=25&&age<=27&&totalScore<=95||age==28&&totalScore<=101){
            sign=2;
        }
        else if(age>28&&totalScore==113){
            sign=3;
        }
        else if(age>28&&totalScore<113){
            sign=4;
        }
        else {
            sign=1;
        }

        return sign;
    }

    private int advance(){
        int same=0;
        if(totalScore<=27.5)
            same=16;
        else if(totalScore<=33.7)
            same=17;
        else if(totalScore<=44)
            same=18;
        else if(totalScore<=48.9)
            same=19;
        else if(totalScore<=61.1)
            same=20;
        else if(totalScore<=63.1)
            same=21;
        else if(totalScore<=77.1)
            same=22;
        else if(totalScore<=78.4)
            same=23;
        else if(totalScore<=95.2)
            same=25;
        else
            same=28;
        return same;
    }

    private void judgeSituation() {

        int judge=judgeRules();
        if (judge==2){
            button10.setText("在测试的113个词汇当中，您的孩子已经掌握了"+noun+"个名词，"+verb+"个动词和"+other+"个其他类别的词语。\n\n"+str2+result[0]);
        }
        else if(judge==3){
            button10.setText("在测试的113个词汇当中，您的孩子已经掌握了"+noun+"个名词，"+verb+"个动词和"+other+"个其他类别的词语。\n\n"+str2+result[1]);
        }
        else if(judge==4){
            button10.setText("在测试的113个词汇当中，您的孩子已经掌握了"+noun+"个名词，"+verb+"个动词和"+other+"个其他类别的词语。\n\n"+str2+result[2]);
        }
        else{
            int re=advance();
            button10.setText("在测试的113个词汇当中，您的孩子已经掌握了"+noun+"个名词，"+verb+"个动词和"+other+"个其他类别的词语。\n\n"+str2+"的词汇表达发展水平相当于"+re+"月大的儿童的发展水平。\n"+result[3]);
        }
    }

    private void showResultList(View view){
        //arrBitmap.clear();
        setContentView(R.layout.pcdisentences_result_list);
        getResultList();
        prepareResultList();
    }

    private void onClickLeftbutton(View view){
        if(count>1){
            count--;
            String strs="PCDISentencespic"+(count)+".png";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            //b3.setText(arrayList.get(count-1).get("opt3name"));
            //b4.setText(arrayList.get(count-1).get("opt4name"));
            checkChosen(count);
        }
        else if(count==1){
            count=arrayList.size();
            String strs="PCDISentencespic"+(count)+".png";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            //b3.setText(arrayList.get(count-1).get("opt3name"));
            //b4.setText(arrayList.get(count-1).get("opt4name"));
            checkChosen(count);
        }
        number.setText(String.valueOf(count)+"/"+String.valueOf(arrayList.size()));
    }

    private void onClickRightbutton(View view){
        if(count<arrayList.size()){
            count++;
            String strs="PCDISentencespic"+(count)+".png";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            //b3.setText(arrayList.get(count-1).get("opt3name"));
            //b4.setText(arrayList.get(count-1).get("opt4name"));
            checkChosen(count);
        }
        else if (count==arrayList.size()){
            count=1;
            String strs="PCDISentencespic"+(count)+".png";
            Bitmap b=getBitmap(strs);

            image.setImageBitmap(b);
            tv.setText(arrayList.get(count-1).get("title"));
            b1.setText(arrayList.get(count-1).get("opt1name"));
            b2.setText(arrayList.get(count-1).get("opt2name"));
            //b3.setText(arrayList.get(count-1).get("opt3name"));
            //b4.setText(arrayList.get(count-1).get("opt4name"));
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
            //b3.setBackgroundColor(Color.parseColor("#65B1BF"));
            //b4.setBackgroundColor(Color.parseColor("#65B1BF"));
        }
        else if(b2.getText().toString()==chosenButton[count-1]){
            System.out.println(chosenButton[count-1]+":"+b2.getText().toString());
            b1.setBackgroundColor(Color.parseColor("#65B1BF"));
            b2.setBackgroundColor(Color.parseColor("#187485"));
            //b3.setBackgroundColor(Color.parseColor("#65B1BF"));
            //b4.setBackgroundColor(Color.parseColor("#65B1BF"));
        }

        else {
            b1.setBackgroundColor(Color.parseColor("#65B1BF"));
            b2.setBackgroundColor(Color.parseColor("#65B1BF"));
            //b3.setBackgroundColor(Color.parseColor("#65B1BF"));
            //b4.setBackgroundColor(Color.parseColor("#65B1BF"));
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
        //arrBitmap.clear();
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
            //设置侧边栏界面
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
            t.setTextSize(25);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int i=t.getId();
                            count=i+1;
                            String strs="PCDISentencespic"+(count)+".png";
                            Bitmap b=getBitmap(strs);

                            image.setImageBitmap(b);
                            tv.setText(arrayList.get(count-1).get("title"));
                            b1.setText(arrayList.get(count-1).get("opt1name"));
                            b2.setText(arrayList.get(count-1).get("opt2name"));
                            //b3.setText(arrayList.get(count-1).get("opt3name"));
                            //b4.setText(arrayList.get(count-1).get("opt3name"));
                            checkChosen(count);
                            number.setText(String.valueOf(count)+"/"+String.valueOf(arrayList.size()));
                        }
                    }
            );
            gridLayout.addView(t);
        }
    }
}
