package ca.uwaterloo.ece155_lab3;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.TimerTask;

public class GameLoopTask extends TimerTask
{
    // context for which to do logic
    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myRL;

    // the gameblock object
    private GameBlock gameBlock;

    public GameBlock getGameBlock()
    {
        return gameBlock;
    }

    // is moving allowed?
    private boolean doMove = false;

    // possible motion directions
    enum gameDirections
    {
        UP, DOWN, LEFT, RIGHT, NO_MOVEMENT
    }

    // last motion direction
    gameDirections gameDirection = gameDirections.NO_MOVEMENT;

    // uptime of program in ms (for debugging)
    private int upTime = 0;

    // constructor for game loop, also creates the game block
    // requires current activity, its context and the relative layout in which animation is done
    public GameLoopTask(Activity a, Context c, RelativeLayout r)
    {
        super();
        myActivity = a;
        myContext = c;
        myRL = r;
        gameBlock = new GameBlock(    c,
                                      MainActivity.gameBoardOrigin[0] - 70,
                                      MainActivity.gameBoardOrigin[0] - 70);
        myRL.addView(gameBlock);
    }

    // called when the block is allowed to move
    public void setDirection(gameDirections dir)
    {
        gameDirection = dir;
        doMove = true;
    }

    // runs ever 50ms (20fps), telling block to move when needed
    @Override
    public void run()
    {
        myActivity.runOnUiThread(
                new Runnable()
                {
                    public void run()
                    {
                        upTime += 50;
                        if (doMove)
                        {
                            doMove = gameBlock.move(gameDirection);
                        }
                        if (upTime % 1000 == 0)
                        {
                            Log.d("debug1", String.format("Program uptime: %d seconds", (upTime / 1000)));
                            Log.d("debug1", "DIR:" + gameDirection.toString());
                        }
                    }
                }
        );
    }
}