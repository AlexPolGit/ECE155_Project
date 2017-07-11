package ca.uwaterloo.ece155_lab4;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import ca.uwaterloo.ece155_lab4.utils.Direction;

public class GameManager
{
    // application context
    private Context context;

    // logic for storing the values of blocks in order to move them
    private String gridString, takenString;
    private int grid[][] = new int[4][4];
    private boolean boardIsFull = false;
    private boolean taken[][] = new boolean[4][4];
    private GameBlock gameBoard[][] = new GameBlock[4][4];

    // all the current blocks
    public ArrayList<GameBlock> blocks;

    // tells the rest of the program if motion is done yet
    public boolean motionIsDone = true;
    public boolean gg = false;

    // contructor for game manager
    public GameManager(Context c)
    {
        context = c;
        blocks = new ArrayList<>();
        setUpEverything();
    }

    // initialize the board OnStartup
    private synchronized void setUpEverything()
    {
        Log.d("debug1", "Setting up game.");
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                taken[i][j] = false;
                grid[i][j] = 0;
                gameBoard[i][j] = null;
            }
        }
        // create 2 initial blocks on the grid
        newRandomBlock(2);
        //setGridString();
        MainActivity.testGrid.setText(gridString);
    }

    // handles block merging when blocks slide in a direction
    private synchronized boolean slide(int group, Direction dir)
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

                            // SLIDE BLOCK AT i TO i+1, DOUBLE ITS VALUE
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

                            // SLIDE BLOCK AT i TO i-1, DOUBLE ITS VALUE
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

                            // SLIDE BLOCK AT i TO i+1, DOUBLE ITS VALUE
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

                            // SLIDE BLOCK AT i TO i-1, DOUBLE ITS VALUE
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
        return hasSlid;
    }

    // slides the grid, row by row or column by column, depending on input direction
    public synchronized void slideGrid(Direction dir)
    {
        boolean spawnBlock = false;
        motionIsDone = false;
        MainActivity.testGrid.setTextColor(Color.RED);
        for (int i = 0; i < 4; i++)
        {
            spawnBlock = slide(i, dir);
        }

        wipeList();
        for (int x = 0; x < 4; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                if (grid[x][y] != 0)
                {
                    blocks.add(new GameBlock(context, x, y, grid[x][y]));
                }
            }
        }

        motionIsDone = true;
        MainActivity.testGrid.setTextColor(Color.BLACK);

        takenScan();
        if (!boardIsFull)
        {
            newRandomBlock(1);
        }
        //setGridString();

        MainActivity.testGrid.setText(gridString);

        if (loseCondition() || winCondition())
        {
            gg = true;
        }
        else
        {
            gg = false;
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
        for(int i = 0; i < 4; i++) {
            for(int k = 0; k < 4; k++) {
                int d = (gameBoard[i][k] == null) ? -1 : gameBoard[i][k].getValue();
                Log.d("Gameboard", String.format("At (%d,%d) value = %d", i, k, d));
            }
        }
    }

    // Updates the board to check which cells are occupied by blocks
    private synchronized void takenScan()
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
    public boolean winCondition()
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

    // FOR DEBUGGING: creates strings for debugging board values
    /*private void setGridString()
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
    }*/

    // clears the list of blocks
    public synchronized void wipeList()
    {
        for (GameBlock g : blocks)
        {
            g.removeFromRL();
        }
        blocks = new ArrayList<>();
    }

    // add a new block to the list of blocks
    private synchronized void addGameBlock(int x, int y, int val)
    {
        GameBlock newGeneratedBlock = new GameBlock(context, x, y, val);
        blocks.add(newGeneratedBlock);
        gameBoard[x][y] = newGeneratedBlock;
        Log.d("debug1", String.format("Gameblock added at (%d, %d)!", x, y));
        String s = "List of Blocks: ";
        for (GameBlock g : blocks)
        {
            s += String.format("(%d, %d, V:%d), ", g.getGridX(), g.getGridY() ,g.getValue());
        }
        Log.d("debug1", s);
    }


    /*private void removeGameBlock(int x, int y)
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
    }*/
}