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
    JMenu fileMenu;
    JMenuItem openItem, saveItem;
    JScrollPane scrollPane;
    JLabel imageLabel;

    BufferedImage image;
    String filePath, fileName;

    ImageContainer imageContainer;

    ImageViewer()
    {
        super("Image Editor");

        setUp();

        setUpMenuButtons();
    }

    private void setUp()
    {
        toolkit = Toolkit.getDefaultToolkit();
        screenSize = toolkit.getScreenSize();

        super.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");

        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);
        super.setJMenuBar(menuBar);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        scrollPane = new JScrollPane(imageLabel);
        super.getContentPane().add(scrollPane, BorderLayout.CENTER);

        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setVisible(true);
    }

    private void setUpMenuButtons()
    {
        openItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                openFileAction();

                imageContainer = new ImageContainer(image);

                displayImage(imageContainer.image);
            }
        });

        saveItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveItemAction(filePath, fileName, image);
            }
        });
    }

    private void openFileAction()
    {
        JFileChooser fileChooser = new JFileChooser();

        int option = fileChooser.showOpenDialog(this);

        if(option == JFileChooser.APPROVE_OPTION)
        {
            File fileOption = fileChooser.getSelectedFile();

            try
            {
                image = ImageIO.read(fileOption);

                fileName = fileOption.getName().substring(0, fileOption.getName().indexOf('.'));

                filePath = fileOption.getPath().substring(0, fileOption.getPath().indexOf(fileName));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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

    public BufferedImage getImage()
    {
        return image;
    }

    public void displayImage(BufferedImage image)
    {
        imageLabel.setIcon(new ImageIcon(image));

        int width = 0;
        int height = 0;

        if(image.getWidth() * 1.5 >= screenSize.width - 50)
        {
            width = (int)(image.getWidth() * .9);
        }
        else
        {
            width = (int)(image.getWidth() * 1.5);
        }

        if(image.getWidth() * 1.5 >= screenSize.height - 50)
        {
            height = (int)(image.getWidth() * .9);
        }
        else
        {
            height = (int)(image.getWidth() * 1.5);
        }

        super.setSize(width, height);
        super.revalidate();
        super.repaint();
    }
}
