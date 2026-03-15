public class ImageEditor
{
    public static int[] downScale(int[] argb, int width, int height, int widthA, int heightA)
    {
        if((widthA > 1 || heightA > 1) && (widthA != 0 && heightA != 0))
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

    public static int[] downScaleSobel(int[] argb, int width, int height, int widthA, int heightA)
    {
        if((widthA > 1 || heightA > 1) && (widthA != 0 && heightA != 0))
        {
            int[] tempPixels = new int[width * height];
            double total = widthA * heightA * 1.5;
            int requirement = 7;
            double a, r, g, b, m;

            for (int x = 0; x < width / widthA; x++)
            {
                for (int y = 0; y < height / heightA; y++)
                {
                    a = 0; r = 0; g = 0; b = 0; m = 0;

                    for (int i = x * widthA; i < x * widthA + widthA; i++)
                    {
                        for (int j = y * heightA; j < y * heightA + heightA; j++)
                        {
                            int index = width * j + i;
                            int value = argb[index];

                            if(((value >> 16) & 0xff) == 255)
                            {
                                r += 1.5;
                            }

                            if(((value >> 8) & 0xff) == 255)
                            {
                                g += 1.5;
                            }

                            if((value & 0xff) == 255)
                            {
                                b += 1;
                            }

                            if(((value >> 16) & 0xff) == 128 && (value & 0xff) == 128)
                            {
                                m += 1;
                            }
                        }
                    }

                    if (r + g + b + m > total / requirement)
                    {
                        if(r > g && r > b && r > m)
                        {
                            a = 255; r = 255; g = 0; b = 0;
                        }

                        if (g > r && g > b && g > m)
                        {
                            a = 255; r = 0; g = 255; b = 0;
                        }

                        if (b > r && b > g && b > m)
                        {
                            a = 255; r = 0; g = 0; b = 255;
                        }

                        if(m > r && m > g && m > b)
                        {
                            a = 255; r = 128; g = 0; b = 128;
                        }
                    }
                    else
                    {
                        a = 0; r = 0; g = 0; b = 0;
                    }

                    tempPixels[width / widthA * y + x] = ((int)(a) << 24) | ((int)(r) << 16) | ((int)(g) << 8) | (int)(b);
                }
            }

            return tempPixels;
        }

        return argb;
    }

    public static int[] upScale(int[] argb, int width, int height, int widthA, int heightA)
    {
        if((widthA > 1 || heightA > 1) && (widthA != 0 && heightA != 0))
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


    public static int[] greyScale(int[] argb, int width, int height)
    {
        int[] tempPixels = new int[width * height];

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
                tempPixels[width * y + x] = (a << 24) | (avg << 16) | (avg << 8) | avg;
            }
        }

        return tempPixels;
    }

    public static int[] sobel(int[]argb, int width, int height, Boolean color, int threshold)
    {
        int index;
        int[] tempPixels = new int[width * height];
        int[] gradient = new int[width * height];
        double[] gAngle = new double[width * height];

        double[] lX = new double[width * height];
        double[] lY = new double[width * height];

        sobelHelper(argb, width, height, gradient, gAngle, lX, lY, threshold);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                index = width * y + x;
                int a, r, g, b;
                int rgb = gradient[index];
                double angle = gAngle[index];

                if(rgb == 255)
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
                            a = 255; r = 128; g = 0; b = 128;
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

    private static void sobelHelper(int[]argb, int width, int height, int[] gradient, double[] gAngle, double[] lX, double[] lY, int threshold)
    {
        int index;
        int[] gA = {-1, 0, 1};
        int[] gB = {-1, -2, -1};

        double[] luminanceKernelX = new double[width * height];
        double[] luminanceKernelY = new double[width * height];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double lGX = 0;
                double lGY = 0;

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
                    lGX += (luminance * gA[i + 1]);
                    lGY += (luminance * gB[i + 1]);
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
                double lGX = 0;
                double lGY = 0;

                for (int i = -1; i < 2; i++)
                {
                    int targetY= y + i;
                    double valueX, valueY;

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
                if (tempG >= threshold)
                {
                    tempG = 255;
                }

                index = width * y + x;
                lX[index] = lGX;
                lY[index] = lGY;

                gradient[index] = tempG;
                gAngle[index] = Math.toDegrees(Math.atan2(lGY, lGX)) + 180;
            }
        }
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

        int[] edgePixels = new int[0];
        if(sobel)
        {
//            edgePixels = differenceOfGaussians(argb, width, height, .6, 3, 4, 3.5);
//            edgePixels = extendedDifferenceOfGaussians(argb, width, height, .5, 2, 1.6, 150, 254, .0025);
//            edgePixels = downScale(edgePixels, width, height, asciiWidth, asciiHeight);
//            edgePixels = sobel(argb, width, height, true, sobelThreshold);
//            edgePixels = downScaleSobel(edgePixels, width, height, downScaleWidthAmount, downScaleHeightAmount);
        }

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

                luminance = (luminance / 255) * (chars.length - 1);
                luminance = Math.round(luminance);

                charImage[width * y + x] = chars[(int)luminance];
            }
        }

        if(sobel)
        {
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

        if(r == 128 && b == 128)
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
    public static int[] gaussianBlur(int[]argb, int width, int height, double sigma, int size)
    {
        if(size < 2)
        {
            size = 2;
        }

        double[] kernel = new double[size];

        makeGaussianKernels(kernel, sigma, size);

        double[] r = new double[width * height];
        double[] g = new double[width * height];
        double[] b = new double[width * height];

        makeGaussianBlur(argb, width, height, r, g, b, kernel, size);

        return applyGaussianBlur(argb, width, height, r, g, b);
    }

    private static void makeGaussianKernels(double[] kernel, double sigma, int size)
    {
        int halfSize = size / 2;
        for (int i = 0; i < size; i++) {
            int x = i - halfSize;
            double value = ((x * x) / (2.0 * sigma * sigma)) * -1;

            value = Math.pow(Math.E, value);
            value = (1 / (2 * Math.PI * sigma * sigma)) * value;

            kernel[i] = value;
        }
    }


    private static void makeGaussianBlur(int[] argb, int width, int height, double[] r, double[] g, double[] b, double[] kernel, int size)
    {
        int halfSize = size / 2;
        int evenSize = 0;
        if(size % 2 == 0)
        {
            evenSize = 1;
        }

        double sum = 0;
        for (int i = 0; i < size; i++)
        {
            sum += kernel[i];
        }

        for (int i = 0; i < size; i++)
        {
            kernel[i] /= sum;
        }

        double[] rX = new double[width * height];
        double[] gX = new double[width * height];
        double[] bX = new double[width * height];
        double sumR, sumG, sumB;

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                sumR = 0;
                sumG = 0;
                sumB = 0;

                for (int i = -halfSize; i <= halfSize - evenSize; i++)
                {
                    int targetX = Math.max(0, Math.min(x + i, width - 1));

                    sumR += ((argb[width * y + targetX] >> 16) & 0xff) * kernel[i + halfSize];
                    sumG += ((argb[width * y + targetX] >> 8) & 0xff) * kernel[i + halfSize];
                    sumB += (argb[width * y + targetX] & 0xff) * kernel[i + halfSize];
                }

                rX[width * y + x] = sumR;
                gX[width * y + x] = sumG;
                bX[width * y + x] = sumB;
            }
        }

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                sumR = 0;
                sumG = 0;
                sumB = 0;

                for (int i = -halfSize; i <= halfSize - evenSize; i++)
                {
                    int targetY = Math.max(0, Math.min(y + i, height - 1));

                    sumR += rX[width * targetY + x] * kernel[i + halfSize];
                    sumG += gX[width * targetY + x] * kernel[i + halfSize];
                    sumB += bX[width * targetY + x] * kernel[i + halfSize];
                }

                r[width * y + x] = sumR;
                g[width * y + x] = sumG;
                b[width * y + x] = sumB;
            }
        }
    }

    private static int[] applyGaussianBlur(int[] argb, int width, int height, double[] rValues, double[] gValues, double[] bValues)
    {
        int[] tempPixels = new int[width * height];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int index = width * y + x;

                int a = (argb[index] >> 24) & 0xff;
                int r = Math.max(0, Math.min(255, (int)rValues[width * y + x]));
                int g = Math.max(0, Math.min(255, (int)gValues[width * y + x]));
                int b = Math.max(0, Math.min(255, (int)bValues[width * y + x]));

                tempPixels[index] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        return tempPixels;
    }

    public static int[] differenceOfGaussians(int[]argb, int width, int height, double sigma, int size, double scale,  double threshold)
    {
        int[] tempPixels = greyScale(argb, width, height);

        if(size < 2)
        {
            size = 2;
        }

        if(scale <= 1)
        {
            scale = 1.1;
        }

        //A
        double[] kernelA = new double[size];
        makeGaussianKernels(kernelA, sigma, size);

        double[] rgbA = new double[width * height];
        makeGaussianBlur(tempPixels, width, height, rgbA, rgbA, rgbA, kernelA, size);

        //B
        size *= 4;
        double[] kernelB = new double[size];
        makeGaussianKernels(kernelB, sigma * scale, size);

        double[] rgbB = new double[width * height];
        makeGaussianBlur(tempPixels, width, height, rgbB, rgbB, rgbB, kernelB, size);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int index = width * y + x;
                int a = (argb[index] >> 24) & 0xff;
                double rgb = (rgbA[index] - rgbB[index]);

                if(rgb < threshold)
                {
                    tempPixels[index] = (a << 24) | (0);
                }
                else
                {
                    tempPixels[index] = (a << 24) | (255 << 16) | (255 << 8) | 255;
                }
            }
        }

        return tempPixels;
    }

    public static int[] extendedDifferenceOfGaussians(int[]argb, int width, int height, double sigma, int size, double scale, double tau, double threshold, double phi)
    {
        // increasing tau sharpens edges
        // threshold determines the white cut-off of pixels
        // increasing phi sharpens the transition between white to black

        int[] tempPixels = greyScale(argb, width, height);

        if(size < 2)
        {
            size = 2;
        }

        if(scale <= 1)
        {
            scale = 1.1;
        }

        //A
        double[] kernelA = new double[size];
        makeGaussianKernels(kernelA, sigma, size);

        double[] rgbA = new double[width * height];
        makeGaussianBlur(tempPixels, width, height, rgbA, rgbA, rgbA, kernelA, size);

        //B
        size *= 4;
        double[] kernelB = new double[size];
        makeGaussianKernels(kernelB, sigma * scale, size);

        double[] rgbB = new double[width * height];
        makeGaussianBlur(tempPixels, width, height, rgbB, rgbB, rgbB, kernelB, size);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int index = width * y + x;
                int a = (argb[index] >> 24) & 0xff;
                double rgb = ((1 + tau) * rgbA[index] - tau * rgbB[index]);

                if(rgb < threshold)
                {
                    int value = (int)(255 * (1 + Math.tanh(phi * (rgb - threshold))));
                    tempPixels[index] = (a << 24) | (value << 16) | (value << 8) | value;
                }
                else
                {
                    tempPixels[index] = (a << 24) | (255 << 16) | (255 << 8) | 255;
                }
            }
        }

        return tempPixels;
    }

    public static int[] flowBasedExtendedDifferenceOfGaussians(int[]argb, int width, int height, double sigmaC, double sigmaE, double sigmaM, int size, double scale, double tau, double threshold, double phi)
    {
        // increasing tau sharpens edges
        // threshold determines the white cut-off of pixels
        // increasing phi sharpens the transition between white to black

        int[] tempPixels = greyScale(argb, width, height);

        if(size < 2)
        {
            size = 2;
        }

        if(scale <= 1)
        {
            scale = 1.1;
        }

        //A
        double[] kernelA = new double[size];
        makeGaussianKernels(kernelA, sigmaC, size);

        double[] rgbA = new double[width * height];
        makeGaussianBlur(tempPixels, width, height, rgbA, rgbA, rgbA, kernelA, size);

        //B
        size *= 4;
        double[] kernelB = new double[size];
        makeGaussianKernels(kernelB, sigmaC * scale, size);

        double[] rgbB = new double[width * height];
        makeGaussianBlur(tempPixels, width, height, rgbB, rgbB, rgbB, kernelB, size);


        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int index = width * y + x;
                int a = (argb[index] >> 24) & 0xff;
                double rgb = ((1 + tau) * rgbA[index] - tau * rgbB[index]);

                if(rgb < threshold)
                {
                    int value = (int)(255 * (1 + Math.tanh(phi * (rgb - threshold))));
                    tempPixels[index] = (a << 24) | (value << 16) | (value << 8) | value;
                }
                else
                {
                    tempPixels[index] = (a << 24) | (255 << 16) | (255 << 8) | 255;
                }
            }
        }

        return tempPixels;
    }
}
