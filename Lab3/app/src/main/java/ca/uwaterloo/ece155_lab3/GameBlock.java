package ca.uwaterloo.ece155_lab3;

import android.content.Context;
import android.util.Log;

import ca.uwaterloo.ece155_lab2.R;

public class GameBlock extends android.support.v7.widget.AppCompatImageView
{
    // size scale of the block image
    private static final float IMAGE_SCALE = 0.55f;
    // variables for translation/animation
    private static final float translationDistance = 250;
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

    // GameBlock constructor, requires context and (x, y) position
    public GameBlock(Context c, int x, int y)
    {
        super(c);
        myCoordX = x;
        myCoordY = y;
        xLoc = 0;
        yLoc = 0;

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

    // logic for motion of game block, as well as animation
    // block is to move either left, right, up or down (if possible)
    // legal xLoc and yLoc are 0, 1, 2 or 3 (4x4 matrix)
    public boolean move(GameLoopTask.gameDirections dir)
    {
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

                    myCoordX += velocity;
                    velocity += acceleration * (1 / (1 + (1 / 5) * (xLoc + 1)));
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

                    myCoordX -= velocity;
                    velocity += acceleration * (1 / (1 + (1 / 5) * (xLoc + 1)));
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

                    myCoordY -= velocity;
                    velocity += acceleration * (1 / (1 + (1 / 5) * (xLoc + 1)));
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

                    myCoordY += velocity;
                    velocity += acceleration * (1 / (1 + (1 / 5) * (xLoc + 1)));
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