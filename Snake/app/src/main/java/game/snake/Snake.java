package game.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

class Snake {

    private ArrayList<Point> segmentLocation;

    private int mSegmentSize;

    private  Point mMoveRange;

    private int halfWayPoint;

    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    private Heading heading = Heading.RIGHT;
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;

    private Bitmap mBitmapBody;

    Snake(Context context, Point mr ,int ss){
        segmentLocation = new ArrayList<>();
        mSegmentSize =ss;
        mMoveRange = mr;

        mBitmapHeadRight = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.head);

        mBitmapHeadLeft = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);
        mBitmapHeadUp = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);
        mBitmapHeadDown = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);

        Matrix matrix = new Matrix();
        matrix.preScale(-1,1);
        mBitmapHeadLeft = Bitmap.createBitmap(mBitmapHeadRight,
                0,0,ss,ss,matrix,false);

        matrix.preRotate(-90);
        mBitmapHeadUp = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, ss, ss, matrix, true);
// Matrix operations are cumulative
// so rotate by 180 to face down
        matrix.preRotate(180);
        mBitmapHeadDown = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, ss, ss, matrix, true);
// Create and scale the body
        mBitmapBody = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.body);
        mBitmapBody = Bitmap
                .createScaledBitmap(mBitmapBody,
                        ss, ss, false);
// The halfway point across the screen in pixels
// Used to detect which side of screen was pressed
        halfWayPoint = mr.x * ss / 2;
    }

    void reset(int w ,int h){
        heading = Heading.RIGHT;

        segmentLocation.clear();

        segmentLocation.add(new Point(w/2,h/2));
    }

    void move(){

        for(int i = segmentLocation.size() -1;i>0;i --){

            segmentLocation.get(i).x = segmentLocation.get(i-1).x;
            segmentLocation.get(i).y = segmentLocation.get(i-1).y;
        }

        Point p = segmentLocation.get(0);

        switch (heading){
            case UP:
                p.y--;
                break;

            case RIGHT:
                p.x++;
                break;

            case DOWN:
                p.y++;
                break;
            case LEFT:
                p.x--;
                break;
        }
        segmentLocation.set(0, p);
        }

        boolean detectDeath(){
        boolean dead = false;

        if(segmentLocation.get(0).x == -1 ||
            segmentLocation.get(0).x > mMoveRange.x ||
                    segmentLocation.get(0).y == -1 ||
                    segmentLocation.get(0).y > mMoveRange.y) {
            dead = true;
        }
        for (int i = segmentLocation.size()-1;i>0;i--){
            if(segmentLocation.get(0).x == segmentLocation.get(i).x &&
                    segmentLocation.get(0).y ==
                            segmentLocation.get(i).y){
            dead = true;}
        }
        return dead;
        }

        boolean checkDinner(Point l){
                if(segmentLocation.get(0).x == l.x &&
        segmentLocation.get(0).y == l.y){
                    segmentLocation.add(new Point(-10,-10));
                    return true;
                }

                return false;
        }

        void draw(Canvas canvas, Paint paint){
           if(!segmentLocation.isEmpty()){

               switch(heading){
                   case RIGHT:
                       canvas.drawBitmap(mBitmapHeadRight,
                               segmentLocation.get(0).x*
                               mSegmentSize,segmentLocation.get(0).y*
                               mSegmentSize,paint);
                       break;

                   case LEFT:
                       canvas.drawBitmap(mBitmapHeadLeft,
                               segmentLocation.get(0).x
                                       * mSegmentSize,
                               segmentLocation.get(0).y
                                       * mSegmentSize, paint);
                       break;
                   case UP:
                       canvas.drawBitmap(mBitmapHeadUp,
                               segmentLocation.get(0).x
                                       * mSegmentSize,
                               segmentLocation.get(0).y
                                       * mSegmentSize, paint);
                       break;
                   case DOWN:
                       canvas.drawBitmap(mBitmapHeadDown,
                               segmentLocation.get(0).x
                                       * mSegmentSize,
                               segmentLocation.get(0).y
                                       * mSegmentSize, paint);
                       break;
               }

               for (int i =1;i<segmentLocation.size();i++){
                   canvas.drawBitmap(mBitmapBody,segmentLocation.get(i).x
                   *mSegmentSize,segmentLocation.get(i).y
                           * mSegmentSize, paint);
               }


           }


        }

    void switchHeading(MotionEvent motionEvent){
         if(motionEvent.getX() >= halfWayPoint){
             switch (heading) {
// Rotate right
                 case UP:
                     heading = Heading.RIGHT;
                     break;
                 case RIGHT:
                     heading = Heading.DOWN;
                     break;
                 case DOWN:
                     heading = Heading.LEFT;
                     break;
                 case LEFT:
                     heading = Heading.UP;
                     break;
         }
    }else{
             switch (heading){
                 case UP:
                     heading= Heading.UP;

                     break;
                 case LEFT:
                     heading= Heading.LEFT;
                     break;
                 case DOWN:

                     heading= Heading.DOWN;
                     break;
                 case RIGHT:
                     heading= Heading.RIGHT;
                     break;
             }

             }
    }
}

