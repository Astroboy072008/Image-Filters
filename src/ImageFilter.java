import java.awt.image.BufferedImage;

public class ImageFilter
{
    ImageContainer imageContainer;
    AsciiImages asciiImages;
    int width, height;

    ImageFilter(ImageContainer imageContainer)
    {
        this.imageContainer = imageContainer;

        width = this.imageContainer.pixels.getWidth();
        height = this.imageContainer.pixels.getHeight();
    }

    ImageFilter(ImageContainer imageContainer, AsciiImages asciiImages)
    {
        this.imageContainer = imageContainer;

        width = this.imageContainer.pixels.getWidth();
        height = this.imageContainer.pixels.getHeight();

        this.asciiImages = asciiImages;
    }

    public ImageContainer getImageContainer() {return imageContainer;}

    public void downScale(int widthA, int heightA)
    {
        width /= widthA;
        height /= heightA;

        int[] tempPixels = new int[width * height];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tempPixels[width * y + x] = getAverage(x, y, widthA, heightA);
            }
        }

        imageContainer.pixels.setARGB(tempPixels, width, height);
        imageContainer.image = imageContainer.pixels.getImage();
    }

    private int getAverage(int x, int y, int widthA, int heightA)
    {
        int a = 0, r = 0, g = 0, b = 0;
        int total = widthA * heightA;

        for (int i = x * widthA; i < x * widthA + widthA; i++)
        {
            for (int j = y * heightA; j < y * heightA + heightA; j++)
            {
                a += imageContainer.pixels.getA(i, j);
                r += imageContainer.pixels.getR(i, j);
                g += imageContainer.pixels.getG(i, j);
                b += imageContainer.pixels.getB(i, j);
            }
        }

        return imageContainer.pixels.getIntARGB(a / total, r / total, g / total, b/ total);
    }

    public void upScale(int widthA, int heightA)
    {
        width *= widthA;
        height *= heightA;

        int[] tempPixels = new int[width * height];

        for (int x = 0; x < imageContainer.pixels.width; x++)
        {
            for (int y = 0; y < imageContainer.pixels.height; y++)
            {
                extend(tempPixels, x, y, widthA, heightA);
            }
        }

        imageContainer.pixels.setARGB(tempPixels, width, height);
        imageContainer.image = imageContainer.pixels.getImage();
    }

    private void extend(int[] tempPixels, int x, int y, int widthA, int heightA)
    {
        int a = imageContainer.pixels.getA(x, y);
        int r = imageContainer.pixels.getR(x, y);
        int g = imageContainer.pixels.getG(x, y);
        int b = imageContainer.pixels.getB(x, y);

        for (int i = 0; i < widthA; i++)
        {
            for (int j = 0; j < heightA; j++)
            {
                int x1 = x * widthA + i;
                int y1 = y * heightA + j;

                tempPixels[width * y1 + x1] = imageContainer.pixels.getIntARGB(a, r, g, b);
            }
        }
    }

    public void greyScale()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int a = imageContainer.pixels.getA(x, y);
                int r = imageContainer.pixels.getR(x, y);
                int g = imageContainer.pixels.getG(x, y);
                int b = imageContainer.pixels.getB(x, y);

                int avg = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                imageContainer.pixels.setARGB(x, y, a, avg, avg, avg);
            }
        }

        imageContainer.pixels.syncImage();
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


        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int rgb = g[x][y];
                double angle = gAngle[x][y];

                if(color)
                {
                    if (rgb >= threshold && ((x > 0 && x < width - 1) && (y > 0 && y < height - 1)))
                    {
                        if ((angle >= 0 && angle < 6) || (angle >= 175 && angle < 186) || angle >= 355)
                        {
                            imageContainer.pixels.setARGB(x, y, 255, 255, 0, 0);
                        }
                        else if ((angle >= 85 && angle < 96) || (angle >= 265 && angle < 276))
                        {
                            imageContainer.pixels.setARGB(x, y,255, 0, 255, 0);
                        }
                        else if ((angle >= 5 && angle < 85) || (angle >= 185 && angle < 265))
                        {
                            imageContainer.pixels.setARGB(x, y,255, 0, 0, 255);
                        }
                        else {
                            imageContainer.pixels.setARGB(x, y,255, 255, 0, 255);
                        }
                    }
                    else
                    {
                        imageContainer.pixels.setARGB(x, y,0, rgb, rgb, rgb);
                    }
                }
                else
                {
                    imageContainer.pixels.setARGB(x, y,255, rgb, rgb, rgb);
                }
            }
        }

        imageContainer.pixels.syncImage();
    }

    private int[][] fillLuminanceKernel(int x, int y)
    {
        int[][] luminanceKernel = new int[3][3];

        for (int i = -1; i < 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                int targetX = x + i;
                int targetY = y + j;

                if(targetX >= 0 && targetX < width && targetY >= 0 && targetY < height)
                {
                    luminanceKernel[i + 1][j + 1] = (int)imageContainer.pixels.getLuminance(targetX, targetY);
                }
                else
                {
                    luminanceKernel[i + 1][j + 1] = 0;
                }
            }
        }

        return luminanceKernel;
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
        printNestedCharArray(makeCharImage(sobel, sobelThreshold));
    }

    public void toText(Boolean sobel, int sobelThreshold, Boolean monochrome)
    {
        downScale(8, 8);

        BufferedImage ogImage = imageContainer.pixels.getImageCopy();

        char[][] charImage = makeCharImage(sobel, sobelThreshold);

        imageContainer.pixels.setImage(ogImage);
        asciiToImage(monochrome, charImage);
    }

    private char[][] makeCharImage(Boolean sobel, int sobelThreshold)
    {
        char[] chars = {' ', '.', ':', 'c', 'o', 'P', 'O', '?', '@', '█'};
        char[][] charImage = new char[height][width];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double luminance = imageContainer.pixels.getLuminance(x, y);

                luminance = (luminance / 255) * (chars.length - 1);
                luminance = Math.round(luminance);

                charImage[y][x] = chars[(int)luminance];
            }
        }

        if(sobel) {
//            differenceOfGaussians(8, .5, 2, 5, 0, 85);
            sobel(true, sobelThreshold);

            char[] sobelChars = {'-', '|', '/', '\\'};

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    int a = imageContainer.pixels.getA(x, y);
                    int r = imageContainer.pixels.getR(x, y);
                    int g = imageContainer.pixels.getG(x, y);
                    int b = imageContainer.pixels.getB(x, y);

                    if( a != 0)
                    {
                        int index = getSobelIndex(r, g, b);

                        charImage[y][x] = sobelChars[index];
                    }
                }
            }
        }

        return charImage;
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
        if(asciiImages == null)
        {
            asciiImages = new AsciiImages(8);
        }

        width *= 8;
        height *= 8;

        int[] tempPixels = new int[width * height];

        for (int y = 0; y < height / 8; y++)
        {
            for (int x = 0; x < width / 8; x++)
            {
                ImageContainer ascii = asciiImages.getAscii(array[y][x]);

                int a = imageContainer.pixels.getA(x, y);
                int r = imageContainer.pixels.getR(x, y);
                int g = imageContainer.pixels.getG(x, y);
                int b = imageContainer.pixels.getB(x, y);

                if(monochrome)
                {
                    a = 255;
                    r = 255;
                    g = 255;
                    b = 255;
                }

                copyImage(ascii.pixels, tempPixels, x * 8, y * 8, a, r, g, b);
            }
        }

        imageContainer.pixels.setARGB(tempPixels, width, height);
        imageContainer.pixels.syncImage();
        imageContainer.image = imageContainer.pixels.getImage();
    }

    private void copyImage(Pixels subject, int[] canvas, int x, int y, int a, int r, int g, int b)
    {
        for (int i = 0; i < subject.width; i++)
        {
            for (int j = 0; j < subject.height; j++)
            {
                int index = width * (y + j) + (x + i);

                if(subject.getR(i, j) == 255)
                {
                    canvas[index] = imageContainer.pixels.getIntARGB(a, r, g, b);
                }
                else
                {
                    canvas[index] = imageContainer.pixels.getIntARGB(255, 0, 0, 0);
                }
            }
        }
    }

//    public void gaussianBlur(int size, double sigma)
//    {
//        double[][] kernel = makeGaussianKernel(size, sigma);
//
//        double[][][] rgb = makeGaussianBlurRGB(kernel);
//
//        applyGaussianBlur(rgb);
//    }
//
//    public void gaussianBlur(double[][] kernel)
//    {
//        double[][][] rgb = makeGaussianBlurRGB(kernel);
//
//        applyGaussianBlur(rgb);
//    }
//
//    private double[][] makeGaussianKernel(int size, double sigma) {
//        double[][] kernel = new double[2][size];
//
//        int halfSize = size / 2;
//        for (int i = 0; i < size; i++) {
//            int x = i - halfSize;
//            int y = i - halfSize;
//
//            double valX = ((x * x) / (2.0 * sigma * sigma)) * -1;
//            double valY = ((y * y) / (2.0 * sigma * sigma)) * -1;
//
//            valX = Math.pow(Math.E, valX);
//            valY = Math.pow(Math.E, valY);
//
//            valX = (1 / (2 * Math.PI * sigma * sigma)) * valX;
//            valY = (1 / (2 * Math.PI * sigma * sigma)) * valY;
//
//            kernel[0][i] = valX;
//            kernel[1][i] = valY;
//        }
//
//        return kernel;
//    }
//
//    private double[][][] makeGaussianBlurRGB(double[][] kernel)
//    {
//        for (int i = 0; i <kernel.length; i++)
//        {
//            double sum = 0;
//            for (int j = 0; j < kernel[0].length; j++)
//            {
//                sum += kernel[i][j];
//            }
//            for (int j = 0; j < kernel[0].length; j++)
//            {
//                kernel[i][j] /= sum;
//            }
//        }
//
//        double[][][] rgbValues = new double[3][width][height];
//
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                double[][] colorKernelX = getRGBColorKernel(i, j, kernel[0].length, null);
//
//                for (int k = 0; k < 3; k++)
//                {
//                    double rgb = doubleElementWiseMultiplication(kernel[0], colorKernelX[k]);
//
//                    rgbValues[k][i][j] = (int) rgb;
//                }
//            }
//        }
//
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                double[][] colorKernelY = getRGBColorKernel(i, j, kernel[1].length, rgbValues);
//
//                for (int k = 0; k < 3; k++)
//                {
//                    double rgb = doubleElementWiseMultiplication(kernel[1], colorKernelY[k]);
//
//                    rgbValues[k][i][j] = (int) rgb;
//                }
//            }
//        }
//
//        return rgbValues;
//    }
//
//    private double[][] getRGBColorKernel(int x, int y, int size, double[][][] rgbValues)
//    {
//        double[][] rbgColorKernel = new double[3][size];
//
//        int halfSize = size / 2;
//        int evenSize = 0;
//        if(size % 2 == 0)
//        {
//            evenSize = 1;
//        }
//
//        for (int i = -halfSize; i <= halfSize - evenSize; i++)
//        {
//            if(rgbValues == null)
//            {
//                int targetX = Math.max(0, Math.min(x + i, width - 1));
//
//                rbgColorKernel[0][i + halfSize] = imageContainer.pixels[targetX][y].getR();
//                rbgColorKernel[1][i + halfSize] = imageContainer.pixels[targetX][y].getG();
//                rbgColorKernel[2][i + halfSize] = imageContainer.pixels[targetX][y].getB();
//            }
//            else
//            {
//                int targetY = Math.max(0, Math.min(y + i, height - 1));
//
//                rbgColorKernel[0][i + halfSize] = rgbValues[0][x][targetY];
//                rbgColorKernel[1][i + halfSize] = rgbValues[1][x][targetY];
//                rbgColorKernel[2][i + halfSize] = rgbValues[2][x][targetY];
//            }
//        }
//
//        return rbgColorKernel;
//    }
//
//    private double[][] makeGaussianBlurGreyscale(double[][] kernel)
//    {
//
//        double[][] rgbValues = new double[width][height];
//
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                double[] colorKernelX = getColorKernel(i, j, kernel[0].length, null);
//
//                double rgb = doubleElementWiseMultiplication(kernel[0], colorKernelX);
//
//                rgbValues[i][j] = rgb;
//            }
//        }
//
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                double[] colorKernelY = getColorKernel(i, j, kernel[1].length, rgbValues);
//
//                double rgb = doubleElementWiseMultiplication(kernel[1], colorKernelY);
//
//                rgbValues[i][j] = rgb;
//            }
//        }
//
//        return rgbValues;
//    }
//
//    private double[] getColorKernel(int x, int y, int size, double[][] colorValues)
//    {
//        double[] colorKernel = new double[size];
//
//        int halfSize = size / 2;
//        int evenSize = 0;
//        if(size % 2 == 0)
//        {
//            evenSize = 1;
//        }
//
//        for (int i = -halfSize; i <= halfSize - evenSize; i++)
//        {
//            if(colorValues == null)
//            {
//                int targetX = Math.max(0, Math.min(x + i, width - 1));
//
//                colorKernel[i + halfSize] = imageContainer.pixels[targetX][y].getR();
//            }
//            else
//            {
//                int targetY = Math.max(0, Math.min(y + i, height - 1));
//
//                colorKernel[i + halfSize] = colorValues[x][targetY];
//            }
//        }
//
//        return colorKernel;
//    }
//
//    private double doubleElementWiseMultiplication(double[] kernelA, double[] kernelB)
//    {
//        //kernels must be same size
//        double sum = 0;
//
//        for (int i = 0; i < kernelA.length; i++)
//        {
//            sum += kernelA[i] * kernelB[i];
//        }
//
//        return sum;
//    }
//
//    private void applyGaussianBlur(double[][][] rgbValues)
//    {
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                int a = imageContainer.pixels[i][j].getA();
//                int r = (int) rgbValues[0][i][j];
//                int g = (int) rgbValues[1][i][j];
//                int b = (int) rgbValues[2][i][j];
//
//                imageContainer.pixels[i][j].setARGB(a, r, g, b);
//            }
//        }
//    }
//
//    public void differenceOfGaussians(int size, double sigma, double scale, int strength, int offset, int threshold)
//    {
//        greyScale();
//
//        if(scale <= 1)
//        {
//            scale = 1.1;
//        }
//
//        double[][] kernelA = makeGaussianKernel(size, sigma);
//        double[][] kernelB = makeGaussianKernel(size, sigma * scale);
//
//        double[][] rgbValues = makeGaussianBlurGreyscale(doubleNestedArraySubtraction(kernelA, kernelB));
//
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                int a = imageContainer.pixels[i][j].getA();
//                int rgb = (int) (rgbValues[i][j] * strength + offset);
//
//                if(rgb < threshold)
//                {
//                    rgb = 0;
//                }
//
//                imageContainer.pixels[i][j].setARGB(a, rgb, rgb, rgb);
//            }
//        }
//    }
//
//    private double[][] doubleNestedArraySubtraction(double[][] kernelA, double[][] kernelB)
//    {
//        //kernels must be same size
//        //A - B
//        double[][] kernelC = new double[kernelA.length][kernelA[0].length];
//
//        for (int i = 0; i < kernelC.length; i++)
//        {
//            for (int j = 0; j < kernelC[i].length; j++)
//            {
//                kernelC[i][j] = kernelA[i][j] - kernelB[i][j];
//            }
//        }
//
//        return kernelC;
//    }
}
