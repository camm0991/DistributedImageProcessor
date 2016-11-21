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
public class ParallelProcessing extends Thread {
    private DefaultListModel imageLIst;
    private byte[] buffer;
    private ImageProcessorLink imgLink;
    private String filterName;

    public ParallelProcessing(DefaultListModel imageLIst, byte[] buffer, ImageProcessorLink imgLink, String filterName) {
        this.imageLIst = imageLIst;
        this.buffer = buffer;
        this.imgLink = imgLink;
        this.filterName = filterName;
    }

    public void run() {
        try {
            Method m = ImageProcessorLink.class.getMethod(filterName, byte[].class);
            BufferedImage imageOutput = ImageIO.read(new ByteArrayInputStream((byte[]) m.invoke(imgLink, buffer)));
            imageLIst.addElement(imageOutput);
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
