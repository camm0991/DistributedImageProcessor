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
    
    BufferedImage ToGreyScale(BufferedImage image){
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int colorAverage = (color.getRed() + color.getGreen() + color.getBlue())/3;
                image.setRGB(x, y, new Color(colorAverage, colorAverage, colorAverage).getRGB());
            }
        }
        return image;
    }

    BufferedImage ToSepia(BufferedImage image){
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                Double sepiaR = (0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue());
                Double sepiaG = (0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue());
                Double sepiaB = (0.272 * color.getRed() + 0.534 * color.getGreen() + 0.131 * color.getBlue());
                int sepiaRValue = (sepiaR > 255.0d) ? 255 : sepiaR.intValue();
                int sepiaGValue = (sepiaG > 255.0d) ? 255 : sepiaG.intValue();
                int sepiaBValue = (sepiaB > 255.0d) ? 255 : sepiaB.intValue();
                image.setRGB(x, y, new Color(sepiaRValue, sepiaGValue, sepiaBValue).getRGB());
            }
        }
        return image;
    }
}
