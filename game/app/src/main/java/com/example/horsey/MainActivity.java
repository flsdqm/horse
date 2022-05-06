package com.example.horsey;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private ImageView back,home,dad,mom,left,right;
    private View decorView;
    private TextView thegameName,number,textLeft,textRight,chapter,level,textLeftTop,textRightTop,score;
    private String gameName,jsonName,needToChoose;
    //所使用的判断题目
    //private String shouldChoose="choose_banana";
    private SoundPool s;

    //随机化结果
    private Verify toVerify=new Verify();

    private int picNum,count,scoreNum=0;
    //存储所有题目对应的图片
    private ArrayList<Bitmap> arrBitmap=new ArrayList<>();
    //存储每一个题目、选项及分数
    private ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
    private HashMap<String,Integer> soundID=new HashMap<String,Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count=1;
        decorView=getWindow().getDecorView();
        UIShrink();
        chronometer=findViewById(R.id.time);
        back=findViewById(R.id.back);
        home=findViewById(R.id.home);
        thegameName=findViewById(R.id.gameName);
        //文本
        number=findViewById(R.id.number);
        textLeft=findViewById(R.id.textLeft);
        textRight=findViewById(R.id.textRight);
        //主界面图片按钮
        dad=findViewById(R.id.dad);
        mom=findViewById(R.id.mom);
        left=findViewById(R.id.left);
        right=findViewById(R.id.right);
        chapter=findViewById(R.id.chapter);
        level=findViewById(R.id.level);
        textLeftTop=findViewById(R.id.textLeftTop);
        textRightTop=findViewById(R.id.textRightTop);
        score=findViewById(R.id.score);
        score.setText(scoreNum+"分");
        //soundID.clear();
        //加载前一窗口的信息
        takeOut();
        verify();

        getOptions();
        initSoundPool();

        allTransactions();
        change();


    }

    private void verify(){
        //此处等待随机化
        //String problem=shouldChoose;
        //toVerify=new Verify(shouldChoose);
        toVerify.setProblem(needToChoose);
        System.out.println("响应："+toVerify.getProblem());
    }

    private void takeOut(){
        Intent intent=getIntent();
        GameType gameType=(GameType) intent.getSerializableExtra("gameType");
        gameName=gameType.getGameName();
        jsonName=gameType.getChooseOne();
        needToChoose=gameType.getNeedToChoose();
        //shouldChoose=gameType.getShouldChoose();
    }

    private void UIShrink(){
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void allTransactions(){
        thegameName.setText(gameName);
        putPictures();
        chronometer.start();
        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                }
        );
        left.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(count>1){
                            count--;
                            change();
                        }
                        else if(count==1){
                            count=picNum;
                            change();
                        }
                    }
                }
        );
        right.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(count<picNum){
                            count++;
                            change();
                        }
                        else if(count==picNum){
                            count=1;
                            change();
                        }
                    }
                }
        );

        dad.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(soundID.get(arrayList.get(count*2-2).get("voice"))!=null)
                            s.play(soundID.get(arrayList.get(count*2-2).get("voice")),1,1,1,0,1);
                        System.out.println(arrayList.get(count*2-2).get("voice")+"1");
                        //s.release();
                        getVerification(arrayList.get(count*2-2).get("picture"));
                    }
                }
        );

        if(mom.isEnabled()==true){
            mom.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(soundID.get(arrayList.get(count*2-1).get("voice"))!=null)
                                s.play(soundID.get(arrayList.get(count*2-1).get("voice")),1,1,1,0,1);
                            //s.release();
                            System.out.println(arrayList.get(count*2-1).get("voice")+"2");
                            getVerification(arrayList.get(count*2-1).get("picture"));
                        }
                    }
            );
        }
        else{
            mom.setOnClickListener(null);
        }
    }

    private void getVerification(String chinese) {
        String getProblem="choose_"+toVerify.getProblem();
        if(chinese.equals(getProblem)){
            System.out.println(chinese);
            System.out.println(getProblem);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s.play(soundID.get("yes"),1,1,1,0,1);
            //s.release();
            scoreNum+=2;
            score.setText(scoreNum+"分");
        }
        else
        {
            System.out.println(chinese);
            System.out.println(getProblem);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s.play(soundID.get("no"),1,1,1,0,1);
            //s.release();
            scoreNum--;
            score.setText(scoreNum+"分");
        }

    }

    private void initSoundPool(){

        SoundPool.Builder builder=new SoundPool.Builder();
        //音频最大流数为2
        builder.setMaxStreams(2);
        s=builder.build();

        //音频注册
        soundID.put("click_clock",s.load(this,R.raw.click_clock,1));
        soundID.put("click_banana",s.load(this,R.raw.click_banana,1));
        soundID.put("click_swim",s.load(this,R.raw.click_swim,1));
        soundID.put("click_bluebook",s.load(this,R.raw.click_bluebook,1));
        soundID.put("banana",s.load(this,R.raw.banana,1));
        soundID.put("bread",s.load(this,R.raw.bread,1));
        soundID.put("grapes",s.load(this,R.raw.grapes,1));
        soundID.put("milk",s.load(this,R.raw.milk,1));
        soundID.put("watermelon",s.load(this,R.raw.watermelon,1));
        soundID.put("no",s.load(this,R.raw.no,1));
        soundID.put("yes",s.load(this,R.raw.yes,1));

        //String problem_Eng="banana";

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String str="click_"+toVerify.getProblem();
        System.out.println(str);
        s.play(soundID.get(str),1,1,1,0,1);
        //s.release();
    }

    private void change() {
        number.setText(count+" / "+picNum);
        //一次展示两个图片
        if(arrayList.size()%2!=0&&count==picNum){
            dad.setImageBitmap(arrBitmap.get(count*2-2));
            textLeftTop.setText(arrayList.get(count*2-2).get("pinyin"));
            textLeft.setText(arrayList.get(count*2-2).get("chinese"));
            mom.setVisibility(View.INVISIBLE);
            mom.setEnabled(false);
            textRight.setVisibility(View.INVISIBLE);
            textRightTop.setVisibility(View.INVISIBLE);
            level.setText("当前等级："+arrayList.get(count*2-2).get("level"));
            chapter.setText("当前关卡："+arrayList.get(count*2-2).get("chapter"));
        }
        else{
            mom.setVisibility(View.VISIBLE);
            mom.setEnabled(true);
            textRight.setVisibility(View.VISIBLE);
            textRightTop.setVisibility(View.VISIBLE);
            dad.setImageBitmap(arrBitmap.get(count*2-2));
            mom.setImageBitmap(arrBitmap.get(count*2-1));
            textLeftTop.setText(arrayList.get(count*2-2).get("pinyin"));
            textLeft.setText(arrayList.get(count*2-2).get("chinese"));
            textRightTop.setText(arrayList.get(count*2-1).get("pinyin"));
            textRight.setText(arrayList.get(count*2-1).get("chinese"));
            level.setText("当前等级："+arrayList.get(count*2-2).get("level"));
            chapter.setText("当前关卡："+arrayList.get(count*2-2).get("chapter"));
        }
    }

    private void putPictures() {
        if(arrayList.size()%2!=0)
            //奇数
            picNum=arrayList.size()/2+1;
        else
            //偶数
            picNum=arrayList.size()/2;
    }

    private void getOptions(){

        try {
            StringBuffer buffer = new StringBuffer();
            InputStream inputStream = getAssets().open(jsonName+".json");
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
                    //if(jsonArray.getJSONObject(i).getString("sort")==jsonName) {
                        map.put("chapter", jsonArray.getJSONObject(i).getString("chapter"));
                        map.put("chinese", jsonArray.getJSONObject(i).getString("chinese"));
                        map.put("pinyin", jsonArray.getJSONObject(i).getString("pinyin"));
                        map.put("level", jsonArray.getJSONObject(i).getString("level"));
                        map.put("picture", jsonArray.getJSONObject(i).getString("picture"));
                        map.put("voice", jsonArray.getJSONObject(i).getString("voice"));
                        arrayList.add(map);
                   // }
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    String name1 = arrayList.get(i).get("picture") + ".png";
                    BufferedInputStream bufferedInputStream1 = new BufferedInputStream(getAssets().open(name1));
                    Bitmap bm1 = BitmapFactory.decodeStream(bufferedInputStream1);
                    arrBitmap.add(bm1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}