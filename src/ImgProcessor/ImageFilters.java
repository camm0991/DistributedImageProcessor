package ImgProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author Carlos Martinez
 */
public class ImageFilters {

    BufferedImage ToNegative(BufferedImage image){
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                image.setRGB(x, y, new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()).getRGB());
            }
        }
        return image;
    }
    
    BufferedImage ToGreyScale(BufferedImage Bimg){
        BufferedImage bi = new BufferedImage(Bimg.getWidth(), Bimg.getHeight(), Bimg.getType());
        for (int x = 0; x < Bimg.getWidth(); x++) {
            for (int y = 0; y < Bimg.getHeight(); y++) {
                Color c = new Color(Bimg.getRGB(x, y));
                int px = (c.getRed() + c.getGreen() + c.getBlue())/3;
                bi.setRGB(x, y, new Color(px, px, px).getRGB());
            }
        }
        return bi;
    }

    BufferedImage ToSepia(BufferedImage image){
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                Double sepiaR = (0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue());
                Double sepiaG = (0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue());
                Double sepiaB = (0.272 * color.getRed() + 0.534 * color.getGreen() + 0.131 * color.getBlue());
                int sepiaRValue = (sepiaR > 255.0) ? 255 : sepiaR.intValue();
                int sepiaGValue = (sepiaB > 255.0) ? 255 : sepiaG.intValue();
                int sepiaBValue = (sepiaB > 255.0) ? 255 : sepiaB.intValue();
                image.setRGB(x, y, new Color(sepiaRValue, sepiaGValue, sepiaBValue).getRGB());
            }
        }
        return image;
    }
}
