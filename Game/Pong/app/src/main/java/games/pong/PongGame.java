package games.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class PongGame extends SurfaceView implements Runnable {

    private final boolean DEBUGGING = true;

    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    private long mFPS;
    private final int MILLIS_IN_SECOND = 1000;

    private int mScreenX;
    private int mScreenY;

    private int mFontSize;
    private int mFontMargin;

    private Bat mBat;
    private Ball mBall;

    private int mScore;
    private int mLives;

    private Thread mGameThread = null;

    private volatile  boolean mPlaying;
    private boolean mPaused = true;


    public PongGame(Context context, int x, int y){

        super(context);

        mScreenX = x;
        mScreenY = y;

        mFontSize = mScreenX/20;
        mFontMargin = mScreenX/75;

        mOurHolder = getHolder();
        mPaint = new Paint();

        mBall = new Ball(mScreenX);
        mBat = new Bat(mScreenX,mScreenY);

        startNewGame();
    }

    private void startNewGame(){
        mBall.reset(mScreenX, mScreenY);

        mScore = 0;
        mLives = 3;
    }

    private void draw(){
        if(mOurHolder.getSurface().isValid()){
            mCanvas = mOurHolder.lockCanvas();

            mCanvas.drawColor(Color.argb(255,16,128,182));

            mPaint.setColor(Color.argb(255,255,255,255));

            mCanvas.drawRect(mBall.getRect(),mPaint);
            mCanvas.drawRect(mBall.getRect(),mPaint);

            mPaint.setTextSize(mFontSize);

            mCanvas.drawText("Score: " + mScore + "Lives"
                               + mLives , mFontMargin, mFontSize, mPaint);

            if(DEBUGGING){
                printDebuggingText();
            }
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    private void printDebuggingText(){
        int debugSize = mFontSize/2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS" + mFPS, 10,
                debugStart + debugSize, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mPaused = false;

                if(motionEvent.getX() > mScreenX/2){
                    mBat.setMovementState(mBat.RIGHT);
                }
                else{
                    mBat.setMovementState(mBat.LEFT);
                }

                break;

            case MotionEvent.ACTION_UP:

                mBat.setMovementState(mBat.STOPPED);
                break;
        }

        return true;
    }

    @Override
    public void run(){

        while (mPlaying){
            long frameStartTime = System.currentTimeMillis();

            if(!mPaused){
                update();

                detectCollision();
            }

            draw();

            long timeThisFrame =
                    System.currentTimeMillis() - frameStartTime;

            if(timeThisFrame > 0){
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }

        }




    }

    private void update(){
        mPaused = false;
        mBall.update(mFPS);
        mBat.update(mFPS);

    }

    private void detectCollision(){
        
    }



    public void pause(){
        mPlaying = false;
        try{
            mGameThread.join();

        }catch(InterruptedException e){
            Log.e("Error","joining thread");
        }
    }

    public void resume(){
        mPlaying = true;

        mGameThread = new Thread(this);

        mGameThread.start();
    }
}
