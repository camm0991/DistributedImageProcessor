package ClientGUI;

import Interface.ImageProcessorLink;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cam on 15/10/14.
 */
class ParallelProcessing extends Thread {
    private DefaultListModel<BufferedImage> imageList;
    private byte[] buffer;
    private ImageProcessorLink imgLink;
    private String filterName;

    ParallelProcessing(DefaultListModel<BufferedImage> imageLIst, byte[] buffer, ImageProcessorLink imgLink, String filterName) {
        this.imageList = imageLIst;
        this.buffer = buffer;
        this.imgLink = imgLink;
        this.filterName = filterName;
    }

    public void run() {
        try {
            Method m = ImageProcessorLink.class.getMethod(filterName, byte[].class);
            BufferedImage imageOutput = ImageIO.read(new ByteArrayInputStream((byte[]) m.invoke(imgLink, (Object) buffer)));
            imageList.addElement(imageOutput);
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
