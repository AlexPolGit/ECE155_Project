package ca.uwaterloo.ece155_lab4.utils;

import android.content.Context;
import android.util.Log;

import ca.uwaterloo.ece155_lab4.GameBlock;

public class GameManager
{
    private Context context;

    public final int RIGHT = 1;
    public final int LEFT = 2;
    public final int UP = 3;
    public final int DOWN = 4;

    public static GameBlock[][] board;
    public static GameBlock testBlock;

    public GameManager(Context c)
    {
        context = c;
        board = new GameBlock[4][4];

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                board[i][j] = null;
            }
        }

        int randX = (int) (Math.random() * 3);
        int randY = (int) (Math.random() * 3);

        board[randX][randY] = new GameBlock(context, randX, randY);
        testBlock = board[randX][randY];
    }

    private void mergeBlocks(GameBlock movedBlock, GameBlock recieverBlock)
    {
        GameBlock newBlock = new GameBlock(context, recieverBlock.getGridX(), recieverBlock.getGridY());
        newBlock.setValue(movedBlock.getValue() + recieverBlock.getValue());

        // remove references to the old blocks
        movedBlock = new GameBlock(context, Integer.MAX_VALUE, Integer.MAX_VALUE);
        recieverBlock = new GameBlock(context, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public String getStringOfBoardValues()
    {
        String b = "";
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (board[i][j] != null)
                {
                    b += String.format("(%d) ", board[i][j].getValue());
                }
                else
                {
                    b += "(0) ";
                }
            }
            b += '\n';
        }
        return b;
    }

    private void slideBlock(GameBlock toMove, int dir)
    {
        int x = toMove.getGridX();
        int y = toMove.getGridY();

        switch (dir)
        {
            case RIGHT:
            {
                for (int i = x; i < 4; i++)
                {
                    if (board[i][y].getValue() != 0)
                    {
                        if (toMove.getValue() == board[i][y].getValue())
                        {
                            mergeBlocks(toMove, board[i][y]);
                        }
                        else
                        {
                            board[i][y] = toMove;
                            toMove.setGridX(i);
                            toMove.setGridY(y);
                        }
                        break;
                    }
                }
            }
            case LEFT:
            {
                for (int i = x; i > -1; i--)
                {
                    if (board[i][y].getValue() != 0)
                    {
                        if (toMove.getValue() == board[i][y].getValue())
                        {
                            mergeBlocks(toMove, board[i][y]);
                        }
                        else
                        {
                            board[i][y] = toMove;
                            toMove.setGridX(i);
                            toMove.setGridY(y);
                        }
                        break;
                    }
                }
            }
            case UP:
            {
                for (int i = y; i < 4; i++)
                {
                    if (board[x][i].getValue() != 0)
                    {
                        if (toMove.getValue() == board[x][i].getValue())
                        {
                            mergeBlocks(toMove, board[x][i]);
                        }
                        else
                        {
                            board[x][i] = toMove;
                            toMove.setGridX(x);
                            toMove.setGridY(i);
                        }
                        break;
                    }
                }
            }
            case DOWN:
            {
                for (int i = y; i > -1; i--)
                {
                    if (board[x][i].getValue() != 0)
                    {
                        if (toMove.getValue() == board[x][i].getValue())
                        {
                            mergeBlocks(toMove, board[x][i]);
                        }
                        else
                        {
                            board[x][i] = toMove;
                            toMove.setGridX(x);
                            toMove.setGridY(i);
                        }
                        break;
                    }
                }
            }
            default:
            {
                Log.d("debug1", "Unable to move! Illegal direction.");
            }
        }
    }
}