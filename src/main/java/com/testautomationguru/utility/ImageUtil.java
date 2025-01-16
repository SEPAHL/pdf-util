package com.testautomationguru.utility;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

class ImageUtil {
	
	static Logger logger = Logger.getLogger(ImageUtil.class.getName());
	
	static boolean compareAndHighlight(final BufferedImage img1, final BufferedImage img2, String fileName, boolean highlight, int colorCode) throws IOException {
		return compareAndHighlight(img1, img2, fileName, highlight, colorCode, null, false, -1);
	}
	
    static boolean compareAndHighlight(final BufferedImage img1, final BufferedImage img2, String fileName, boolean highlightDifferences, int colorCodeDifferences, Rectangle[] excludedAreas, boolean highlightExcludedAreas, int colorCodeExcludedAreas) throws IOException {

        final int w = img1.getWidth();
        final int h = img1.getHeight();

        // Convert images to RGB arrays
        final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
        final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);

        boolean matches = true;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int index = y * w + x;

                // Check if the pixel is in any of the excluded areas
                boolean inExcludedArea = false;
                if(null != excludedAreas && excludedAreas.length > 0) {
	                for (Rectangle rect : excludedAreas) {
	                    if (rect.contains(x, y)) {
	                        inExcludedArea = true;
	                        break;
	                    }
	                }
                }

                // Skip comparison if pixel is in an excluded area
                if (inExcludedArea) {
                	if(highlightExcludedAreas) {
                		p1[index] = colorCodeExcludedAreas; // Highlight excluded pixel
                	}
                    continue;
                }

                // Compare pixels
                if (p1[index] != p2[index]) {
                    matches = false;
                    if (highlightDifferences) {
                        p1[index] = colorCodeDifferences; // Highlight differing pixel
                    }
                }
            }
        }

        if (!matches) {
            logger.warning("Image compared - does not match");
            if (highlightDifferences) {
                final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                out.setRGB(0, 0, w, h, p1, 0, w);
                saveImage(out, fileName);
            }
        }

        return matches;
    }

	static void saveImage(BufferedImage image, String file){
		try{
			File outputfile = new File(file);
			ImageIO.write(image, "png", outputfile);	
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
