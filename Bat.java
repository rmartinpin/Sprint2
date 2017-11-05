package prueba.app.raulmartin.com.sprint2;

import android.graphics.RectF;
import android.content.Context;
import android.view.SurfaceView;


import java.util.Random;

public class Bat  implements Cloneable{

    private RectF rect;
    private float xVelocity;
    private float yVelocity;
    private float batWidth = 250;
    private float batHeight = 10;

    private float x,y;

    Bat(float X, float Y){

        xVelocity = 600;
        yVelocity = 1000;

        rect = new RectF(375 , 30 , 375+batWidth, 30+batHeight);

        x=X;
        y=Y;
    }

    RectF getRect(){
        return rect;
    }

    void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + batWidth;
        rect.bottom = rect.top - batHeight;

        // Bounce the ball back when it hits the bottom of screen
        // And deduct a life
        if(this.getRect().bottom > y - 50){
            this.reverseYVelocity();
        }

        // Bounce the ball back when it hits the top of screen
        if(this.getRect().top < 30) {
            this.reverseYVelocity();
        }

        // If the bat hits left wall bounce
        if(this.getRect().left < 10){
            this.reverseXVelocity();
        }

        // If the ball hits right wall bounce
        if(this.getRect().right > x - 10){
            this.reverseXVelocity();
        }
    }

    void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }


    void reset(int x, int y){
        rect.left = x / 2 + 125;
        rect.top =50;
        rect.right = x/2 +125 +batWidth;
        rect.bottom = 50 + batHeight;
    }


}

