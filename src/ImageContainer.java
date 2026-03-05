import java.awt.image.BufferedImage;
import java.util.Stack;

public class ImageContainer
{
    public BufferedImage image;
    public Pixels pixels;

    private Stack<BufferedImage> imageHistory;

    ImageContainer(BufferedImage image)
    {
        this.image = image;
        pixels = new Pixels(this.image);

        imageHistory = new Stack<>();
        imageHistory.add(pixels.getImageCopy());
    }

    public ImageContainer copy()
    {
        return new ImageContainer(pixels.getImageCopy());
    }

    public void undo()
    {
        if(imageHistory.size() > 1)
        {
            image = imageHistory.pop();
        }
        else
        {
            pixels.setImage(imageHistory.getFirst());
            image = pixels.getImageCopy();
        }

        pixels.setImage(image);
    }

    public void apply(ImageContainer editedImage)
    {
        imageHistory.add(pixels.getImageCopy());

        image = editedImage.image;
        pixels = editedImage.pixels;
    }
}
