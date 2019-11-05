package game.snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable {

    private Thread mThread = null;

    private long mNextFrameTime;

    private volatile  boolean mPlaying = false;
    private volatile  boolean mPause = true;

    private SoundPool mSP;
    private int mEat_ID = -1;
    private  int mCrashID = -1;

    private final int NUM_BLOCK_WIDE = 40;
    private int mNumBlocksHigh;

    private int mScore;

    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    private Snake mSnake;
    private Apple mApple;

    SnakeGame(Context context, Point size) {
        super(context);

        int blockSize = size.x / NUM_BLOCK_WIDE;

        mNumBlocksHigh = size.y / blockSize;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes
                                    .CONTENT_TYPE_SONIFICATION)
                            .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
// Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);
            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);
        } catch (IOException e) {
// Error
        }

        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        mApple = new Apple(context,
                new Point(NUM_BLOCK_WIDE,
                mNumBlocksHigh),blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCK_WIDE,
                        mNumBlocksHigh),
                blockSize);

    }
    public void newGame(){

        mSnake.reset(NUM_BLOCK_WIDE, mNumBlocksHigh);
        mApple.spawn();
        mScore = 0;
        mNextFrameTime = System.currentTimeMillis();
    }

    @Override
    public void run(){
        while(mPlaying){
            if(!mPause){
                if(updateRequired()){
                    update();
                }
            }
            draw();
        }
    }

    public Boolean updateRequired(){
        final long TARGET_FPS = 10;
        final long MILLIS_PER_SECOND = 1000;

        if(mNextFrameTime <= System.currentTimeMillis()){
            mNextFrameTime = System.currentTimeMillis() +
                     MILLIS_PER_SECOND/TARGET_FPS;

            return  true;
        }

        return false;
    }

    public void update(){
        mSnake.move();
        if(mSnake.checkDinner(mApple.getLocation())){
            mApple.spawn();

            mScore = mScore + 1;

            mSP.play(mEat_ID,1,1,0,0,1);
        }

        if(mSnake.detectDeath()){
            mSP.play(mCrashID,1,1,0,0,1);

            mPause = true;
        }
    }

    public void draw(){

        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
// Fill the screen with a color
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));
// Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);
// Draw the score
            mCanvas.drawText("" + mScore, 20, 120, mPaint);

            mApple.draw(mCanvas,mPaint);
            mSnake.draw(mCanvas,mPaint);

            if(mPause){
// Set the size and color of mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(250);
// Draw the message
// We will give this an international upgrade soon
                mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                if(mPause){
                    mPause = false;
                    newGame();

                    return  true;
                }
                mSnake.switchHeading(motionEvent);
                break;

            default :
                break;
        }

        return true;
    }

    public void pause(){
        mPlaying = false;
        try{
            mThread.join();
        }catch(InterruptedException e){
            //
        }
    }

    public void resume(){
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }


    }



