package Logic;

/**
 * Created by Adam on 09/01/2017.
 */
public class Wall {
    public Pos first, second; // a wall exists between 2 adjacent positions - left,right or up,down

    public Wall(Pos first, Pos second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        Wall other = (Wall) obj;
        // convention is Right,Left or Up,Down - so maybe need to swap
        if (other != null) {
            if ((other.first.equals(this.first) && other.second.equals(this.second))
                    || (other.second.equals(this.first) && other.first.equals(this.second))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Wall{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
