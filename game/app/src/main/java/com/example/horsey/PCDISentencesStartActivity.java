package com.example.horsey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;

public class PCDISentencesStartActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private String intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcdisentences_activity_start);

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
        textView=findViewById(R.id.textView);
        button=findViewById(R.id.getstarted);
        getIntro();
        loadTransactions();
    }

    public void loadTransactions() {
        textView.setText(intro);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PCDISentencesStartActivity.this,PCDISentencesMainActivity2.class);
                startActivity(intent);
            }
        });
    }

    public void getIntro() {
        try {
            StringBuffer buffer = new StringBuffer();
            InputStream inputStream = getAssets().open("PCDISentencesINTRO.json");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] b = new byte[1024];

            while (bufferedInputStream.read(b) != -1) {
                String sta = new String(b);
                buffer.append(sta);
            }
            String problems=buffer.toString();
            try {
                JSONArray jsonArray=new JSONArray(URLDecoder.decode(problems,"GB2312"));
                System.out.println(jsonArray.toString());
                intro=jsonArray.getJSONObject(0).getString("intro");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

}