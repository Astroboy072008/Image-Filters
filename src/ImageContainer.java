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
}
