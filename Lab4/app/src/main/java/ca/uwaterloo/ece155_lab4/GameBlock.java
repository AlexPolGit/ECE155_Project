package ca.uwaterloo.ece155_lab4;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ca.uwaterloo.ece155_lab2.R;
import ca.uwaterloo.ece155_lab4.utils.Direction;

public class GameBlock extends android.support.v7.widget.AppCompatImageView
{
    private Context context;

    // size scale of the block image
    private static final float IMAGE_SCALE = 0.30f;
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
    private float myCoordX;
    private float myCoordY;
    private int xLoc;
    private int yLoc;
    private int value;
    private TextView gameBlockValue;
    private int xOffset;
    private int yOffset = 30;

    public boolean isMoving = false;

    // GameBlock constructor, requires context and (x, y) position
    public GameBlock(Context c, int x, int y, int v)
    {
        super(c);
        context = c;
        // TODO: make this proper, not some constants
        myCoordX = -220 + 150 * y * 1.5f;
        myCoordY = -220 + 150 * x * 1.5f;
        xLoc = x;
        yLoc = y;
        value = v;

        // assign the proper colored block and x-offset for the block based on value
        setResourceByValue();

        // create a new TextView to output the value of the block
        gameBlockValue = new TextView(context);
        gameBlockValue.setText("" + v);
        gameBlockValue.setTextColor(Color.BLACK);
        gameBlockValue.setTextSize(80);
        gameBlockValue.setX(150 * y * 1.5f + xOffset);
        gameBlockValue.setY(150 * x * 1.5f + yOffset);

        blockState = state.STOPPED;
        velocity = baseVelocity;

        // set the image recourse, size, scale, location, visibility
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setTranslationX(myCoordX);
        this.setTranslationY(myCoordY);
        this.setVisibility(VISIBLE);

        MainActivity.relativeLayout.addView(this);
        gameBlockValue.bringToFront();
        MainActivity.relativeLayout.addView(gameBlockValue);
    }

    public void setResourceByValue()
    {
        switch (value)
        {
            case 2: this.setImageResource(R.drawable.block_2); xOffset = 145; break;
            case 4: this.setImageResource(R.drawable.block_4); xOffset = 145; break;
            case 8: this.setImageResource(R.drawable.block_8); xOffset = 145; break;
            case 16: this.setImageResource(R.drawable.block_16); xOffset = 115; break;
            case 32: this.setImageResource(R.drawable.block_32); xOffset = 115; break;
            case 64: this.setImageResource(R.drawable.block_64); xOffset = 115; break;
            case 128: this.setImageResource(R.drawable.block_128); xOffset = 85; break;
            case 256: this.setImageResource(R.drawable.block_256); xOffset = 85; break;
            default: this.setImageResource(R.drawable.block_2); xOffset = 145; break;
        }
    }

    public void removeFromRL()
    {
        this.setVisibility(View.INVISIBLE);
        gameBlockValue.setVisibility((View.INVISIBLE));
        MainActivity.relativeLayout.removeView(gameBlockValue);
        MainActivity.relativeLayout.removeView(this);
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

    public synchronized void setValue(int val)
    {
        value = val;
        setResourceByValue();
    }

    // logic for motion of game block, as well as animation
    // block is to move either left, right, up or down (if possible)
    // legal xLoc and yLoc are 0, 1, 2 or 3 (4x4 matrix)
    public synchronized void move(Direction dir, int x, int y)
    {
        isMoving = true;

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
                        targetCoordX = myCoordX + (x-xLoc)*translationDistance;
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
                    this.gameBlockValue.setX(myCoordX + xOffset);
                    if (myCoordX >= targetCoordX)
                    {
                        blockState = state.STOPPED;
                        xLoc = x;
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
                        targetCoordX = myCoordX - (x-xLoc)*translationDistance;
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
                    this.gameBlockValue.setX(myCoordX + xOffset);
                    if (myCoordX <= targetCoordX)
                    {
                        blockState = state.STOPPED;
                        xLoc = x;
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
                        targetCoordY = myCoordY - (y-yLoc)*translationDistance;
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
                    this.gameBlockValue.setY(myCoordY + yOffset);
                    if (myCoordY <= targetCoordY)
                    {
                        blockState = state.STOPPED;
                        yLoc = y;
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
                        targetCoordY = myCoordY + (y-yLoc)*translationDistance;
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
                    this.gameBlockValue.setY(myCoordY + yOffset);
                    if (myCoordY >= targetCoordY)
                    {
                        blockState = state.STOPPED;
                        yLoc = y;
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
            isMoving = false;
        }
        else
        {
            isMoving = true;
        }
    }
}