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

    public int getIntARGB()
    {
        return argb;
    }

    public void setARGB(int a, int r, int g, int b)
    {
        this.a = Math.max(0, Math.min(255, a));
        this.r = Math.max(0, Math.min(255, r));
        this.g = Math.max(0, Math.min(255, g));
        this.b = Math.max(0, Math.min(255, b));

        argb = (this.a << 24) | (this.r << 16) | (this.g << 8) | this.b;

        image.setRGB(x, y, argb);
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;

        image.setRGB(x, y, argb);
    }

    public void setA(int a)
    {
        setARGB(a, r, g, b);
    }

    public void setR(int r)
    {
        setARGB(a, r, g, b);
    }

    public void setG(int g)
    {
        setARGB(a, r, g, b);
    }

    public void setB(int b)
    {
        setARGB(a, r, g, b);
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
