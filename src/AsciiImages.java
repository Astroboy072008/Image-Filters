import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AsciiImages
{
    HashMap<Character, ImageHandler> ascii;
    int size;

    AsciiImages(int size)
    {
        this.size = size;
        ascii = new HashMap<>();

        HashMap<Character, Integer> key = new HashMap<>();

        String chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz .,:!?@#$%^&*()/\\|-";

        for (int i = 0; i < chars.length(); i++)
        {
            key.put(chars.charAt(i), (int)chars.charAt(i));
        }

        key.put('█', 219);

        chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz .,:!?@#$%^&*()/\\|-█";

        String filePath = "src\\Ascii_" + size + "x" + size + "\\";
        BufferedImage image = null;

        try
        {
            File file = new File(filePath + "test.png");
            image = ImageIO.read(file);
        }
        catch (IOException e)
        {
            System.out.println("Could NOT FIND ASCII IMAGES");
            throw new RuntimeException(e);
        }

        for (int i = 0; i < chars.length(); i++)
        {
            try
            {
                File file = new File(filePath + key.get(chars.charAt(i)) + ".png");

                image = ImageIO.read(file);
            }
            catch (IOException e)
            {
                image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
            }

            ascii.put(chars.charAt(i), new ImageHandler(image));
        }
    }

    public ImageHandler getAscii(char key)
    {
        return ascii.get(key);
    }

    public int getSize()
    {
        return size;
    }
}
