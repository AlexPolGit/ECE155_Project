package ca.uwaterloo.ece155_lab4;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import ca.uwaterloo.ece155_lab2.R;
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
    Direction lastGameDirection = Direction.NO_MOVEMENT;

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
    }

    public void resetGame()
    {
        gameManager.wipeList();
        gameManager = new GameManager(myContext);
        for (GameBlock g: gameManager.blocks)
        {
            g.getTextView().setTextColor(Color.BLACK);
        }
        lastGameDirection = Direction.NO_MOVEMENT;
        MainActivity.text_direction.setText("NO DIR");
        MainActivity.text_direction.setTextColor(Color.BLACK);
        upTime = 0;
        MainActivity.text_time.setText(String.format("0 s"));
    }

    // called when the block is allowed to move
    public void doMove(Direction dir)
    {
        lastGameDirection = dir;
        MainActivity.text_direction.setText(dir.toString());

        if (!gameManager.gg && gameManager.motionIsDone)
        {
            gameManager.slideGrid(lastGameDirection);
        }
        else if (gameManager.gg)
        {
            if (gameManager.winCondition())
            {
                MainActivity.text_direction.setText(R.string.game_won);
                MainActivity.text_direction.setTextColor(myContext.getResources().getColor(R.color.winGreen));

                for (GameBlock g: gameManager.blocks)
                {
                    g.getTextView().setTextColor(Color.GREEN);
                }
            }
            else
            {
                MainActivity.text_direction.setText(R.string.game_lost);
                MainActivity.text_direction.setTextColor(myContext.getResources().getColor(R.color.loseRed));

                for (GameBlock g: gameManager.blocks)
                {
                    g.getTextView().setTextColor(Color.RED);
                }
            }

        }
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

                        if (upTime % 1000 == 0)
                        {
                            MainActivity.text_time.setText(String.format("%d s", upTime / 1000));
                        }
                    }
                }
        );
    }
}