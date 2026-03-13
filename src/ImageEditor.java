public class ImageEditor
{
    public static int[] downScale(int[] argb, int width, int height, int widthA, int heightA)
    {
        if(widthA > 1 || heightA > 1)
        {
            int[] tempPixels = new int[width * height];
            int total = widthA * heightA;
            int a, r, g, b;

            for (int x = 0; x < width / widthA; x++)
            {
                for (int y = 0; y < height / heightA; y++)
                {
                    a = 0;
                    r = 0;
                    g = 0;
                    b = 0;

                    for (int i = x * widthA; i < x * widthA + widthA; i++)
                    {
                        for (int j = y * heightA; j < y * heightA + heightA; j++)
                        {
                            int index = width * j + i;
                            int value = argb[index];

                            a += (value >> 24) & 0xff;
                            r += (value >> 16) & 0xff;
                            g += (value >> 8) & 0xff;
                            b += value & 0xff;
                        }
                    }

                    tempPixels[width / widthA * y + x] = ((a / total) << 24) | ((r / total) << 16) | ((g / total) << 8) | (b / total);
                }
            }

            return tempPixels;
        }

        return argb;
    }

    public static int[] upScale(int[] argb, int width, int height, int widthA, int heightA)
    {
        if(widthA > 1 || heightA > 1)
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

        return argb;
    }


    public static void greyScale(int[] argb, int width, int height)
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int value = argb[width * y + x];

                int a = (value >> 24) & 0xff;
                int r = (value >> 16) & 0xff;
                int g = (value >> 8) & 0xff;
                int b = value & 0xff;

                int avg = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                argb[width * y + x] = (a << 24) | (avg << 16) | (avg << 8) | avg;
            }
        }
    }

    public static int[] sobel(int[]argb, int width, int height, Boolean color, int threshold)
    {
        int[] tempPixels = new int[width * height];
        int[] gradient = new int[width * height];
        double[] gAngle = new double[width * height];
        int index;

        int[] gA = {-1, 0, 1};
        int[] gB = {-1, -2, -1};

        int[] luminanceKernelX = new int[width * height];
        int[] luminanceKernelY = new int[width * height];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int lGX = 0;
                int lGY = 0;

                for (int i = -1; i < 2; i++)
                {
                    int targetX= x + i;
                    int value;

                    if(targetX >= 0 && targetX < width)
                    {
                        value = argb[width * y + targetX];
                    }
                    else
                    {
                        value = argb[width * y + x];
                    }

                    double luminance = (0.2126 * (value >> 16 & 0xff) + 0.7152 * (value >> 8 & 0xff) + 0.0722 * (value & 0xff));
                    lGX += (int)(luminance * gA[i + 1]);
                    lGY += (int)(luminance * gB[i + 1]);
                }

                index = width * y + x;

                luminanceKernelX[index] = lGX;
                luminanceKernelY[index] = lGY;
            }
        }

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int lGX = 0;
                int lGY = 0;

                for (int i = -1; i < 2; i++)
                {
                    int targetY= y + i;
                    int valueX, valueY;

                    if(targetY >= 0 && targetY < height)
                    {
                        valueX = luminanceKernelX[width * targetY + x];
                        valueY = luminanceKernelY[width * targetY + x];
                    }
                    else
                    {
                        valueX = luminanceKernelX[width * y + x];
                        valueY = luminanceKernelY[width * y + x];
                    }

                    lGX += valueX * gB[i + 1];
                    lGY += valueY * gA[i + 1];
                }

                int tempG = (int)Math.sqrt(lGX * lGX + lGY * lGY);
                if (tempG > 255)
                {
                    tempG = 255;
                }

                index = width * y + x;

                gradient[index] = tempG;
                gAngle[index] = Math.toDegrees(Math.atan2(lGY, lGX)) + 180;

            }
        }

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                index = width * y + x;
                int a, r, g, b;
                int rgb = gradient[index];
                double angle = gAngle[index];

                if(rgb >= threshold)
                {
                    if (color)
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
                        a = 255; r = rgb; g = rgb; b = rgb;
                    }
                }
                else
                {
                    a = 0; r = 0; g = 0; b = 0;
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
    public static int[] toText(int[] argb, int width, int height, int downScaleWidthAmount, int downScaleHeightAmount, Boolean sobel, int sobelThreshold, AsciiImages asciiImages, Boolean monochrome)
    {
        int asciiWidth = asciiImages.width;
        int asciiHeight = asciiImages.height;

        argb = downScale(argb, width, height, downScaleWidthAmount, downScaleHeightAmount);
        width /= downScaleWidthAmount;
        height /= downScaleHeightAmount;

        char[] chars = {' ', '.', ':', 'c', 'o', 'P', 'O', '?', '@', '█'};
        char[] charImage = new char[width * height];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int value = argb[width * y + x];
                double luminance = (0.2126 * (value >> 16 & 0xff) + 0.7152 * (value >> 8 & 0xff) + 0.0722 * (value & 0xff));
//                System.out.println(luminance);

                luminance = (luminance / 255) * (chars.length - 1);
                luminance = Math.round(luminance);

                charImage[width * y + x] = chars[(int)luminance];
            }
        }

        if(sobel)
        {
            int[] edgePixels;

//            differenceOfGaussians(8, .5, 2, 5, 0, 85);
            edgePixels = sobel(argb, width, height, true, sobelThreshold);

            char[] sobelChars = {'-', '|', '/', '\\'};
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    int value = edgePixels[width * y + x];

                    int a = (value >> 24) & 0xff;
                    int r = (value >> 16) & 0xff;
                    int g = (value >> 8) & 0xff;
                    int b = value & 0xff;

                    if( a != 0)
                    {
                        int index = getSobelIndex(r, g, b);

                        charImage[width * y + x] = sobelChars[index];
                    }
                }
            }
        }

//        for(int x = 0; x < width; x++)
//        {
//            for(int y = 0; y < height; y++)
//            {
//                System.out.print(charImage[width * y + x]);
//            }
//            System.out.println();
//        }

        width *= asciiWidth;
        height *= asciiHeight;

        int[] tempPixels = new int[width * height];

        for (int x = 0; x < width / asciiWidth; x++)
        {
            for (int y = 0; y < height / asciiHeight; y++)
            {
                int[] ascii = asciiImages.getAscii(charImage[width / asciiWidth * y + x]);
                int value = argb[width / asciiWidth * y + x];

                int a = (value >> 24) & 0xff;
                int r = (value >> 16) & 0xff;
                int g = (value >> 8) & 0xff;
                int b = value & 0xff;

                if(monochrome)
                {
                    a = 255;
                    r = 255;
                    g = 255;
                    b = 255;
                }

                for (int i = 0; i < asciiWidth; i++)
                {
                    for (int j = 0; j < asciiHeight; j++)
                    {
                        int index = width * (y * asciiHeight + j) + (x * asciiHeight + i);
                        if(index < tempPixels.length)
                        {
                            if ((ascii[asciiWidth * j + i] & 0xff) == 255)
                            {
                                tempPixels[index] = (a << 24) | (r << 16) | (g << 8) | b;
                            } else
                            {
                                tempPixels[index] = (255 << 24);
                            }
                        }
                    }
                }
            }
        }

        return tempPixels;
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
//
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
