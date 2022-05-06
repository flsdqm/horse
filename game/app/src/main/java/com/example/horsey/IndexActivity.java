package com.example.horsey;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IndexActivity extends AppCompatActivity {

    private int i=1;    //记录声音是否打开
    private ImageButton tablebutton,gamebutton,voicebutton;
    private View decorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_index);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        decorView=getWindow().getDecorView();
        UIShrink();
        tablebutton=findViewById(R.id.TableButton);
        gamebutton=findViewById(R.id.GameButton);
        voicebutton=findViewById(R.id.MainVoiceButton);
        setDecoration(voicebutton);
        isPlayingBgm(i);
    }

    private void UIShrink(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void setDecoration(ImageButton voicebutton) {
        tablebutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Intent intent=new Intent(IndexActivity.this,TableChooseActivity.class);
                        startActivity(intent);
                    }
                });

        gamebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                Intent intent=new Intent(IndexActivity.this,TableMeasure.class);
                startActivity(intent);
            }
        });

        voicebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                //i++;
                if(i==1)
                {
                    i=0;
                    voicebutton.setBackgroundResource(R.drawable.close_main_voice);
                    isPlayingBgm(i);
                    Toast toast=Toast.makeText(getApplicationContext(),"背景声音关闭了哦！\n若想听到声音，可以再点击一下哦！",Toast.LENGTH_SHORT);
                    toast.show();
                }
                //**************关闭背景音乐************************
                else
                {
                    i=1;
                    voicebutton.setBackgroundResource(R.drawable.open_main_voice);
                    isPlayingBgm(i);
                    Toast toast=Toast.makeText(getApplicationContext(),"背景声音打开了哦！\n若不想听到声音，可以再点击一下哦！",Toast.LENGTH_SHORT);
                    toast.show();
                }
                //**************打开背景音乐************************
            }
        });

    }

    private void isPlayingBgm(int i) {
        Intent intent=new Intent(this,BgmService.class);
        intent.putExtra("signal",i);
        startService(intent);
    }
}
