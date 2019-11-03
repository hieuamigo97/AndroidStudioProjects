package game.bullethell;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

class Bob {

    RectF mRect;
    float mBobHeight;
    float mBobWidth;
    boolean mTeleporting = false;

    Bitmap mBitmap;

    public Bob (Context context, float screenX, float screenY){
        mBobHeight = screenY/10;
        mBobWidth = mBobHeight/ 2;

        mRect = new RectF(screenX/2, screenY/2,
                (screenX/2)+ mBobWidth,
                (screenY/2) + mBobHeight);

        mBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.bob);

    }

    boolean teleport(float newX, float newY){

        boolean success = false;

        if(!mTeleporting){
            mRect.left = newX - mBobWidth/2;
            mRect.top = newY - mBobHeight / 2;
            mRect.bottom = mRect.top + mBobHeight;
            mRect.right = mRect.left + mBobWidth;

            mTeleporting = true;

            success = true;


        }

        return  success;
    }

    void setTelePortAvailable(){
        mTeleporting = false;
    }

    RectF getRect(){
        return mRect;
    }

    Bitmap getBitmap(){
        return  mBitmap;
    }
}
