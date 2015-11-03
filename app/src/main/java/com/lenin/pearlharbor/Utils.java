package com.lenin.pearlharbor;

/**
 * Created by vhalme on 02/11/15.
 */
public class Utils {

    public static void rotate(double ang, int oldPix[], int newPix[], int width) {

        float angle = (float)(6.2831853071795862D - ang);
        int height = oldPix.length / width;
        float hw = width / 2;
        float hh = height / 2;
        float posX[] = new float[2];
        float posY[] = new float[2];
        posX[0] = -(float)((double)width * Math.sin((double)angle + 1.5707963267948966D));
        posX[1] = (float)((double)width * Math.cos((double)angle + 1.5707963267948966D));
        posY[0] = -(float)((double)height * Math.sin(angle));
        posY[1] = (float)((double)height * Math.cos(angle));
        float Xline[] = {
                posX[0] / (float)width, posX[1] / (float)width
        };
        float Yline[] = {
                posY[0] / (float)height, posY[1] / (float)height
        };
        float xOff = 0.0F;
        float yOff = 0.0F;
        xOff = hw - (Xline[0] * hw + Yline[0] * hh);
        yOff = hh - (Xline[1] * hw + Yline[1] * hh);
        float xBase[][] = new float[2][width];
        float yBase[][] = new float[2][height];
        for(int x = 0; x < width; x++)
        {
            xBase[0][x] = Xline[0] * (float)x + xOff;
            yBase[0][x] = Xline[1] * (float)x + yOff;
        }

        for(int y = 0; y < height; y++)
        {
            xBase[1][y] = Yline[0] * (float)y;
            yBase[1][y] = Yline[1] * (float)y;
        }

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                int xi = (int)(xBase[0][x] + xBase[1][y]);
                int yi = (int)(yBase[0][x] + yBase[1][y]);
                if((xi < width) & (yi < height) & (yi >= 0) & (xi >= 0))
                    newPix[y * width + x] = oldPix[yi * width + xi];
                else
                    newPix[y * width + x] = 0;
            }

        }

    }

    public static void rotate(double ang, boolean oldPix[], boolean newPix[], int d[]) {

        float angle = (float)(6.2831853071795862D - ang);
        int width = d[0];
        int height = d[1];
        float hw = width / 2;
        float hh = height / 2;
        float posX[] = new float[2];
        float posY[] = new float[2];
        posX[0] = -(float)((double)width * Math.sin((double)angle + 1.5707963267948966D));
        posX[1] = (float)((double)width * Math.cos((double)angle + 1.5707963267948966D));
        posY[0] = -(float)((double)height * Math.sin(angle));
        posY[1] = (float)((double)height * Math.cos(angle));
        float Xline[] = {
                posX[0] / (float)width, posX[1] / (float)width
        };
        float Yline[] = {
                posY[0] / (float)height, posY[1] / (float)height
        };
        float xOff = 0.0F;
        float yOff = 0.0F;
        xOff = hw - (Xline[0] * hw + Yline[0] * hh);
        yOff = hh - (Xline[1] * hw + Yline[1] * hh);
        float xBase[][] = new float[2][width];
        float yBase[][] = new float[2][height];
        for(int x = 0; x < width; x++)
        {
            xBase[0][x] = Xline[0] * (float)x + xOff;
            yBase[0][x] = Xline[1] * (float)x + yOff;
        }

        for(int y = 0; y < height; y++)
        {
            xBase[1][y] = Yline[0] * (float)y;
            yBase[1][y] = Yline[1] * (float)y;
        }

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                int xi = (int)(xBase[0][x] + xBase[1][y]);
                int yi = (int)(yBase[0][x] + yBase[1][y]);
                if((xi < width) & (yi < height) & (yi >= 0) & (xi >= 0))
                    newPix[y * width + x] = oldPix[yi * width + xi];
                else
                    newPix[y * width + x] = false;
            }

        }


    }


}
