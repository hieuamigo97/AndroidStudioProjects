package games.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

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

    private SoundPool mSP;
    private int mBeepID = -1;
    private int mBoopID = -1;
    private int mBopID = -1;
    private int mMissID = -1;



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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder().
                            setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).
                            build();

            mSP = new SoundPool.Builder().setMaxStreams(5).
                    setAudioAttributes(audioAttributes).build();
        }else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC,0);

        }

        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            mBeepID = mSP.load(descriptor, 0);
            descriptor = assetManager.openFd("boop.ogg");
            mBoopID = mSP.load(descriptor, 0);
            descriptor = assetManager.openFd("bop.ogg");
            mBopID = mSP.load(descriptor, 0);
            descriptor = assetManager.openFd("miss.ogg");
            mMissID = mSP.load(descriptor, 0);
        }catch(IOException e){
            Log.d("error", "failed to load sound files");}

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

    private void detectCollision() {
        if (RectF.intersects(mBat.getRect(), mBall.getRect())) {
            mBall.batBounce(mBat.getRect());
            mBall.increaseVelocity();
            mScore++;
            mSP.play(mBeepID, 1, 1, 0, 0, 1);

        }

        if (mBall.getRect().bottom > mScreenY) {
            mBall.reverseYVelocity();

            mLives--;
            mSP.play(mMissID, 1, 1, 0, 0, 1);

            if (mLives == 0) {
                mPaused = true;
                startNewGame();
            }


        }
        if (mBall.getRect().top < 0) {
            mBall.reverseYVelocity();
            mSP.play(mBoopID, 1, 1, 0, 0, 1);
        }
// Left
        if (mBall.getRect().left < 0) {
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);

        }

        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);
        }
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
