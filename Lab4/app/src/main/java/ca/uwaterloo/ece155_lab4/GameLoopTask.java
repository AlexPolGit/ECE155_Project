package ca.uwaterloo.ece155_lab4;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import ca.uwaterloo.ece155_lab4.utils.Direction;

public class GameLoopTask extends TimerTask
{
    // context for which to do logic
    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myRL;
    private GameManager gameManager;

    // the gameblock object
    private GameBlock[][] gameBlocks;

    /*
    public void setGameBlocks()
    {
        gameBlocks = GameManager.board;
    }
    */

    // is moving allowed?
    private boolean doMove = false;

    // last motion direction
    Direction gameDirection = Direction.NO_MOVEMENT;

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
        gameManager = new GameManager(myContext);

        Timer gameTime = new Timer();
        TimerTask countTime = new TimerTask()
        {
            @Override
            public void run()
            {
                gameManager.tickTime(1.0f);
            }
        };
        gameTime.schedule(countTime, 1000, 1000);
    }

    public void resetGame()
    {
        gameManager.wipeList();
    }

    // called when the block is allowed to move
    public void setDirection(Direction dir)
    {
        gameDirection = dir;
        doMove = true;
    }

    public GameManager getGameManager()
    {
        return gameManager;
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
                            if (!gameManager.gg && gameManager.motionIsDone)
                            {
                                gameManager.slideGrid(gameDirection);
                            }
                        }
                        /*
                        if (upTime % 1000 == 0)
                        {
                            Log.d("debug1", String.format("Program uptime: %d seconds", (upTime / 1000)));
                            Log.d("debug1", "DIR:" + gameDirection.toString());
                            gameManager.tickTime(1.0f);
                        }
                        */
                    }
                }
        );
    }
}