package ca.uwaterloo.ece155_lab4;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ca.uwaterloo.ece155_lab4.utils.Direction;

public class GameManager
{
    private Context context;

    private String gridString, takenString;
    private int grid[][] = new int[4][4];
    private boolean boardIsFull = false;

    private int numberOfMoves = 0;
    private float timeElapsed = 0;
    private int difficulty = 4;

    private boolean taken[][] = new boolean[4][4];

    private ArrayList<GameBlock> blocks;

    private boolean ret = false;

    public boolean motionIsDone = true;
    public boolean gg = false;

    public GameManager(Context c)
    {
        context = c;
        blocks = new ArrayList<>();
        setUpEverything();
    }

    // initialize the board OnStartup
    private void setUpEverything()
    {
        Log.d("debug1", "Setting up game.");
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                taken[i][j] = false;
                grid[i][j] = 0;
            }
        }
        // create 2 initial blocks on the grid
        newRandomBlock(2);
        setGridString();
        MainActivity.testGrid.setText(gridString);
    }

    // handles block merging when blocks slide in a direction
    private void slide(int group, Direction dir)
    {
        boolean hasSlid = false;
        switch (dir)
        {
            case RIGHT:
            {
                for (int s = 0; s < 3; s++)
                {
                    for (int i = 2; i > -1; i--)
                    {
                        if (grid[group][i] == grid[group][i+1])
                        {
                            grid[group][i+1] *= 2;
                            grid[group][i] = 0;
                            hasSlid = true;
                        }
                        else if (grid[group][i+1] == 0)
                        {
                            grid[group][i+1] = grid[group][i];
                            grid[group][i] = 0;
                            hasSlid = true;
                        }
                    }
                }
                break;
            }
            case LEFT:
            {
                for (int s = 3; s > 0; s--)
                {
                    for (int i = 1; i < 4; i++)
                    {
                        if (grid[group][i] == grid[group][i-1])
                        {
                            grid[group][i-1] *= 2;
                            grid[group][i] = 0;
                            hasSlid = true;
                        }
                        else if (grid[group][i-1] == 0)
                        {
                            grid[group][i-1] = grid[group][i];
                            grid[group][i] = 0;
                            hasSlid = true;
                        }
                    }
                }
                break;
            }
            case DOWN:
            {
                for (int s = 0; s < 3; s++)
                {
                    for (int i = 2; i > -1; i--)
                    {
                        if (grid[i][group] == grid[i + 1][group])
                        {
                            grid[i + 1][group] *= 2;
                            grid[i][group] = 0;
                            hasSlid = true;
                        }
                        else if (grid[i + 1][group] == 0)
                        {
                            grid[i + 1][group] = grid[i][group];
                            grid[i][group] = 0;
                            hasSlid = true;
                        }
                    }
                }
                break;
            }
            case UP:
            {
                for (int s = 3; s > 0; s--)
                {
                    for (int i = 1; i < 4; i++)
                    {
                        if (grid[i][group] == grid[i - 1][group])
                        {
                            grid[i - 1][group] *= 2;
                            grid[i][group] = 0;
                            hasSlid = true;
                        }
                        else if (grid[i - 1][group] == 0)
                        {
                            grid[i - 1][group] = grid[i][group];
                            grid[i][group] = 0;
                            hasSlid = true;
                        }
                    }
                }
                break;
            }
            default:
            {
                Log.d("debug", "CANT SLIDE ROW UP OR DOWN");
                break;
            }
        }
        if (hasSlid)
        {
            numberOfMoves++;
        }
    }

    public synchronized void slideGrid(Direction dir)
    {
        motionIsDone = false;
        for (int i = 0; i < 4; i++)
        {
            slide(i, dir);
        }
        motionIsDone = true;

        takenScan();
        if (!boardIsFull)
        {
            newRandomBlock(1);
        }
        setGridString();

        MainActivity.testGrid.setText(gridString);

        if (loseCondition())
        {
            gg = true;
        }
        else if (winCondition())
        {
            gg = true;
        }
    }

    // creates a block of randomly generated value and position
    private synchronized void newRandomBlock(int numberToAdd)
    {
        int x = 0;
        int y = 0;
        int r = 0;

        for (int X = 0; X < numberToAdd; X++)
        {
            do
            {
                x = (int) ( Math.random() * 4 );
                y = (int) ( Math.random() * 4 );
            }
            while (taken[x][y]);

            r = rand2or4(0.90f);
            grid[x][y] = r;

            takenScan();

            Log.d("debug1", String.format("Making new block at (%d, %d) of value %d!", x, y, r));
            addGameBlock(x, y, r);
        }
    }

    // Updates the board to check which cells are occupied by blocks
    private void takenScan()
    {
        int c = 0;
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                taken[i][j] = false;
                if (grid[i][j] > 0)
                {
                    taken[i][j] = true;
                    c++;
                }
            }
        }
        boardIsFull = (c == 16) ? true : false;
    }

    // check if the user has lost, i.e. no more blocks can shift in any direction
    private boolean loseCondition()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (grid[i][j] == 0)
                {
                    return false;
                }
                else
                {
                    if (i - 1 > -1 && grid[i - 1][j] == grid[i][j])
                    {
                        return false;
                    }
                    if (i + 1 < 4 && grid[i + 1][j] == grid[i][j])
                    {
                        return false;
                    }
                    if (j - 1 > -1 && grid[i][j - 1] == grid[i][j])
                    {
                        return false;
                    }
                    if (j + 1 < 4 && grid[i][j + 1] == grid[i][j])
                    {
                        return false;
                    }
                }
            }
        }
        Log.d("debug1", "LOST STATE ACTIVE!");
        return true;
    }

    // check if the user has won, i.e. got a block with value 256
    private boolean winCondition()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (grid[i][j] == 256)
                {
                    Log.d("debug1", "WIN STATE ACTIVE!");
                    return true;
                }
            }
        }
        return false;
    }

    // generates a random value either 2 or 4 for each new block created
    private int rand2or4(double chance2)
    {
        return (Math.random() > 1 - chance2) ? 2 : 4;
    }

    private void setGridString()
    {
        gridString = "";
        takenString = "";
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                gridString += String.format("(%d) ", grid[i][j]);

                if (taken[i][j])
                {
                    takenString += "(T) ";
                }
                else
                {
                    takenString += "(F) ";
                }
            }
            gridString += '\n';
            takenString += '\n';
            takenScan();
        }
    }

    public void tickTime(float t)
    {
        timeElapsed += t;
    }

    public double getUserScore()
    {
        double s = 10000 - difficulty * numberOfMoves * Math.log10(difficulty * timeElapsed);
        return (s >= 0) ? s : 0;
    }

    public void wipeList()
    {
        blocks = new ArrayList<>();
    }

    private void addGameBlock(int x, int y, int val)
    {
        blocks.add(new GameBlock(context, x, y, val));
        Log.d("debug1", String.format("Gameblock added at (%d, %d)!", x, y));
        String s = "List of Blocks: ";
        for (GameBlock g : blocks)
        {
            s += String.format("(%d, %d), ", g.getGridX(), g.getGridY());
        }
        Log.d("debug1", s);
    }

    private void removeGameBlock(int x, int y)
    {
        for (GameBlock g : blocks)
        {
            if (g.getGridX() == x && g.getGridY() == y)
            {
                blocks.remove(g);
                break;
            }
        }
        Log.d("debug1", "Gameblock to remove not found!");
    }

    private GameBlock getGameBlock(int x, int y)
    {
        Log.d("debug1", String.format("Trying to get block (%d, %d)", x, y));
        for (GameBlock g : blocks)
        {
            if (g.getGridX() == x && g.getGridY() == y)
            {
                return g;
            }
        }
        Log.d("debug1", "Gameblock not found!");
        return null;
    }
}