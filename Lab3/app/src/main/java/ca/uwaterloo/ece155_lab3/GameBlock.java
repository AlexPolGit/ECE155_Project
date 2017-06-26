package ca.uwaterloo.ece155_lab3;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.widget.ImageView;

import ca.uwaterloo.ece155_lab2.R;

public class GameBlock extends android.support.v7.widget.AppCompatImageView
{
    private final float IMAGE_SCALE = 0.50f;
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

        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setX(myCoordX);
        this.setY(myCoordY);
        this.setVisibility(VISIBLE);
    }

    private final int c = 150;

    public void move(GameLoopTask.gameDirections dir)
    {
        switch (dir)
        {
            case RIGHT:
            {
                if (xLoc < 3)
                {
                    xLoc++;
                    myCoordX += MainActivity.gameboardUnitWidth;
                    setX(myCoordX);
                }
                break;
            }
            case LEFT:
            {
                if (xLoc > 0)
                {
                    xLoc--;
                    myCoordX -= MainActivity.gameboardUnitWidth;
                    setX(myCoordX);
                }
                break;
            }
            case UP:
            {
                if (yLoc > 0)
                {
                    yLoc--;
                    myCoordY -= MainActivity.gameboardUnitHeight;
                    setY(myCoordY);
                }
                break;
            }
            case DOWN:
            {
                if (yLoc < 3)
                {
                    yLoc++;
                    myCoordY += MainActivity.gameboardUnitHeight;
                    setY(myCoordY);
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
        Log.d("debug1", String.format("Block Position: (%d, %d)", xLoc, yLoc));
    }
}