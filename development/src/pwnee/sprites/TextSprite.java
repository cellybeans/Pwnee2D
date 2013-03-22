package pwnee.sprites;

import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;
import pwnee.image.*;
import pwnee.text.BlitteredFont;

public class TextSprite extends Sprite {
  public BlitteredFont bfont;
  
  public String text = "";
  
  public TextSprite(double x, double y, BlitteredFont bf, String txt) {
    super(x,y);
    bfont = bf;
    text = txt;
  }
  
  public void draw(Graphics2D g) {
    bfont.renderString(g, text);
  }
}