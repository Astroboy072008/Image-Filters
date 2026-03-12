import java.awt.image.BufferedImage;
import java.util.Stack;

public class ImageHandler
{
    public BufferedImage image;

    private Stack<ImageData> imageHistory;
    private int[] imageARGB;
    private int width, height;

    private class ImageData
    {
        int[] argb;
        int width, height;

        public ImageData(int[] argb, int width, int height)
        {
            this.argb = new int[argb.length];
            System.arraycopy(argb, 0, this.argb, 0, argb.length);

            this.width = width;
            this.height = height;
        }
    }

    ImageHandler(BufferedImage image)
    {
        this.image = image;
        width = this.image.getWidth();
        height = this.image.getHeight();
        imageARGB = new int[width * height];
        this.image.getRGB(0, 0, width, height, imageARGB, 0, width);

        imageHistory = new Stack<>();
        imageHistory.add(new ImageData(imageARGB, width, height));
    }

    public void undo()
    {
        ImageData data;

        if(imageHistory.size() > 2)
        {
            imageHistory.pop();
            data = imageHistory.pop();
            imageARGB = data.argb;
        }
        else
        {
            if (imageHistory.size() == 2) {imageHistory.pop();}

            data = imageHistory.getFirst();
            imageARGB = new int[data.width * data.height];
            System.arraycopy(data.argb, 0, imageARGB, 0, data.argb.length);
        }

        sync(data.width, data.height);
    }

    public void softUndo()
    {
        ImageData data = imageHistory.getLast();
        imageARGB = new int[data.width * data.height];
        System.arraycopy(data.argb, 0, imageARGB, 0, data.argb.length);

        sync(data.width, data.height);
    }

    public void apply()
    {
        imageHistory.add(new ImageData(imageARGB, width, height));
    }

    public void sync(int width, int height)
    {
        if(this.width != width || this.height != height)
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        this.width = width;
        this.height = height;
        image.setRGB(0, 0, this.width, this.height, imageARGB, 0, this.width);
    }

    public void downScale(int widthA, int heightA)
    {
        imageARGB = ImageEditor.downScale(imageARGB, width, height, widthA, heightA);

        sync(width / widthA, height / heightA);
    }

    public void upScale(int widthA, int heightA)
    {
        imageARGB = ImageEditor.upScale(imageARGB, width, height, widthA, heightA);

        sync(width * widthA, height * heightA);
    }

    public void greyScale()
    {
        ImageEditor.greyScale(imageARGB, width, height);

        sync(width, height);
    }

    public void sobel(boolean color, int threshold)
    {
        imageARGB = ImageEditor.sobel(imageARGB, width, height, color, threshold);

        sync(width, height);
    }
}
