import java.awt.image.BufferedImage;

public class ImageContainer
{
    public BufferedImage image;
    public Pixel[][] pixels;

    ImageContainer(BufferedImage image)
    {
        this.image = image;
        this.pixels = getPixelArray(this.image);
    }

    private Pixel[][] getPixelArray(BufferedImage image)
    {
        Pixel[][] pixels = new Pixel[image.getWidth()][image.getHeight()];

        for (int i = 0; i < image.getWidth(); i++)
        {
            for (int j = 0; j < image.getHeight(); j++)
            {
                pixels[i][j] = new Pixel(image, i, j);
            }
        }

        return pixels;
    }

    public BufferedImage imageCopy()
    {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int i = 0; i < pixels.length; i++)
        {
            for (int j = 0; j < pixels[i].length; j++)
            {
                copy.setRGB(i, j, pixels[i][j].getIntARGB());
            }
        }

        return copy;
    }

    public Pixel[][] pixelsCopy()
    {
        Pixel[][] copy = new Pixel[pixels.length][pixels[0].length];

        for (int i = 0; i < copy.length; i++)
        {
            System.arraycopy(pixels[i], 0, copy[i], 0, copy[i].length);
        }

        return copy;
    }

    public ImageContainer copy()
    {
        return new ImageContainer(imageCopy());
    }
}
