package ImgProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author Carlos Martinez
 */
public class ImageFilters {

    public BufferedImage ToNegative(BufferedImage Bimg){
        long time0 = System.nanoTime();
        //BufferedImage bi = new BufferedImage(Bimg.getWidth(), Bimg.getHeight(), Bimg.getType());
        for (int x = 0; x < Bimg.getWidth(); x++) {
            for (int y = 0; y < Bimg.getHeight(); y++) {
                Color c = new Color(Bimg.getRGB(x, y));
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                r=255-r;
                g=255-g;
                b=255-b;
                Bimg.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        System.out.println((System.nanoTime() - time0) / 1000000000.0);
        return Bimg;
    }
    
    public BufferedImage ToGreyScale(BufferedImage Bimg){
        long time0 = System.nanoTime();
        BufferedImage bi = new BufferedImage(Bimg.getWidth(), Bimg.getHeight(), Bimg.getType());
        for (int x = 0; x < Bimg.getWidth(); x++) {
            for (int y = 0; y < Bimg.getHeight(); y++) {
                Color c = new Color(Bimg.getRGB(x, y));
                int px = (c.getRed() + c.getGreen() + c.getBlue())/3;
                bi.setRGB(x, y, new Color(px, px, px).getRGB());
            }
        }
        System.out.println((System.nanoTime() - time0) / 1000000000.0);
        return bi;
    }
}
