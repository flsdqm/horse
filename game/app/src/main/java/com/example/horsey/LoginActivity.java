package com.example.horsey;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button signupButton;
    private Button loginButton;
    private View decorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signupButton=findViewById(R.id.signupButton);
        loginButton=findViewById(R.id.loginButton);
        decorView=getWindow().getDecorView();
        UIShrink();
        init();
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


    private void init(){
        signupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toRegister(view);
                    }
                }
        );

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toLogin();
                    }
                }
        );
    }

    public void toLogin(){
        Intent intent = new Intent(this,IndexActivity.class);
        startActivity(intent);
        finish();
    }

    public void toRegister(View view)
    {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }

}