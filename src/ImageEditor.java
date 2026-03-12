public class ImageEditor
{
    public static int[] downScale(int[] argb, int width, int height, int widthA, int heightA)
    {
        int[] tempPixels = new int[width / widthA * height / heightA];
        int total = widthA * heightA;
        int a, r, g, b;
        int color;

        for (int x = 0; x < width / widthA; x++)
        {
            for (int y = 0; y < height / heightA; y++)
            {
                a = 0; r = 0; g = 0; b = 0;

                for (int i = x * widthA; i < x * widthA + widthA; i++)
                {
                    for (int j = y * heightA; j < y * heightA + heightA; j++)
                    {
                        color = argb[width * y + x];

                        a += color >> 24 & 0xff;
                        r += color >> 16 & 0xff;
                        g += color >> 8 & 0xff;
                        b += color & 0xff;
                    }
                }

                tempPixels[width / widthA * y + x] = ((a / total) << 24) | ((r / total) << 16) |  ((g / total) << 8) | (b/ total);
            }
        }

        return tempPixels;
    }

    public static int[] upScale(int[] argb, int width, int height, int widthA, int heightA)
    {
        int[] tempPixels = new int[width * widthA * height * heightA];
        int index;

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int i = 0; i < widthA; i++)
                {
                    for (int j = 0; j < heightA; j++)
                    {
                        index = (width * widthA) * (y * heightA + j) + (x * widthA + i);
                        tempPixels[index] = argb[width * y + x];
                    }
                }
            }
        }

        return tempPixels;
    }


    public static void greyScale(int[] argb, int width, int height)
    {
        int a, r, g, b;
        int index, avg;

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                index = width * y + x;

                a = argb[index] >> 24 & 0xff;
                r = argb[index] >> 16 & 0xff;
                g = argb[index] >> 8 & 0xff;
                b = argb[index] & 0xff;

                avg = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                argb[index] = (a << 24) | (avg << 16) | (avg << 8) | avg;
            }
        }
    }

    public static int[] sobel(int[]argb, int width, int height, Boolean color, int threshold)
    {
        int[] tempPixels = new int[width * height];
        int[] gradient = new int[width * height];
        double[] gAngle = new double[width * height];
        int index;

        int[] gX = {-1, 0, 1,
                    -2, 0, 2,
                    -1, 0, 1};

        int[] gY = {-1, -2, -1,
                     0, 0, 0,
                     1, 2, 1};

        int[] luminanceKernel = new int[9];
        int targetX, targetY, value, lGX, lGY, tempG;

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int i = -1; i < 2; i++)
                {
                    for (int j = -1; j < 2; j++)
                    {
                        index = 3 * (j + 1) + i + 1;
                        targetX = x + i;
                        targetY = y + j;

                        if(targetX >= 0 && targetX < width && targetY >= 0 && targetY < height)
                        {
                            value = argb[width * targetY + targetX];
                            luminanceKernel[index] = (int)(0.2126 * (value >> 16 & 0xff) + 0.7152 * (value >> 8 & 0xff) + 0.0722 * (value & 0xff));
                        }
                        else
                        {
                            luminanceKernel[index] = 0;
                        }
                    }
                }

                index = width * y + x;
                lGX = 0;
                lGY = 0;
                for (int i = 0; i < luminanceKernel.length; i++)
                {
                    lGX += gX[i] * luminanceKernel[i];
                    lGY += gY[i] * luminanceKernel[i];
                }

                tempG = (int)Math.sqrt(lGX * lGX + lGY * lGY);
                if (tempG > 255)
                {
                    tempG = 255;
                }

                gradient[index] = tempG;
                gAngle[index] = Math.toDegrees(Math.atan2(lGY, lGX)) + 180;
            }
        }

        int rgb;
        int a, r, g, b;
        double angle;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                index = width * y + x;
                rgb = gradient[index];
                angle = gAngle[index];

                if(color)
                {
                    if (rgb >= threshold && ((x > 0 && x < width - 1) && (y > 0 && y < height - 1)))
                    {
                        if ((angle >= 0 && angle < 6) || (angle >= 175 && angle < 186) || angle >= 355)
                        {
                            a = 255; r = 255; g = 0; b = 0;
                        }
                        else if ((angle >= 85 && angle < 96) || (angle >= 265 && angle < 276))
                        {
                            a = 255; r = 0; g = 255; b = 0;
                        }
                        else if ((angle >= 5 && angle < 85) || (angle >= 185 && angle < 265))
                        {
                            a = 255; r = 0; g = 0; b = 255;
                        }
                        else
                        {
                            a = 255; r = 255; g = 0; b = 255;
                        }
                    }
                    else
                    {
                        a = 0; r = rgb; g = rgb; b = rgb;
                    }
                }
                else
                {
                    a = 255; r = rgb; g = rgb; b = rgb;
                }

                tempPixels[index] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        return tempPixels;
    }
//
//
//    public void toText(Boolean sobel, int sobelThreshold)
//    {
//        printNestedCharArray(makeCharImage(sobel, sobelThreshold));
//    }
//
//    public void toText(Boolean sobel, int sobelThreshold, Boolean monochrome)
//    {
//        downScale(8, 8);
//
//        BufferedImage ogImage = imageHandler.pixels.getImageCopy();
//
//        char[][] charImage = makeCharImage(sobel, sobelThreshold);
//
//        imageHandler.pixels.setImage(ogImage);
//        asciiToImage(monochrome, charImage);
//    }
//
//    private char[][] makeCharImage(Boolean sobel, int sobelThreshold)
//    {
//        char[] chars = {' ', '.', ':', 'c', 'o', 'P', 'O', '?', '@', '█'};
//        char[][] charImage = new char[height][width];
//
//        for (int y = 0; y < height; y++)
//        {
//            for (int x = 0; x < width; x++)
//            {
//                double luminance = imageHandler.pixels.getLuminance(x, y);
//
//                luminance = (luminance / 255) * (chars.length - 1);
//                luminance = Math.round(luminance);
//
//                charImage[y][x] = chars[(int)luminance];
//            }
//        }
//
//        if(sobel) {
//            differenceOfGaussians(8, .5, 2, 5, 0, 85);
//            sobel(true, sobelThreshold);
//
//            char[] sobelChars = {'-', '|', '/', '\\'};
//
//            for (int y = 0; y < height; y++)
//            {
//                for (int x = 0; x < width; x++)
//                {
//                    int a = imageHandler.pixels.getA(x, y);
//                    int r = imageHandler.pixels.getR(x, y);
//                    int g = imageHandler.pixels.getG(x, y);
//                    int b = imageHandler.pixels.getB(x, y);
//
//                    if( a != 0)
//                    {
//                        int index = getSobelIndex(r, g, b);
//
//                        charImage[y][x] = sobelChars[index];
//                    }
//                }
//            }
//        }
//
//        return charImage;
//    }
//
//    private static int getSobelIndex(int r, int g, int b)
//    {
//        int index = -1;
//
//        if(r == 255)
//        {
//            index = 0;
//        }
//
//        if(g == 255)
//        {
//            index = 1;
//        }
//
//        if(b == 255)
//        {
//            index = 2;
//        }
//
//        if(r == 255 && b == 255)
//        {
//            index = 3;
//        }
//        return index;
//    }
//
//    private void printNestedCharArray(char[][] array)
//    {
//
//        for (int i = 0; i < array.length; i++)
//        {
//            String line = "";
//
//            for (int j = 0; j < array[i].length; j++)
//            {
//                line += array[i][j];
//            }
//
//            System.out.println(line);
//        }
//    }
//
//    private void asciiToImage(Boolean monochrome, char[][] array)
//    {
//        if(asciiImages == null)
//        {
//            asciiImages = new AsciiImages(8);
//        }
//
//        width *= 8;
//        height *= 8;
//
//        int[] tempPixels = new int[width * height];
//
//        for (int y = 0; y < height / 8; y++)
//        {
//            for (int x = 0; x < width / 8; x++)
//            {
//                ImageHandler ascii = asciiImages.getAscii(array[y][x]);
//
//                int a = imageHandler.pixels.getA(x, y);
//                int r = imageHandler.pixels.getR(x, y);
//                int g = imageHandler.pixels.getG(x, y);
//                int b = imageHandler.pixels.getB(x, y);
//
//                if(monochrome)
//                {
//                    a = 255;
//                    r = 255;
//                    g = 255;
//                    b = 255;
//                }
//
//                copyImage(ascii.pixels, tempPixels, x * 8, y * 8, a, r, g, b);
//            }
//        }
//
//        imageHandler.pixels.setARGB(tempPixels, width, height);
//        imageHandler.pixels.syncImage();
//        imageHandler.image = imageHandler.pixels.getImage();
//    }
//
//    private void copyImage(Pixels subject, int[] canvas, int x, int y, int a, int r, int g, int b)
//    {
//        for (int i = 0; i < subject.width; i++)
//        {
//            for (int j = 0; j < subject.height; j++)
//            {
//                int index = width * (y + j) + (x + i);
//
//                if(subject.getR(i, j) == 255)
//                {
//                    canvas[index] = imageHandler.pixels.getIntARGB(a, r, g, b);
//                }
//                else
//                {
//                    canvas[index] = imageHandler.pixels.getIntARGB(255, 0, 0, 0);
//                }
//            }
//        }
//    }
//
//    public void gaussianBlur(double sigma)
//    {
//        //size is radius * 2 + 1, radius is sigma * 3
//        int size = (int)(sigma * 5 + 1);
//        double[][] kernel = makeGaussianKernel(size, sigma);
//
//        double[][]rgb = makeGaussianBlur(kernel);
//
//        applyGaussianBlur(rgb);
//        imageHandler.pixels.syncImage();
//    }
//
//    public void gaussianBlur(double sigma, int size)
//    {
//        double[][] kernel = makeGaussianKernel(size, sigma);
//
//        double[][] rgb = makeGaussianBlur(kernel);
//
//        applyGaussianBlur(rgb);
//        imageHandler.pixels.syncImage();
//    }
//
//    public void gaussianBlur(double[][] kernel)
//    {
//        double[][] rgb = makeGaussianBlur(kernel);
//
//        applyGaussianBlur(rgb);
//        imageHandler.pixels.syncImage();
//    }
//
//    private double[][] makeGaussianKernel(int size, double sigma)
//    {
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
//    private double[][] makeGaussianBlur(double[][] kernel)
//    {
//        int halfSize = kernel[0].length / 2;
//        int evenSize = 0;
//        if(kernel[0].length % 2 == 0)
//        {
//            evenSize = 1;
//        }
//
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
//        double sumR, sumG, sumB;
//        double[][] rgbValuesX = new double[3][width * height];
//
//        for (int x = 0; x < width; x++)
//        {
//            for (int y = 0; y < height; y++)
//            {
//                sumR = 0;
//                sumG = 0;
//                sumB = 0;
//
//                for (int i = -halfSize; i <= halfSize - evenSize; i++)
//                {
//                    int targetX = Math.max(0, Math.min(x + i, width - 1));
//
//                    sumR += imageHandler.pixels.getR(targetX, y) * kernel[0][i + halfSize];
//                    sumG += imageHandler.pixels.getG(targetX, y) * kernel[0][i + halfSize];
//                    sumB += imageHandler.pixels.getB(targetX, y) * kernel[0][i + halfSize];
//                }
//
//                rgbValuesX[0][width * y + x] = sumR;
//                rgbValuesX[1][width * y + x] = sumG;
//                rgbValuesX[2][width * y + x] = sumB;
//            }
//        }
//
//        double[][] rgbValues = new double[3][width * height];
//
//        for (int x = 0; x < width; x++)
//        {
//            for (int y = 0; y < height; y++)
//            {
//                sumR = 0;
//                sumG = 0;
//                sumB = 0;
//
//                for (int i = -halfSize; i <= halfSize - evenSize; i++)
//                {
//                    int targetY = Math.max(0, Math.min(y + i, height - 1));
//
//                    sumR += rgbValuesX[0][width * targetY + x] * kernel[1][i + halfSize];
//                    sumG += rgbValuesX[1][width * targetY + x] * kernel[1][i + halfSize];
//                    sumB += rgbValuesX[2][width * targetY + x] * kernel[1][i + halfSize];
//                }
//
//                rgbValues[0][width * y + x] = sumR;
//                rgbValues[1][width * y + x] = sumG;
//                rgbValues[2][width * y + x] = sumB;
//            }
//        }
//
//        return rgbValues;
//    }
//
//    private void applyGaussianBlur(double[][] rgbValues)
//    {
//        for (int x = 0; x < width; x++)
//        {
//            for (int y = 0; y < height; y++)
//            {
//                int a = imageHandler.pixels.getA(x, y);
//                int r = (int) rgbValues[0][width * y + x];
//                int g = (int) rgbValues[1][width * y + x];
//                int b = (int) rgbValues[2][width * y + x];
//
//                imageHandler.pixels.setARGB(x, y, a, r, g, b);
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
//        size = (int)(sigma * 5 + 1);
//
//        double[][] kernelA = makeGaussianKernel(size, sigma);
//
//        size = (int)(sigma * scale * 5 + 1);
//        double[][] kernelB = makeGaussianKernel(size, sigma * scale);
//
//        double[][] rgbValuesA = makeGaussianBlur(kernelA);
//        double[][] rgbValuesB = makeGaussianBlur(kernelB);
//
//        for (int x = 0; x < width; x++)
//        {
//            for (int y = 0; y < height; y++)
//            {
//                int a = imageHandler.pixels.getA(x, y);
//                int rgb = (int)(rgbValuesA[0][width * y + x] - rgbValuesB[0][width * y + x]) * strength + offset;
//
//                if(rgb < threshold)
//                {
//                    rgb = 0;
//                }
//
//                imageHandler.pixels.setARGB(x, y, a, rgb, rgb, rgb);
//            }
//        }
//
//        imageHandler.pixels.syncImage();
//    }
}
