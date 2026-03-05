import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

public class Pixels {
    BufferedImage image;
    int[] argb;
    int width, height;

    Pixels(BufferedImage image)
    {
        this.image = image;
        width = this.image.getWidth();
        height = this.image.getHeight();

        argb = new int[width * height];
        image.getRGB(0, 0, width, height, argb, 0, width);
    }

    Pixels(int width, int height)
    {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.width = width;
        this.height = height;

        argb = new int[width * height];
        image.getRGB(0, 0, width, height, argb, 0, width);
    }

    public int getIntARGB(int x, int y) {return argb[width * y + x];}

    public int getIntARGB(int a, int r, int g, int b)
    {
        a = Math.max(0, Math.min(255, a));
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public void setARGB(int x, int y, int argb)
    {
        this.argb[width * y + x] = argb;
    }

    public void setARGB(int[] argb, int width, int height)
    {
        this.argb = argb;

        if(this.width != width || this.height != height)
        {
            this.width = width;
            this.height = height;

            image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        }

        syncImage();
    }

    public void setARGB(int x, int y, int a, int r, int g, int b)
    {
        a = Math.max(0, Math.min(255, a));
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        int argb = (a << 24) | (r << 16) | (g << 8) | b;

        this.argb[width * y + x] = argb;
    }

    public void syncImage() {image.setRGB(0, 0, width, height, argb, 0, width);}

    public void setImage(BufferedImage image)
    {
        this.image = image;
        width = this.image.getWidth();
        height = this.image.getHeight();

        argb = new int[width * height];
        image.getRGB(0, 0, width, height, argb, 0, width);
    }

    public void setA(int x, int y, int a)
    {
        int index = width * y + x;
        a = Math.max(0, Math.min(255, a));

        argb[index] = (argb[index] & 0x00ffffff) | (a << 24);
    }

    public void setR(int x, int y, int r)
    {
        int index = width * y + x;
        r = Math.max(0, Math.min(255, r));

        argb[index] = (argb[index] & 0xff00ffff) | (r << 16);
    }

    public void setG(int x, int y, int g)
    {
        int index = width * y + x;
        g = Math.max(0, Math.min(255, g));

        argb[index] = (argb[index] & 0xffff00ff) | (g << 8);
    }

    public void setB(int x, int y, int b)
    {
        int index = width * y + x;
        b = Math.max(0, Math.min(255, b));

        argb[index] = (argb[index] & 0xffffff00) | b;
    }

    public int getWidth() {return width;}

    public int getHeight() {return height;}

    public double getLuminance(int x, int y) {return ((0.2126 * getR(x, y)) + (0.7152 * getG(x, y)) + (0.0722 * getB(x, y)));}

    public int getA(int x, int y) {return (argb[width * y + x] >> 24) & 0xff;}

    public int getR(int x, int y) {return (argb[width * y + x] >> 16) & 0xff;}

    public int getG(int x, int y) {return (argb[width * y + x] >> 8) & 0xff;}

    public int getB(int x, int y) {return argb[width * y + x] & 0xff;}

    public BufferedImage getImage() {return image;}

    public BufferedImage getImageCopy()
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        image.setRGB(0, 0, width, height, argb, 0, width);

        return image;
    }

}
