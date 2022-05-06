package com.example.horsey;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

public class WhichtoChoose {
    public void chooseIt(ImageButton ib1,ImageButton ib2,ImageButton ib3,ImageButton ib4){
        ib1.setBackgroundColor(Color.parseColor("#b1e4f0"));
        ib2.setBackgroundColor(0);
        ib3.setBackgroundColor(0);
        ib4.setBackgroundColor(0);
    }
    public void chooseIt(ImageButton ib1,ImageButton ib2,ImageButton ib3){
        ib1.setBackgroundColor(Color.parseColor("#b1e4f0"));
        ib2.setBackgroundColor(0);
        ib3.setBackgroundColor(0);
    }
    public void clear(ImageButton ib1,ImageButton ib2,ImageButton ib3,ImageButton ib4){
        ib1.setBackgroundColor(0);
        ib2.setBackgroundColor(0);
        ib3.setBackgroundColor(0);
        ib4.setBackgroundColor(0);
    }
    public void clear(ImageButton ib1,ImageButton ib2,ImageButton ib3){
        ib1.setBackgroundColor(0);
        ib2.setBackgroundColor(0);
        ib3.setBackgroundColor(0);
    }
}
