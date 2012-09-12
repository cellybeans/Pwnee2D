package pwnee.sprites;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import pwnee.GameMath;

/** Creates a fake 3D effect in which a source image is projected onto a tilted plane. */
public abstract class Mode7Sprite extends BlitterSprite {
	/** The angle in degrees of the direction that fake Mode7 camera is facing. */
   public double cameraAngle = 0;
   
   /** focal X coordinate of the Mode7 camera. */
	public double cameraX = 0;
   
   /** focal Y coordinte of the Mode7 camera. */
   public double cameraY = 0;
   
   /** The elevation of the camera from the Mode7-tilted source image. */
   public double elevation = 50;
   
   /** The distance of the camera from its focal coordinates. */
   public double camDist = 50;
   
   /** An array of colors used to display the camera's focal point if showCamPoint is true. */
	protected int[] camColors = {0xFFFFCCCC, 0xFFCCFFCC,0xFFCCCCFF};
   
   /** The current index into camColors. */
	protected int curCamColor = 0;
	
   /** The y-position of the Mode7-tilted source image's horizon relative to the top edge of the Sprite. */
	public double horizonY = 0;
   
   /** Flag for displaying the camera's focal point as a square of flashing pixels. */
	public boolean showCamPoint = false;
	
   // private reused variables for the Mode7 transformation.
   protected double camDx = 0;
   protected double camDy = 0;
   protected double camCos = 1;
   protected double camSin = 0;
   protected double aspectRatio = 1;
   protected double horizonCenter = 0;
   
	// CONSTRUCTOR
	
	public Mode7Sprite(Image srcImg, double x, double y, double w, double h) {
		super(srcImg, x,y,w,h);
	}
	
	// RENDERING METHODS
	
   
	protected void draw(Graphics2D g) {
		BufferedImage mode7Img = new BufferedImage((int) width,(int) height - 1,BufferedImage.TYPE_INT_ARGB);
		int[] writePixels = ((DataBufferInt)(mode7Img.getRaster().getDataBuffer())).getData(); 
		
		// obtain the source image's pixels and its width/height
		grabPixels();
		
		// compute any values that will be reused for each pixel.
		computeReusedValues();
		
      // process each pixel in the sprite's viewport.
		for(int j = 1; j < height; j++) {	
			for(int i = 0; i < width; i++) {	
				// transform our pixel coordinates. 
            int[] pix = getPixelAt(i,j,camDx,camDy,rCos,rSin,horizonCenter,aspectRatio);
				int pixX = pix[0];
				int pixY = pix[1];
				
            // check if this pixel is near the camera focal point.
				boolean isCamPoint = false;
				if(Math.abs(pixX - cameraX) < 10 && Math.abs(pixY - cameraY) < 10)
					isCamPoint = true;
            
            // default color is transparent black if we map to an invalid pixel.
            int pixelColor = 0x00000000;
            
            // apply image wrapping if our flags are true.
            if(srcWrapX) {
               pixX = pix.x % pixWidth;
               if(pixX < 0)
                  pixX = pixWidth+pixX;
            }
            if(srcWrapY) {
               pixY = pix.y % pixHeight;
               if(pixY < 0)
                  pixY = pixHeight+pixY;
            }
				
            // get our pixel if it is within our source image's bounds.
				if(pixX >= 0 && pixX < pixWidth && pixY >= 0 && pixY < pixHeight) 
               pixelColor =  pixels[pixY*pixWidth + pixX];
            
            // write this pixel to our result image.
				try {
					if(!showCamPoint || !isCamPoint)
						writePixels[(int)width*(j-1)+i] = pixelColor;
					else
						writePixels[(int)width*(j-1)+i] = camColor;
				}
				catch(Exception ex) {
					System.out.println("Mode7Sprite - error writing pixels to result image");
					ex.printStackTrace();
				}
			}
		}

		// draw the resulting mode7 image onto the scene.
		g.drawImage(mode7Img, null, null);
	}
	
   /** Computes the translation and rotation values for the mode7 transform. Also iterates through the camera point colors. */
   public void computeReusedValues() {
      double camDx = cameraX - camDist*GameMath.cos(cameraAngle);
		double camDy = cameraY + camDist*GameMath.sin(cameraAngle);
		
		double rCos = GameMath.cos(180-cameraAngle+90);
		double rSin = GameMath.sin(180-cameraAngle+90);
		
		int camColor = camColors[curCamColor];
		curCamColor++;
		
		//AffineTransform rotation = AffineTransform.getRotateInstance(GameMath.d2r(180-cameraAngle+90));
		double aspectRatio = width/height;
		double horizonCenter = width/2.0;
		
		if(curCamColor > 2)
			curCamColor = 0;
   }
   
   /** Performs a transformation to produce a tilted image plane effect. */
	public int[] transformPixel(double i,double j)
	{
		double z = (elevation)/(j+horizonY);
		double rasterX = (horizonCenter - i)*z;
		
		int pixX = (int) (rasterX);// % pixWidth;
		int pixY = (int) (elevation*z*aspectRatio);// % pixHeight;
		
		int rpixX = (int)(pixX*camCos - pixY*camSin + camDx);
		int rpixY = (int)(pixX*camSin + pixY*camCos + camDy);
		return {rpixX,rpixY};
	}
}

