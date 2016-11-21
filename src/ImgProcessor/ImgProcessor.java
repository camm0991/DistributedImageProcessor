package ImgProcessor;

import Interface.ImageProcessorLink;
import Interface.Processor;
import MetaheuristicsAndTools.Tools.CalculatePower;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Carlos on 25/08/2014.
 */
public class ImgProcessor implements ImageProcessorLink, Processor {

    private int imagesProcessed = 0;
    private static double processingPower;
    private static String ip;
    private static String port;

    private ImgProcessor() {}

    public static void main(String[] p) {
        String[] args = {"localhost", "7002", "localhost", "8000"};
        try {
            if(args.length == 4) {
                //Self //
                processingPower = new CalculatePower().getPower();
                System.out.print("Initializing...");
                ip = args[0];
                port = args[1];
                System.setProperty("java.rmi.server.hostname", args[0]  );
                Registry registry = LocateRegistry.createRegistry(Integer.valueOf(args[1]));
                ImgProcessor imgProcessor = new ImgProcessor();
                ImgProcessor imgProcessor1 = new ImgProcessor();
                ImageProcessorLink processorLink = (ImageProcessorLink) UnicastRemoteObject.exportObject(imgProcessor, 0);
                Processor processor = (Processor) UnicastRemoteObject.exportObject(imgProcessor1, 0);
                registry.bind("ImageProcessorLink", processorLink);
                registry.bind("Processor", processor);
                //Server
                Registry registry1 = LocateRegistry.getRegistry(args[2], Integer.valueOf(args[3]));
                Processor processor1 = (Processor) registry1.lookup("Processor");

                processor1.processorAddress(args[0], Integer.valueOf(args[1]));
                System.out.println("Done\nReady to work");
            } else {
                System.out.println("Expected more params");
                System.out.println("ImgProcessor reploidIP reploidPort ServerIP ServerPort");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] imageNegative(byte[] img) throws RemoteException {
        ByteArrayInputStream inputStream;
        ByteArrayOutputStream outputStream;
        BufferedImage bufferedImage;

        try {
            inputStream = new ByteArrayInputStream(img);
            bufferedImage = new ImageFilters().ToNegative(ImageIO.read(inputStream));
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            System.out.println("Jobs done: " + String.valueOf(++imagesProcessed));
            return outputStream.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] imageGreyScale(byte[] img) throws RemoteException {
        ByteArrayInputStream inputStream;
        ByteArrayOutputStream outputStream;
        BufferedImage bufferedImage;

        try {
            inputStream = new ByteArrayInputStream(img);
            bufferedImage = new ImageFilters().ToGreyScale(ImageIO.read(inputStream));
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            System.out.println("Jobs done: " + String.valueOf(++imagesProcessed));
            return outputStream.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] imageSepia(byte[] img) throws RemoteException {
        ByteArrayInputStream inputStream;
        ByteArrayOutputStream outputStream;
        BufferedImage bufferedImage;

        try {
            inputStream = new ByteArrayInputStream(img);
            bufferedImage = new ImageFilters().ToSepia(ImageIO.read(inputStream));
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            System.out.println("Jobs done: " + String.valueOf(++imagesProcessed));
            return outputStream.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double processorPower() throws RemoteException {
        return processingPower;
    }

    @Override
    public String processorId() throws RemoteException {
        return (ip + ":" + port);
    }

    @Override
    public void processorAddress(String ip, Integer port) throws RemoteException {}

}