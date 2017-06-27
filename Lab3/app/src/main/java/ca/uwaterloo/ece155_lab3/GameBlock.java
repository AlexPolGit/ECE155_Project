package ca.uwaterloo.ece155_lab3;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.widget.ImageView;

import ca.uwaterloo.ece155_lab2.R;

public class GameBlock extends android.support.v7.widget.AppCompatImageView
{
    private static final float IMAGE_SCALE = 0.50f;
    // variables for animation
    private static final float baseVelocity = 30.0f;
    private static final float acceleration = 10.0f;
    private static float velocity;
    enum state {STARTED, MOVING, STOPPED};

    // block properties
    private int targetCoordX;
    private int targetCoordY;
    private state blockState;
    public state getBlockState() {
        return blockState;
    }
    private int blockOffset = 94;

    // block properties
    private int myCoordX;
    private int myCoordY;
    private int xLoc;
    private int yLoc;

    public GameBlock(Context c, int x, int y)
    {
        super(c);
        myCoordX = x;
        myCoordY = y;
        xLoc = 0;
        yLoc = 0;

        blockState = state.STOPPED;
        velocity = baseVelocity;

        // set the image view
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setX(myCoordX);
        this.setY(myCoordY);
        this.setVisibility(VISIBLE);
    }

    public boolean move(GameLoopTask.gameDirections dir)
    {
        switch (dir)
        {
            case RIGHT:
            {
                if (xLoc < 3)
                {
                    if (blockState == state.STOPPED)
                        blockState = state.STARTED;
                    if (blockState == state.STARTED) {
                        xLoc++;
                        targetCoordX = myCoordX + MainActivity.gameboardUnitWidth;
                    }

                    myCoordX += velocity;
                    velocity += acceleration;
                    this.setX(myCoordX);
                    if (myCoordX >= targetCoordX)
                        blockState = state.STOPPED;
                }
                break;
            }
            case LEFT:
            {
                if (xLoc > 0)
                {
                    if (blockState == state.STOPPED)
                        blockState = state.STARTED;
                    if (blockState == state.STARTED) {
                        xLoc--;
                        targetCoordX = myCoordX - MainActivity.gameboardUnitWidth;
                    }

                    myCoordX -= velocity;
                    velocity += acceleration;
                    this.setX(myCoordX);
                    if (myCoordX <= targetCoordX)
                        blockState = state.STOPPED;
                }
                break;
            }
            case UP:
            {
                if (yLoc > 0)
                {
                    if (blockState == state.STOPPED)
                        blockState = state.STARTED;
                    if (blockState == state.STARTED) {
                        yLoc--;
                        targetCoordY = myCoordY - MainActivity.gameboardUnitHeight;
                    }

                    myCoordY -= velocity;
                    velocity += acceleration;
                    this.setY(myCoordY);
                    if (myCoordY <= targetCoordY)
                        blockState = state.STOPPED;
                }
                break;
            }
            case DOWN:
            {
                if (yLoc < 3)
                {
                    if (blockState == state.STOPPED)
                        blockState = state.STARTED;
                    if (blockState == state.STARTED) {
                        yLoc++;
                        targetCoordY = myCoordY + MainActivity.gameboardUnitHeight;
                    }

                    myCoordY += velocity;
                    velocity += acceleration;
                    this.setY(myCoordY);
                    if (myCoordY >= targetCoordY)
                        blockState = state.STOPPED;
                }
                break;
            }
            case NO_MOVEMENT:
            default:
            {
                Log.d("debug1", "Could not move block.");
                break;
            }
        }

        // block is now in motion after the first iteration of the first tick
        if (blockState == state.STARTED)
            blockState = state.MOVING;

        Log.d("debug1", String.format("Block Position: (%d, %d)", xLoc, yLoc));

        if (blockState == state.STOPPED) {
            velocity = baseVelocity;
            targetCoordX = 0;
            targetCoordY = 0;
            return false; // doMove in gamelooptask is then false, so stops moving
        } else {
            return true; // doMove in gamelooptask is then true so continues moving
        }
    }
}