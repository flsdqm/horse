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
import android.graphics.drawable.BitmapDrawable;
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


import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

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

public class ABCMainActivity2 extends AppCompatActivity implements View.OnClickListener {

    public static final String KEYWORD="com.example.evasch";
    //存储所有题目对应的图片
    //private ArrayList<Bitmap> arrBitmap=new ArrayList<>();
    //private ArrayList<String> arrBitmapStr=new ArrayList<>();
    //存储每一个题目、选项及分数
    private ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
    //存储选中的选项
    private String[] chosenButton;
    private int[] splitScore;
    private boolean[] isAnswered;
    private String[] result=new String[3];

    private int count=1;
    private int totalScore=0;
    private ImageView image;
    private TextView tv;
    private Button b1;
    private Button b2;
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
        setContentView(R.layout.abc_activity_main2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
            InputStream inputStream =getAssets().open("ABC.json");
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
                    //JSONObject j3=new JSONObject(URLDecoder.decode(jsonArray.getJSONObject(i).getString("option3"),"GB2312"));
                    //JSONObject j4=new JSONObject(URLDecoder.decode(jsonArray.getJSONObject(i).getString("option4"),"GB2312"));

                    map.put("opt1name",j1.getString("name").toString());
                    map.put("opt1score",j1.getString("score").toString());
                    map.put("opt2name",j2.getString("name").toString());
                    map.put("opt2score",j2.getString("score").toString());
                    arrayList.add(map);
                }
                splitScore=new int[arrayList.size()];
                isAnswered=new boolean[arrayList.size()];
                chosenButton=new String[arrayList.size()];
                autoWrap();

                //for(int i=0;i<jsonArray.length();i++){
                    //改为按文件名存取
                    //String name="ABC"+(i+1)+".png";
                    //arrBitmapStr.add(name);
                    //改为按文件名存取

                    //arrBitmap.add(bm);
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
        String strs="ABC"+(count)+".png";
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

            inputStream = getAssets().open("ABCRESULT.json");
            resultIS=getAssets().open("ABCEVA.json");

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
                result=new String[3];
                result[0]=jsonArray1.getJSONObject(0).getString("testResult1");
                result[1]=jsonArray1.getJSONObject(0).getString("testResult2");
                result[2]=jsonArray1.getJSONObject(0).getString("testResult3");
                //result[3]=jsonArray1.getJSONObject(0).getString("testResult4");

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
        RadarChart radar;                                    //******需要插入*******
        List<RadarEntry> list;                                        //******需要插入*******

        radar=(RadarChart)findViewById(R.id.radar);
        list=new ArrayList<>();

        //分类统计分数！！
        list.add(new RadarEntry(50f));                          //***************将50替换为（‘感觉’类所得分数➗0.3）
        list.add(new RadarEntry(86f));                          //***************将86替换为（‘交往’类所得分数➗0.44）
        list.add(new RadarEntry(29f));                          //***************将29替换为（‘躯体运动’类所得分数➗0.28）
        list.add(new RadarEntry(58f));                          //***************将58替换为（‘语言’类所得分数➗0.31）
        list.add(new RadarEntry(73f));                          //***************将73替换为（‘生活自理’类所得分数➗0.25）

        RadarDataSet radarDataSet=new RadarDataSet(list,"孩子得分情况");
        radarDataSet.setColor(Color.parseColor("#65B1BF"));
        RadarData radarData=new RadarData(radarDataSet);
        radar.setData(radarData);

        radarDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return v+"%";
            }
        });

        radar.getYAxis().setAxisMinimum(0);


        radar.setWebColor(Color.GRAY);
        radar.setWebColorInner(Color.GRAY);


        radar.setBackgroundColor(Color.parseColor("#fafafa"));


        radarDataSet.setFillColor(Color.parseColor("#65B1BF"));
        radarDataSet.setDrawFilled(true);

        radarDataSet.setLineWidth(3);


        XAxis xAxis=radar.getXAxis();

        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if(v==0)
                {
                    return "感觉（S）";
                }
                if(v==1)
                {
                    return "交往（R）";
                }
                if(v==2)
                {
                    return "躯体运动（B）";
                }
                if(v==3)
                {
                    return "语言（L）";
                }
                if(v==4)
                {
                    return "生活自理（S）";
                }
                return "";
            }
        });

        radarDataSet.setDrawValues(false);
        radarDataSet.setValueTextSize(10);
        radarDataSet.setValueTextColor(Color.BLACK);


        YAxis yAxis=radar.getYAxis();
        yAxis.setValueFormatter(new PercentFormatter());

        yAxis.setDrawLabels(true);
        yAxis.setTextColor(Color.BLACK);

        yAxis.setAxisMaximum(80);


        yAxis.setLabelCount(4,false);

        radar.getDescription().setEnabled(false);

        Legend legend=radar.getLegend();
        legend.setEnabled(false);


        radarDataSet.setHighlightLineWidth(1f);
        radarDataSet.setDrawHighlightCircleEnabled(true);
        radarDataSet.setHighlightCircleFillColor(Color.parseColor("#65B1BF"));
        radarDataSet.setHighlightCircleInnerRadius(1f);
        radarDataSet.setHighlightCircleOuterRadius(6f);
        radarDataSet.setHighlightCircleStrokeColor(Color.parseColor("#65B1BF"));
        radarDataSet.setHighlightCircleStrokeAlpha(102);
        radarDataSet.setHighlightCircleStrokeWidth(6f);
        radarDataSet.setDrawHorizontalHighlightIndicator(false);
        radarDataSet.setDrawVerticalHighlightIndicator(false);

        //*****************截至到这里**************************

    }

    private void judgeSituation() {
        if (totalScore<=31){
            button10.setText("本次量表得分为："+totalScore+"分\n\n"+result[0]);
        }
        else if(totalScore<=53){
            button10.setText("本次量表得分为："+totalScore+"分\n\n"+result[1]);
        }
        else {
            button10.setText("本次量表得分为："+totalScore+"分\n\n"+result[2]);
        }
    }

    private void showResultList(View view){
        //arrBitmap.clear();
        setContentView(R.layout.abc_result_list);
        getResultList();
        prepareResultList();
    }

    private void onClickLeftbutton(View view){
        if(count>1){
            count--;
            String strs="ABC"+(count)+".png";
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
            String strs="ABC"+(count)+".png";
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
            String strs="ABC"+(count)+".png";
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
            String strs="ABC"+(count)+".png";
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
                            String strs="ABC"+(count)+".png";
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
