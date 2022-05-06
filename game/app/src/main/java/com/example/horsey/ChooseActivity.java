package com.example.horsey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class ChooseActivity extends AppCompatActivity {

    //存储所有题目对应的图片
    private ArrayList<Bitmap> arrBitmap=new ArrayList<>();
    //存储每一个题目、选项及分数
    private ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
    //呈现主界面的序号
    private int count=1;
    //判断
    private String s="choose_food";

    private String needToChoose="banana";
    private TextView level,name1,name2,name3,name4;
    private ImageButton b1,b2,b3,b4,left,right,back,enter;
    View decorView;
    private String option="";
    private String json="";
    WhichtoChoose whichtoChoose=new WhichtoChoose();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_voice);
        decorView=getWindow().getDecorView();
        UIShrink();
        //索引控件
        level=findViewById(R.id.level);
        name1=findViewById(R.id.name1);
        name2=findViewById(R.id.name2);
        name3=findViewById(R.id.name3);
        name4=findViewById(R.id.name4);
        b1=findViewById(R.id.cv_ib1);
        b2=findViewById(R.id.cv_ib2);
        b3=findViewById(R.id.cv_ib3);
        b4=findViewById(R.id.cv_ib4);
        left=findViewById(R.id.left);
        right=findViewById(R.id.right);
        back=findViewById(R.id.back);
        enter=findViewById(R.id.enter);
        init();
    }

    private void init(){
        getOptions();
        change();
        transactions();
    }

    private void UIShrink(){
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void transactions() {
        chooseAndEnter();
        fourButtons();
    }


    private void chooseAndEnter(){
        left.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(count>1){
                            count--;
                            change();
                        }
                        else if(count==1){
                            count=arrayList.size();
                            change();
                        }
                    }
                }
        );
        right.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(count<arrayList.size()){
                            count++;
                            change();
                        }
                        else if(count==arrayList.size()){
                            count=1;
                            change();
                        }
                    }
                }
        );
        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );

            enter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(option==""){
                            Toast toast = Toast.makeText(getApplicationContext(), "选择一个游戏吧！", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else{
                            if (s.equals(json)){
                                Toast toast = Toast.makeText(getApplicationContext(), "进入游戏！", Toast.LENGTH_SHORT);
                                toast.show();
                                Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                                Bundle bundle = new Bundle();
                                //"pic" 可以作为json文件名称
                                GameType gameType = new GameType("语音游戏", option,json,s,needToChoose);
                                bundle.putSerializable("gameType", gameType);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }
                            else{
                                Toast toast=Toast.makeText(getApplicationContext(),"当前的等级还不能做这一部分哦！",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }
            );

    }

    private void fourButtons(){
        b1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        option=getOptionName("opt1");
                        json=getJSONName("pic1");
                        whichtoChoose.chooseIt(b1,b2,b3,b4);
                    }
                }
        );
        b2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        option=getOptionName("opt2");
                        json=getJSONName("pic2");
                        whichtoChoose.chooseIt(b2,b1,b3,b4);
                    }
                }
        );
        b3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        option=getOptionName("opt3");
                        json=getJSONName("pic3");
                        whichtoChoose.chooseIt(b3,b1,b2,b4);
                    }
                }
        );
        b4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        option=getOptionName("opt4");
                        json=getJSONName("pic4");
                        whichtoChoose.chooseIt(b4,b1,b2,b3);
                    }
                }
        );
    }

    private String getOptionName(String str) {
        String optionName=arrayList.get(count-1).get(str);
        return optionName;
    }

    private String getJSONName(String str){
        String JSONName=arrayList.get(count-1).get(str);
        return JSONName;
    }

    private void getOptions() {
        try {
            StringBuffer buffer = new StringBuffer();
            InputStream inputStream = getAssets().open("yuyin.json");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] b = new byte[1024];

            while (bufferedInputStream.read(b) != -1) {
                String sta = new String(b);
                buffer.append(sta);
            }

            String problems = buffer.toString();
            try {
                JSONArray jsonArray = new JSONArray(URLDecoder.decode(problems, "GB2312"));
                System.out.println(jsonArray.toString());
                //生成一个问题
                for (int i = 0; i < jsonArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("level", jsonArray.getJSONObject(i).getString("level"));
                    JSONObject j1 = new JSONObject(URLDecoder.decode(jsonArray.getJSONObject(i).getString("options"), "GB2312"));
                    //选项名称存储
                    map.put("opt1", j1.getString("opt1").toString());
                    map.put("opt2", j1.getString("opt2").toString());
                    map.put("opt3", j1.getString("opt3").toString());
                    map.put("opt4", j1.getString("opt4").toString());
                    //图片名称存储
                    map.put("pic1", j1.getString("pic1").toString());
                    map.put("pic2", j1.getString("pic2").toString());
                    map.put("pic3", j1.getString("pic3").toString());
                    map.put("pic4", j1.getString("pic4").toString());
                    arrayList.add(map);
                }
                for(int i=0;i<jsonArray.length();i++){
                    String str1=arrayList.get(i).get("pic1");
                    String name1=str1+".png";
                    BufferedInputStream bufferedInputStream1=new BufferedInputStream(getAssets().open(name1));
                    String str2=arrayList.get(i).get("pic2");
                    String name2=str2+".png";
                    BufferedInputStream bufferedInputStream2=new BufferedInputStream(getAssets().open(name2));
                    String str3=arrayList.get(i).get("pic3");
                    String name3=str3+".png";
                    BufferedInputStream bufferedInputStream3=new BufferedInputStream(getAssets().open(name3));
                    String str4=arrayList.get(i).get("pic4");
                    String name4=str4+".png";
                    BufferedInputStream bufferedInputStream4=new BufferedInputStream(getAssets().open(name4));
                    Bitmap bm1= BitmapFactory.decodeStream(bufferedInputStream1);
                    Bitmap bm2= BitmapFactory.decodeStream(bufferedInputStream2);
                    Bitmap bm3= BitmapFactory.decodeStream(bufferedInputStream3);
                    Bitmap bm4= BitmapFactory.decodeStream(bufferedInputStream4);
                    arrBitmap.add(bm1);
                    arrBitmap.add(bm2);
                    arrBitmap.add(bm3);
                    arrBitmap.add(bm4);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void change(){
        whichtoChoose.clear(b1,b2,b3,b4);
        option="";
        level.setText(arrayList.get(count-1).get("level"));
        name1.setText(arrayList.get(count-1).get("opt1"));
        name2.setText(arrayList.get(count-1).get("opt2"));
        name3.setText(arrayList.get(count-1).get("opt3"));
        name4.setText(arrayList.get(count-1).get("opt4"));
        b1.setImageBitmap(arrBitmap.get(count*4-4));
        b2.setImageBitmap(arrBitmap.get(count*4-3));
        b3.setImageBitmap(arrBitmap.get(count*4-2));
        b4.setImageBitmap(arrBitmap.get(count*4-1));
    }

}