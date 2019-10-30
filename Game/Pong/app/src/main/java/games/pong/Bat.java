package games.pong;

import android.graphics.RectF;

class Bat {

    private RectF mRect;
    private float mLength;
    private float mXCoord;
    private float mBatSpeed;
    private int mScreenX;

    final int STOPPED =0;
    final int LEFT = 1;
    final int RIGHT = 2;

    private int mBatmoving = STOPPED;

    Bat(int sx, int sy){
        mScreenX = sx;

        mLength = mScreenX/8;

        float height = sy/40;

        mXCoord = mScreenX/2;

        float mYCoord = sy - height;

        mRect = new RectF(mXCoord, mYCoord,
                mXCoord + mLength,
                mYCoord+ height);

        mBatSpeed = mScreenX;
    }

    RectF getRect(){
        return mRect;
    }

    void setMovementState(int state){
        mBatmoving = state;
    }

    void update (long fps){

        if(mBatmoving == LEFT){
            mXCoord = mXCoord - mBatSpeed/fps;
        }

        if(mBatmoving == RIGHT){
            mXCoord = mXCoord + mBatSpeed / fps;
        }

        if(mXCoord < 0){
            mXCoord = 0;
        }
        else if(mXCoord + mLength > mScreenX){
            mXCoord = mScreenX - mLength;
        }
// Update mRect based on the results from
// the previous code in update
        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }
    }


