package Strips;

import Logic.Furniture;

/**
 * Created by Laptop on 01/11/17.
 */
public interface StripsOperator extends StripsObject{
    Furniture getFurniture();
    byte getDirection();
}
