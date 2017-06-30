package uwaterloo.ca.a2048test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{

    private TextView gridTxt, gridTaken, ggText;
    private String gridString, takenString;
    private Button upBtn, downBtn, rightBtn, leftBtn, resetBtn;
    private int grid[][] = new int[4][4];
    private boolean gg = false;
    private boolean boardIsFull = false;

    private final int RIGHT = 1;
    private final int LEFT = 2;
    private final int UP = 3;
    private final int DOWN = 4;

    private int numberOfMoves = 0;
    private float timeElapsed = 0;
    private int difficulty = 4;

    private boolean taken[][] = new boolean[4][4];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridTxt = (TextView) findViewById(R.id.grid);
        gridTaken = (TextView) findViewById(R.id.taken);
        ggText = (TextView) findViewById(R.id.txtGameOver);
        upBtn = (Button) findViewById(R.id.btnUp);
        downBtn = (Button) findViewById(R.id.btnDown);
        rightBtn = (Button) findViewById(R.id.btnRight);
        leftBtn = (Button) findViewById(R.id.btnLeft);
        resetBtn = (Button) findViewById(R.id.btnReset);

        upBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!gg)
                {
                    slideGrid(UP);
                    setGridString();
                    updateGridText();
                }
            }
        });

        downBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!gg)
                {
                    slideGrid(DOWN);
                    setGridString();
                    updateGridText();
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!gg)
                {
                    slideGrid(RIGHT);
                    setGridString();
                    updateGridText();
                }
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!gg)
                {
                    slideGrid(LEFT);
                    setGridString();
                    updateGridText();
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                setUpEverything();
                gg = false;
            }
        });

        Timer gameTimer = new Timer();
        TimerTask countTime = new TimerTask()
        {
            @Override
            public void run()
            {
                timeElapsed += 0.1f;
                if (timeElapsed %10 == 0)
                {
                    Log.d("debug", "Time Elapsed: " + timeElapsed);
                }
            }
        };
        gameTimer.schedule(countTime, 100, 100);

        setUpEverything();
    }

    private void setUpEverything()
    {
        ggText.setVisibility(View.INVISIBLE);

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
        updateGridText();
    }

    private void slide(int group, int dir)
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

    private void slideGrid(int dir)
    {
        for (int i = 0; i < 4; i++)
        {
            slide(i, dir);
        }

        takenScan();
        if (!boardIsFull)
        {
            newRandomBlock(1);
        }
        setGridString();
        updateGridText();

        if (loseCondition())
        {
            gg = true;
            ggText.setText(String.format("YOU LOSE.\nSCORE: %.0f", getUserScore()));
            ggText.setVisibility(View.VISIBLE);
        }
        else if (winCondition())
        {
            gg = true;
            ggText.setText(String.format("YOU WIN.\nSCORE: %.0f", getUserScore()));
            ggText.setVisibility(View.VISIBLE);
        }
    }

    private void newRandomBlock(int numberToAdd)
    {
        int x;
        int y;

        for (int X = 0; X < numberToAdd; X++)
        {
            do
            {
                x = (int) ( Math.random() * 4 );
                y = (int) ( Math.random() * 4 );
            }
            while (taken[x][y]);

            Log.d("debug", "MAKING BLOCK AT (" + x + ", " + y + ")");
            grid[x][y] = rand2or4(0.90f);

            takenScan();
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
        Log.d("debug", "LOST STATE ACTIVE!");
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
                    Log.d("debug", "WIN STATE ACTIVE!");
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

    private void updateGridText()
    {
        gridTxt.setText(gridString);
        gridTaken.setText(takenString);
    }

    private double getUserScore()
    {
        double s = 10000 - difficulty * numberOfMoves * Math.log10(difficulty * timeElapsed);
        return (s >= 0) ? s : 0;
    }
}
