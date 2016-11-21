package ClientGUI;

import ImgProcessor.ImageFilters;
import Interface.CallServer;
import Interface.ImageProcessorLink;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 * Created by carlos.martinez on 11/12/2016.
 */
public class ClientGUI {

    private JPanel mainPanel;
    private JButton saveImagesButton;
    private JButton loadImagesButton;
    private JButton processImagesButton;
    private JTextField serverIPValue;
    private JTextField serverPortValue;
    private JComboBox filterList;
    private JLabel serverIPLabel;
    private JLabel portLabel;
    private JLabel filterLabel;
    private JList loadedImagesList;
    private JList processedImagesList;
    private JLabel unprocessedImageBox;
    private JButton serverTestConnectionButton;
    private JLabel serverConnectionMessage;
    private JLabel processedImageBox;

    private ClientGUI() {

        final DefaultListModel<Path> imgPathList = new DefaultListModel<>();
        final DefaultListModel<BufferedImage> bufferedImageList = new DefaultListModel<>();
        final boolean[] serverLocated = {false};
        final CallServer[] serverLink = {null};

        loadedImagesList.setModel(imgPathList);
        processedImagesList.setModel(bufferedImageList);

        for (Method i : ImageFilters.class.getDeclaredMethods()) {
            filterList.addItem(i.getName());
        }


        loadImagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setFileFilter(new FileNameExtensionFilter("JPG, PNG & GIF", "jpg", "png", "gif"));
                    if (!chooser.isMultiSelectionEnabled()) {
                        chooser.setMultiSelectionEnabled(true);
                    }
                    int r = chooser.showOpenDialog(null);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        File[] files = chooser.getSelectedFiles();
                        for (File file : files) {
                            imgPathList.addElement(file.toPath());
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        //Apply the specified filter to the images from the loadedImagesList
        processImagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Checks if the server has been found and the image list is not empty
                if (imgPathList.isEmpty() || !serverLocated[0]) {
                    return;
                }
                try {
                    ArrayList<Integer> weights = new ArrayList<>();
                    for (int i = 0; i < imgPathList.size(); i++) {
                        weights.add(Files.readAllBytes(imgPathList.getElementAt(i)).length >> 1);
                    }
                    ArrayList<Remote> assignments = new ArrayList<>(serverLink[0].getProcessor(weights));
                    for (int index = 0; index < imgPathList.size(); index++) {
                        String filterName = filterList.getSelectedItem().toString();
                        filterName = "image" + filterName.substring(2, filterName.length());
                        (new ParallelProcessing(bufferedImageList, Files.readAllBytes(imgPathList.getElementAt(index)), (ImageProcessorLink) assignments.get(index), filterName)).start();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        saveImagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bufferedImageList.isEmpty()) {
                    return;
                }
                String suffix = "jpg";
                File outfile;
                for (int i = 0; i < bufferedImageList.getSize(); i++) {
                    try {
                        outfile = new File("Negative" + String.valueOf(i) + "." + suffix);
                        ImageIO.write(bufferedImageList.getElementAt(i), "jpg", outfile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        serverTestConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.setProperty("java.rmi.server.hostname", serverIPValue.getText());
                    Registry registry = LocateRegistry.getRegistry(serverIPValue.getText(), Integer.valueOf(serverPortValue.getText()));
                    serverLink[0] = (CallServer) registry.lookup("CallServer");
                    serverLocated[0] = true;
                    serverConnectionMessage.setText("Connection Ok");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    serverConnectionMessage.setText("Error");
                }
            }
        });

        loadedImagesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Path path = imgPathList.elementAt(loadedImagesList.getSelectedIndex());
                File f = new File(path.toString());
                BufferedImage bi;
                try {
                    bi = ImageIO.read(f);
                    ImageIcon ii = new ImageIcon(bi.getScaledInstance(unprocessedImageBox.getWidth(), unprocessedImageBox.getHeight(), 0));
                    unprocessedImageBox.setIcon(null);
                    unprocessedImageBox.setIcon(ii);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        processedImagesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try {
                    BufferedImage bi = bufferedImageList.elementAt(processedImagesList.getSelectedIndex());
                    ImageIcon ii = new ImageIcon(bi.getScaledInstance(processedImageBox.getWidth(), processedImageBox.getHeight(), 0));
                    processedImageBox.setIcon(ii);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });


    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(new ClientGUI().mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }
}
