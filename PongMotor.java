package prueba.app.raulmartin.com.sprint2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;
import java.math.*;



@RequiresApi(api = Build.VERSION_CODES.O)
class PongMotor extends SurfaceView implements Runnable{

    // This is our thread
    private Thread gameThread = null;

    // This is new. We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    // We will see it in action in the draw method soon.
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // Game is paused at the start
    private boolean paused = true;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;

    // How wide and high is the screen?
    private int screenX;
    private int screenY;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The player's bat
    Bat bat;

    // A ball
    private Ball ball;



    //Clonar el bate
    ArrayList <Bat> batArray = new ArrayList<Bat>();

    //Timer para generar cada 10s una barra
    Timer timer;
    TimerTask timerTask;

    //Random


    // The constructor is called when the object is first created
    public PongMotor(Context context, int x, int y) {
        // This calls the default constructor to setup the rest of the object
        super(context);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Initialize screenX and screenY because x and y are local
        screenX = x;
        screenY = y;

        // Initialize the player's bat
        bat = new Bat(x,y);
        batArray.add(bat);



        // Create a ball
        ball = new Ball(screenX, screenY);



        System.out.print(System.currentTimeMillis());
        restart();
    }

    // Runs when the OS calls onPause on BreakoutActivity method
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    // Runs when the OS calls onResume on BreakoutActivity method
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Bat clone = new Bat(screenX,screenY);
                batArray.add(clone);
                paint.setColor(Color.argb(255,  255, 255, 255));

                int longitud = batArray.size() -1;
                canvas.drawRect(batArray.get(longitud).getRect(), paint);
            }
        };

        timer.schedule(timerTask,10000,10000);
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 3500 / timeThisFrame;
            }

        }
    }

    private void update(){


        for(int j =0;j< batArray.size();j++){
            batArray.get(j).update(fps);
        }

        // Update the ball
        ball.update(fps);



        for(int i=0;i<batArray.size();i++){
            if(RectF.intersects(ball.getRect(),batArray.get(i).getRect())) {
                batArray.get(i).setRandomXVelocity();
                batArray.get(i).reverseYVelocity();




                //paint.setColor(Color.argb((int)Math.random()*255,  (int)Math.random()*255, (int)Math.random()*255, (int)Math.random()*255));
                paint.setColor(Color.argb(0,  0, 0, 0));
                canvas.drawRect(ball.getRect(), paint);

            }
        }


    }

    void restart(){
        // Put the ball back to the start
        bat.reset(screenX/2, screenY/2);


    }

    private void draw(){
        // Make sure our drawing surface is valid or game will crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255,  26, 128, 182));

            // Draw everything to the screen

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));



            // Draw the ball
            canvas.drawRect(ball.getRect(), paint);

            Iterator <Bat>  it = batArray.iterator();

            for(int i=0; i<batArray.size();i++){
                canvas.drawRect(batArray.get(i).getRect(),paint);
            }
            // Change the brush color for drawing
            paint.setColor(Color.argb(255,  249, 129, 0));


            // Show everything we have drawn
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // Our code here
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if(motionEvent.getX() > screenX / 2){
                    ball.setMovementState(ball.RIGHT);
                }
                else{
                    ball.setMovementState(ball.LEFT);
                }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                ball.setMovementState(ball.STOPPED);
                break;
        }

        return true;
    }




}
