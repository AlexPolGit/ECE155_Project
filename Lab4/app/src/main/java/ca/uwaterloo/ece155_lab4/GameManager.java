package ca.uwaterloo.ece155_lab4;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ca.uwaterloo.ece155_lab4.GameBlock;
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

    public boolean motionIsDone = true;
    public boolean gg = false;

    public GameManager(Context c)
    {
        context = c;
        blocks = new ArrayList<>();
        setUpEverything();
    }

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

        newRandomBlock(2);
        setGridString();
    }

    private synchronized void slide(int group, Direction dir)
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

                            GameBlock temp = getGameBlock(group, i);
                            temp.move(Direction.RIGHT, group, i+1);
                            while (temp.isMoving) {}
                            removeGameBlock(group, i);
                            temp = getGameBlock(group, i+1);
                            temp.setValue(grid[group][i+1]);
                        }
                        else if (grid[group][i+1] == 0)
                        {
                            grid[group][i+1] = grid[group][i];
                            grid[group][i] = 0;
                            hasSlid = true;

                            GameBlock temp = getGameBlock(group, i);
                            temp.move(Direction.RIGHT, group, i+1);
                            while (temp.isMoving) {}
                            removeGameBlock(group, i);
                            temp = getGameBlock(group, i+1);
                            temp.setValue(grid[group][i+1]);
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

                            GameBlock temp = getGameBlock(group, i);
                            temp.move(Direction.LEFT, group, i-1);
                            while (temp.isMoving) {}
                            removeGameBlock(group, i);
                            temp = getGameBlock(group, i-1);
                            temp.setValue(grid[group][i-1]);
                        }
                        else if (grid[group][i-1] == 0)
                        {
                            grid[group][i-1] = grid[group][i];
                            grid[group][i] = 0;
                            hasSlid = true;

                            GameBlock temp = getGameBlock(group, i);
                            temp.move(Direction.LEFT, group, i-1);
                            while (temp.isMoving) {}
                            removeGameBlock(group, i);
                            temp = getGameBlock(group, i-1);
                            temp.setValue(grid[group][i-1]);
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

                            GameBlock temp = getGameBlock(i, group);
                            temp.move(Direction.DOWN, i+1, group);
                            while (temp.isMoving) {}
                            removeGameBlock(i, group);
                            temp = getGameBlock(i+1, group);
                            temp.setValue(grid[i+1][group]);
                        }
                        else if (grid[i + 1][group] == 0)
                        {
                            grid[i + 1][group] = grid[i][group];
                            grid[i][group] = 0;
                            hasSlid = true;

                            GameBlock temp = getGameBlock(i, group);
                            temp.move(Direction.DOWN, i+1, group);
                            while (temp.isMoving) {}
                            removeGameBlock(i, group);
                            temp = getGameBlock(i+1, group);
                            temp.setValue(grid[i+1][group]);
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

                            GameBlock temp = getGameBlock(i, group);
                            temp.move(Direction.UP, i-1, group);
                            while (temp.isMoving) {}
                            removeGameBlock(i, group);
                            temp = getGameBlock(i-1, group);
                            temp.setValue(grid[i-1][group]);
                        }
                        else if (grid[i - 1][group] == 0)
                        {
                            grid[i - 1][group] = grid[i][group];
                            grid[i][group] = 0;
                            hasSlid = true;

                            GameBlock temp = getGameBlock(i, group);
                            temp.move(Direction.UP, i-1, group);
                            while (temp.isMoving) {}
                            removeGameBlock(i, group);
                            temp = getGameBlock(i-1, group);
                            temp.setValue(grid[i-1][group]);
                        }
                    }
                }
                break;
            }
            default:
            {
                Log.d("debug1", "CANT SLIDE ROW UP OR DOWN");
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

            Log.d("debug1", String.format("Making new block at (%d, %d) or value %d!", x, y, r));
            addGameBlock(x, y, r);
        }
    }

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

    private void addGameBlock(int x, int y, int val)
    {
        blocks.add(new GameBlock(context, x, y, val));
        Log.d("debug1", String.format("Gameblock added at (%d, %d)!", x, y));
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