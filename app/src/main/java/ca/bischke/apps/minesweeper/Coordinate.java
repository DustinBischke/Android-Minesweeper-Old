package ca.bischke.apps.minesweeper;

public class Coordinate
{
    private int x;
    private int y;

    public Coordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Coordinate))
        {
            return false;
        }

        Coordinate c = (Coordinate) object;

        return (this.x == c.getX() && this.y == c.getY());
    }
}
