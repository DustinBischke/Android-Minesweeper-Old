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

                                    if (allMinesFound())
                                    {
                                        win();
                                        gameOver = true;
                                    }
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

    public int getMines()
    {
        return mines;
    }

    public ArrayList<Cell> getNeighbours(Cell cell)
    {
        ArrayList<Cell> neighbours = new ArrayList<>();
        Coordinate coordinate = cell.getCoordinate();
        int x = coordinate.getX();
        int y = coordinate.getY();

        if (y > 0)
        {
            neighbours.add(cells[x][y - 1]);
        }

        // Bottom
        if (y < rows - 1)
        {
            neighbours.add(cells[x][y + 1]);
        }

        // Left
        if (x > 0)
        {
            neighbours.add(cells[x - 1][y]);

            // Top Left Corner
            if (y > 0)
            {
                neighbours.add(cells[x - 1][y - 1]);
            }

            // Bottom Left Corner
            if (y < rows - 1)
            {
                neighbours.add(cells[x - 1][y + 1]);
            }
        }

        // Right
        if (x < columns - 1)
        {
            neighbours.add(cells[x + 1][y]);

            // Top Right Corner
            if (y > 0)
            {
                neighbours.add(cells[x + 1][y - 1]);
            }

            // Bottom Right Corner
            if (y < rows - 1)
            {
                neighbours.add(cells[x + 1][y + 1]);
            }
        }

        return neighbours;
    }

    public void activateNeighbours(Cell cell)
    {
        Coordinate coordinate = cell.getCoordinate();
        int x = coordinate.getX();
        int y = coordinate.getY();

        ArrayList<Cell> neighbours = getNeighbours(cells[x][y]);

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

    public boolean allMinesFound()
    {
        for (int i = 0; i < columns; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                if (cells[i][j].isActive() && !cells[i][j].isMine())
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void win()
    {
        for (int i = 0; i < columns; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                if (cells[i][j].isMine())
                {
                    cells[i][j].setColor(R.color.colorWin);
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
        setTextSize(16);
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
            setText("F");
            setTextColor(getResources().getColor(R.color.colorFlag));
        }
        else
        {
            setText("");
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

    private int getTextColor()
    {
        int color = 0;

        switch(nearbyMines)
        {
            case 1: color = R.color.color1;
                break;
            case 2: color = R.color.color2;
                break;
            case 3: color = R.color.color3;
                break;
            case 4: color = R.color.color4;
                break;
            case 5: color = R.color.color5;
                break;
            case 6: color = R.color.color6;
                break;
            case 7: color = R.color.color7;
                break;
            case 8: color = R.color.color8;
                break;
        }

        return color;
    }

    public void displayText()
    {
        setText(String.valueOf(nearbyMines));
        setTextColor(getResources().getColor(getTextColor()));
    }
}