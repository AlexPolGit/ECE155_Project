package ca.uwaterloo.ece155_lab4;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import ca.uwaterloo.ece155_lab2.R;
import ca.uwaterloo.ece155_lab4.utils.Direction;
import ca.uwaterloo.ece155_lab4.utils.GameBlockTemplate;

public class GameBlock extends GameBlockTemplate
{
    // GameBlock constructor, requires context and (x, y) position
    public GameBlock(Context c, int x, int y, int v)
    {
        super(c);
        context = c;
        myCoordX = -220 + 150 * y * 1.5f;
        myCoordY = -220 + 150 * x * 1.5f;
        xLoc = x;
        yLoc = y;
        this.setValue(v);

        numOfCellsToMove = 0;
        mergeValue = 0;
        deleteThis = false;

        // assign the proper colored block and x-offset for the block based on value
        setResourceByValue();

        // create a new TextView to output the value of the block
        gameBlockValue = new TextView(context);
        gameBlockValue.setText(String.format("%d", v));
        gameBlockValue.setTextColor(Color.BLACK);
        gameBlockValue.setTextSize(60);
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

    // sets the image of game block depending on its value
    public void setResourceByValue()
    {
        switch (this.getValue())
        {
            case 2: this.setImageResource(R.drawable.block_2); xOffset = 110; break;
            case 4: this.setImageResource(R.drawable.block_4); xOffset = 110; break;
            case 8: this.setImageResource(R.drawable.block_8); xOffset = 110; break;
            case 16: this.setImageResource(R.drawable.block_16); xOffset = 80; break;
            case 32: this.setImageResource(R.drawable.block_32); xOffset = 80; break;
            case 64: this.setImageResource(R.drawable.block_64); xOffset = 80; break;
            case 128: this.setImageResource(R.drawable.block_128); xOffset = 50; break;
            case 256: this.setImageResource(R.drawable.block_256); xOffset = 50; break;
            default: this.setImageResource(R.drawable.block_2); xOffset = 110; break;
        }
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