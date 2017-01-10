package Logic;

/**
 * Created by Adam on 04/01/2017.
 */
public class Pos {
    public int x, y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        Pos other = (Pos) obj;
        if (other != null) {
            return other.x == this.x && other.y == this.y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pos{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
