package ca.bischke.apps.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Random;

@SuppressLint("ViewConstructor")

public class Board extends TableLayout
{
    private Random random = new Random();
    private Cell[][] cells;
    private int columns;
    private int rows;
    private int mines;
    private boolean firstTurn;
    private boolean gameOver;

    public Board(Context context, int columns, int rows, int buttonWidth)
    {
        super(context);
        this.columns = columns;
        this.rows = rows;
        this.cells = new Cell[columns][rows];
        mines = (rows * columns) / 5;
        firstTurn = true;
        gameOver = false;

        // Creates Grid of Cells
        for (int i = 0; i < rows; i++)
        {
            TableRow row = new TableRow(context);
            addView(row);

            for (int j = 0; j < columns; j++)
            {
                LinearLayout layout = new LinearLayout(context);
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(buttonWidth, buttonWidth);

                // Creates Cell with X = j, Y = i
                final Cell cell = new Cell(context, new Coordinate(j, i));
                cell.setLayoutParams(buttonLayoutParams);

                cell.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Cell cell = ((Cell)view);

                        if (firstTurn)
                        {
                            placeMines(cell);
                            calculateNearbyMines();

                            firstTurn = false;
                        }

                        if (!gameOver)
                        {
                            if (!cell.isFlag())
                            {
                                cell.setActive(true);

                                if (cell.isMine())
                                {
                                    displayAllMines();
                                    gameOver = true;
                                }
                                else
                                {
                                    if (cell.getNearbyMines() == 0)
                                    {
                                        activateNeighbours(cell);
                                    }
                                    else
                                    {
                                        cell.displayText();
                                    }

                                    cell.setActive(false);
                                    cell.setColor(R.color.colorInactive);
                                }
                            }
                        }
                    }
                });

                cell.setOnLongClickListener(new OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        Cell cell = ((Cell)view);

                        if (cell.isActive())
                        {
                            cell.setFlag(!cell.isFlag());
                        }

                        return true;
                    }
                });

                cells[j][i] = cell;
                layout.addView(cells[j][i]);
                row.addView(layout);
            }
        }
    }

    public ArrayList<Cell> getNeighbours(Cell cell)
    {
        ArrayList<Cell> neighbours = new ArrayList<>();
        Coordinate coordinate = cell.getCoordinate();
        int i = coordinate.getX();
        int j = coordinate.getY();

        if (j > 0)
        {
            neighbours.add(cells[i][j - 1]);
        }

        // Bottom
        if (j < rows - 1)
        {
            neighbours.add(cells[i][j + 1]);
        }

        // Left
        if (i > 0)
        {
            neighbours.add(cells[i - 1][j]);

            // Top Left Corner
            if (j > 0)
            {
                neighbours.add(cells[i - 1][j - 1]);
            }

            // Bottom Left Corner
            if (j < rows - 1)
            {
                neighbours.add(cells[i - 1][j + 1]);
            }
        }

        // Right
        if (i < columns - 1)
        {
            neighbours.add(cells[i + 1][j]);

            // Top Right Corner
            if (j > 0)
            {
                neighbours.add(cells[i + 1][j - 1]);
            }

            // Bottom Right Corner
            if (j < rows - 1)
            {
                neighbours.add(cells[i + 1][j + 1]);
            }
        }

        return neighbours;
    }

    public void activateNeighbours(Cell cell)
    {
        Coordinate coordinate = cell.getCoordinate();
        int i = coordinate.getX();
        int j = coordinate.getY();

        ArrayList<Cell> neighbours = getNeighbours(cells[i][j]);

        for (Cell neighbor : neighbours)
        {
            if (neighbor.isActive())
            {
                neighbor.setActive(false);
                neighbor.setColor(R.color.colorInactive);

                if (neighbor.getNearbyMines() == 0)
                {
                    activateNeighbours(neighbor);
                }
                else
                {
                    neighbor.displayText();
                }
            }
        }
    }

    public void placeMines(Cell cell)
    {
        Coordinate coordinate = cell.getCoordinate();
        ArrayList<Cell> invalid = getNeighbours(cells[coordinate.getX()][coordinate.getY()]);
        invalid.add(cell);

        for (int i = 0; i < mines; i++)
        {
            boolean valid = false;

            while(!valid)
            {
                int x = random.nextInt(columns);
                int y = random.nextInt(rows);

                if (!invalid.contains(cells[x][y]) && !cells[x][y].isMine())
                {
                    cells[x][y].setMine(true);
                    valid = true;
                }
            }
        }
    }

    public void calculateNearbyMines()
    {
        for (int i = 0; i < columns; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                ArrayList<Cell> neighbours = getNeighbours(cells[i][j]);
                int nearbyMines = 0;

                for (Cell neighbor : neighbours)
                {
                    if (neighbor.isMine())
                    {
                        nearbyMines += 1;
                    }
                }

                cells[i][j].setNearbyMines(nearbyMines);
            }
        }
    }

    public void displayAllMines()
    {
        for (int i = 0; i < columns; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                if (cells[i][j].isMine())
                {
                    cells[i][j].setColor(R.color.colorMine);
                }
            }
        }
    }
}


@SuppressLint("ViewConstructor")

class Cell extends AppCompatButton
{
    private Coordinate coordinate;
    private boolean active;
    private boolean flag;
    private boolean mine;
    private int nearbyMines;

    Cell(Context context, Coordinate coordinate)
    {
        super(context);
        active = true;
        flag = false;
        mine = false;
        this.coordinate = coordinate;
        setColor(R.color.colorBoard);
        setPadding(0, 0, 0, 0);
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;

        if (flag)
        {
            setColor(R.color.colorFlag);
        }
        else
        {
            setColor(R.color.colorBoard);
        }
    }

    public boolean isMine()
    {
        return mine;
    }

    public void setMine(boolean mine)
    {
        this.mine = mine;
    }

    public int getNearbyMines()
    {
        return nearbyMines;
    }

    public void setNearbyMines(int nearbyMines)
    {
        this.nearbyMines = nearbyMines;
    }

    public void setColor(int color)
    {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(4, getResources().getColor(R.color.colorStroke));
        drawable.setColor(getResources().getColor(color));
        setBackgroundDrawable(drawable);
    }

    public void displayText()
    {
        setText(String.valueOf(nearbyMines));
    }
}