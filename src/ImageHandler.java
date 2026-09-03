import java.awt.image.BufferedImage;
import java.util.Stack;

public class ImageHandler
{
    public BufferedImage image;
    public AsciiImages asciiImages;

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

        asciiImages = new AsciiImages(8, 8);
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
        imageARGB = ImageEditor.greyScale(imageARGB, width, height);

        sync(width, height);
    }

    public void sobel(boolean color, int threshold)
    {
        imageARGB = ImageEditor.sobel(imageARGB, width, height, color, threshold);

        sync(width, height);
    }

    public void toAsciiImage(boolean sobel, int sobelThreshold, boolean monochrome)
    {
        imageARGB = ImageEditor.toText(imageARGB, width, height, asciiImages.width, asciiImages.height, sobel, sobelThreshold, asciiImages, monochrome);

        int floorWidth = (width / asciiImages.width) * asciiImages.width;
        int floorHeight = (height / asciiImages.height) * asciiImages.height;

        sync(floorWidth, floorHeight);
    }

    public void toAsciiImage(int downScaleWidthAmount, int downScaleHeightAmount, boolean sobel, int sobelThreshold, boolean monochrome)
    {
        imageARGB = ImageEditor.toText(imageARGB, width, height, downScaleWidthAmount, downScaleHeightAmount, sobel, sobelThreshold, asciiImages, monochrome);

        int floorWidth = (width / downScaleWidthAmount) * asciiImages.width;
        int floorHeight = (height / downScaleHeightAmount) * asciiImages.height;

        sync(floorWidth, floorHeight);
    }

    public void gaussianBlur(double sigma, int size)
    {
        imageARGB = ImageEditor.gaussianBlur(imageARGB, width, height, sigma, size);

        sync(width, height);
    }

    public void differenceOfGaussians(double sigma, double scale, double threshold)
    {
        imageARGB = ImageEditor.differenceOfGaussians(imageARGB, width, height, sigma, scale, threshold);

        sync(width, height);
    }

    public void extendedDifferenceOfGaussians(double sigma, double scale, double tau, double threshold, double phi)
    {
        // increasing tau sharpens edges
        // threshold determines the white cut-off of pixels
        // increasing phi sharpens the transition between white to black

        imageARGB = ImageEditor.extendedDifferenceOfGaussians(imageARGB, width, height, sigma, scale, tau, threshold, phi);

        sync(width, height);
    }

    public void pixelSort(boolean vertical, boolean inverse, int maskMin, int maskMax)
    {
        imageARGB = ImageEditor.pixelSort(imageARGB, width, height, vertical, inverse, maskMin, maskMax);

        sync(width, height);
    }

    public void idk()
    {
        imageARGB = ImageEditor.idk(imageARGB, width, height);

        sync(width, height);
    }

    public void tests()
    {


        imageARGB = ImageEditor.downScale(imageARGB, width, height, 8, 8);

        imageARGB = ImageEditor.extendedDifferenceOfGaussians(imageARGB, width / 8, height / 8, 4.16, 1.6, 120, 160, 1);

        imageARGB = ImageEditor.sobel(imageARGB, width / 8, height / 8, true, 200);

//        imageARGB = ImageEditor.downScaleSobel(imageARGB, width, height, 8, 8);

        imageARGB = ImageEditor.upScale(imageARGB, width / 8, height / 8, 8, 8);

        sync(width / 8 * 8, height / 8 * 8);

//        sync(width, height);
    }
}
