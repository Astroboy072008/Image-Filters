import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AsciiImages
{
    HashMap<Character, int[]> ascii;
    int width, height;

    AsciiImages(int width, int height)
    {
        this.width = width;
        this.height = height;
        ascii = new HashMap<>();

        HashMap<Character, Integer> key = new HashMap<>();

        String chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz .,:!?@#$%^&*()/\\|-";

        for (int i = 0; i < chars.length(); i++)
        {
            key.put(chars.charAt(i), (int)chars.charAt(i));
        }

        key.put('█', 219);

        chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz .,:!?@#$%^&*()/\\|-█";

        String filePath = "src\\Ascii_" + this.width + "x" + this.height + "\\";
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

            int[] argb = new int[this.width * this.height];
            image.getRGB(0, 0, this.width, this.height, argb, 0, this.width);

            ascii.put(chars.charAt(i), argb);
        }
    }

    public int[] getAscii(char key)
    {
        return ascii.get(key);
    }

    public int getWidth() {return width;}

    public int getHeight()
    {
        return height;
    }
}
