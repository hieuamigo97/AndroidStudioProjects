package game.snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
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

    Snake(Context context, Point size){
        super(context);

        int blockSize = size.x / NUM_BLOCK_WIDE;

        mNumBlocksHigh = size.y/blockSize;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
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
        }
    }


