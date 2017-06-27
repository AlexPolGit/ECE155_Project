package ca.uwaterloo.ece155_lab3;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.TimerTask;

public class GameLoopTask extends TimerTask
{
    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myRL;

    private GameBlock gameBlock;
    public GameBlock getGameBlock() {
        return gameBlock;
    }
    private boolean doMove = false;

    enum gameDirections
    {
        UP, DOWN, LEFT, RIGHT, NO_MOVEMENT
    };
    gameDirections gameDirection = gameDirections.NO_MOVEMENT;

    private int upTime = 0;

    public GameLoopTask(Activity a, Context c, RelativeLayout r)
    {
        super();
        myActivity = a;
        myContext = c;
        myRL = r;
        gameBlock = new GameBlock(c,
                MainActivity.gameBoardOrigin[0] + 45, // -94 when not horizontally aligned!
                MainActivity.gameBoardOrigin[0] - 94);
        myRL.addView(gameBlock);
    }

    public void setDirection(gameDirections dir)
    {
        gameDirection = dir;
        doMove = true;
    }

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