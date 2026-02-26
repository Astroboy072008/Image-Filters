import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Main {
    public static void main(String[] args)
    {
        String name = "Screenshot 2026-02-24 225904.png";
        File file = new File("C:\\Users\\ljwel\\Desktop\\Image\\" + name);
        BufferedImage image = null;

        try {
            image = ImageIO.read(file);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageContainer imageContainer = new ImageContainer(image);

        AsciiImages asciiImages = new AsciiImages(8);
        ImageFilter imageFilter = new ImageFilter(imageContainer, asciiImages);

        int scale = 8;


//        imageFilter.greyScale();
//
//        imageFilter.sobel(true, 1);
//
//        imageFilter.downScale(scale, scale);
//        imageFilter.toText(true, 250);
//
        imageFilter.toText(true, 250, false);

//        imageFilter.gaussianBlur(10, 10);
//
//        imageFilter.greyScale();

//        imageFilter.differenceOfGaussians(10, .5, 2, 10, 0, 128);

//        imageFilter.differenceOfGaussians(8, .5, 2, 5, 0, 85);
//        imageFilter.sobel(true, 250);





//
//        imageFilter.upScale(scale, scale);

        imagePrint(imageContainer.image);

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
