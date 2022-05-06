package com.example.horsey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by 86132 on 2022/4/26.
 */

@SuppressLint("AppCompatCustomView")
public class MyImageButton extends ImageButton {
    public String text=null;
    public float textX,textY;
    public int color;
    public float textsize=0f;
    public MyImageButton(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }
    public void setText(String text)
    {
        this.text=text;
    }
    public void setColor(int color)
    {
        this.color=color;
    }
    public  void setTextsize(float textsize)
    {
        this.textsize=textsize;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(color);
        paint.setTextSize(textsize);
        canvas.drawText(text,textX,textY,paint);
    }

}
