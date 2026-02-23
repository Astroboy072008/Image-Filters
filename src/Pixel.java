import java.awt.image.BufferedImage;

public class Pixel {
    BufferedImage image;
    int a, r, g, b;
    int argb;
    int x, y;

    Pixel(BufferedImage image, int x, int y)
    {
        this.image = image;
        this.x = x;
        this.y = y;

        getARGB();
    }

    private void getARGB()
    {
        argb = image.getRGB(x, y);

        a = (argb >> 24) & 0xff;
        r = (argb >> 16) & 0xff;
        g = (argb >> 8) & 0xff;
        b = argb & 0xff;
    }

    public void setARGB(int a, int r, int g, int b)
    {
        this.a = a & 0xff;
        this.r = r & 0xff;
        this.g = g & 0xff;
        this.b = b & 0xff;

        argb = (this.a << 24) | (this.r << 16) | (this.g << 8) | this.b;

        image.setRGB(x, y, argb);
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;

        image.setRGB(x, y, argb);
    }

    public double getLuminance()
    {
        return ((0.2126 * r) + (0.7152 * g) + (0.0722 * b));
    }

    public int getA()
    {
        return a;
    }

    public int getR()
    {
        return r;
    }

    public int getG()
    {
        return g;
    }

    public int getB()
    {
        return b;
    }

    public BufferedImage getImage()
    {
        return image;
    }

    @Override
    public String toString()
    {
        return a + ", " + r + ", " + g + ", " + b;
    }
}
