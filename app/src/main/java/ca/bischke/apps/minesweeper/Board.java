package ca.bischke.apps.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

@SuppressLint("ViewConstructor")

public class Board extends TableLayout
{
    private Cell[][] cells;
    private int rows;
    private int columns;

    public Board(Context context, int rows, int columns, int buttonWidth)
    {
        super(context);
        this.rows = rows;
        this.columns = columns;
        this.cells = new Cell[columns][rows];

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

                cells[j][i] = cell;
                layout.addView(cells[j][i]);
                row.addView(layout);
            }
        }
    }
}


@SuppressLint("ViewConstructor")

class Cell extends AppCompatImageButton
{
    private Coordinate coordinate;
    boolean clicked = false;

    Cell(Context context, Coordinate coordinate)
    {
        super(context);
        this.coordinate = coordinate;
        setColor(R.color.colorBoard);
        setPadding(0, 0, 0, 0);
        setScaleType(ScaleType.FIT_CENTER);
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public boolean isClicked()
    {
        return clicked;
    }

    public void setClicked(boolean clicked)
    {
        this.clicked = clicked;
    }

    public void setColor(int color)
    {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(4, getResources().getColor(R.color.colorStroke));
        drawable.setColor(getResources().getColor(color));
        setBackgroundDrawable(drawable);
    }

    public void setImage(int image)
    {
        setImageResource(image);
    }
}