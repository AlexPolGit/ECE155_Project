package ca.uwaterloo.ece155_lab4.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.TextView;

import ca.uwaterloo.ece155_lab4.MainActivity;

public abstract class GameBlockTemplate extends android.support.v7.widget.AppCompatImageView
{
    // current state of the block
    public enum state
    {
        STARTED, MOVING, STOPPED
    }

    public Context context;

    // size scale of the block image
    public static final float IMAGE_SCALE = 0.30f;
    // variables for translation/animation
    public static float translationDistance = MainActivity.gameboardUnitWidth;
    public static final float baseVelocity = 30.0f;
    public static final float acceleration = 10.0f;
    public static float velocity;

    // block properties
    public float targetCoordX;
    public float targetCoordY;
    public state blockState;

    public state getBlockState()
    {
        return blockState;
    }

    // block properties
    public float myCoordX;
    public float myCoordY;
    public int xLoc;
    public int yLoc;
    public int value;
    public TextView gameBlockValue;
    public int xOffset;
    public int yOffset = 25;

    public boolean isMoving = false;

    public GameBlockTemplate(Context context)
    {
        super(context);
    }

    public void removeFromRL()
    {
        this.setVisibility(View.INVISIBLE);
        gameBlockValue.setVisibility((View.INVISIBLE));
        MainActivity.relativeLayout.removeView(gameBlockValue);
        MainActivity.relativeLayout.removeView(this);
    }

    public TextView getTextView()
    {
        return gameBlockValue;
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
        setResourceByValue();
    }

    public abstract void setResourceByValue();
    protected abstract void move(Direction dir, int x, int y);
}
