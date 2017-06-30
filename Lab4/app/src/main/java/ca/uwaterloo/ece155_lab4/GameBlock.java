package ca.uwaterloo.ece155_lab4;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.util.Log;

import ca.uwaterloo.ece155_lab2.R;

public class GameBlock extends android.support.v7.widget.AppCompatImageView
{
    private Context context;

    // size scale of the block image
    private static final float IMAGE_SCALE = 0.55f;
    // variables for translation/animation
    private static float translationDistance = MainActivity.gameboardUnitWidth;
    private static final float baseVelocity = 30.0f;
    private static final float acceleration = 10.0f;
    private static float velocity;
    //private static final float translationDistance = MainActivity.gameboardUnitWidth;

    // possible motion states of block
    enum state
    {
        STARTED, MOVING, STOPPED
    }

    // block properties
    private float targetCoordX;
    private float targetCoordY;
    private state blockState;

    public state getBlockState()
    {
        return blockState;
    }

    //private int blockOffset = 94;

    // block properties
    private int myCoordX;
    private int myCoordY;
    private int xLoc;
    private int yLoc;
    private int value;

    // GameBlock constructor, requires context and (x, y) position
    public GameBlock(Context c, int x, int y)
    {
        super(c);
        context = c;
        myCoordX = x;
        myCoordY = y;
        xLoc = 0;
        yLoc = 0;
        value = 2;

        blockState = state.STOPPED;
        velocity = baseVelocity;

        // set the image recourse, size, scale, location, visibility
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setTranslationX(x);
        this.setTranslationY(y);
        this.setVisibility(VISIBLE);
    }

    public int getGridX()
    {
        return xLoc;
    }

    public int getGridY()
    {
        return yLoc;
    }

    public void setGridX(int x)
    {
        xLoc = x;
    }

    public void setGridY(int y)
    {
        yLoc = y;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int val)
    {
        value = val;
    }

    // logic for motion of game block, as well as animation
    // block is to move either left, right, up or down (if possible)
    // legal xLoc and yLoc are 0, 1, 2 or 3 (4x4 matrix)
    public boolean move(GameLoopTask.gameDirections dir)
    {
        translationDistance = MainActivity.img_gameboard.getWidth() / 4;
        //Log.d("debug1", "WIDTH TO MOVE: " + Integer.toString(MainActivity.img_gameboard.getWidth() / 4));
        switch (dir)
        {
            // right direction logic
            case RIGHT:
            {
                if (xLoc < 3)
                {
                    if (blockState == state.STOPPED) blockState = state.STARTED;
                    if (blockState == state.STARTED)
                    {
                        targetCoordX = myCoordX + translationDistance;
                    }

                    if (myCoordX + velocity < targetCoordX)
                    {
                        myCoordX += velocity;
                    }
                    else
                    {
                        myCoordX = (int) targetCoordX;
                    }
                    velocity += acceleration;
                    this.setTranslationX(myCoordX);
                    if (myCoordX >= targetCoordX)
                    {
                        blockState = state.STOPPED;
                        xLoc++;
                    }
                }
                break;
            }
            // left direction logic
            case LEFT:
            {
                if (xLoc > 0)
                {
                    if (blockState == state.STOPPED) blockState = state.STARTED;
                    if (blockState == state.STARTED)
                    {
                        targetCoordX = myCoordX - translationDistance;
                    }

                    if (myCoordX - velocity > targetCoordX)
                    {
                        myCoordX -= velocity;
                    }
                    else
                    {
                        myCoordX = (int) targetCoordX;
                    }
                    velocity += acceleration;
                    this.setTranslationX(myCoordX);
                    if (myCoordX <= targetCoordX)
                    {
                        blockState = state.STOPPED;
                        xLoc--;
                    }
                }
                break;
            }
            // up direction logic
            case UP:
            {
                if (yLoc > 0)
                {
                    if (blockState == state.STOPPED) blockState = state.STARTED;
                    if (blockState == state.STARTED)
                    {
                        targetCoordY = myCoordY - translationDistance;
                    }

                    if (myCoordY - velocity > targetCoordY)
                    {
                        myCoordY -= velocity;
                    }
                    else
                    {
                        myCoordY = (int) targetCoordY;
                    }
                    velocity += acceleration;
                    this.setTranslationY(myCoordY);
                    if (myCoordY <= targetCoordY)
                    {
                        blockState = state.STOPPED;
                        yLoc--;
                    }
                }
                break;
            }
            // down direction logic
            case DOWN:
            {
                if (yLoc < 3)
                {
                    if (blockState == state.STOPPED) blockState = state.STARTED;
                    if (blockState == state.STARTED)
                    {
                        targetCoordY = myCoordY + translationDistance;
                    }

                    if (myCoordY + velocity < targetCoordY)
                    {
                        myCoordY += velocity;
                    }
                    else
                    {
                        myCoordY = (int) targetCoordY;
                    }
                    velocity += acceleration;
                    this.setTranslationY(myCoordY);
                    if (myCoordY >= targetCoordY)
                    {
                        blockState = state.STOPPED;
                        yLoc++;
                    }
                }
                break;
            }
            // other/error logic (should not happen)
            case NO_MOVEMENT:
            default:
            {
                Log.d("debug1", "Could not move block.");
                break;
            }
        }

        // block is now in motion after the first iteration of the first tick
        if (blockState == state.STARTED) blockState = state.MOVING;

        Log.d("debug1", String.format("Block Position: (%d, %d)", xLoc, yLoc));
        Log.d("debug1", "Block State: " + getBlockState().toString());

        if (blockState == state.STOPPED)
        {
            velocity = baseVelocity;
            targetCoordX = 0;
            targetCoordY = 0;
            return false; // doMove in gamelooptask is then false, so stops moving
        }
        else
        {
            return true; // doMove in gamelooptask is then true so continues moving
        }
    }
}