import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Main {
    public static void main(String[] args)
    {

        File file = new File("C:\\Users\\ljwel\\Desktop\\Image\\download.jpg");
        BufferedImage image = null;

        try {
            image = ImageIO.read(file);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageContainer imageContainer = new ImageContainer(image);

        ImageFilter imageFilter = new ImageFilter(imageContainer);

        int scale = 4;


//        imageFilter.greyScale();
//
//        imageFilter.sobel(true, 1);
//
//        imageFilter.downScale(scale, scale);
//        imageFilter.toText(true, 100);
//
//        imageFilter.toText(true, 1, false);

//        imageFilter.gaussianBlur(5, 5);

//
//        imageFilter.upScale(scale, scale);

//        imagePrint(imageContainer.image);

        display(imageContainer.image);


    }

    public static void display(BufferedImage image)
    {
        System.out.println("displaying");

        JFrame frame = new JFrame();
        JLabel label = new JLabel();

        frame.setSize(image.getWidth(), image.getHeight());
        label.setIcon(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void imagePrint(BufferedImage image)
    {

        File output = new File("C:\\Users\\ljwel\\Desktop\\Image\\Test_Output.png");

        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
