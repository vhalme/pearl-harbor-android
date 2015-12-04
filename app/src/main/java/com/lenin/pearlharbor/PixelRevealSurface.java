package com.lenin.pearlharbor;

/**
 * Created by vhalme on 07/11/15.
 */
import gfx.graphics.Surface;

public class PixelRevealSurface extends Surface {

    int sx;
    int shade;
    int s0[];

    public PixelRevealSurface(int width, int height, int s0[], int shade) {
        super(width, height);
        sx = 0;
        this.shade = 10;
        this.s0 = s0;
        this.shade = shade;
    }

    public boolean reveal(Surface rev) {
        int sr = 0;
        int ran = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < sx; j++)
                pixels[i * width + j] = rev.getPixels()[i * width + j];

        }

        for(int i = sx; i < width - 3; i++) {
            sr++;
            ran = (int)(System.currentTimeMillis() % (long)sr);
            for(int j = 0; j < height - 3; j++) {
                int pos = j * width + i;
                if((j + ran) % sr == 0) {
                    pixels[pos] = rev.getPixels()[pos];
                    pixels[pos + 1] = rev.getPixels()[pos + 1];
                    pixels[pos + 2] = rev.getPixels()[pos + 2];
                    pixels[pos + 3] = rev.getPixels()[pos + 3];
                    pixels[pos + width] = rev.getPixels()[pos + width];
                    pixels[pos + 1 + width] = rev.getPixels()[pos + 1 + width];
                    pixels[pos + 2 + width] = rev.getPixels()[pos + 2 + width];
                    pixels[pos + 3 + width] = rev.getPixels()[pos + 3 + width];
                    pixels[pos + width + width] = rev.getPixels()[pos + width + width];
                    pixels[pos + 1 + width + width] = rev.getPixels()[pos + 1 + width + width];
                    pixels[pos + 2 + width + width] = rev.getPixels()[pos + 2 + width + width];
                    pixels[pos + 3 + width + width] = rev.getPixels()[pos + 3 + width + width];
                    pixels[pos + width + width + width] = rev.getPixels()[pos + width + width + width];
                    pixels[pos + 1 + width + width + width] = rev.getPixels()[pos + 1 + width + width + width];
                    pixels[pos + 2 + width + width + width] = rev.getPixels()[pos + 2 + width + width + width];
                    pixels[pos + 3 + width + width + width] = rev.getPixels()[pos + 3 + width + width + width];
                }
            }

        }

        if(sx < width)
            sx += 4;
        return sx < width;
    }

}

