package pwnee.image;

/*======================================================================
 * 
 * Pwnee - A lightweight 2D Java game engine
 * 
 * Copyright (c) 2012 by Stephen Lindberg (sllindberg21@students.tntech.edu)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
======================================================================*/

import java.awt.MediaTracker;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

/** A class that can force the application to wait while it finishes loading new Images. */
public class ImageLoader {
   
   /** This is the object that actually waits on the images. */
	private MediaTracker mt;
   
   /** Used by mt to track the images it is waiting on. */
	private int nextId;
   
   /** This will pretty much always be your application's GamePanel class. */
	private Component parent;
	
   /** Creates an empty ImageLoader assigned to a rendering Component. */
	public ImageLoader(Component parent) {
		this.parent = parent;
		this.reset();
	}
   
   /** Creates an ImageLoader used only for loading images from a file. This doesn't initialize a MediaTracker and is only used by this class's static methods. */
   public ImageLoader() {
      // Do nothing.
   }
	
   /** Empties the ImageLoader. */
	public void reset() {
		mt = new MediaTracker(this.parent);
		nextId = 0;
	}
	
   /** Adds an Image to the ImageLoader for it to wait on. */
	public void addImage(Image img) {
		mt.addImage(img, this.nextId);
		this.nextId++;
	}
	
   /** Forces the application to wait while the ImageLoader finishes loading all the Images currently assigned to it. Afterwards it empties itself.*/
	public void waitForAll() {
		try {
			mt.waitForAll();	// wait for the filtered image to load before drawing anything.
		}
		catch ( InterruptedException e ) {
         System.err.println("ImageLoader - failed to load an Image.");
			Thread.currentThread().interrupt();
		}
		reset();
	}
	
   
   /** A static method used to load an image from a file and return that image. */
   public Image loadFromFile(String path) {
      URL imageURL =  this.getClass().getClassLoader().getResource(path);
      Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
      return image;
   }
	
}

