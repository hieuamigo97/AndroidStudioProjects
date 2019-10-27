package game.subhunter;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.widget.ImageView;
import java.util.Random;

public class MainActivity extends Activity {

    int score;
    long millisecondElapsed;
    String playerName;
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 40;
    int gridHeight ;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition ;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    int distanceFromSub;
    boolean debugging= true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display  = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels/gridWidth;
        gridHeight = numberVerticalPixels/blockSize;

        Log.d("Debugging","In Oncreate");
        newGame();
        draw();
    }

    void newGame() {
        Random random = new Random();
        subHorizontalPosition = random.nextInt(gridWidth);
        subVerticalPosition = random.nextInt(gridHeight);
        shotsTaken = 0;

    }

    void draw(){

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
       return true;
    }

    void takeShot(){

    }

    void Boom(){

    }

    void printDebuggingText(){
        Log.d("numberHorizontalPixels",
                "" + numberHorizontalPixels);
        Log.d("numberVerticalPixels",
                "" + numberVerticalPixels);
        Log.d("blockSize", "" + blockSize);
        Log.d("gridWidth", "" + gridWidth);
        Log.d("gridHeight", "" + gridHeight);
        Log.d("horizontalTouched",
                "" + horizontalTouched);
        Log.d("verticalTouched",
                "" + verticalTouched);
        Log.d("subHorizontalPosition",
                "" + subHorizontalPosition);
        Log.d("subVerticalPosition",
                "" + subVerticalPosition);
        Log.d("hit", "" + hit);
        Log.d("shotsTaken", "" + shotsTaken);
        Log.d("debugging", "" + debugging);
        Log.d("distanceFromSub",
                "" + distanceFromSub);
    }
}
