package Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

	public static BufferedImage loadImage(String path) {
		File imageFile = new File(path);
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return image;
	}
	
//    public static BufferedImage getTexturedImage(
//            BufferedImage src, Shape shp, int x, int y) {
//        Rectangle r = shp.getBounds();
//        // create a transparent image with 1 px padding.
//        BufferedImage tmp = new BufferedImage(
//                r.width+2,r.height+2,BufferedImage.TYPE_INT_ARGB);
//        // get the graphics object
//        Graphics2D g = tmp.createGraphics();
//        // set some nice rendering hints
//        g.setRenderingHint(
//                RenderingHints.KEY_ANTIALIASING, 
//                RenderingHints.VALUE_ANTIALIAS_ON);
//        g.setRenderingHint(
//                RenderingHints.KEY_COLOR_RENDERING, 
//                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//        // create a transform to center the shape in the image
//        AffineTransform centerTransform = AffineTransform.
//                getTranslateInstance(-r.x+1, -r.y+1);
//        // set the transform to the graphics object
//        g.setTransform(centerTransform);
//        // set the shape as the clip
//        g.setClip(shp);
//        // draw the image
//        g.drawImage(src, x, y, null);
//        // clear the clip
//        g.setClip(null);
//        // draw the shape as an outline
//        g.setColor(Color.RED);
//        g.setStroke(new BasicStroke(1f));
//        g.draw(shp);
//        // dispose of any graphics object we explicitly create
//        g.dispose();
//
//        return tmp;
//    }
}
