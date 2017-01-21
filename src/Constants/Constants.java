package Constants;

import java.awt.*;

/**
 * Created by Adam on 09/01/2017.
 */
public class Constants {

    public static class Sizes {

        public static final byte boardHeight = 12;
        public static final byte boardWidth = 20;
        public static final int normalBorderWidth = 1;
        public static final int wallBorderWidth = 2;
        public static final int ThickBorderWidth = 5;
    }

    public class Directions {
        public static final byte UP = 0;
        public static final byte DOWN = 1;
        public static final byte LEFT = 2;
        public static final byte RIGHT = 3;
        // None is used to check the current furniture area (new furniture for example)
        public static final byte NONE = 4;

    }

    public static class Colors {
        public static final Color MainBoardItemColor = new Color(220,220,220);
        public static final Color MainBoardFurnitureColor = new Color(40,50,200);
        public static final Color WallColor = new Color(255,0,0);
        public static final Color StandardBorderColor = new Color(80,80,80);

    }

    public static class Numbers {
        public static final int ENCSIZE = 19;
        public static final int MAX_MOVES_PER_TRY = 10000;
        public static final int MAX_TRIES = 5;

    }


}
