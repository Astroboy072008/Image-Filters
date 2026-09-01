import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageViewer extends JFrame
{
    Toolkit toolkit;
    Dimension screenSize;
    JMenuBar menuBar;
    JMenu fileMenu, editMenu, basicFiltersMenu;
    JMenuItem openItem, saveItem, undoButton, greyScale;
    JScrollPane scrollPane;
    JLabel imageLabel;

    String imageFilePath, imageFileName;

    ImageHandler imageHandler;

    ImageViewer()
    {
        super("Image Editor");

        setUp();

        setUpFileMenuButtons();

        setUpEditMenuButtons();

        setUpBasicFiltersMenuButtons();
    }

    private void setUp()
    {
        toolkit = Toolkit.getDefaultToolkit();
        screenSize = toolkit.getScreenSize();

        super.setSize(400, 400);
        super.setLocation((int)(screenSize.getWidth() / 2) - 200, (int)(screenSize.getHeight() / 2) - 200);

        //File Menu
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");

        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);

        //Edit Menu
        editMenu = new JMenu("Edit");
        undoButton = new JMenuItem("Undo");

        editMenu.add(undoButton);

        //Basic Filters Menu
        basicFiltersMenu = new JMenu("Basic Filters");
        greyScale = new JMenuItem("To ASCII");

        basicFiltersMenu.add(greyScale);

        //MenuBar Setup
        menuBar = new JMenuBar();

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(basicFiltersMenu);
        super.setJMenuBar(menuBar);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        scrollPane = new JScrollPane(imageLabel);
        super.getContentPane().add(scrollPane, BorderLayout.CENTER);

        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setVisible(true);
    }

    private void setUpFileMenuButtons()
    {
        openItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BufferedImage image = openFileAction();

                if(image != null)
                {
                    imageHandler = new ImageHandler(image);

                    displayImage(imageHandler.image, true);
                }
            }
        });

        saveItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveItemAction(imageFilePath, imageFileName, imageHandler.image);
            }
        });
    }

    private void setUpEditMenuButtons()
    {
        undoButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(imageHandler != null)
                {
                    imageHandler.undo();

                    displayImage(imageHandler.image, true);
                }
            }
        });
    }

    private void setUpBasicFiltersMenuButtons()
    {
        greyScale.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(imageHandler != null)
                {
//                    imageHandler.greyScale();
                    //imageHandler.toAsciiImage(8, 8, true, 200, false);
//                    imageHandler.tests();
//                    imageHandler.extendedDifferenceOfGaussians(4.16, 1.6, 120, 160, 1);
//                    imageHandler.downScale(8, 8);
//                    imageHandler.sobel(false, 200);
//                    imageHandler.upScale(8, 8);
                    imageHandler.pixelSort(false, 64, 192);
                    applyImageFilters(true);
                }
            }
        });
    }

    private BufferedImage openFileAction()
    {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);

        BufferedImage image = null;

        if(option == JFileChooser.APPROVE_OPTION)
        {
            File fileOption = fileChooser.getSelectedFile();

            try
            {
                image = ImageIO.read(fileOption);

                imageFileName = fileOption.getName().substring(0, fileOption.getName().indexOf('.'));

                imageFilePath = fileOption.getPath().substring(0, fileOption.getPath().indexOf(imageFileName));
            }
            catch (IOException e)
            {
                return null;
            }
        }

        return image;
    }

    private void saveItemAction(String filePath, String name, BufferedImage image)
    {
        File output = new File(filePath + name + "_Output.png");

        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyImageFilters(boolean recenter)
    {
        imageHandler.apply();

        displayImage(imageHandler.image, recenter);
    }

    public void displayImage(BufferedImage image, boolean recenter)
    {
        imageLabel.setIcon(new ImageIcon(image));

        int width;
        int height;
        int x = 0;
        int y = 0;

        if(image.getWidth() * 1.5 >= screenSize.width - 50)
        {
            width = (int)(image.getWidth() * .9);
        }
        else
        {
            width = (int)(image.getWidth() * 1.5);
            x = (int)(screenSize.getWidth() / 2.0 - image.getWidth() * 1.5 / 2.0);
        }

        if(image.getHeight() * 1.5 >= screenSize.height - 50)
        {
            height = (int)(image.getHeight() * .9);
        }
        else
        {
            height = (int)(image.getHeight() * 1.5);
            y = (int)(screenSize.getHeight() / 2.0 - image.getHeight() * 1.5 / 2.0);
        }

        if(x < 0) {x = 0;}
        if(y < 0) {y = 0;}

        if(recenter) {super.setLocation(x, y);}
        super.setSize(width, height);
        super.revalidate();
        super.repaint();
    }
}
