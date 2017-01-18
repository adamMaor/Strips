package Logic;

/**
 * Created by Laptop on 01/17/17.
 */
public class Diff {
    int tlx;
    int tly;
    int brx;
    int bry;

    public int getTlx() {
        return tlx;
    }

    public void setTlx(int tlx) {
        this.tlx = tlx;
    }

    public int getTly() {
        return tly;
    }

    public void setTly(int tly) {
        this.tly = tly;
    }

    public int getBrx() {
        return brx;
    }

    public void setBrx(int brx) {
        this.brx = brx;
    }

    public int getBry() {
        return bry;
    }

    public void setBry(int bry) {
        this.bry = bry;
    }



    public Diff(int tlx, int tly, int brx, int bry) {
        this.tlx = tlx;
        this.tly = tly;
        this.brx = brx;
        this.bry = bry;
    }

    public Diff(FurnitureLocation destination, FurnitureLocation origin) {
        this.tlx = destination.tl.x - origin.tl.x;
        this.tly = destination.tl.y - origin.tl.y;
        this.brx = destination.br.x - origin.br.x;
        this.bry = destination.br.y - origin.br.y;
    }

    public Diff(Diff desired, Diff current) {
        this.tlx = desired.getTlx() - current.getTlx();
        this.tly = desired.getTly() - current.getTly();
        this.brx = desired.getBrx() - current.getBrx();
        this.bry = desired.getBry() - current.getBry();
    }

    @Override
    public String toString() {
        return "Diff{" +
                "tlx=" + tlx +
                ", tly=" + tly +
                ", brx=" + brx +
                ", bry=" + bry +
                '}';
    }
}
