import java.awt.image.BufferedImage;

public class ImageFilter
{
    ImageContainer imageContainer;
    AsciiImages asciiImages;
    int width, height;

    ImageFilter(ImageContainer imageContainer)
    {
        this.imageContainer = imageContainer;

        width = this.imageContainer.pixels.length;
        height = this.imageContainer.pixels[0].length;

        asciiImages = new AsciiImages(8);
    }

    public void downScale(int widthA, int heightA)
    {
        width /= widthA;
        height /= heightA;

        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Pixel[][] tempPixels = new Pixel[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                tempPixels[i][j] = getAverage(i, j, widthA, heightA);
                tempPixels[i][j].setImage(tempImage);
            }
        }


        imageContainer.image = tempImage;
        imageContainer.pixels = tempPixels;
    }

    private Pixel getAverage(int x, int y, int widthA, int heightA)
    {
        Pixel average = new Pixel(imageContainer.image, x, y);

        int a = 0, r = 0, g = 0, b = 0;
        int total = widthA * heightA;

        for (int i = x * widthA; i < x * widthA + widthA; i++)
        {
            for (int j = y * heightA; j < y * heightA + heightA; j++)
            {
                a += imageContainer.pixels[i][j].getA();
                r += imageContainer.pixels[i][j].getR();
                g += imageContainer.pixels[i][j].getG();
                b += imageContainer.pixels[i][j].getB();

                imageContainer.pixels[i][j].setARGB(0, 0, 0, 0);
            }
        }

        average.setARGB(a / total, r / total, g / total, b / total);

        return average;
    }

    public void upScale(int widthA, int heightA)
    {
        width *= widthA;
        height *= heightA;

        imageContainer.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Pixel[][] tempPixels = new Pixel[width][height];

        for (int i = 0; i < imageContainer.pixels.length; i++)
        {
            for (int j = 0; j < imageContainer.pixels[i].length; j++)
            {
                extend(tempPixels, i, j, widthA, heightA);
            }
        }

        imageContainer.pixels = tempPixels;
    }

    private void extend(Pixel[][] tempPixels, int x, int y, int widthA, int heightA)
    {
        int a = imageContainer.pixels[x][y].getA();
        int r = imageContainer.pixels[x][y].getR();
        int g = imageContainer.pixels[x][y].getG();
        int b = imageContainer.pixels[x][y].getB();

        for (int i = 0; i < widthA; i++)
        {
            for (int j = 0; j < heightA; j++)
            {
                int x1 = x * widthA + i;
                int y1 = y * heightA + j;

                tempPixels[x1][y1] = new Pixel(imageContainer.image, x1, y1);
                tempPixels[x1][y1].setARGB(a, r, g, b);
            }
        }
    }

    public void greyScale()
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                int a = imageContainer.pixels[i][j].getA();
                int r = imageContainer.pixels[i][j].getR();
                int g = imageContainer.pixels[i][j].getG();
                int b = imageContainer.pixels[i][j].getB();

                int avg = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                imageContainer.pixels[i][j].setARGB(a, avg, avg, avg);
            }

        }
    }

    public void sobel(Boolean color, int threshold)
    {
        int[][] g = new int[width][height];
        double[][] gAngle = new double[width][height];

        int[][] gX = {{-1, 0, 1},
                      {-2, 0, 2},
                      {-1, 0, 1}};

        int[][] gY = {{-1, -2, -1},
                      {0, 0, 0},
                      {1, 2, 1}};

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                int[][] luminanceKernel = fillLuminanceKernel(i, j);

                int lGX = intElementWiseMultiplication(gX, luminanceKernel);
                int lGY = intElementWiseMultiplication(gY, luminanceKernel);

                int tempG = (int)Math.sqrt(lGX * lGX + lGY * lGY);

                if (tempG > 255)
                {
                    tempG = 255;
                }

                g[i][j] = tempG;
                gAngle[i][j] = Math.toDegrees(Math.atan2(lGY, lGX)) + 180;
            }
        }


        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                int rgb = g[i][j];
                double angle = gAngle[i][j];

                if(color)
                {
                    if (rgb >= threshold && ((i > 0 && i < width - 1) && (j > 0 && j < height - 1)))
                    {
                        if ((angle >= 0 && angle < 6) || (angle >= 175 && angle < 186) || angle >= 355)
                        {
                            imageContainer.pixels[i][j].setARGB(255, 255, 0, 0);
                        }
                        else if ((angle >= 85 && angle < 96) || (angle >= 265 && angle < 276))
                        {
                            imageContainer.pixels[i][j].setARGB(255, 0, 255, 0);
                        }
                        else if ((angle >= 5 && angle < 85) || (angle >= 185 && angle < 265))
                        {
                            imageContainer.pixels[i][j].setARGB(255, 0, 0, 255);
                        }
                        else {
                            imageContainer.pixels[i][j].setARGB(255, 255, 0, 255);
                        }
                    }
                    else
                    {
                        imageContainer.pixels[i][j].setARGB(0, rgb, rgb, rgb);
                    }
                }
                else
                {
                    imageContainer.pixels[i][j].setARGB(255, rgb, rgb, rgb);
                }
            }
        }

    }

    private int[][] fillLuminanceKernel(int x, int y)
    {
        int[][] luminanceMatrix = new int[3][3];

        for (int i = -1; i < 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                int targetX = x + i;
                int targetY = y + j;

                if(targetX >= 0 && targetX < width && targetY >= 0 && targetY < height)
                {
                    luminanceMatrix[i + 1][j + 1] = (int)imageContainer.pixels[targetX][targetY].getLuminance();
                }
                else
                {
                    luminanceMatrix[i + 1][j + 1] = 0;
                }
            }
        }

        return luminanceMatrix;
    }

    private int[][] matrixMultiplication(int[][] matrixA, int[][] matrixB)
    {
        int[][] matrixAB = new int[matrixB.length][matrixA[0].length];

        for (int i = 0; i < matrixAB.length; i++)
        {
            for (int j = 0; j < matrixAB[0].length; j++)
            {
                int[] a = new int[matrixA.length];
                int[] b = new int[matrixB[0].length];

                for (int k = 0; k < matrixA.length; k++) {
                    a[k] = matrixA[i][k];
                }

                for (int k = 0; k < matrixB[0].length; k++) {
                    b[k] = matrixB[k][j];
                }

                int sum = 0;

                for (int k = 0; k < a.length; k++)
                {
                    sum += a[k] * b[k];
                }

                matrixAB[i][j] = sum;
            }
        }


        return matrixAB;
    }

    private int intElementWiseMultiplication(int[][] kernelA, int[][] kernelB)
    {
        //kernels must be same size
        int sum = 0;

        for (int i = 0; i < kernelA.length; i++)
        {
            for (int j = 0; j < kernelA[i].length; j++)
            {
                sum += kernelA[i][j] * kernelB[i][j];
            }
        }

        return sum;
    }


    public void toText(Boolean sobel, int sobelThreshold)
    {

        char[] chars = {' ', '.', ':', 'c', 'o', 'P', 'O', '?', '@', '█'};
        char[][] charImage = new char[height][width];

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                double luminance = imageContainer.pixels[j][i].getLuminance();

                luminance = (luminance / 255) * (chars.length - 1);
                luminance = Math.round(luminance);

                charImage[i][j] = chars[(int)luminance];
            }
        }

        if(sobel) {
            greyScale();
            sobel(true, sobelThreshold);

            char[] sobelChars = {'-', '|', '/', '\\'};

            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    int a = imageContainer.pixels[j][i].getA();
                    int r = imageContainer.pixels[j][i].getR();
                    int g = imageContainer.pixels[j][i].getG();
                    int b = imageContainer.pixels[j][i].getB();

                    if( a != 0)
                    {
                        int index = getSobelIndex(r, g, b);

                        charImage[i][j] = sobelChars[index];
                    }
                }
            }
        }

        printNestedCharArray(charImage);
    }

    public void toText(Boolean sobel, int sobelThreshold, Boolean monochrome)
    {
        downScale(8, 8);

        Pixel[][] ogPixels = new Pixel[width][height];

        copyPixelArray(ogPixels, imageContainer.pixels);

        char[] chars = {' ', '.', ':', 'c', 'o', 'P', 'O', '?', '@', '█'};
        char[][] charImage = new char[height][width];

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                double luminance = imageContainer.pixels[j][i].getLuminance();

                luminance = (luminance / 255) * (chars.length - 1);
                luminance = Math.round(luminance);

                charImage[i][j] = chars[(int)luminance];
            }
        }

        if(sobel) {
            greyScale();
            sobel(true, sobelThreshold);

            char[] sobelChars = {'-', '|', '/', '\\'};

            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    int a = imageContainer.pixels[j][i].getA();
                    int r = imageContainer.pixels[j][i].getR();
                    int g = imageContainer.pixels[j][i].getG();
                    int b = imageContainer.pixels[j][i].getB();

                    if( a != 0)
                    {
                        int index = getSobelIndex(r, g, b);

                        charImage[i][j] = sobelChars[index];
                    }
                }
            }
        }

        imageContainer.pixels = ogPixels;
        asciiToImage(monochrome, charImage);
    }

    private static int getSobelIndex(int r, int g, int b)
    {
        int index = -1;

        if(r == 255)
        {
            index = 0;
        }

        if(g == 255)
        {
            index = 1;
        }

        if(b == 255)
        {
            index = 2;
        }

        if(r == 255 && b == 255)
        {
            index = 3;
        }
        return index;
    }

    private void printNestedCharArray(char[][] array)
    {

        for (int i = 0; i < array.length; i++)
        {
            String line = "";

            for (int j = 0; j < array[i].length; j++)
            {
                line += array[i][j];
            }

            System.out.println(line);
        }
    }

    private void asciiToImage(Boolean monochrome, char[][] array)
    {
        width *= 8;
        height *= 8;

        imageContainer.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Pixel[][] pixels = new Pixel[width][height];

        for (int i = 0; i < height / 8; i++)
        {
            for (int j = 0; j < width / 8; j++)
            {
                ImageContainer ascii = asciiImages.getAscii(array[i][j]);

                int a = imageContainer.pixels[j][i].getA();
                int r = imageContainer.pixels[j][i].getR();
                int g = imageContainer.pixels[j][i].getG();
                int b = imageContainer.pixels[j][i].getB();

                if(monochrome)
                {
                    a = 255;
                    r = 255;
                    g = 255;
                    b = 255;
                }

                copyImage(ascii.pixels, pixels, j * 8, i * 8, a, r, g, b);
            }
        }

        imageContainer.pixels = pixels;
    }

    private void copyImage(Pixel[][] subject, Pixel[][] canvas, int x, int y, int a, int r, int g, int b)
    {
        for (int i = 0; i < subject.length; i++)
        {
            for (int j = 0; j < subject[i].length; j++)
            {
                canvas[x + i][y + j] = new Pixel(imageContainer.image, x + i, y + j);
                canvas[x + i][y + j].setARGB(255, 0, 0, 0);

                if(subject[i][j].getR() == 255)
                {
                    canvas[x + i][y + j].setARGB(a, r, g, b);
                }
            }
        }
    }

    private void copyPixelArray(Pixel[][] to, Pixel[][] from)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < to.length; i++)
        {
            for (int j = 0; j < to[i].length; j++)
            {
                to[i][j] = new Pixel(image, i, j);

                int a = from[i][j].getA();
                int r = from[i][j].getR();
                int g = from[i][j].getG();
                int b = from[i][j].getB();

                to[i][j].setARGB(a, r, g, b);
            }
        }
    }

    public void gaussianBlur(int size, double sigma)
    {
        double[][] kernel = new double[size][size];
        double kernelTotal = 0;

        int halfSize = size / 2;
        for (int i = 0; i < kernel.length; i++)
        {
            for (int j = 0; j < kernel[i].length; j++)
            {
                int x = i - halfSize;
                int y = j - halfSize;

                double val = ((x * x + y * y) / (2.0 * sigma * sigma)) * -1;

                val = Math.pow(Math.E, val);

                val = (1 / (2 * Math.PI * sigma * sigma)) * val;

                kernel[i][j] = val;
                kernelTotal += val;
            }
        }

        int[][] rValues = new int[width][height];
        int[][] gValues = new int[width][height];
        int[][] bValues = new int[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                double[][][] rgbKernels = fillRGBKernel(i, j, size);

                double r = doubleElementWiseMultiplication(kernel, rgbKernels[0]);
                double g = doubleElementWiseMultiplication(kernel, rgbKernels[1]);
                double b = doubleElementWiseMultiplication(kernel, rgbKernels[2]);

                r /= kernelTotal;
                g /= kernelTotal;
                b /= kernelTotal;

                rValues[i][j] = Math.max(0, Math.min(255, (int) r));
                gValues[i][j] = Math.max(0, Math.min(255, (int) g));
                bValues[i][j] = Math.max(0, Math.min(255, (int) b));
            }
        }

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                int a = imageContainer.pixels[i][j].getA();
                int r = rValues[i][j];
                int g = gValues[i][j];
                int b = bValues[i][j];

                imageContainer.pixels[i][j].setARGB(a, r, g, b);
            }
        }
    }

    private double[][][] fillRGBKernel(int x, int y, int size)
    {
        double[][][] rgbKernel = new double[3][size][size];

        int halfSize = size / 2;
        for (int i = -halfSize; i <= halfSize; i++)
        {
            for (int j = -halfSize; j <= halfSize; j++)
            {
                int targetX = x + i;
                int targetY = y + j;

                if(targetX >= 0 && targetX < width && targetY >= 0 && targetY < height)
                {
                    rgbKernel[0][i + halfSize][j + halfSize] = imageContainer.pixels[targetX][targetY].getR();
                    rgbKernel[1][i + halfSize][j + halfSize] = imageContainer.pixels[targetX][targetY].getG();
                    rgbKernel[2][i + halfSize][j + halfSize] = imageContainer.pixels[targetX][targetY].getB();

                }
                else
                {
                    rgbKernel[0][i + halfSize][j + halfSize] = 0;
                    rgbKernel[1][i + halfSize][j + halfSize] = 0;
                    rgbKernel[2][i + halfSize][j + halfSize] = 0;
                }
            }
        }

        return rgbKernel;
    }

    private double doubleElementWiseMultiplication(double[][] kernelA, double[][] kernelB)
    {
        //kernels must be same size
        double sum = 0;

        for (int i = 0; i < kernelA.length; i++)
        {
            for (int j = 0; j < kernelA[i].length; j++)
            {
                sum += kernelA[i][j] * kernelB[i][j];
            }
        }

        return sum;
    }
}
