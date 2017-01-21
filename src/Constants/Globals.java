package Constants;

import Logic.Pos;
import Logic.Wall;

import java.util.ArrayList;

/**
 * Created by Adam on 09/01/2017.
 */
public final class Globals {

    public static ArrayList<Wall> wallList;
    public int nextFurnitureIndex;
    private int delayTime;

    public Globals() {

        delayTime = 25;
        nextFurnitureIndex = 1;

        wallList = new ArrayList<Wall>();
        Pos w1p1 = new Pos(7,0);
        Pos w1p2 = new Pos(8,0);
        wallList.add(new Wall(w1p1,w1p2));

        Pos w2p1 = new Pos(7,4);
        Pos w2p2 = new Pos(8,4);
        wallList.add(new Wall(w2p1,w2p2));

        Pos w3p1 = new Pos(7,5);
        Pos w3p2 = new Pos(8,5);
        wallList.add(new Wall(w3p1,w3p2));

        Pos w4p1 = new Pos(7,11);
        Pos w4p2 = new Pos(8,11);
        wallList.add(new Wall(w4p1,w4p2));

        Pos w5p1 = new Pos(0,4);
        Pos w5p2 = new Pos(0,5);
        wallList.add(new Wall(w5p1,w5p2));

        Pos w6p1 = new Pos(1,4);
        Pos w6p2 = new Pos(1,5);
        wallList.add(new Wall(w6p1,w6p2));

        Pos w7p1 = new Pos(6,4);
        Pos w7p2 = new Pos(6,5);
        wallList.add(new Wall(w7p1,w7p2));

        Pos w8p1 = new Pos(7,4);
        Pos w8p2 = new Pos(7,5);
        wallList.add(new Wall(w8p1,w8p2));
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
}
