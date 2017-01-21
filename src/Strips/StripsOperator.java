package Strips;

import Logic.Furniture;

import java.util.ArrayList;

/**
 * Represent a Strips Operator - Move or Rotate
 * Created by Laptop on 01/11/17.
 */
public interface StripsOperator extends StripsObject{
    Furniture getFurniture();
    byte getDirection();
}
